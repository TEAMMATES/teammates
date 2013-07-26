package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.FeedbackQuestion;

public class FeedbackQuestionAttributes extends EntityAttributes
	implements Comparable<FeedbackQuestionAttributes>{
	private String feedbackQuestionId = null;
	public String feedbackSessionName;
	public String courseId;
	public String creatorEmail;
	public Text questionText;
	public int questionNumber;
	public FeedbackQuestionType questionType;
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
			FeedbackQuestionType questionType, FeedbackParticipantType giverType,
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
	
	/**
	 * Checks if updating this question to the {@code newAttributes} will
	 * require the responses to be deleted for consistency.
	 * Does not check if any responses exist.
	 * @param newAttributes
	 * @return
	 */
	public boolean isChangesRequiresResponseDeletion(
			FeedbackQuestionAttributes newAttributes) {
		if( newAttributes.giverType.equals(this.giverType) == false ||
			newAttributes.recipientType.equals(this.recipientType) == false	) {
			return true;
		}
		if( this.showResponsesTo.containsAll(newAttributes.showResponsesTo) == false || 
			this.showGiverNameTo.containsAll(newAttributes.showGiverNameTo) == false ||
			this.showRecipientNameTo.containsAll(newAttributes.showRecipientNameTo) == false ) {
			return true;
		}
		return false;
	}
	
	public void updateValues(FeedbackQuestionAttributes newAttributes) {
		// These can't be changed anyway. Copy values to defensively avoid
		// invalid parameters.
		newAttributes.feedbackSessionName = this.feedbackSessionName;
		newAttributes.courseId = this.courseId;
		newAttributes.creatorEmail = this.creatorEmail;

		if (newAttributes.questionText == null) {
			newAttributes.questionText = this.questionText;
		}
		if (newAttributes.questionType == null) {
			newAttributes.questionType = this.questionType;
		}
		if (newAttributes.giverType == null) {
			newAttributes.giverType = this.giverType;
		}
		if (newAttributes.recipientType == null) {
			newAttributes.recipientType = this.recipientType;
		}
		if (newAttributes.showResponsesTo == null) {
			newAttributes.showResponsesTo = this.showResponsesTo;
		}
		if (newAttributes.showGiverNameTo == null) {
			newAttributes.showGiverNameTo = this.showGiverNameTo;
		}
		if (newAttributes.showRecipientNameTo == null) {
			newAttributes.showRecipientNameTo = this.showRecipientNameTo;
		}
	}
	
	// TODO: move following methods to PageData?
	// Answer: OK to move to the respective PageData class. Unit test this thoroughly.
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
			line = participant.toVisibilityString() + " ";
			if(participant == FeedbackParticipantType.RECEIVER) {
				if (recipientType == FeedbackParticipantType.OWN_TEAM ||
						recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
					line = recipientType.toVisibilityString() + " ";
				} else {
					line += (recipientType.toSingularFormString());
					if(numberOfEntitiesToGiveFeedbackTo > 1) {
						line += "s";
					}
					line += " ";
				}
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
			message.add("No-one can see your responses.");
		}
		
		return message;
	}
	
	public boolean isRecipientNameHidden() {
		return (recipientType == FeedbackParticipantType.NONE || recipientType == FeedbackParticipantType.SELF);
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
