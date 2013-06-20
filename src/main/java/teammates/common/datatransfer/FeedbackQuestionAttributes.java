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

public class FeedbackQuestionAttributes extends EntityAttributes
	implements Comparable<FeedbackQuestionAttributes>{
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
	public List<FeedbackParticipantType> showResponsesTo;
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
		this.showResponsesTo = fq.getShowResponsesTo();
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
			List<FeedbackParticipantType> showResponsesTo,
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
		this.showResponsesTo = showResponsesTo;
		this.showGiverNameTo = showGiverNameTo;
		this.showRecipientNameTo = showRecipientNameTo;
	}
	
	public String getId() {
		return feedbackQuestionId;
	}
	
	// NOTE: Only use this to match and search for the ID of a known existing question entity.
	public void setId(String id) {
		this.feedbackQuestionId = id;
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
		
		error= validator.getValidityInfoForFeedbackParticipantType(giverType, recipientType);
		if(!error.isEmpty()) { errors.add(error); }
		
		return errors;
	}
	
	public FeedbackQuestion toEntity() {
		return new FeedbackQuestion(
				feedbackSessionName, courseId, creatorEmail,
				questionText, questionNumber, questionType, giverType,
				recipientType, numberOfEntitiesToGiveFeedbackTo,
				showResponsesTo, showGiverNameTo, showRecipientNameTo);
	}
	
	public List<String> getVisibilityMessage(){
		
		List<String> message = new ArrayList<String>();
		
		// General feedback message.
		if (this.recipientType == FeedbackParticipantType.NONE) {
			message.add("Everyone can see your feedback and your name as this is a general feedback question.");
			return message;
		}
		
		for(FeedbackParticipantType participant : showResponsesTo) {
			String line = "";
			// Self feedback message.
			if(participant == FeedbackParticipantType.RECEIVER && 
					this.recipientType == FeedbackParticipantType.SELF) {
				message.add("You can see your own feedback in the results page later on.");
				break;
			}
			line += participant.toDisplayNameVisibility() + " ";
			if(participant == FeedbackParticipantType.RECEIVER) {
				line += (recipientType.toString().toLowerCase());
				if(numberOfEntitiesToGiveFeedbackTo < 2) {
					// remove letter 's'.
					line = line.substring(0, line.length()-1);
				}
				line += " ";
			}
			line += "can see your response";
			if(showRecipientNameTo.contains(participant) == false) {
				if(showGiverNameTo.contains(participant) == true) {
					line += ", and your name";
				} 
				line += ", but <span class=\"bold color_red\">not</span> the name of the recipient";
				if(showGiverNameTo.contains(participant) == false) {
					line += ", or your name";
				}
			} else if (showRecipientNameTo.contains(participant) == true) {
				if(participant != FeedbackParticipantType.RECEIVER) {
					line += ", the name of the recipient";
				}
				if(showGiverNameTo.contains(participant)) {
					line += ", and your name";
				} else {
					line += ", but <span class=\"bold color_red\">not</span> your name";
				}
			}
			line += ".";
			message.add(line);
		}
		
		if (message.isEmpty()) {
			message.add("No-one but the feedback session creator can see your responses.");
		} else if (message.size() < FeedbackParticipantType.MAX_VISIBILITY_ENTITIES) {
			message.add("<span class=\"bold color_brown\">No-one else can see your response.</span>");
		}
		
		return message;
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
				+ numberOfEntitiesToGiveFeedbackTo + ", showResponsesTo="
				+ showResponsesTo + ", showGiverNameTo=" + showGiverNameTo
				+ ", showRecipientNameTo=" + showRecipientNameTo + "]";
	}

	@Override
	public String getIdentificationString() {
		return this.questionNumber + ". " + this.questionText.toString() + "/" + this.feedbackSessionName + "/" + this.courseId;
	}

	@Override
	public String getEntityTypeAsString() {
		return "Feedback Question";
	}
	
	@Override
	public int compareTo(FeedbackQuestionAttributes o) {
		if (o == null) {
			return 1;
		} else {
			return Integer.compare(this.questionNumber, o.questionNumber);
		}
	}
	
}
