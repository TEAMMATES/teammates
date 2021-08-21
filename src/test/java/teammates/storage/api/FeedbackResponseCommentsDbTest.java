package teammates.storage.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.JsonUtils;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCaseWithLocalDatabaseAccess;

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */
public class FeedbackResponseCommentsDbTest extends BaseTestCaseWithLocalDatabaseAccess {

    private final FeedbackResponseCommentsDb frcDb = FeedbackResponseCommentsDb.inst();

    private DataBundle dataBundle;
    private FeedbackResponseCommentAttributes frcaData;
    private String frId;
    private FeedbackResponseCommentAttributes anotherFrcaData;
    private List<FeedbackResponseCommentAttributes> frcasData;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dataBundle = getTypicalDataBundle();

        frcaData = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        frId = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1").getFeedbackResponseId();
        anotherFrcaData = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcasData = new ArrayList<>();

        frcaData = frcDb.putEntity(frcaData);
        anotherFrcaData = frcDb.putEntity(anotherFrcaData);

        frcasData.add(frcaData);
        frcasData.add(anotherFrcaData);
    }

    @AfterMethod
    public void afterMethod() {
        frcDb.deleteFeedbackResponseComment(frcaData.getId());
        frcDb.deleteFeedbackResponseComment(anotherFrcaData.getId());
    }

    @Test
    public void testAll() throws Exception {

        testEntityCreationAndDeletion();

        testGetFeedbackResponseCommentFromId();

        testGetFeedbackResponseCommentFromCommentDetails();

        testGetFeedbackResponseCommentForGiver();

        testGetFeedbackResponseCommentForResponse();

        testUpdateFeedbackResponseComment();

        testGetFeedbackResponseCommentsForSession();

        testUpdateFeedbackResponseCommentsGiverEmail();
    }

    private void testEntityCreationAndDeletion() throws Exception {
        FeedbackResponseCommentAttributes frcaTemp =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.setCreatedAt(Instant.now());
        frcaTemp.setCommentText("test creation and deletion");

        ______TS("Entity creation");

        frcDb.createEntity(frcaTemp);
        verifyPresentInDatabase(frcaTemp);

        ______TS("Entity deletion");

        frcaTemp = frcDb.getFeedbackResponseComment(
                frcaTemp.getFeedbackResponseId(), frcaTemp.getCommentGiver(), frcaTemp.getCreatedAt());
        assertNotNull(frcaTemp);
        frcDb.deleteFeedbackResponseComment(frcaTemp.getId());
        verifyAbsentInDatabase(frcaTemp);
    }

    private void testGetFeedbackResponseCommentFromId() {

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected = getFeedbackResponseComment(
                frcaData.getCourseId(), frcaData.getCreatedAt(), frcaData.getCommentGiver());

        FeedbackResponseCommentAttributes frcaActual =
                frcDb.getFeedbackResponseComment(frcaExpected.getId());

        assertEquals(frcaExpected.toString(), frcaActual.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment(-1L));
    }

    private void testGetFeedbackResponseCommentFromCommentDetails() {

        ______TS("null parameter");

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseComment(null, "", Instant.now()));

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseComment("", null, Instant.now()));

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseComment("", "", null));

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected = frcaData;
        FeedbackResponseCommentAttributes frca =
                frcDb.getFeedbackResponseComment(frId, frcaExpected.getCommentGiver(), frcaExpected.getCreatedAt());

        // fill back the Ids
        frcaExpected.setFeedbackResponseId(frId);
        frcaExpected.setId(frca.getId());
        frcaExpected.setFeedbackQuestionId(frca.getFeedbackQuestionId());

        assertEquals(frcaExpected.toString(), frca.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment("123", frca.getCommentGiver(), frca.getCreatedAt()));

        ______TS("non-existent giver");

        assertNull(frcDb.getFeedbackResponseComment(
                frca.getId().toString(), "nonExistentGiverEmail", frca.getCreatedAt()));
    }

    private void testGetFeedbackResponseCommentForGiver() {
        List<FeedbackResponseCommentAttributes> frcasExpected = frcasData;

        ______TS("null parameter");

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentForGiver(null, frcaData.getCommentGiver()));

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentForGiver(frcaData.getCourseId(), null));

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> frcas =
                frcDb.getFeedbackResponseCommentForGiver(frcaData.getCourseId(), frcaData.getCommentGiver());
        verifyListsContainSameResponseCommentAttributes(new ArrayList<>(frcasExpected), frcas);

        ______TS("non-existent course id");

        frcas = frcDb.getFeedbackResponseCommentForGiver("idOfNonExistentCourse", frcaData.getCommentGiver());
        assertTrue(frcas.isEmpty());

        ______TS("non-existent giver");

        frcas = frcDb.getFeedbackResponseCommentForGiver(frcaData.getCourseId(), "nonExistentGiverEmail");
        assertTrue(frcas.isEmpty());
    }

    private void testGetFeedbackResponseCommentForResponse() {
        String responseId = "1%student1InCourse1@gmail.tmt%student1InCourse1@gmail.tmt";
        List<FeedbackResponseCommentAttributes> frcasExpected = new ArrayList<>();
        frcasExpected.add(frcaData);

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> frcas = frcDb.getFeedbackResponseCommentsForResponse(responseId);
        verifyListsContainSameResponseCommentAttributes(new ArrayList<>(frcasExpected), frcas);
    }

    @Test
    public void testUpdateFeedbackResponseComment_noChangeToComment_shouldNotIssueSaveRequest() throws Exception {
        FeedbackResponseCommentAttributes updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(frcaData.getId())
                        .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(frcaData), JsonUtils.toJson(updatedComment));

        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(frcaData.getId())
                        .withFeedbackResponseId(frcaData.getFeedbackResponseId())
                        .withCommentText(frcaData.getCommentText())
                        .withShowCommentTo(frcaData.getShowCommentTo())
                        .withShowGiverNameTo(frcaData.getShowGiverNameTo())
                        .withLastEditorEmail(frcaData.getLastEditorEmail())
                        .withLastEditorAt(frcaData.getLastEditedAt())
                        .withGiverSection(frcaData.getGiverSection())
                        .withReceiverSection(frcaData.getReceiverSection())
                        .build());

        // please verify the log message manually to ensure that saving request is not issued
        assertEquals(JsonUtils.toJson(frcaData), JsonUtils.toJson(updatedComment));
    }

    private void testUpdateFeedbackResponseComment() throws Exception {

        ______TS("null parameter");

        assertThrows(AssertionError.class, () -> frcDb.updateFeedbackResponseComment(null));

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaTemp =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.setCreatedAt(Instant.now());
        frcaTemp.setCommentText("Update feedback response comment");
        frcDb.createEntity(frcaTemp);
        frcaTemp = frcDb.getFeedbackResponseComment(frcaTemp.getFeedbackResponseId(),
                frcaTemp.getCommentGiver(), frcaTemp.getCreatedAt());

        FeedbackResponseCommentAttributes frcaExpected = getFeedbackResponseComment(
                frcaTemp.getCourseId(), frcaTemp.getCreatedAt(), frcaTemp.getCommentGiver());
        frcaExpected.setCommentText("This is new Text");
        FeedbackResponseCommentAttributes updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(frcaExpected.getId())
                        .withCommentText("This is new Text")
                        .build()
        );
        assertEquals(frcaExpected.getCommentText(), updatedComment.getCommentText());

        FeedbackResponseCommentAttributes frcaActual =
                getFeedbackResponseComment(
                        frcaExpected.getCourseId(), frcaExpected.getCreatedAt(), frcaExpected.getCommentGiver());

        frcaExpected.setId(frcaActual.getId());
        frcaExpected.setFeedbackQuestionId(frcaActual.getFeedbackQuestionId());
        assertEquals(frcaExpected.getCourseId(), frcaActual.getCourseId());
        assertEquals(frcaExpected.getCommentText(), frcaActual.getCommentText());

        frcDb.deleteFeedbackResponseComment(frcaActual.getId());

        ______TS("non-existent comment");

        FeedbackResponseCommentAttributes.UpdateOptions updateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(-1L)
                        .withCommentText("This is new Text")
                        .build();
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcDb.updateFeedbackResponseComment(updateOptions));
        assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + updateOptions, ednee.getMessage());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateFeedbackResponseComment_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        FeedbackResponseCommentAttributes typicalComment =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        typicalComment.setCreatedAt(Instant.now());
        typicalComment.setCommentText("Update feedback response comment");
        typicalComment = frcDb.createEntity(typicalComment);

        assertNotEquals("responseId1", typicalComment.getFeedbackResponseId());
        FeedbackResponseCommentAttributes updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withFeedbackResponseId("responseId1")
                        .build());
        FeedbackResponseCommentAttributes actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals("responseId1", updatedComment.getFeedbackResponseId());
        assertEquals("responseId1", actualComment.getFeedbackResponseId());

        assertNotEquals("This is new Text", actualComment.getCommentText());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withCommentText("This is new Text")
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals("This is new Text", updatedComment.getCommentText());
        assertEquals("This is new Text", actualComment.getCommentText());

        assertNotEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualComment.getShowCommentTo());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withShowCommentTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), updatedComment.getShowCommentTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualComment.getShowCommentTo());

        assertNotEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualComment.getShowGiverNameTo());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withShowGiverNameTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), updatedComment.getShowGiverNameTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualComment.getShowGiverNameTo());

        assertNotEquals("editor1@email.com", updatedComment.getLastEditorEmail());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withLastEditorEmail("editor1@email.com")
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals("editor1@email.com", updatedComment.getLastEditorEmail());
        assertEquals("editor1@email.com", actualComment.getLastEditorEmail());

        assertNotEquals(Instant.ofEpochMilli(1000), actualComment.getLastEditedAt());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withLastEditorAt(Instant.ofEpochMilli(1000))
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals(Instant.ofEpochMilli(1000), updatedComment.getLastEditedAt());
        assertEquals(Instant.ofEpochMilli(1000), actualComment.getLastEditedAt());

        assertNotEquals("section1", actualComment.getGiverSection());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withGiverSection("section1")
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals("section1", updatedComment.getGiverSection());
        assertEquals("section1", actualComment.getGiverSection());

        assertNotEquals("section1", actualComment.getReceiverSection());
        updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(typicalComment.getId())
                        .withReceiverSection("section1")
                        .build());
        actualComment = frcDb.getFeedbackResponseComment(typicalComment.getId());
        assertEquals("section1", updatedComment.getReceiverSection());
        assertEquals("section1", actualComment.getReceiverSection());

        frcDb.deleteFeedbackResponseComment(typicalComment.getId());
    }

    private void testGetFeedbackResponseCommentsForSession() {

        ______TS("null parameter");

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentsForSession(null, ""));

        assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentsForSession("", null));

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> actualFrcas =
                frcDb.getFeedbackResponseCommentsForSession(frcaData.getCourseId(), frcaData.getFeedbackSessionName());
        List<FeedbackResponseCommentAttributes> expectedFrcas = new ArrayList<>();
        expectedFrcas.add(frcaData);
        expectedFrcas.add(anotherFrcaData);
        verifyListsContainSameResponseCommentAttributes(expectedFrcas, actualFrcas);
    }

    private void testUpdateFeedbackResponseCommentsGiverEmail() throws Exception {
        FeedbackResponseCommentAttributes frcaDataOfNewGiver =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q3S1C1");
        String giverEmail = "frcdb.newGiver@email.com";
        String courseId = "frcdb.giver.courseId";
        Instant createdAt = Instant.now();
        frcaDataOfNewGiver.setCreatedAt(createdAt);
        frcaDataOfNewGiver.setCommentText("another comment for this response");
        frcaDataOfNewGiver.setId(null);
        frcaDataOfNewGiver.setCommentGiver(giverEmail);
        frcaDataOfNewGiver.setCourseId(courseId);
        frcDb.createEntity(frcaDataOfNewGiver);
        assertNotNull(getFeedbackResponseComment(courseId, createdAt, giverEmail));

        ______TS("typical success case");

        String updatedEmail = "frcdb.updatedGiver@email.com";
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, updatedEmail);
        assertNull(getFeedbackResponseComment(courseId, createdAt, giverEmail));
        assertNotNull(getFeedbackResponseComment(courseId, createdAt, updatedEmail));

        ______TS("Same email");

        FeedbackResponseCommentAttributes expectedFrca =
                getFeedbackResponseComment(courseId, createdAt, updatedEmail);
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, updatedEmail, updatedEmail);
        FeedbackResponseCommentAttributes actualFrca =
                getFeedbackResponseComment(courseId, createdAt, updatedEmail);
        assertEquals(actualFrca.getCourseId(), expectedFrca.getCourseId());
        assertEquals(actualFrca.getCreatedAt(), expectedFrca.getCreatedAt());
        assertEquals(actualFrca.getCommentGiver(), expectedFrca.getCommentGiver());

        ______TS("null parameter");

        assertThrows(AssertionError.class,
                () -> frcDb.updateGiverEmailOfFeedbackResponseComments(null, giverEmail, updatedEmail));

        assertThrows(AssertionError.class,
                () -> frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, null, updatedEmail));

        assertThrows(AssertionError.class,
                () -> frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, null));
    }

    @Test
    public void testDeleteFeedbackResponseComment() {

        ______TS("delete non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment(-123L));

        ______TS("typical success case");

        assertNotNull(frcDb.getFeedbackResponseComment(frcaData.getId()));

        frcDb.deleteFeedbackResponseComment(frcaData.getId());

        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));

        ______TS("delete again same comment");

        frcDb.deleteFeedbackResponseComment(frcaData.getId());

        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_byResponseId() throws Exception {

        ______TS("non-existent response id");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId("not_exist")
                        .build());

        ______TS("typical success case");

        // get another frc from data bundle and use it to create another feedback response
        FeedbackResponseCommentAttributes tempFrcaData =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        tempFrcaData.setCreatedAt(Instant.now());
        tempFrcaData.setCommentText("another comment for this response");
        // for some reason, the id is 0 instead of null. so we explicitly set it to be null
        tempFrcaData.setId(null);
        // set this comment to have the same responseId as frcaData
        String responseId = "1%student1InCourse1@gmail.com%student1InCourse1@gmail.com";
        tempFrcaData.setFeedbackResponseId(responseId);
        frcDb.createEntity(tempFrcaData);

        // two comments exist in the DB
        assertFalse(frcDb.getFeedbackResponseCommentsForResponse(responseId).isEmpty());
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));

        // delete one
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(responseId)
                        .build());

        assertEquals(0, frcDb.getFeedbackResponseCommentsForResponse(responseId).size());
        // other irrelevant comment remains
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_byQuestionId() {
        ______TS("non-existent question id");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withQuestionId("not_exist")
                        .build());

        ______TS("typical success case");

        assertNotNull(frcDb.getFeedbackResponseComment(frcaData.getId()));

        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withQuestionId(frcaData.getFeedbackQuestionId())
                        .build());

        // comment deleted
        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
        // other irrelevant comment remains
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_byCourseIdAndSessionName() {
        ______TS("non-existent course");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId("course_not_exist")
                        .withFeedbackSessionName(frcaData.getFeedbackSessionName())
                        .build());

        ______TS("non-existent session");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frcaData.getCourseId())
                        .withFeedbackSessionName("session_not_exist")
                        .build());

        ______TS("non-existent course and session");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId("course_not_exist")
                        .withFeedbackSessionName("session_not_exist")
                        .build());

        ______TS("typical success case");

        assertFalse(frcDb.getFeedbackResponseCommentsForSession(
                frcaData.getCourseId(), frcaData.getFeedbackSessionName()).isEmpty());

        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frcaData.getCourseId())
                        .withFeedbackSessionName(frcaData.getFeedbackSessionName())
                        .build());

        assertEquals(0, frcDb.getFeedbackResponseCommentsForSession(
                frcaData.getCourseId(), frcaData.getFeedbackSessionName()).size());
    }

    @Test
    public void testDeleteFeedbackResponseComments_byCourseId() throws Exception {

        ______TS("non-existent course");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId("course_not_exist")
                        .build());

        ______TS("typical success case");

        assertFalse(frcDb.getFeedbackResponseCommentsForSession(
                frcaData.getCourseId(), frcaData.getFeedbackSessionName()).isEmpty());
        // the two existing comment are in the same course
        assertEquals(frcaData.getCourseId(), anotherFrcaData.getCourseId());
        assertNotNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));

        // create another comments different course
        FeedbackResponseCommentAttributes tempFrcaData =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        tempFrcaData.setFeedbackResponseId("randomId");
        tempFrcaData.setCourseId("anotherCourse");
        frcDb.createEntity(tempFrcaData);
        tempFrcaData = frcDb.getFeedbackResponseComment(tempFrcaData.getFeedbackResponseId(),
                tempFrcaData.getCommentGiver(), tempFrcaData.getCreatedAt());
        assertNotNull(tempFrcaData);

        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frcaData.getCourseId())
                        .build());

        assertTrue(frcDb.getFeedbackResponseCommentsForSession(
                frcaData.getCourseId(), frcaData.getFeedbackSessionName()).isEmpty());
        // same course's comments are deleted
        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
        assertNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));
        // other course's data is not affected
        assertNotNull(frcDb.getFeedbackResponseComment(tempFrcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_nullInput_shouldThrowException() {
        assertThrows(AssertionError.class, () -> frcDb.deleteFeedbackResponseComments(null));
    }

    private void verifyListsContainSameResponseCommentAttributes(
            List<FeedbackResponseCommentAttributes> expectedFrcas,
            List<FeedbackResponseCommentAttributes> actualFrcas) {
        AssertHelper.assertSameContentIgnoreOrder(expectedFrcas, actualFrcas);
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestion_typicalCase_shouldQueryCorrectly() {
        FeedbackResponseCommentAttributes frc = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        frc = getFeedbackResponseComment(frc.getCourseId(), frc.getCreatedAt(), frc.getCommentGiver());
        List<FeedbackResponseCommentAttributes> comments =
                frcDb.getFeedbackResponseCommentsForQuestion(frc.getFeedbackQuestionId());
        assertEquals(1, comments.size());

        comments = frcDb.getFeedbackResponseCommentsForQuestion("not_exist");
        assertEquals(0, comments.size());
    }

    @Test
    public void testGetFeedbackResponseCommentsForQuestionInSection_typicalCase_shouldQueryCorrectly() {
        FeedbackResponseCommentAttributes frc = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        frc = getFeedbackResponseComment(frc.getCourseId(), frc.getCreatedAt(), frc.getCommentGiver());
        List<FeedbackResponseCommentAttributes> comments =
                frcDb.getFeedbackResponseCommentsForQuestionInSection(frc.getFeedbackQuestionId(), "Section 1");
        assertEquals(1, comments.size());

        comments = frcDb.getFeedbackResponseCommentsForQuestionInSection(frc.getFeedbackQuestionId(), "not_exist");
        assertEquals(0, comments.size());

        comments = frcDb.getFeedbackResponseCommentsForQuestionInSection("not_exist", "Section 1");
        assertEquals(0, comments.size());

        comments = frcDb.getFeedbackResponseCommentsForQuestionInSection("not_exist", "not_exist");
        assertEquals(0, comments.size());
    }

    private FeedbackResponseCommentAttributes getFeedbackResponseComment(String courseId, Instant createdAt, String giver) {
        return frcDb.getFeedbackResponseCommentForGiver(courseId, giver)
                .stream()
                .filter(frc -> frc.getCreatedAt().equals(createdAt))
                .findFirst()
                .orElse(null);
    }

}
