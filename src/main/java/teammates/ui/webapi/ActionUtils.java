package teammates.ui.webapi;

import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.logic.api.Logic;

/**
 * Utility class for the Action Classes.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class ActionUtils {

    /**
     * In case map contains the CourseAttribute return it from the map.
     * Else compute and persist CourseAttribute in the map before returning.
     *
     * @param map - map to check if CourseAttribute is present
     * @param courseId - Key to the map
     * @param logic - Logic for accessing the data from DB
     * @return - CourseAttribute for the courseId
     */
    public static CourseAttributes getCourseAttributes(Map<String, CourseAttributes> map,
                                                       String courseId,
                                                       Logic logic) {
        if (!map.containsKey(courseId)) {
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            map.put(courseId, courseAttributes);
            return courseAttributes;
        }
        return map.get(courseId);
    }

    /**
     * In case map contains the FeedbackResponseAttributes return it from the map.
     * Else compute and persist FeedbackResponseAttributes in the map before returning.
     *
     * @param map - map to check if FeedbackResponseAttributes is present
     * @param feedbackResponseId - Key to the map
     * @param logic - Logic for accessing the data from DB
     * @return FeedbackResponseAttributes for the feedbackResponseId
     */
    public static FeedbackResponseAttributes getFeedbackResponse(Map<String, FeedbackResponseAttributes> map,
                                                                 String feedbackResponseId,
                                                                 Logic logic) {
        if (!map.containsKey(feedbackResponseId)) {
            FeedbackResponseAttributes feedbackResponseAttributes = logic.getFeedbackResponse(feedbackResponseId);
            map.put(feedbackResponseId, feedbackResponseAttributes);
            return feedbackResponseAttributes;
        }
        return map.get(feedbackResponseId);
    }

    /**
     * In case map contains the FeedbackQuestionAttributes return it from the map.
     * Else compute and persist FeedbackQuestionAttributes in the map before returning.
     *
     * @param map - map to check if FeedbackQuestionAttributes is present
     * @param questionId - Key to the map
     * @param logic - Logic for accessing the data from DB
     * @return FeedbackQuestionAttributes for the feedbackQuestionsId
     */
    public static FeedbackQuestionAttributes getFeedbackQuestion(Map<String, FeedbackQuestionAttributes> map,
                                                                 String questionId,
                                                                 Logic logic) {
        if (!map.containsKey(questionId)) {
            FeedbackQuestionAttributes feedbackQuestionAttributes = logic.getFeedbackQuestion(questionId);
            map.put(questionId, feedbackQuestionAttributes);
            return feedbackQuestionAttributes;
        }
        return map.get(questionId);
    }

    /**
     * In case map contains the InstructorAttributes return it from the map.
     * Else compute and persist InstructorAttributes in the map before returning.
     * Key used to access the map - CourseId_GoogleId
     *
     * @param map - map to check if InstructorAttributes is present
     * @param courseId - Used to construct key to the map
     * @param googleId - Used to construct key to the map
     * @param logic - Logic for accessing the data from DB
     * @return InstructorAttributes for the courseId and googleEmailId
     */
    public static InstructorAttributes getInstructorForGoogleId(Map<String, InstructorAttributes> map,
                                                                String courseId,
                                                                String googleId,
                                                                Logic logic) {
        String key = String.format("%s_%s", courseId, googleId);
        if (!map.containsKey(key)) {
            InstructorAttributes instructorAttributes = logic.getInstructorForGoogleId(courseId, googleId);
            map.put(key, instructorAttributes);
            return instructorAttributes;
        }
        return map.get(key);
    }

}
