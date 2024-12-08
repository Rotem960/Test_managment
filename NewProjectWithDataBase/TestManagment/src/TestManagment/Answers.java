package TestManagment;

import java.io.Serializable;

public class Answers implements Serializable {

	// members//
	private String answerText;
	private boolean isCorrect;
	private int id;

	// constructor//
	public Answers(String text, boolean isCorrect) {
		setAnswerText(text);
		setAnswerTruthValue(isCorrect);
		id = 0;
	}

	// setter//
	public void setAnswerText(String text) {
		answerText = text;
	}

	public void setAnswerTruthValue(Boolean truthValue) {
		isCorrect = truthValue;
	}

	public void setAnswerId(int num) { 
		id = num; 
	}

	// getters//
	public String getAnswerText() {
		return answerText;
	}

	public boolean getIsCorrect() {
		return isCorrect;
	}
	
	public int getAnsId()
	{
		return id;
	}

	// toString()
	public String toString() {
		return "Answer: " + answerText + ",    ( " + isCorrect + " )";
	}

}
