package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackResponseCommentsDb;

public class FeedbackResponseCommentsLogic {
	private static FeedbackResponseCommentsLogic instance;

	private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

	public static FeedbackResponseCommentsLogic inst() {
		if (instance == null)
			instance = new FeedbackResponseCommentsLogic();
		return instance;
	}

	public void createFeedbackResponseComment(
			FeedbackResponseCommentAttributes frca)
			throws InvalidParametersException, EntityAlreadyExistsException {
		frcDb.createEntity(frca);
	}
	
	public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSession(String courseId,
			String feedbackSessionName) {
		return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
	}
}
