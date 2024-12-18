package TestManagment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import idoandtal.Question.difficulty;


public class DataBaseHelper {

	//a function that establishes a connection to Postgres
	public Connection createConnection()
	{
      	Connection conn = null;
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");
            // Database connection URL and credentials
            String dbUrl = "jdbc:postgresql://localhost:5432/FinalProjectDataBase";
            String user = "postgres";
            String password = "Tal123";
            // Establish the connection
            conn = DriverManager.getConnection(dbUrl, user, password);
            //System.out.println("Connection established.");            
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
		return conn;
	}
	
	//a function that sets a certian stock with its ID from the data base
	public void getStockIdBySubject(Connection conn,Stock repository ,String subject) {
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    int stockId = -1;

	    try {
	        // SQL query to select the stock_id based on the subject
	        String query = "SELECT stock_id FROM StockTable WHERE subject = ?";
	        pstmt = conn.prepareStatement(query);
	        pstmt.setString(1, subject);
	        // Execute the query
	        rs = pstmt.executeQuery();
	        // If a matching stock_id is found, retrieve it
	        if (rs.next()) {
	            stockId = rs.getInt("stock_id");
	            repository.setStockID(stockId);
	        } else {
	            System.out.println("No stock found for subject: " + subject);
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//a function that reads all subjects from the data base and enters them to an array
	public String[] getAllSubjects(Connection conn)
	{
		List<String> allSubjects = new ArrayList<>();
        String query = "SELECT * FROM StockTable";
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String subject = resultSet.getString("subject");
                allSubjects.add(subject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        // Convert the list to an array and return it
        return allSubjects.toArray(new String[0]);
    }

	//a function that reads a certain stock from the data base
	public void getStockBySubject(Connection conn, Stock repository) {
	    String query = "SELECT q.question_id, q.question_text, q.question_difficulty, q.question_type, a.answer_text, a.is_correct, a.answer_id " +
	                   "FROM QuestionsTable q " +
	                   "JOIN AnswersTable a ON q.question_id = a.question_id " +
	                   "JOIN StockQuestionsTable sq ON q.question_id = sq.question_id " + 
	                   "JOIN StockTable s ON sq.stock_id = s.stock_id " +
	                   "WHERE s.subject = ? " +
	                   "ORDER BY q.question_id, a.answer_id";
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, repository.getSubject());
	        ResultSet rs = pstmt.executeQuery();

	        Question currentQuestion = null;
	        int currentQuestionId = -1;
	        int questionIndex = 0;

	        
	        
	        while (rs.next()) {
	            int questionId = rs.getInt("question_id");
	            String questionText = rs.getString("question_text");
	            String difficulty = rs.getString("question_difficulty");
	            int dif = getDifficultyNumber(difficulty);
	            String type = rs.getString("question_type");
	            String answerText = rs.getString("answer_text");
	            boolean isCorrect = rs.getBoolean("is_correct");
	            int ansId = rs.getInt("answer_id");

	            // Check if we are processing a new question
	            if (questionId != currentQuestionId) {
	                if (currentQuestion != null) {
	                    repository.setQuestionInStock(currentQuestion, questionIndex);
	                    questionIndex++;
	                }

	                // Create a new question based on its type
	                if ("Open".equalsIgnoreCase(type)) {
	                    currentQuestion = new OpenQuestion(questionText, answerText, dif);
		                currentQuestion.setQuestionID(questionId);
		                ((OpenQuestion)currentQuestion).getSchoolAnswer().setAnswerId(ansId);
	                } else if ("American".equalsIgnoreCase(type)) {
	                    currentQuestion = new AmericanQuestion(questionText, dif);
		                currentQuestion.setQuestionID(questionId);
	                    ((AmericanQuestion) currentQuestion).addAnswer(answerText, isCorrect, ansId);
	                }
	                currentQuestionId = questionId;
	            } else if (currentQuestion instanceof AmericanQuestion) {
	                ((AmericanQuestion) currentQuestion).addAnswer(answerText, isCorrect, ansId);
	            }
	            
	        }
	        // Add the last question to the stock
	        if (currentQuestion != null) {
	            repository.setQuestionInStock(currentQuestion, questionIndex);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	//a function that returns the Enum number of a certain Enum String 
	public static int getDifficultyNumber(String dif)
	{
		switch(dif)
		{
		case "Easy":
			return 1;
		case "Medium":
			return 2;
		case "Hard":
			return 3;
		default:
			return -1;
		}
	}
	
	//a function that returns the Enum String given a number
	public String getDifficultyString(int num)
	{
		if (num == 1) {
			return "Easy";
		}
		else if (num == 2) {
			return "Medium";
		}
		else {
			return "Hard";
		}
	}
	
	//a function that adds a row (Answer) to the answersTable in the data base
	public int addAnswerToQuestion(Connection conn, int questionId, String answerText, boolean isCorrect) {
	    String query = "INSERT INTO AnswersTable (question_id, answer_text, is_correct) VALUES (?, ?, ?) RETURNING answer_id";
	    int answerId = -1;

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setString(2, answerText);
	        pstmt.setBoolean(3, isCorrect);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                answerId = rs.getInt("answer_id");
	                //System.out.println("Answer added successfully with ID: " + answerId);
	            } else {
	                System.out.println("Failed to retrieve the answer ID.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return answerId;
	}
	
	//a function that updated an existing row in the answersTable (changes the content of its answer_text column)
	public void updateAnswerText(Connection conn, int questionId, int answerId, String newText) {
	    String query = "UPDATE AnswersTable SET answer_text = ? WHERE question_id = ? AND answer_id = ?";
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, newText);      // Set the new answer text
	        pstmt.setInt(2, questionId);      // Set the question_id
	        pstmt.setInt(3, answerId);        // Set the answer_id
	        
	        int rowsAffected = pstmt.executeUpdate();  // Execute the update statement
	        
	        if (rowsAffected > 0) {
	            //System.out.println("Answer text updated successfully.");
	        } else {
	            System.out.println("No answer found with the given question_id and answer_id.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//a function that updates the num_of_answers column in the QuestionsTable
	public void updateNumOfAnswers(Connection conn, int questionId, int numOfAnswers) {
	    String query = "UPDATE QuestionsTable SET num_of_answers = ? WHERE question_id = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, numOfAnswers);  // Set the new number of answers
	        pstmt.setInt(2, questionId);    // Set the question ID

	        int rowsAffected = pstmt.executeUpdate();  // Execute the update statement

	        if (rowsAffected > 0) {
	            // System.out.println("Number of answers updated successfully.");
	        } else {
	            System.out.println("No question found with the given ID.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	
	//a function that adds new row (question) to the questionsTable
	public int addQuestionToTable(Connection connection, String questionText, String diff, String questionType, int numOfAnswers) {
		int questionId=-1;
	    String query = "INSERT INTO QuestionsTable (question_text, question_difficulty, question_type, num_of_answers) " +
	                   "VALUES (?, CAST(? AS DifficultyLevel), CAST(? AS QuestionType), ?) RETURNING question_id";

	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setString(1, questionText);
	        statement.setString(2, diff); // Assuming 'diff' is a string like "easy", "medium", or "hard"
	        statement.setString(3, questionType);
	        statement.setInt(4, numOfAnswers);

	        // Use executeQuery() to handle the result returned by the query
	        try (ResultSet rs = statement.executeQuery()) {
	            if (rs.next()) {
	                questionId = rs.getInt("question_id");
	                //System.out.println("The question was added successfully with ID: " + questionId);
	            } else {
	                System.out.println("Failed to retrieve the question ID.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return questionId;
	}
	
	//a function the adds a new row (question to stock) to the stockQuestionTable
	public void addQuestionToStock(Connection conn, int stockId, int questionId) {
	    String query = "INSERT INTO StockQuestionsTable (stock_id, question_id) VALUES (?, ?)";

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, stockId);
	        pstmt.setInt(2, questionId);

	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            //System.out.println("Question successfully added to stock.");
	        } else {
	            System.out.println("Failed to add the question to stock.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//a function that deletes a row in the answersTable
	public void deleteAnswer(Connection conn, int questionId, int answerId) {
	    String query = "DELETE FROM AnswersTable WHERE question_id = ? AND answer_id = ?";
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        pstmt.setInt(2, answerId);
	        
	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            //System.out.println("Answer deleted successfully.");
	        } else {
	            System.out.println("No answer found with the given IDs.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//a function that deletes a row from the questionsTable
	public void deleteQuestionById(Connection conn, int questionId) {
	    String query = "DELETE FROM QuestionsTable WHERE question_id = ?";
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, questionId);
	        
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected > 0) {
	            //System.out.println("Question deleted successfully.");
	        } else {
	            System.out.println("No question found with the given ID.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//a function that adds a new row (test) to the testTable
	public int addTestToTable(Connection conn, String testCreationType, int numOfQues, String subject) {
	    String query = "INSERT INTO testTable (test_creation_type, num_of_ques, subject) VALUES (CAST(? AS TestCreationType), ?, ?) RETURNING test_id";
	    int testId = -1;

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, testCreationType); // Set the test creation type (e.g., "Manual" or "Automatic")
	        pstmt.setInt(2, numOfQues);          // Set the number of questions
	        pstmt.setString(3, subject);         // Set the subject

	        // Execute the query and retrieve the test ID
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                testId = rs.getInt("test_id");
	                //System.out.println("Test added successfully with ID: " + testId);
	            } else {
	                System.out.println("Failed to retrieve the test ID.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return testId;
	}
	
	//a function that adds a row (user answer selection) to the userAnswerTable
	public void addToUserAnswersTable(Connection conn, int testId, Question question) {
	    String query = "INSERT INTO userAnswerTable (test_id, question_id, answer_id) VALUES (?, ?, ?)";

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        int questionId = question.id;

	        if (question instanceof AmericanQuestion) {
	            AmericanQuestion americanQuestion = (AmericanQuestion) question;
	            Answers[] userAnswers = americanQuestion.getUsersArray(); 

	            for (int i = 2; i < userAnswers.length; i++) {
	                pstmt.setInt(1, testId);
	                pstmt.setInt(2, questionId);
	                pstmt.setInt(3, userAnswers[i].getAnsId());
	                pstmt.addBatch(); // Add this set of values to the batch
	            }

	            int[] rowsAffected = pstmt.executeBatch(); // Execute the batch insert
	            //System.out.println("Added " + rowsAffected.length + " user answers to the table.");

	        } else if (question instanceof OpenQuestion) {
	            OpenQuestion openQuestion = (OpenQuestion) question;
	            int correctAnswerId = openQuestion.getSchoolAnswerId();

	            pstmt.setInt(1, testId);
	            pstmt.setInt(2, questionId);
	            pstmt.setInt(3, correctAnswerId);
	            pstmt.executeUpdate();

	           // System.out.println("Added the school answer to the userAnswerTable.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	//a function that add a new row (stock) for stock Table 
	public int addStock(Connection conn, String subject) {
	    String query = "INSERT INTO StockTable (subject) VALUES (?) RETURNING stock_id";
	    int stockId = -1;

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setString(1, subject);  // Set the subject

	        // Execute the query and retrieve the generated stock_id
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                stockId = rs.getInt("stock_id");
	                //System.out.println("Stock added successfully with ID: " + stockId);
	            } else {
	                System.out.println("Failed to retrieve the stock ID.");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return stockId;
	}
	
}
