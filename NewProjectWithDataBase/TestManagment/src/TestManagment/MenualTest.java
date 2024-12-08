package TestManagment;

import java.sql.Connection;
import java.util.Scanner;

public class MenualTest extends Test {

	public static Scanner input = new Scanner (System.in);

	//constructor//
	public MenualTest(int numOfQuestions) throws QuestionInTestException {
		super(numOfQuestions);
	}

	@Override
	public void createExam(DataBaseHelper dbHelper, Connection conn, Stock repository) {
		//adding the new test to the data
		int testId = dbHelper.addTestToTable(conn, "Manual", numOfQuestion, repository.getSubject());
		setTestId(testId);
		//loop that runs the same number of times as the number of question in the test.
		for (int i = 0; i < numOfQuestion; i++) {
			//printing the user the questions in the repository.
			System.out.println(repository.printQuestionOnly() + "\n");
			//getting the question to add from the user.
			System.out.println("Which question would you like to add? (there are " + (numOfQuestion - i) + " questions left to select)");
			int chosenQuestion = input.nextInt();
			//checking that the user entered a valid input.
			while (chosenQuestion < 1 || chosenQuestion > repository.numOfQuestionsInStock()) {
				System.out.println("\u001B[31m" + "Invalid input.\n Enter a new number." + "\u001B[0m");
				chosenQuestion = input.nextInt();
			}
			Question userQuestion = repository.getQuestionInIndex(chosenQuestion - 1);
			//checking if the chosen question isn't already in the test.
			while(isQuestionAlreadyInTest(userQuestion)) {
				System.out.println("\u001B[31m" + "You have alredy chosen this question. \n Please enter a different one." + "\u001B[0m");
				chosenQuestion = input.nextInt();
				userQuestion = repository.getQuestionInIndex(chosenQuestion - 1);
			}
			//printing to user the question he chose.
			System.out.println(" You chose :" + userQuestion.getQuestionText() + "\n ");
			if (userQuestion instanceof AmericanQuestion) {
				AmericanQuestion q = (AmericanQuestion)userQuestion;
				boolean isUserArrayValid = false;
				while( !isUserArrayValid ) {
					//try and catch for the number of answers in an American Question
					try {
						//getting the number of answers the user would like to have for the question he chose.
						System.out.println(q.toStringAnswer() + "\n How many answers whould you like your question to have? (Besides answers number 1 and 2 , they already considered part of your answers). ");
						int numOfAnswers = input.nextInt();
						//creating a NULL array of USER answers in the length of the users choice.
						q.setUsersAnswersArray(numOfAnswers);
						System.out.println("Please pick an answer :" );
						//adding the users choice of answers to the USER array.
						for(int j = 0; j < numOfAnswers;) {
							System.out.println("there are " + (numOfAnswers - j) + " answers left to pick.");
							int chosenAnswer = input.nextInt();
							//checking if user's choice of answer is valid:
							while(chosenAnswer > q.numOfAnswersInArray() || chosenAnswer < 1 ) {
								System.out.println("\u001B[31m" + "Invalid input. please enter a different number.\n" + "\u001B[0m");
								chosenAnswer = input.nextInt();
							}
							//checking if the answer is already in the array.
							while( q.isAnswerInUserArray(chosenAnswer - 1) ){
								System.out.println("\u001B[31m" + "This answer is already in the test.\n Please pick different one." + "\u001B[0m");
								chosenAnswer = input.nextInt();
							}
							//adding the answer to the USER answers array.
							if( q.AddAnswerToUserArray(chosenAnswer - 1)) {
								j++;
							}
							else {
								System.out.println("\u001B[31m" + "This answers already was chosen.\n Please pick a different one.\n" + "\u001B[0m");
							}

							isUserArrayValid = true;
						}
					}
					catch(AmericanQuestionAnswersException e ) {
						System.out.println(e.getMessage());
					}
				}
			}
			//adding the question to the Test
			setQuestionToTest(userQuestion);
			dbHelper.addToUserAnswersTable(conn, testId, userQuestion);
			System.out.println("Question number " + (i+1) + " added successfully!!! \n");
		}

	}

}
