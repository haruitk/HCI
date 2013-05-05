public class DBQuestion{
	private String taskId;
	private String qId;
	private String hitId;
	private String instructions;
	private String url;
	private String mcQuestion;
	private String answerA;
	private String answerB;
	private String answerC;
	private String answerD;
	private String correctAnswer;
	private String question2;
	private String correctAnswer2;
	private String mcResult;
	private String result2;
	
	DBQuestion(){}
	
	public String getQuestion2() {
		return question2;
	}
	public void setQuestion2(String question2) {
		this.question2 = question2;
	}
	
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getHitId() {
		return hitId;
	}

	public void setHitId(String hitId) {
		this.hitId = hitId;
	}

	public String getqId() {
		return qId;
	}

	public void setqId(String qId) {
		this.qId = qId;
	}

	public String getMcQuestion() {
		return mcQuestion;
	}

	public void setMcQuestion(String mcQuestion) {
		this.mcQuestion = mcQuestion;
	}

	public String getAnswerA() {
		return answerA;
	}

	public void setAnswerA(String answerA) {
		this.answerA = answerA;
	}

	public String getAnswerB() {
		return answerB;
	}

	public void setAnswerB(String answerB) {
		this.answerB = answerB;
	}

	public String getAnswerC() {
		return answerC;
	}

	public void setAnswerC(String answerC) {
		this.answerC = answerC;
	}

	public String getAnswerD() {
		return answerD;
	}

	public void setAnswerD(String answerD) {
		this.answerD = answerD;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}

	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getCorrectAnswer2() {
		return correctAnswer2;
	}

	public void setCorrectAnswer2(String correctAnswer2) {
		this.correctAnswer2 = correctAnswer2;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMcResult() {
		return mcResult;
	}

	public void setMcResult(String mcResult) {
		this.mcResult = mcResult;
	}

	public String getResult2() {
		return result2;
	}

	public void setResult2(String result2) {
		this.result2 = result2;
	}
}