package TestManagment;

import java.sql.Connection;
import java.util.Random;

public class AutomaticTest extends Test {

	//constructor//
	public AutomaticTest(int numOfQuestions) throws QuestionInTestException {
		super(numOfQuestions);
	}
	
	public void getRandomAnswers(AmericanQuestion question) {
		int trueCounter = 0;
		Answers[] allAnswer = question.getAnswersArray();
		try { //this exception will never be because the number 2 is always a valid input for this method
		//setting the USER array:
		question.setUsersAnswersArray(2);
		}
		catch(AmericanQuestionAnswersException e){
			System.out.println(e.getMessage());
		}
		//deleting default answer no. 1 - "more than one answer is correct" from USER array
		Answers[] userAnswers = question.getUsersArray();
		userAnswers[0] = userAnswers[1];
		//generate a random object
		Random rand = new Random();
		int randNum;
		for (int i = 1; i < 4; i++) {
			randNum = rand.nextInt(question.numOfAnswersInArray());
			//generating a random answer
			Answers ans = allAnswer[randNum];
			//checking if the answer is already in User array OR if it is the Default answer "more than one answer is correct" OR if there is more than one answer correct
			if (question.isAnswerInUserArray(randNum) || randNum == 0 || (trueCounter == 1 && ans.getIsCorrect()) ) {
				i--;
			}
			else {
				//checking if it is the first true answer that came up 
				if(ans.getIsCorrect()) {
					trueCounter++;
				}
				userAnswers[i] = ans;
			}
		}
	}
	
	
	@Override
	public void createExam(DataBaseHelper dbHelper, Connection conn, Stock repository) {
		//adding the new test to the data
		int testId = dbHelper.addTestToTable(conn, "Automatic", numOfQuestion, repository.getSubject());
		setTestId(testId);
		int randNum;
		//generate a random object
		Random rand = new Random();
		for(int i = 0; i < numOfQuestion; i++) {
			randNum = rand.nextInt(repository.numOfQuestionsInStock());
			//generating a random question
			Question q = repository.getQuestionInIndex(randNum);
			//checking if the question was already chosen
			if(isQuestionAlreadyInTest(q)) {
				i--;
			}
			else {
				//checking if it is an American question
				if (q instanceof AmericanQuestion) {
					AmericanQuestion ques = (AmericanQuestion)q;
					//checking there are enough answers in ANSWERS array (minimum 4 - not included DEFAULT_ANSWER_1}
					if (ques.numOfAnswersInArray() < 5) {
						i--;
					}
					else { //there are enough answers, the question is valid
					getRandomAnswers(ques);
					ques.makeUserArrayValid();
					testQuestions[i] = q;
					dbHelper.addToUserAnswersTable(conn, testId, q);
					}
				}
				else {//it is an open question
				testQuestions[i] = q;
				dbHelper.addToUserAnswersTable(conn, testId, q);
				}
			}
		}
		
	}

}
