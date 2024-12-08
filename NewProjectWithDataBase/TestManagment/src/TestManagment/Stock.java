package TestManagment;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Stock implements Serializable{
	
	//member//
	private Question[] stock;
	private String subject;
	protected int id;
	
	//final//
	private final int MAX_NUM_OF_QUESTIONS = 100;
	
	//constructor//
	public Stock (String sub) {
		stock = new Question[MAX_NUM_OF_QUESTIONS];
		setSubject(sub);
		id = 0;
	}
	
	//setters//
	public void setQuestionInStock(Question q, int index) {
		stock[index] = q;
	}
	
	public void setSubject(String sub) {
		subject = sub;
	}
	
	public void setStockID(int num)
	{
		id = num;
	}
	// A method that sets the stock with 5 Hard-Coded questions:
	/*
	 * public void setStockWith5Questions() throws Exception{
	 * 
	 * //question number 1: AmericanQuestion q1 = new AmericanQuestion(" 10 + 5 = ",
	 * 1); q1.addAnswer("12", false); q1.addAnswer("6", false); q1.addAnswer("15",
	 * true); q1.addAnswer("8", false); stock[0] = q1;
	 * 
	 * //question number 2: AmericanQuestion q2 = new
	 * AmericanQuestion(" 45 - 17 = ", 1); q2.addAnswer("28", true);
	 * q2.addAnswer("0", false); q2.addAnswer("3", false); q2.addAnswer("-46",
	 * false); stock[1] = q2;
	 * 
	 * 
	 * //question number 3: OpenQuestion q3 = new
	 * OpenQuestion("what is the name of Isreal's prime minister? ",
	 * " Benjamin Nethanyahu ", 2); stock[2] = q3;
	 * 
	 * 
	 * //question number 4: AmericanQuestion q4 = new AmericanQuestion(" 2 ^ 4 =",
	 * 3); q4.addAnswer("67", false); q4.addAnswer("16", true); q4.addAnswer("-7",
	 * false); stock[3] = q4;
	 * 
	 * 
	 * //question number 5: OpenQuestion q5 = new
	 * OpenQuestion(" How many states are there in the United States? " ,
	 * " There are 50 states ", 2); stock[4] = q5;
	 * 
	 * }
	 */
	
	//getters//
	public Question[] getStock() {
		return stock;
	}
	
	public Question getQuestionInIndex(int index) {
		return stock[index];
	}
	
	public String getSubject() {
		return subject;
	}
	
	
	//toString()
	public String toString() {
		StringBuffer st = new StringBuffer("");
		for (int i = 0; i < numOfQuestionsInStock() ; i++) {
			st.append("\n");
			st.append("Question " + (i+1) + ": \n");
			st.append(stock[i].toString());
			st.append("\n");
			st.append(stock[i].toStringAnswer());
			st.append(System.lineSeparator());
		}
		return st.toString();
	}
	
	//toString() for questions array ONLY (without answers) .
	public String printQuestionOnly() {
		StringBuffer st = new StringBuffer("");
		for (int i = 0; i < stock.length; i++) {
			if(stock[i] != null) {
				st.append("  " + (i+1) + ") ");
				st.append(stock[i].toString());
				st.append(System.lineSeparator());

			}
			else
				break;
		}
		return st.toString();
	}
	
	//other methods

	
	//method that returns the number of questions in the stock.
	public int numOfQuestionsInStock() {
		int count = 0;
		for(int i = 0; i < stock.length; i++) {
			if(stock[i] != null) {
				count++;
			}
			else
				break;
		}
		return count;
	}
	
	//method that returns whether the stock is full.
	public boolean isFull() {
		return numOfQuestionsInStock() == MAX_NUM_OF_QUESTIONS;
	}
	
	
	//method that delete a certain question from stock.
	public void deleteAQuestionFromStock(int index) throws Exception {
		//checking if the index is valid
		if ( index > numOfQuestionsInStock() || index < 1) {
			throw new Exception("Invalid Input. Please enter a valid number.");
		}
		else {
		//get the index of the last object in the array.
		int lastIndex = numOfQuestionsInStock() - 1; 
		//replace the last object with the object to delete.
		stock[index - 1] = stock[lastIndex];
		stock[lastIndex] = null;
		}
	}
	
	//method that writes a Stock object to a BINARI file.
	public void WriteRepositoryToBinariFile() throws FileNotFoundException, IOException {
		ObjectOutputStream writeFile = new ObjectOutputStream(new FileOutputStream(subject + ".dat"));
		writeFile.writeObject(this);
		writeFile.close();
	}
	
	//method that checks how many questions are valid for Automatic Test.
	public int howManyValidQuestions() {
		int validNumOfQuestions = 0;
		for(int i = 0; i < numOfQuestionsInStock(); i++) {
			//checking if it is an American Question
			if (stock[i] instanceof AmericanQuestion) { 
				AmericanQuestion americanQues = (AmericanQuestion)stock[i];
				//check how many answers there are
				if (americanQues.numOfAnswersInArray() >= 5 && americanQues.howManyFalseAnswers() >= 4) {
					validNumOfQuestions ++;
				}
			}
			//it is an Open Question - it is valid for test
			else 
				validNumOfQuestions++;
		}
		return validNumOfQuestions;
	}
	

}
