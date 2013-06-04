package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.FeedbackParticipantType;
import teammates.common.FieldValidator;
import teammates.common.Sanitizer;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackQuestion.QuestionType;

public class FeedbackQuestionAttributes extends EntityAttributes {
	private String feedbackQuestionId = null;
	public String feedbackSessionName;
	public String courseId;
	public String creatorEmail;
	public Text questionText;
	public int questionNumber;
	public QuestionType questionType;
	public FeedbackParticipantType giverType;
	public FeedbackParticipantType recipientType;
	public int numberOfEntitiesToGiveFeedbackTo;
	public List<FeedbackParticipantType> showGiverNameTo;
	public List<FeedbackParticipantType> showRecipientNameTo;
	
	public FeedbackQuestionAttributes(){
		
	}
	
	public FeedbackQuestionAttributes(FeedbackQuestion fq) {
		this.feedbackQuestionId = fq.getId();
		this.feedbackSessionName = fq.getFeedbackSessionName();
		this.courseId = fq.getCourseId();
		this.creatorEmail = fq.getCreatorEmail();
		this.questionText = fq.getQuestionText();
		this.questionNumber = fq.getQuestionNumber();
		this.questionType = fq.getQuestionType();
		this.giverType = fq.getGiverType();
		this.recipientType = fq.getRecipientType();
		this.numberOfEntitiesToGiveFeedbackTo = fq.getNumberOfEntitiesToGiveFeedbackTo();
		this.showGiverNameTo = fq.getShowGiverNameTo();
		this.showRecipientNameTo = fq.getShowRecipientNameTo();
	}
	
	public FeedbackQuestionAttributes(
			String feedbackQuestionText, String feedbackSessionName,
			String courseId, String creatorEmail, Text questionText,
			int questionNumber,
			QuestionType questionType, FeedbackParticipantType giverType,
			FeedbackParticipantType recipientType,
			int numberOfEntitiesToGiveFeedbackTo,
			List<FeedbackParticipantType> showGiverNameTo,
			List<FeedbackParticipantType> showRecipientNameTo) {
		
		this.feedbackSessionName = Sanitizer.sanitizeTitle(feedbackSessionName);
		this.courseId = Sanitizer.sanitizeTitle(courseId);
		this.creatorEmail = Sanitizer.sanitizeGoogleId(creatorEmail);
		this.questionText = Sanitizer.sanitizeTextField(questionText);
		this.questionNumber = questionNumber;
		this.questionType = questionType;
		this.giverType = giverType;
		this.recipientType = recipientType;
		this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
		this.showGiverNameTo = showGiverNameTo;
		this.showRecipientNameTo = showRecipientNameTo;
	}
	
	public String getId() {
		return feedbackQuestionId;
	}
	
	public List<String> getInvalidityInfo() {

		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getInvalidityInfo(FieldType.FEEDBACK_SESSION_NAME, feedbackSessionName);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EMAIL, "creator's email", creatorEmail);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfoForFeedbackParticipantType(FieldType.GIVER_TYPE, giverType);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfoForFeedbackParticipantType(FieldType.RECIPIENT_TYPE, recipientType);
		if(!error.isEmpty()) { errors.add(error); }
		
		return errors;
	}
	
	public FeedbackQuestion toEntity() {
		return new FeedbackQuestion(
				feedbackSessionName, courseId, creatorEmail,
				questionText, questionNumber, questionType, giverType,
				recipientType, numberOfEntitiesToGiveFeedbackTo,
				showGiverNameTo, showRecipientNameTo);
	}
	
	@Override
	public boolean isValid() {
		return getInvalidityInfo().isEmpty();
	}

	@Override
	public String toString() {
		return "FeedbackQuestionAttributes [feedbackSessionName="
				+ feedbackSessionName + ", courseId=" + courseId
				+ ", creatorEmail=" + creatorEmail + ", questionText="
				+ questionText + ", questionNumber=" + questionNumber
				+ ", questionType=" + questionType + ", giverType=" + giverType
				+ ", recipientType=" + recipientType
				+ ", numberOfEntitiesToGiveFeedbackTo="
				+ numberOfEntitiesToGiveFeedbackTo + ", showGiverNameTo="
				+ showGiverNameTo + ", showRecipientNameTo="
				+ showRecipientNameTo + "]";
	}

	@Override
	public String getIdentificationString() {
		return this.questionNumber + ". " + this.questionText + "/" + this.feedbackSessionName;
	}

	@Override
	public String getEntityTypeAsString() {
		return "Feedback Question";
	}
}
