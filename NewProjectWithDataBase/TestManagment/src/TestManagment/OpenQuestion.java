package TestManagment;


public class OpenQuestion extends Question{

	//members//
	private Answers schoolAnswer;
	
	//constructor//
	public OpenQuestion(String text, String answer, int level) throws Exception {
		super(text, level);
		setSchoolAnswer(answer);
	}
	
	
	//setters//
	public void setSchoolAnswer(String answerText) {
		schoolAnswer = new Answers(answerText, true);
	}
	
	
	//getters//
	public Answers getSchoolAnswer() {
		return schoolAnswer;
	}
	
	public int getSchoolAnswerId() {
		return schoolAnswer.getAnsId();
	}
	
	//toString() for the answer text.
	public String toStringAnswer() {
		return "The correct answer: " + schoolAnswer.getAnswerText();
	}

}
