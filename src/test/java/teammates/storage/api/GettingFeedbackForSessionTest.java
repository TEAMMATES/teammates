package teammates.storage.api;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;

public class GettingFeedbackForSessionTest {

    @Test
public void testGetFeedbackResponseCommentsForSession_returnsCorrectResult() {
    // Arrange
    String courseId = "course1";
    String feedbackSessionName = "feedbackSession1";
    List<FeedbackResponseCommentAttributes> expectedResult = Arrays.asList(
        new FeedbackResponseCommentAttributes("comment1", "giver1", "receiver1", "course1", "feedbackSession1"),
        new FeedbackResponseCommentAttributes("comment2", "giver2", "receiver2", "course1", "feedbackSession1")
    );
    Object mockDataBundle;
    when(mockDataBundle.feedbackResponseComments).thenReturn(expectedResult);

    // Act
    List<FeedbackResponseCommentAttributes> result = service.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);

    // Assert
    assertEquals(expectedResult, result);
}
    
}
