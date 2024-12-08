package TestManagment;

public class AmericanQuestion extends Question {
	
	//finals//
	private final int MAX_NUM_OF_ANSWERS = 10;
	private final int MIN_NUM_OF_ANSWERS = 4;
	private final Answers DEAFAULT_ANSWER_1 = new Answers("More than one answer is correct", false);
	private final Answers DEAFAULT_ANSWER_2 = new Answers("None of the answers are correct", false);
	
	
	//members//
	private Answers[] answers;
	private Answers[] usersAnswers;//the array of the answers that the user will pick to have in it's created test.
	
	//constructors//
	public AmericanQuestion(String text, int level) throws Exception {
		super(text, level);
		setAnswersArray();
	}
	
	//setters//	
	public void setAnswersArray() {
		answers = new Answers[MAX_NUM_OF_ANSWERS];
		answers[0] = DEAFAULT_ANSWER_1;
		answers[1] = DEAFAULT_ANSWER_2;
	}
	
	public void setUsersAnswersArray(int arrayLength) throws AmericanQuestionAnswersException {
		//this setter gets the number of answers that the user will decide to have and will add to it the default answers.
		if((arrayLength + 2) < MIN_NUM_OF_ANSWERS || (arrayLength + 2) > numOfAnswersInArray()) {
			throw new AmericanQuestionAnswersException("\u001B[31m" + "You need to pick 2 answers minimum and " + (numOfAnswersInArray()-2) + " answers maximum.\n" + "\u001B[0m");
		}
		else {
			//user's input is valid, creating the USER array.
			usersAnswers = new Answers[arrayLength + 2];
			usersAnswers[0] = DEAFAULT_ANSWER_1;
			usersAnswers[1] = DEAFAULT_ANSWER_2;
		}
	}
	
	//getters//
	public Answers[] getAnswersArray() {
		return answers;
	}
	
	public Answers[] getUsersArray() {
		return usersAnswers;
	}
	
	public int getAnsId(Answers ans)
	{
		return ans.getAnsId();
	}
	
	public Answers getAnsInIndex(int index)
	{
		return answers[index];
	}
	
	//toString() for answers array ONLY.
	public String toStringAnswer() {
		StringBuffer st = new StringBuffer("");
		for (int i = 0; i < answers.length; i++) {
			if(answers[i] != null ) {
				st.append((i+1) + ").  " +answers[i].toString());
				st.append("\n");
			}
			else
				break;
		}
		return st.toString();
	}
	
	//toString() for User Array ONLY (without the values of the answers in the array).
	public String toStringUserArry() {
		StringBuffer st = new StringBuffer("");
		for(int i = 0; i < numOfAnswersInUserArray(); i++) {
			st.append((i+1) + ").  " + usersAnswers[i].getAnswerText());
			st.append("\n");
		}
		return st.toString();
	}
	
	//toString() for User array ONLY, WITH THE VALUES OF THE ANSWERS. 
	public String toStringUserArryWithTruthValue() {
		StringBuffer st = new StringBuffer("");
		for(int i = 0; i < usersAnswers.length; i++) {
			st.append(usersAnswers[i].toString());
			st.append("\n");
		}
		return st.toString();
	}
	
	
	
	
	
	//other methods:

	
	//method that adds an answer to ANSWERS ARRAY.
	public void addAnswer(String tempAnswer, boolean isCorrect, int id) {
		for (int i = 0; i < answers.length ; i++) {
			if (answers[i] == null) {
				answers[i] = new Answers(tempAnswer, isCorrect);
				answers[i].setAnswerId(id);
				break;
			}
		}
	}
	
	//method that adds an answer to USER ARRAY.
	public boolean AddAnswerToUserArray(int index) {
		Answers chosenAnswer = answers[index];
		for(int i = 0; i < numOfAnswersInUserArray(); i++) {
			if(usersAnswers[i].getAnswerText().equals(chosenAnswer.getAnswerText())) {
				return false;
			}
		}
		usersAnswers[numOfAnswersInUserArray()] = chosenAnswer;
		return true;
	}
	
	//method that returns the number of answers in the ANSWERS array.
	public int numOfAnswersInArray() {
		int count = 0;
		for(int i = 0; i < MAX_NUM_OF_ANSWERS; i++) {
			if (answers[i] != null) {
				count++;
			}
			else
				break;
		}
		return count;
	}
	
	//method that returns the number of answers in USER array.
	public int numOfAnswersInUserArray() {
		int count = 0;
		for(int i = 0; i < usersAnswers.length; i++) {
			if (usersAnswers[i] != null) {
				count++;
			}
			else
				break;
		}
		return count;
	}
	
	
	//method that returns whether the ANSWERS ARRAY is full.
	public boolean isAnswersArrayFull() {
		return numOfAnswersInArray() == MAX_NUM_OF_ANSWERS;
	}

	//method that delete an answer from the ANSWERS ARRAY.
	public void deleteAnswer(int index) throws Exception {
		//checking if the index is valid
		if (index == 1 || index == 2 || index < 1 || index >= (numOfAnswersInArray() + 1) ) {
			throw new Exception("\u001B[31m" + "Invalid Input. Remaider - You can't delete answer number 1 and 2." + "\u001B[0m");
		}
		else {
			int lastIndex = numOfAnswersInArray() - 1; //get the index of the last object in the array.
			answers[index - 1] = answers[lastIndex];// replace the last object with the object to delete.
			answers[lastIndex] = null;
		}
	}
	
	
	
	//method that checks if a certain answer is already in USER array.
	public boolean isAnswerInUserArray(int index) {
		Answers answer = answers[index];
		for (int i = 0; i < numOfAnswersInUserArray(); i++) {
			if (usersAnswers[i].getAnswerText() == answer.getAnswerText() && usersAnswers[i].getIsCorrect() == answer.getIsCorrect()) {
				return true;
			}
		}
		return false;
	}
	
	
	
	//method that checks if the USERS array is valid.
	public void makeUserArrayValid() {
		int counter = 0;
		//checking how many answers with the value true there are in User array.
		for(int i = 0 ; i < numOfAnswersInUserArray(); i++) {
			if(usersAnswers[i].getIsCorrect()) {
				counter++;
			}
		}
		//setting the truth values so the array will have only 1 answer that is true.
		if(counter > 1) {
			usersAnswers[0].setAnswerTruthValue(true);
			for(int i = 2; i < numOfAnswersInUserArray(); i++) {
				usersAnswers[i].setAnswerTruthValue(false);	
			}
		}
		else if(counter == 0) {
			usersAnswers[1].setAnswerTruthValue(true);
		}
	}
	
	//method that checks how many answers from the Answers array are false
	public int howManyFalseAnswers() {
		int counter = 0;
		for (int i=0;i< numOfAnswersInArray(); i++) {
			if(!answers[i].getIsCorrect()) {
				counter++;
			}
		}
		return counter;
	}
	
}
