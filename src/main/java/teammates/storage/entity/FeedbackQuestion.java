package teammates.storage.entity;

import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

import teammates.common.FeedbackParticipantType;

@PersistenceCapable
public class FeedbackQuestion {
	
	public enum QuestionType {
		TEXT, MCQ;
	}
	
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
	private transient String feedbackQuestionId;
		
	@Persistent
	private String feedbackSessionName;
	
	@Persistent
	private String courseId;
	
	// TODO: Do we need this field since creator of FS = creator of qn?
	@Persistent
	private String creatorEmail;
	
	@Persistent
	private Text questionText;
	
	@Persistent
	private int questionNumber;
	
	@Persistent
	private QuestionType questionType;

	@Persistent
	private FeedbackParticipantType giverType;
	
	@Persistent
	private FeedbackParticipantType recipientType;
	
	// Check for consistency in questionLogic/questionAttributes.
	// (i.e. if type is own team, numberOfEntities must = 1).
	@Persistent
	private int numberOfEntitiesToGiveFeedbackTo;
	
	// We can actually query the list in JDOQL if needed.
	// We can derive whether to show answer based on the following two variables.
	@Persistent
	private List<FeedbackParticipantType> showGiverNameTo;
	
	@Persistent
	private List<FeedbackParticipantType> showRecipientNameTo;

	
	public FeedbackQuestion(
			String feedbackSessionName, String courseId, String creatorEmail,
			Text questionText, int questionNumber, QuestionType questionType,
			FeedbackParticipantType giverType,
			FeedbackParticipantType recipientType,
			int numberOfEntitiesToGiveFeedbackTo,
			List<FeedbackParticipantType> showGiverNameTo,
			List<FeedbackParticipantType> showRecipientNameTo) {
		
		this.feedbackQuestionId = null; // Allow GAE to generate key.
		this.feedbackSessionName = feedbackSessionName;
		this.courseId = courseId;
		this.creatorEmail = creatorEmail;
		this.questionText = questionText;
		this.questionNumber = questionNumber;
		this.questionType = questionType;
		this.giverType = giverType;
		this.recipientType = recipientType;
		this.numberOfEntitiesToGiveFeedbackTo =	numberOfEntitiesToGiveFeedbackTo;
		this.showGiverNameTo = showGiverNameTo;
		this.showRecipientNameTo = showRecipientNameTo;
	}
	
	public String getId() {
		return feedbackQuestionId;
	}

	/* Auto generated. Don't set this.
	public void setFeedbackQuestionId(String feedbackQuestionId) {
		this.feedbackQuestionId = feedbackQuestionId;
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

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public Text getQuestionText() {
		return questionText;
	}

	public void setQuestionText(Text questionText) {
		this.questionText = questionText;
	}

	public QuestionType getQuestionType() {
		return questionType;
	}

	public void setQuestionType(QuestionType questionType) {
		this.questionType = questionType;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public FeedbackParticipantType getGiverType() {
		return giverType;
	}

	public void setGiverType(FeedbackParticipantType giverType) {
		this.giverType = giverType;
	}

	public FeedbackParticipantType getRecipientType() {
		return recipientType;
	}

	public void setReceiverType(FeedbackParticipantType receiverType) {
		this.recipientType = receiverType;
	}
	
	public int getNumberOfEntitiesToGiveFeedbackTo() {
		return numberOfEntitiesToGiveFeedbackTo;
	}

	public void setNumberOfEntitiesToGiveFeedbackTo(
			int numberOfEntitiesToGiveFeedbackTo) {
		this.numberOfEntitiesToGiveFeedbackTo = numberOfEntitiesToGiveFeedbackTo;
	}

	public List<FeedbackParticipantType> getShowGiverNameTo() {
		return showGiverNameTo;
	}

	public void setShowGiverNameTo(List<FeedbackParticipantType> showGiverNameTo) {
		this.showGiverNameTo = showGiverNameTo;
	}

	public List<FeedbackParticipantType> getShowRecipientNameTo() {
		return showRecipientNameTo;
	}

	public void setShowRecipientNameTo(
			List<FeedbackParticipantType> showRecipientNameTo) {
		this.showRecipientNameTo = showRecipientNameTo;
	}

	public void setRecipientType(FeedbackParticipantType recipientType) {
		this.recipientType = recipientType;
	}
}
