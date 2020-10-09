package teammates.ui.webapi;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.logic.api.Logic;

/**
 * Unit Test Cases for ActionUtils class.
 */
public class ActionUtilsTest {

    // Test constants
    private static final String DEFAULT_COURSE_ID = "DEFAULT_COURSE_ID";
    private static final String DEFAULT_FEEDBACK_RESPONSE_ID = "DEFAULT_FEEDBACK_RESPONSE_ID";
    private static final String DEFAULT_GIVER = "DEFAULT_GIVER";
    private static final String DEFAULT_RECEIVER = "DEFAULT_RECEIVER";
    private static final String DEFAULT_FEEDBACK_QUESTION_ID = "DEFAULT_FEEDBACK_QUESTION_ID";
    private static final String DEFAULT_EMAIL_ID = "DEFAULT_EMAIL_ID";

    private Logic logic;

    @BeforeMethod
    public void init() {
        logic = mock(Logic.class);
    }

    @Test
    public void getCourseAttributes_whenNoElementInMap() {
        Map<String, CourseAttributes> map = new HashMap<>();
        CourseAttributes expectedCourseAttributes = CourseAttributes
                .builder(DEFAULT_COURSE_ID)
                .build();

        when(logic.getCourse(DEFAULT_COURSE_ID)).thenReturn(expectedCourseAttributes);

        CourseAttributes actualAttributes = ActionUtils.getCourseAttributes(map, DEFAULT_COURSE_ID, logic);

        verify(logic).getCourse(DEFAULT_COURSE_ID);

        assertThat(actualAttributes, is(expectedCourseAttributes));

        verifyNoMoreInteractions(logic);
    }

    @Test
    public void getCourseAttributes_whenElementInMap() {
        Map<String, CourseAttributes> map = new HashMap<>();

        CourseAttributes expectedCourseAttributes = CourseAttributes
                .builder(DEFAULT_COURSE_ID)
                .build();

        map.put(DEFAULT_COURSE_ID, expectedCourseAttributes);

        CourseAttributes actualAttributes = ActionUtils.getCourseAttributes(map, DEFAULT_COURSE_ID, logic);

        verifyZeroInteractions(logic);
        assertThat(actualAttributes, is(expectedCourseAttributes));
    }

    @Test
    public void getFeedbackResponse_whenElementNotInMap() {
        Map<String, FeedbackResponseAttributes> map = new HashMap<>();

        FeedbackResponseAttributes expectedResponseAttributes = FeedbackResponseAttributes
                .builder(DEFAULT_FEEDBACK_RESPONSE_ID, DEFAULT_GIVER, DEFAULT_RECEIVER)
                .build();

        when(logic.getFeedbackResponse(DEFAULT_FEEDBACK_RESPONSE_ID)).thenReturn(expectedResponseAttributes);

        FeedbackResponseAttributes actualAttributes =
                ActionUtils.getFeedbackResponse(map, DEFAULT_FEEDBACK_RESPONSE_ID, logic);

        verify(logic).getFeedbackResponse(DEFAULT_FEEDBACK_RESPONSE_ID);

        assertThat(actualAttributes, is(expectedResponseAttributes));

        verifyNoMoreInteractions(logic);
    }

    @Test
    public void getFeedbackResponse_whenElementInMap() {
        Map<String, FeedbackResponseAttributes> map = new HashMap<>();

        FeedbackResponseAttributes expectedResponseAttributes = FeedbackResponseAttributes
                .builder(DEFAULT_FEEDBACK_RESPONSE_ID, DEFAULT_GIVER, DEFAULT_RECEIVER)
                .build();

        map.put(DEFAULT_FEEDBACK_RESPONSE_ID, expectedResponseAttributes);

        FeedbackResponseAttributes actualAttributes =
                ActionUtils.getFeedbackResponse(map, DEFAULT_FEEDBACK_RESPONSE_ID, logic);

        verifyZeroInteractions(logic);
        assertThat(actualAttributes, is(expectedResponseAttributes));
    }

    @Test
    public void getFeedbackQuestion_whenElementNotInMap() {
        Map<String, FeedbackQuestionAttributes> map = new HashMap<>();

        FeedbackQuestionAttributes expectedQuestionAttributes = FeedbackQuestionAttributes.builder().build();

        when(logic.getFeedbackQuestion(DEFAULT_FEEDBACK_QUESTION_ID)).thenReturn(expectedQuestionAttributes);

        FeedbackQuestionAttributes actualAttributes =
                ActionUtils.getFeedbackQuestion(map, DEFAULT_FEEDBACK_QUESTION_ID, logic);

        verify(logic).getFeedbackQuestion(DEFAULT_FEEDBACK_QUESTION_ID);

        assertThat(actualAttributes, is(expectedQuestionAttributes));

        verifyNoMoreInteractions(logic);
    }

    @Test
    public void getFeedbackQuestion_whenElementInMap() {
        Map<String, FeedbackQuestionAttributes> map = new HashMap<>();

        FeedbackQuestionAttributes expectedQuestionAttributes = FeedbackQuestionAttributes.builder().build();

        map.put(DEFAULT_FEEDBACK_QUESTION_ID, expectedQuestionAttributes);

        FeedbackQuestionAttributes actualAttributes =
                ActionUtils.getFeedbackQuestion(map, DEFAULT_FEEDBACK_QUESTION_ID, logic);

        verifyZeroInteractions(logic);

        assertThat(actualAttributes, is(expectedQuestionAttributes));
    }

    @Test
    public void getInstructorForGoogleId_whenElementNotInMap() {
        Map<String, InstructorAttributes> map = new HashMap<>();

        InstructorAttributes expectedInstructorAttributes = InstructorAttributes
                .builder(DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID)
                .build();

        when(logic.getInstructorForGoogleId(DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID))
                .thenReturn(expectedInstructorAttributes);

        InstructorAttributes actualAttributes =
                ActionUtils.getInstructorForGoogleId(map, DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID, logic);

        verify(logic).getInstructorForGoogleId(DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID);

        assertThat(actualAttributes, is(expectedInstructorAttributes));

        verifyNoMoreInteractions(logic);
    }

    @Test
    public void getInstructorForGoogleId_whenElementInMap() {
        Map<String, InstructorAttributes> map = new HashMap<>();

        InstructorAttributes expectedInstructorAttributes = InstructorAttributes
                .builder(DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID)
                .build();

        map.put(String.format("%s_%s", DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID), expectedInstructorAttributes);

        InstructorAttributes actualAttributes =
                ActionUtils.getInstructorForGoogleId(map, DEFAULT_COURSE_ID, DEFAULT_EMAIL_ID, logic);

        verifyZeroInteractions(logic);

        assertThat(actualAttributes, is(expectedInstructorAttributes));
    }
}
