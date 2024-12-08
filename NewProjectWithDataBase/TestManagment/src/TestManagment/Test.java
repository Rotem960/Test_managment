package TestManagment;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;

public abstract class Test implements Examable{
	
	//finals//
	protected final int MAX_NUM_OF_QUESTIONS = 10;
	
	//members//
	protected int numOfQuestion;
	protected Question[] testQuestions;
	protected int id;
	
	//constructor//
	public Test(int numOfQuestions) throws QuestionInTestException {
		setNumOfQuestions(numOfQuestions);
		id = 0;
	}
	
	
	//setters//
	public void setNumOfQuestions(int numOfQuestions) throws QuestionInTestException {
		if(numOfQuestions < 1 || numOfQuestions > MAX_NUM_OF_QUESTIONS) {
			throw new QuestionInTestException( "\u001B[31m" + "Invalid input. You can create a test up to 10 questions.\n" + "\u001B[0m");
		}
		else {
			this.numOfQuestion = numOfQuestions;
			testQuestions = new Question[numOfQuestions]; //User's input is valid, creating the test question array
		}

	}
	
	public void setQuestionToTest(Question question) {
		if (question instanceof AmericanQuestion) {
			AmericanQuestion q = (AmericanQuestion)question;
			q.makeUserArrayValid();
			testQuestions[questionsActaullyInTest()] = question;
		}
		else {
			//it is an open question
			testQuestions[questionsActaullyInTest()] = question;
		}
	}
	
	public void setTestId(int num)
	{
		id = num;
	}
	
	//getters//
	public int getNumOfQuestionsInTest() {
		return numOfQuestion;
	}
	
	public Question[] getTestQuestionArray() {
		return testQuestions;
	}
	
	
	//other methods//
	
	
	//method that returns the number of questions CURRENTLY in test
	public int questionsActaullyInTest() {
		int count = 0;
		for(int i = 0; i < testQuestions.length; i++) {
			if (testQuestions[i] != null) {
				count++;
			}
			else
				break;
		}
		return count;
	}
	
	
	
	//method that checks if a certain question is already in the test - compared by Strings.
	public boolean isQuestionAlreadyInTest(Question question) {
		if (numOfQuestion != 1 ) {
			for (int i = 0; i < numOfQuestion; i++) {
				if (testQuestions[i] != null) {
					if(testQuestions[i].getQuestionText().equals(question.getQuestionText())) {
						return true;
					}
				}
				else
					break;
			}
		}
		return false;
	}
	
	
	
	
	//method that writes a Test object to file (wuthOUT the truth values of the answers).
	public void WriteTestToFile(String fileName) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(fileName);
		for (int i = 0; i < numOfQuestion; i++) {
			pw.println(testQuestions[i].toString());
			pw.println();
			//checking if it is an American question.
			//if so, write to file the answers options.
			if (testQuestions[i] instanceof AmericanQuestion) {
				AmericanQuestion q = (AmericanQuestion)testQuestions[i];
				pw.println(q.toStringUserArry());
			}

		}
		pw.println();
		pw.close();
	}
	
	//method that writes a Test object to file (WITH the truth values of the answers).
	public void WriteTestWithAnswersToFile(String fileName) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(fileName);
		for (int i = 0; i < numOfQuestion; i++) {
			pw.println(testQuestions[i].toString());
			pw.println();
			if (testQuestions[i] instanceof AmericanQuestion) {
				AmericanQuestion q = (AmericanQuestion)testQuestions[i];
				pw.println(q.toStringUserArryWithTruthValue());
			}
			else {
				//it is an open question.
				OpenQuestion o = (OpenQuestion)testQuestions[i];
				pw.println(o.toStringAnswer());
			}
		}
		pw.println();
		pw.close();
	}


	
}
