package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.storage.entity.FeedbackQuestion.QuestionType;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class FeedbackResponse {
	
	// Format is feedbackQuestionId%giverEmail%receiver
	// i.e. if response is feedback for team: qnId%giver@gmail.com%Team1
	// 		if response is feedback for person: qnId%giver@gmail.com%reciever@email.com
	@PrimaryKey
	@Persistent
	private String feedbackResponseId;
	
	@Persistent
	private String feedbackSessionName;
	
	@Persistent
	private String courseId;
	
	@Persistent
	private String feedbackQuestionId;
	
	@Persistent
	private QuestionType feedbackQuestionType;
	
	@Persistent
	private String giverEmail;
		
	@Persistent
	private String receiver;
	
	@Persistent
	private Text answer;

	public String getId() {
		return feedbackResponseId;
	}

	/* Auto-generated. Do not set this.
	public void setFeedbackResponseId(String feedbackResponseId) {
		this.feedbackResponseId = feedbackResponseId;
	}*/

	public String getFeedbackSessionName() {
		return feedbackSessionName;
	}

	public void setFeedbackSessionName(String feedbackSessionName) {
		this.feedbackSessionName = feedbackSessionName;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getFeedbackQuestionId() {
		return feedbackQuestionId;
	}

	public void setFeedbackQuestionId(String feedbackQuestionId) {
		this.feedbackQuestionId = feedbackQuestionId;
	}

	public QuestionType getFeedbackQuestionType() {
		return feedbackQuestionType;
	}

	public void setFeedbackQuestionType(QuestionType feedbackQuestionType) {
		this.feedbackQuestionType = feedbackQuestionType;
	}

	public String getGiverEmail() {
		return giverEmail;
	}

	public void setGiverEmail(String giverEmail) {
		this.giverEmail = giverEmail;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Text getAnswer() {
		return answer;
	}

	public void setAnswer(Text answer) {
		this.answer = answer;
	}

	public FeedbackResponse(String feedbackSessionName, String courseId,
			String feedbackQuestionId, QuestionType feedbackQuestionType,
			String giverEmail, String receiver,	Text answer) {
		this.feedbackSessionName = feedbackSessionName;
		this.courseId = courseId;
		this.feedbackQuestionId = feedbackQuestionId;
		this.feedbackQuestionType = feedbackQuestionType;
		this.giverEmail = giverEmail;
		this.receiver = receiver;
		this.answer = answer;
				
		this.feedbackResponseId = feedbackQuestionId + "%" + giverEmail + "%" + receiver;								
	}
}
