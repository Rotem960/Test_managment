package TestManagment;

import java.io.Serializable;

public abstract class Question implements Serializable{

	//Enum//
	public enum difficulty {Easy, Meduim, Hard};
	
	//static members//
	protected static int questionOrderNumber = 0;
	
	//members//
	protected String questionText;
	protected int numQuestion;
	protected difficulty level;
	protected int id;
	

	
	//constructor//
	public Question(String text, int level) throws Exception {
		setQuestionText(text);
		questionOrderNumber++;
		numQuestion = questionOrderNumber;
		setDifficulty(level);
		id = 0;
	}
	
	//setter//
	public void setQuestionText(String text) {
		questionText = text;
	}
	
	public void setDifficulty (int difficult) throws Exception {
		if ( difficult != 1 && difficult != 2 && difficult != 3) {
			throw new Exception("\u001B[31m" +"Invalid difficulty level. Please enter a valid number, 1/2/3 ." + "\u001B[0m");
		}
		else if (difficult == 1) {
			level = difficulty.Easy;
		}
		else if (difficult == 2) {
			level = difficulty.Meduim;
		}
		else {
			level = difficulty.Hard;
		}
	}
	
	public void setQuestionID(int num)
	{
		id = num;
	}
	
	//getters//
	public int getQuestionNumber() {
		return numQuestion;
	}
	
	public String getQuestionText() {
		return questionText;
	}
	
	//toString()// 
	public String toString() {
		return "The question: " + questionText;
	}	
	
	//method that returns the data of the object: the question and answer\s text, the question number and the question's difficulty level.
	public String showData() {
		StringBuffer st = new StringBuffer("");
		st.append(toString() + "\n");
		st.append(toStringAnswer()+ "\n");
		st.append("question id: " + numQuestion + "\n");
		st.append("question level: " + level + "\n");
		
		return st.toString();

	}
	
	//abstract method//	
	public abstract String toStringAnswer();

	
}

