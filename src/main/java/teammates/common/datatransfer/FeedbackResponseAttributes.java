package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.FeedbackResponse;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;

public class FeedbackResponseAttributes extends EntityAttributes {
	private String feedbackResponseId = null;
	public String feedbackSessionName;
	public String courseId;
	public String feedbackQuestionId;
	public FeedbackQuestionType feedbackQuestionType;
	public String giverEmail;
	public String recipientEmail;
	/** Contains the JSON formatted string that holds the information of the response details <br>
	 * Don't use directly unless for storing/loading from data store <br>
	 * To get the answer text use {@code getQuestionDetails().getAnswerString()} 
	 */
	public Text responseMetaData;
	
	public FeedbackResponseAttributes() {
		
	}
	
	public FeedbackResponseAttributes(String feedbackSessionName,
			String courseId, String feedbackQuestionId,
			FeedbackQuestionType feedbackQuestionType, String giverEmail,
			String recipientEmail, Text responseMetaData) {
		this.feedbackSessionName = Sanitizer.sanitizeTitle(feedbackSessionName);
		this.courseId = Sanitizer.sanitizeTitle(courseId);
		this.feedbackQuestionId = feedbackQuestionId;
		this.feedbackQuestionType = feedbackQuestionType;
		this.giverEmail = Sanitizer.sanitizeEmail(giverEmail);
		this.recipientEmail = recipientEmail;
		this.responseMetaData = responseMetaData;
	}

	public FeedbackResponseAttributes(FeedbackResponse fr) {
		this.feedbackResponseId = fr.getId();
		this.feedbackSessionName = fr.getFeedbackSessionName();
		this.courseId = fr.getCourseId();
		this.feedbackQuestionId = fr.getFeedbackQuestionId();
		this.feedbackQuestionType = fr.getFeedbackQuestionType();
		this.giverEmail = fr.getGiverEmail();
		this.recipientEmail = fr.getRecipientEmail();
		this.responseMetaData = fr.getResponseMetaData();
	}
	
	public FeedbackResponseAttributes(FeedbackResponseAttributes copy) {
		this.feedbackResponseId = copy.getId();
		this.feedbackSessionName = copy.feedbackSessionName;
		this.courseId = copy.courseId;
		this.feedbackQuestionId = copy.feedbackQuestionId;
		this.feedbackQuestionType = copy.feedbackQuestionType;
		this.giverEmail = copy.giverEmail;
		this.recipientEmail = copy.recipientEmail;
		this.responseMetaData = copy.responseMetaData;
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
				giverEmail, recipientEmail, responseMetaData);
	}
	
	@Override
	public String getIdentificationString() {
		return feedbackQuestionId + "/" + giverEmail + ":" + recipientEmail;
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
				+ ", giverEmail=" + giverEmail + ", recipientEmail=" + recipientEmail
				+ ", answer=" + responseMetaData + "]";
	}

	@Override
	public void sanitizeForSaving() {
		// TODO implement this
	}
	
	/** This method converts the given Feedback*ResponseDetails object to JSON for storing
	 * @param responseDetails
	 */
	public void setResponseDetails(FeedbackAbstractResponseDetails responseDetails) {
		Gson gson = teammates.common.util.Utils.getTeammatesGson();
		
		if(responseDetails.questionType == FeedbackQuestionType.TEXT) {
			// For Text questions, the answer simply contains the response text, not a JSON
			// This is due to legacy data in the data store before there were multiple question types
			responseMetaData = new Text(responseDetails.getAnswerString());
		} else {
			responseMetaData = new Text(gson.toJson(responseDetails, getFeedbackResponseDetailsClass()));
		}
	}
	
	/** This method retrieves the Feedback*ResponseDetails object for this response
	 * @return The Feedback*ResponseDetails object representing the response's details
	 */
	public FeedbackAbstractResponseDetails getResponseDetails(){
		Class<? extends FeedbackAbstractResponseDetails> responseDetailsClass = getFeedbackResponseDetailsClass();
		
		if(responseDetailsClass == FeedbackTextResponseDetails.class) {
			// For Text questions, the questionText simply contains the question, not a JSON
			// This is due to legacy data in the data store before there are multiple question types
			return new FeedbackTextResponseDetails(responseMetaData.getValue());
		} else {
			Gson gson = teammates.common.util.Utils.getTeammatesGson();
			return gson.fromJson(responseMetaData.getValue(), responseDetailsClass);
		}
	}
	
	/** This method gets the appropriate class type for the Feedback*ResponseDetails object
	 * for this response.
	 * @return The Feedback*ResponseDetails class type appropriate for this response.
	 */
	private Class<? extends FeedbackAbstractResponseDetails> getFeedbackResponseDetailsClass(){
		Class<? extends FeedbackAbstractResponseDetails> responseDetailsClass = null;
		
		switch(feedbackQuestionType){
		case TEXT:
			responseDetailsClass = FeedbackTextResponseDetails.class;
			break;
		case MCQ:
			responseDetailsClass = FeedbackMcqResponseDetails.class;
			break;
		default:
			Assumption.fail("FeedbackQuestionType unsupported by FeedbackQuestionAttributes");
			break;
		}
		
		return responseDetailsClass;
	}
}
