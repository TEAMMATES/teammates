package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

public class FeedbackMsqResponseDetails extends FeedbackAbstractResponseDetails {
	public List<String> answers;
	
	public FeedbackMsqResponseDetails() {
		super(FeedbackQuestionType.MSQ);
		this.answers = new ArrayList<String>();
	}
	
	public FeedbackMsqResponseDetails(List<String> answers) {
		super(FeedbackQuestionType.MSQ);
		this.answers = answers;
	}

	@Override
	public String getAnswerString() {
		StringBuilder answerSb = new StringBuilder();
		
		for (String answer : answers) {
			answerSb.append(answer);
			answerSb.append(", ");
		}
		
		//Remove extra comma and space at the end
		answerSb.setLength(Math.max(0, answerSb.length() - 2));
		
		return answerSb.toString();
	}

	public boolean contains(String candidateAnswer) {
		return answers.contains(candidateAnswer);
	}

}
