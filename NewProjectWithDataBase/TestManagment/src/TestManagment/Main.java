package TestManagment;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import idoandtal.Question.difficulty;

import java.awt.Menu;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.*;


public class Main{

	public static Scanner input = new Scanner (System.in);

	public static void main(String[] args) throws Exception {
		
		DataBaseHelper dbHelper = new DataBaseHelper();
		Connection conn = dbHelper.createConnection();
		String[] subjects = dbHelper.getAllSubjects(conn);
		
		
		//reading from the Binary file - "subjects" that contains the subjects the program already have
		//String[] subjects = getSubjectsFromFile("subjects.dat");
		Stock repository = null;
		//printing the subjects to user
		System.out.println("Hello!!! \nEnter your choice:\n");
		System.out.println("0 ----> for a new subject");
		PrintSubjects(subjects);
		//getting users choice
		int selector = input.nextInt();
		//checking the selection valid
		while( selector < 0 || selector > subjects.length){
			System.out.println("\u001B[31m" + "Invalid Input, enter a different number." + "\u001B[0m");
			selector = input.nextInt();
			input.nextLine(); //cleaning buffer
		}
		if(selector == 0) {
			//getting the new subject from the user
			System.out.println("Enter the subject name: ");
			input.nextLine(); //cleaning buffer
			String sub = input.nextLine();
			//checking that the subject isn't already exist in the program
			while(isSubAlreadyInProgram(sub, subjects)) {
				System.out.println("\u001B[31m" + "This subject already exists. \nPlese enter a different subject." + "\u001B[0m");
				sub = input.nextLine();
			}
			//the subject is valid, adding it to the array of subjects
			int size = subjects.length;
			subjects = Arrays.copyOf(subjects, size + 1);
			subjects[size] = sub;
			repository = new Stock(sub);
			int stockID = dbHelper.addStock(conn, sub);
			repository.setStockID(stockID);
		}
		else {//the subject already exist
			//getting the subject that the user chose
			String sub = subjects[selector - 1];
			repository = new Stock(sub);
			dbHelper.getStockIdBySubject(conn, repository, sub);
			try {
				dbHelper.getStockBySubject(conn, repository);
			}
			catch(Exception e) {
				System.out.println("\u001B[31m " + "Could not read repository from file" + "\u001B[0m ");

			}
		}
		
		//System.out.println(repository.toString());
		
		//menu//
		final int EXIT = 0;
		final int Q1 = 1;
		final int Q2 = 2;
		final int Q3 = 3;
		final int Q6 = 6;
		final int Q7 = 7;
		final int Q8 = 8;


		int choice;
		do {
			//checking if the repository is empty
			if(repository.numOfQuestionsInStock() == 0) {
				choice = 3;
			}
			else {
				System.out.println("1 ---> to print data");
				System.out.println("2 ---> to add new answer to an existing question ");
				System.out.println("3 ---> to add a new question");
				System.out.println("6 ---> to delete an answer from a question");
				System.out.println("7 ---> to delete a question (and all its answers)");
				System.out.println("8 ---> to create a test");
				System.out.println("0 ---> to exit program and save changes. \n");
				System.out.println("Please enter your choice: ");


				choice = input.nextInt();

				//checking if the user entered a valid input.
				while (choice < EXIT || (choice > Q3 && choice < Q6) || choice > Q8) {
					System.out.println("\u001B[31m" + "You've entered an invalid input, please enter a new number.\n" + "\u001B[0m");
					choice = input.nextInt();
				}

			}

			switch (choice) {

			case Q1:

				System.out.println(repository.toString());
				System.out.println("\n");

				break;

			case Q2:
				
				//printing to the user the questions in the repository.
				System.out.println(repository.printQuestionOnly());
				System.out.println("To which question would you like to add an answer? ");
				int select = input.nextInt();

				//checking if the users selection is valid.
				while( !isQuestionSelectionValid(repository, select)) {
					System.out.println("\u001B[31m" + "You've entered an invalid input, please pick a different number.\n" + "\u001B[0m" );
					select = input.nextInt();
				}

				if (repository.getQuestionInIndex(select - 1) instanceof AmericanQuestion) {

					AmericanQuestion q = (AmericanQuestion)repository.getQuestionInIndex(select - 1);
					//checking if the answers array of the chosen question is full.
					if (q.isAnswersArrayFull()) { 
						System.out.println("\u001B[31m" + "You've reached the maximum amount of answers.\nYou can't add more answers." + "\u001B[0m" );
					}
					else {
						//getting an answer from the user to the question.
						enterAnswersToQuestions(dbHelper, conn, q);
						int newNumOfAns = q.numOfAnswersInArray() -2;
						dbHelper.updateNumOfAnswers(conn, q.id, newNumOfAns);
						System.out.println("The answer added successfully!! :) \n");
						System.out.println("\n");
					}
				}
				else {
					//it is an open question
					System.out.println("You can NOT add an answer to an open question.\nYou can only set a NEW correct answer instead of the existing one.");
					System.out.println("Enter the new answers text: ");
					input.nextLine();//cleaning buffer
					String text = input.nextLine();
					OpenQuestion q = (OpenQuestion)repository.getQuestionInIndex(select - 1);
					int ansID = q.getSchoolAnswerId();
					q.setSchoolAnswer(text);
					dbHelper.updateAnswerText(conn, q.id, ansID, text);
					q.getSchoolAnswer().setAnswerId(ansID);
					System.out.println("The answer changed successfully!! :) \n");
					System.out.println("\n");
				}

				break;

			case Q3 :

				//checking if repository is full.
				if(repository.isFull()) { 
					System.out.println("\u001B[31m" + "You've reached the maximum amount of questions.\n You can't add more questions." + "\u001B[0m" );
				}
				else { 
					//entering the new question from the user.
					addingQuestionFromUserToRepository(dbHelper, conn,repository);
				}

				System.out.println("The question added successfully!! :) \n");
				System.out.println("\n");
				break;

			case Q6 :

				//printing the user the questions in the repository.
				System.out.println(repository.printQuestionOnly());
				//getting the wanted question from the user.
				System.out.println("To which question would you like to delete an answer? ");
				int userIndex = input.nextInt();

				//checking if the user selection of question is valid.
				while( !isQuestionSelectionValid(repository, userIndex)) {
					System.out.println("\u001B[31m" + "You've entered an invalid input, please enter a new number.\n" + "\u001B[0m" );
					userIndex = input.nextInt();
				}

				if(repository.getQuestionInIndex(userIndex - 1) instanceof AmericanQuestion) {

					AmericanQuestion chosenQuestion = (AmericanQuestion)repository.getQuestionInIndex(userIndex - 1);

					//printing the user the answers in the question he chose.
					System.out.println(chosenQuestion.toStringAnswer());
					//checking if the user selection of answer is valid - Exception loop.
					boolean isAnswerToSDeleteValid = false;
					while( !isAnswerToSDeleteValid ) {
						try {
							//getting the answer to delete from user.
							System.out.println("Which answer whould you like to delete? (besides answers number 1 and 2) ");
							userIndex = input.nextInt();
							//deleting the answer.
							dbHelper.deleteAnswer(conn, chosenQuestion.id, chosenQuestion.getAnsInIndex(userIndex-1).getAnsId());
							chosenQuestion.deleteAnswer(userIndex); 
							int newNumOfAns = chosenQuestion.numOfAnswersInArray() -2;
							dbHelper.updateNumOfAnswers(conn, chosenQuestion.id, newNumOfAns);
							System.out.println("The answer deleted successfully!! :) \n");
							System.out.println("\n");
							isAnswerToSDeleteValid = true;
						}
						catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}
				else {
					System.out.println("You can not delete an open question's answer. Would you like to delete the question? y/n ");
					input.nextLine();
					String userInput = input.nextLine();
					while ( !userInput.equals("n") && !userInput.equals("N") && !userInput.equals("y") && !userInput.equals("Y")) {
						System.out.println("\u001B[31m" + "Please enter a valid input." + "\u001B[0m" );
						userInput = input.nextLine();
					}
					if (userInput.equals("y") || userInput.equals("Y")) {
						try {
							dbHelper.deleteQuestionById(conn, repository.getQuestionInIndex(userIndex-1).id);
							repository.deleteAQuestionFromStock(userIndex);
							System.out.println("The question deleted successfully!! :) \n");
							System.out.println("\n");
						}
						catch(Exception e) {
							System.out.println(e.getMessage());
						}
					}
				}

				break;

			case Q7 :
				//printing the user the questions in the repository.
				System.out.println(repository.printQuestionOnly());
				//getting the question to delete from the user.
				boolean isQuestionSelectedValid = false;
				while (!isQuestionSelectedValid ) {
					try {
						System.out.println("Which question would you like to delete? ");
						int userSelect = input.nextInt();
						//deleting the question.
						dbHelper.deleteQuestionById(conn, repository.getQuestionInIndex(userSelect-1).id);
						repository.deleteAQuestionFromStock(userSelect); 
						System.out.println("The question deleted successfully!! :) \n");
						System.out.println("\n");
						isQuestionSelectedValid = true;
					}
					catch(Exception e) {
						System.out.println(e.getMessage());
					}
				}
				break;
			case Q8 :
				//asking the user what kind of test he would like to create
				System.out.println("1 ---> to create a Menual Test. \n2 ---> to create an Automatic Test.");
				int userChoice = input.nextInt();
				while(userChoice != 1 && userChoice != 2) {
					System.out.println("\u001B[31m" + "You entered an invalid input. Try again." + "\u001B[0m" );
					userChoice = input.nextInt();
				}
				//getting the current date and time for the test file name.
				LocalDateTime date = LocalDateTime.now();
				DateTimeFormatter formattedDate = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm");
				String fileName = date.format(formattedDate);
				boolean isTestValid = false;
				while (!isTestValid) {
					try {
						System.out.println("How many questions would you like your test to have? ");
						int numOfQuestions = input.nextInt();
						//it is a Manual test
						if(userChoice == 1) {
							//checking that the user didn't chose a number that is bigger than the number of question exist.
							while(numOfQuestions > repository.numOfQuestionsInStock()) {
								System.out.println("\u001B[31m" + "there are only " + repository.numOfQuestionsInStock() + " questions in repository. \nPlease enter a valid input." + "\u001B[0m" );
								numOfQuestions = input.nextInt();
							}
							MenualTest test = new MenualTest(numOfQuestions);
							test.createExam(dbHelper,conn,repository);
							//writing the test to file.
							test.WriteTestToFile("exam_" + fileName + ".txt");
							//writing the solution to file.
							test.WriteTestWithAnswersToFile("solution_"+fileName + ".txt");
						}
						//it is an Automatic test
						else {
							//checking that the user didn't chose a number that is bigger than the number of question that valid for Automatic Test.
							while(numOfQuestions > repository.howManyValidQuestions()) {
								System.out.println("\u001B[31m" + "there are only " + repository.howManyValidQuestions() + " questions Valid for Automatic Test. \nPlease enter a valid input." + "\u001B[0m" );
								numOfQuestions = input.nextInt();
							}
							AutomaticTest test = new AutomaticTest(numOfQuestions);
							test.createExam(dbHelper, conn, repository);
							//writing the test to file.
							test.WriteTestToFile("exam_" + fileName + ".txt");
							//writing the solution to file.
							test.WriteTestWithAnswersToFile("solution_"+fileName + ".txt");
						}

						isTestValid = true;
					}
					catch(QuestionInTestException e){
						System.out.println(e.getMessage());
					}
				}

				System.out.println("The test created successfully!! :)");
				System.out.println("the test is saved in a file named: exam_" + fileName + ".txt");
				System.out.println("the solution is saved in a file named: solution_" + fileName + ".txt");
				System.out.println("\n");

				break;

			case EXIT:
				try {
					repository.WriteRepositoryToBinariFile();
					writeSubjectsToBinary(subjects);
				}
				catch(FileNotFoundException e) {
					System.out.println("\u001B[31m" + "This file does not exist." + "\u001B[0m" );
				}
				catch(Exception e) {
					System.out.println("\u001B[31m" + "General file Exception" + "\u001B[0m" );
				}
				break;

			default:

				System.out.println("\u001B[31m" + "Invalid input\n" + "\u001B[0m" );

				break;
			}

		}while (choice != EXIT);
		System.out.println("\u001B[32m" + "\n Have a nice day!" + "\u001B[0m" );	
		
		

	}
	
	
	//methods that are in the main://


	//method that checks if the user selection of question is valid.
	public static boolean isQuestionSelectionValid(Stock repository, int select) {
		if (select < 1 || select > repository.numOfQuestionsInStock() ) {
			return false;
		}
		else
			return true;
	}


	//method that get answers from the USER,  and adds it to a certain question.
	public static void enterAnswersToQuestions(DataBaseHelper dbHelper, Connection conn, AmericanQuestion question) {
		int id = -1;
		//getting the answer's text from the user.
		System.out.println("Please enter the answer's text: ");
		input.nextLine();
		String text = input.nextLine();
		//getting the answer's truth value and adding it to the question. 
		System.out.println("If this is a right answer enter T if it is a wrong answer enter F: ");
		String isCorrect = input.next();
		while( !isCorrect.equals("T") && !isCorrect.equals("t") && !isCorrect.equals("F") && !isCorrect.equals("f")) {
			System.out.println("\u001B[31m" + "Invalid Input, enter a new value." + "\u001B[0m" );
			isCorrect = input.next();
		}
		if(isCorrect.equals("T") || isCorrect.equals("t")) {
			id = dbHelper.addAnswerToQuestion(conn, question.id, text, true);
			question.addAnswer(text, true, id);
		}
		else {
			id = dbHelper.addAnswerToQuestion(conn, question.id, text, false);
			question.addAnswer(text, false, id);
		}

	}



	//method that adds a question from the USER to repository. 
	public static void addingQuestionFromUserToRepository(DataBaseHelper dbHelper, Connection conn,Stock repository) {
		//asking the user what kind of question he would like to add:
		System.out.println("To add an Americam question press 1. \nTo add an open question press 2. \nYour choice: ");
		int select = input.nextInt();
		//checking if the user selection is valid:
		while (select != 1 && select != 2) {
			System.out.println("\u001B[31m" + "Invalid input, Please enter a different number." + "\u001B[0m");
			select = input.nextInt();
		}
		//getting the question text from the user.
		System.out.println("Please enter the question's text: ");
		input.nextLine();
		String text = input.nextLine();
		//getting the difficulty from the user, and checking it's valid:
		boolean isLevelValid = false;
		while (!isLevelValid) {
			System.out.println("Please select the question difficulty level:\n1 ---> easy. \n2 ---> medium. \n3 ---> hard.");
			int level = input.nextInt();
			//getting the difficulty String
			String diff = dbHelper.getDifficultyString(level);
			try {
				//try and catch for the possible exception from the user's level input
				if (select == 1 ) { //it is an American question.
					AmericanQuestion newQuestion = new AmericanQuestion(text, level);//creating an AmericanQuestion object with the text and the level the user entered.
					int index = repository.numOfQuestionsInStock(); //getting the index of the last object in repository that is not Null.
					//adding the new question to the repository.
					repository.setQuestionInStock(newQuestion, index);
					//getting the number of answers the user would like to add:
					System.out.println("How many answers would you like to enter?");
					int numOfAns=input.nextInt();
					//checking if the user entered a valid number.
					while(numOfAns < 1 || numOfAns > 8) {
						System.out.println("\u001B[31m" + "You can add 1 to 10 answers. (you already have 2 defaultive answers).\n Please enter a different number.\n " + "\u001B[0m" );
						numOfAns = input.nextInt();
					}
					//adding the question to the data base
					int quesId = dbHelper.addQuestionToTable(conn, text, diff, "American", numOfAns);
					newQuestion.setQuestionID(quesId);
					//adding the answers.
					for (int i=0; i < numOfAns; i++) {
						System.out.println("Answer number " + (i+1));
						enterAnswersToQuestions(dbHelper, conn, newQuestion);
					}
					//adding the new question to the stockQuestion table in the data base
					dbHelper.addQuestionToStock(conn, repository.id, newQuestion.id);
				}
				else {// it is an open question:
					OpenQuestion newQuestion = new OpenQuestion(text, "", level);
					System.out.println("Please enter the school answer text: ");
					input.nextLine();
					String answertext  = input.nextLine();
					//adding the question to the data base
					int quesId = dbHelper.addQuestionToTable(conn, text, diff, "Open", 1);
					newQuestion.setQuestionID(quesId);
					int ansId = dbHelper.addAnswerToQuestion(conn, newQuestion.id, answertext, true);
					newQuestion.setSchoolAnswer(answertext);	
					newQuestion.getSchoolAnswer().setAnswerId(ansId);
					int index = repository.numOfQuestionsInStock(); //getting the index of the last object in repository that is not Null.
					//adding the new question to the repository.
					repository.setQuestionInStock(newQuestion, index);
					//adding the new question to the stockQuestion table in the data base
					dbHelper.addQuestionToStock(conn, repository.id, newQuestion.id);
				}
				isLevelValid = true;
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}


	}

	//method that reads the subjects that the system already have from BINARI file and return it in array
	public static String[] getSubjectsFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream readFile = new ObjectInputStream(new FileInputStream(fileName));
		String[] subjects = (String[])readFile.readObject();
		readFile.close();
		return subjects;
	}

	//method that returns the Stock of a certain subject
	public static Stock getStockOfSubjects(String sub) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream readFile = new ObjectInputStream(new FileInputStream(sub + ".dat"));
		Stock rep = (Stock)readFile.readObject();
		readFile.close();
		return rep;

	}

	//method that prints the subjects array TO USER
	public static void PrintSubjects(String[] sub) {
		System.out.println("The subjects the program already have: ");
		for (int i = 0; i < sub.length; i++) {
			System.out.println((i+1) + "---->  " + sub[i]);
		}
	}

	//method that writes subjects array to Binary file
	public static void writeSubjectsToBinary(String[] sub) throws FileNotFoundException, IOException {
		ObjectOutputStream writeFile = new ObjectOutputStream(new FileOutputStream("subjects.dat"));
		writeFile.writeObject(sub);
		writeFile.close();
	}
	
	//method that is checking is a certain subject already exists
	public static boolean isSubAlreadyInProgram(String sub, String[] subArray) {
		for(int i = 0; i < subArray.length; i++) {
			if (sub.equals(subArray[i])) {
				return true;
			}
		}
		return false;
	}
	
//	String[] subi = {"Hard-coded questions"};
//	ObjectOutputStream writeFile = new ObjectOutputStream(new FileOutputStream("subjects.dat"));
//	writeFile.writeObject(subi);
//	writeFile.close();
//	Stock repo = new Stock("Hard-coded questions");
//	repo.setStockWith5Questions();
//	repo.WriteRepositoryToBinariFile();
}


