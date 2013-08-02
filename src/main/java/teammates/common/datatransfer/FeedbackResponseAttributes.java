package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.FeedbackResponse;

import com.google.appengine.api.datastore.Text;

public class FeedbackResponseAttributes extends EntityAttributes {
	private String feedbackResponseId = null;
	public String feedbackSessionName;
	public String courseId;
	public String feedbackQuestionId;
	public FeedbackQuestionType feedbackQuestionType;
	public String giverEmail;
	public String recipient;
	public Text answer;
	
	public FeedbackResponseAttributes() {
		
	}
	
	public FeedbackResponseAttributes(String feedbackSessionName,
			String courseId, String feedbackQuestionId,
			FeedbackQuestionType feedbackQuestionType, String giverEmail,
			String recipient, Text answer) {
		this.feedbackSessionName = Sanitizer.sanitizeTitle(feedbackSessionName);
		this.courseId = Sanitizer.sanitizeTitle(courseId);
		this.feedbackQuestionId = feedbackQuestionId;
		this.feedbackQuestionType = feedbackQuestionType;
		this.giverEmail = Sanitizer.sanitizeEmail(giverEmail);
		this.recipient = recipient;
		this.answer = Sanitizer.sanitizeTextField(answer);
	}

	public FeedbackResponseAttributes(FeedbackResponse fr) {
		this.feedbackResponseId = fr.getId();
		this.feedbackSessionName = fr.getFeedbackSessionName();
		this.courseId = fr.getCourseId();
		this.feedbackQuestionId = fr.getFeedbackQuestionId();
		this.feedbackQuestionType = fr.getFeedbackQuestionType();
		this.giverEmail = fr.getGiverEmail();
		this.recipient = fr.getRecipient();
		this.answer = fr.getAnswer();
	}
	
	public FeedbackResponseAttributes(FeedbackResponseAttributes copy) {
		this.feedbackResponseId = copy.getId();
		this.feedbackSessionName = copy.feedbackSessionName;
		this.courseId = copy.courseId;
		this.feedbackQuestionId = copy.feedbackQuestionId;
		this.feedbackQuestionType = copy.feedbackQuestionType;
		this.giverEmail = copy.giverEmail;
		this.recipient = copy.recipient;
		this.answer = copy.answer;
	}

	public String getId() {
		return feedbackResponseId;
	}
	
	public void setId(String feedbackResponseId) {
		this.feedbackResponseId = feedbackResponseId;
	}
	
	@Override
	public List<String> getInvalidityInfo() {
		
		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getInvalidityInfo(FieldType.FEEDBACK_SESSION_NAME, feedbackSessionName);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EMAIL, "answerer's email", giverEmail);
		if(!error.isEmpty()) { errors.add(error); }
		
		return errors;
	}
	
	@Override
	public boolean isValid() {
		return getInvalidityInfo().isEmpty();
	}
	
	@Override
	public Object toEntity() {
		return new FeedbackResponse(feedbackSessionName, courseId,
				feedbackQuestionId, feedbackQuestionType,
				giverEmail, recipient, answer);
	}
	
	@Override
	public String getIdentificationString() {
		return feedbackQuestionId + "/" + giverEmail + ":" + recipient;
	}
	
	@Override
	public String getEntityTypeAsString() {
		return "Feedback Response";
	}

	@Override
	public String toString() {
		return "FeedbackResponseAttributes [feedbackSessionName="
				+ feedbackSessionName + ", courseId=" + courseId
				+ ", feedbackQuestionId=" + feedbackQuestionId
				+ ", feedbackQuestionType=" + feedbackQuestionType
				+ ", giverEmail=" + giverEmail + ", recipient=" + recipient
				+ ", answer=" + answer + "]";
	}
}
