package teammates.test.cases.pagedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.ui.pagedata.InstructorFeedbackSessionsPageData;
import teammates.ui.template.FeedbackSessionsCopyFromModal;
import teammates.ui.template.FeedbackSessionsForm;
import teammates.ui.template.FeedbackSessionsTable;
import teammates.ui.template.FeedbackSessionsTableRow;

/**
 * SUT: {@link InstructorFeedbackSessionsPageData}.
 */
public class InstructorFeedbackSessionsPageDataTest extends BaseTestCase {

    private static DataBundle dataBundle = getTypicalDataBundle();
    private static final int NUMBER_OF_HOURS_IN_DAY = 24;

    @Test
    public void testInitWithoutDefaultFormValues() {

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");

        ______TS("typical success case");

        InstructorFeedbackSessionsPageData data = new InstructorFeedbackSessionsPageData(instructorAccount,
                                                                                         dummySessionToken);

        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<>();
        List<InstructorAttributes> instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }

        List<InstructorAttributes> instructorsForUser = new ArrayList<>(courseInstructorMap.values());
        List<CourseAttributes> courses = getCoursesForInstructor(instructorsForUser);

        List<FeedbackSessionAttributes> fsList = getFeedbackSessionsListForInstructor(instructorsForUser);

        data.initWithoutDefaultFormValues(courses, null, fsList, courseInstructorMap, null);

        ______TS("typical success case: test new fs form");
        // Test new fs form model
        FeedbackSessionsForm formModel = data.getNewFsForm();

        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Team peer evaluation session", formModel.getFeedbackSessionTypeOptions().get(1).getContent());
        assertNull(formModel.getFeedbackSessionTypeOptions().get(1).getAttributes().get("selected"));
        assertTrue(formModel.getFeedbackSessionTypeOptions().get(1).getAttributes().containsKey("selected"));
        assertEquals("", formModel.getFsEndDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsEndTimeOptions().size());
        assertEquals("", formModel.getFsName());
        assertEquals("", formModel.getFsStartDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsStartTimeOptions().size());

        assertEquals(7, formModel.getGracePeriodOptions().size());

        int expectedDefaultGracePeriodOptionsIndex = 3;
        assertNull(formModel.getGracePeriodOptions()
                            .get(expectedDefaultGracePeriodOptionsIndex)
                            .getAttributes().get("selected"));
        assertTrue(formModel.getGracePeriodOptions()
                            .get(expectedDefaultGracePeriodOptionsIndex)
                            .getAttributes().containsKey("selected"));

        assertEquals("Please answer all the given questions.", formModel.getInstructions());
        assertEquals("", formModel.getAdditionalSettings().getResponseVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getResponseVisibleTimeOptions().size());
        assertEquals("", formModel.getAdditionalSettings().getSessionVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getSessionVisibleTimeOptions().size());
        assertEquals(40, formModel.getTimezoneSelectField().size());

        assertTrue(formModel.getAdditionalSettings().isResponseVisiblePublishManuallyChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleDateChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleImmediatelyChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleNeverChecked());
        assertTrue(formModel.getAdditionalSettings().isResponseVisibleDateDisabled());

        assertTrue(formModel.getAdditionalSettings().isSessionVisibleAtOpenChecked());
        assertTrue(formModel.getAdditionalSettings().isSessionVisibleDateDisabled());
        assertFalse(formModel.getAdditionalSettings().isSessionVisibleDateButtonChecked());
        assertFalse(formModel.getAdditionalSettings().isSessionVisiblePrivateChecked());

        ______TS("typical success case: session rows");
        FeedbackSessionsTable fsTableModel = data.getFsList();

        List<FeedbackSessionsTableRow> fsRows = fsTableModel.getExistingFeedbackSessions();
        assertEquals(6, fsRows.size());

        String firstFsName = "Grace Period Session";
        assertEquals(firstFsName, fsRows.get(0).getName());
        String lastFsName = "First feedback session";
        assertEquals(lastFsName, fsRows.get(fsRows.size() - 1).getName());

        ______TS("typical success case: copy modal");
        FeedbackSessionsCopyFromModal copyModalModel = data.getCopyFromModal();

        assertEquals(1, copyModalModel.getCoursesSelectField().size());
        assertEquals("", copyModalModel.getFsName());
        assertEquals(6, copyModalModel.getExistingFeedbackSessions().size());

        ______TS("case with instructor with only archived course");
        AccountAttributes instructorOfArchivedCourseAccount = dataBundle.accounts.get("instructorOfArchivedCourse");
        InstructorFeedbackSessionsPageData instructorArchivedCourseData =
                new InstructorFeedbackSessionsPageData(instructorOfArchivedCourseAccount, dummySessionToken);
        Map<String, InstructorAttributes> archivedCourseInstructorMap = new HashMap<>();

        instructors = getInstructorsForGoogleId(instructorOfArchivedCourseAccount.googleId, true);

        for (InstructorAttributes instructor : instructors) {
            archivedCourseInstructorMap.put(instructor.courseId, instructor);
        }

        List<InstructorAttributes> instructorsForArchivedCourse = new ArrayList<>(archivedCourseInstructorMap.values());
        List<CourseAttributes> archivedCourses = getCoursesForInstructor(instructorsForArchivedCourse);
        List<FeedbackSessionAttributes> archivedFsList = getFeedbackSessionsListForInstructor(instructorsForArchivedCourse);
        instructorArchivedCourseData.initWithoutDefaultFormValues(archivedCourses, null, archivedFsList,
                                                                  archivedCourseInstructorMap, null);

        ______TS("case with instructor with only archived course: test new fs form");
        // Test new fs form model
        formModel = instructorArchivedCourseData.getNewFsForm();

        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(Const.StatusMessages.INSTRUCTOR_NO_ACTIVE_COURSES,
                     formModel.getCoursesSelectField().get(0).getContent());

        assertTrue(formModel.isSubmitButtonDisabled());

        ______TS("case with instructor with restricted permissions");
        AccountAttributes helperAccount = dataBundle.accounts.get("helperOfCourse1");

        InstructorFeedbackSessionsPageData helperData = new InstructorFeedbackSessionsPageData(helperAccount,
                                                                                               dummySessionToken);

        Map<String, InstructorAttributes> helperCourseInstructorMap = new HashMap<>();
        instructors = getInstructorsForGoogleId(helperAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            helperCourseInstructorMap.put(instructor.courseId, instructor);
        }

        List<InstructorAttributes> instructorsForHelper = new ArrayList<>(helperCourseInstructorMap.values());
        List<CourseAttributes> helperCourses = getCoursesForInstructor(instructorsForHelper);

        List<FeedbackSessionAttributes> helperFsList = getFeedbackSessionsListForInstructor(instructorsForHelper);

        helperData.initWithoutDefaultFormValues(helperCourses, null, helperFsList, helperCourseInstructorMap, null);

        ______TS("case with instructor with restricted permissions: test new fs form");
        // Test new fs form model
        formModel = helperData.getNewFsForm();

        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(Const.StatusMessages.INSTRUCTOR_NO_MODIFY_PERMISSION_FOR_ACTIVE_COURSES_SESSIONS,
                     formModel.getCoursesSelectField().get(0).getContent());

        assertTrue(formModel.isSubmitButtonDisabled());

        ______TS("case with instructor with restricted permissions: session rows");
        fsTableModel = helperData.getFsList();

        fsRows = fsTableModel.getExistingFeedbackSessions();
        assertEquals(6, fsRows.size());

        ______TS("case with instructor with restricted permissions: copy modal");
        copyModalModel = helperData.getCopyFromModal();

        assertEquals(1, copyModalModel.getCoursesSelectField().size());
        assertEquals("", copyModalModel.getFsName());
        assertEquals(0, copyModalModel.getExistingFeedbackSessions().size());

        ______TS("case with highlighted session in session table");

        instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");

        data = new InstructorFeedbackSessionsPageData(instructorAccount, dummySessionToken);

        courseInstructorMap = new HashMap<>();
        instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }

        instructorsForUser = new ArrayList<>(courseInstructorMap.values());
        courses = getCoursesForInstructor(instructorsForUser);

        fsList = getFeedbackSessionsListForInstructor(instructorsForUser);

        data.initWithoutDefaultFormValues(courses, "idOfTypicalCourse1", fsList, courseInstructorMap,
                                          "First feedback session");

        List<FeedbackSessionsTableRow> sessionRows = data.getFsList().getExistingFeedbackSessions();
        boolean isFirstFeedbackSessionHighlighted = false;
        boolean isOtherFeedbackSessionHighlighted = false;
        for (FeedbackSessionsTableRow row : sessionRows) {
            if ("First feedback session".equals(row.getName())) {
                isFirstFeedbackSessionHighlighted =
                        row.getRowAttributes().getAttributes().get("class").matches(".*\\bwarning\\b.*");
            } else {
                if (row.getRowAttributes().getAttributes().get("class").matches(".*\\bwarning\\b.*")) {
                    isOtherFeedbackSessionHighlighted = true;
                }
            }
        }
        assertTrue(isFirstFeedbackSessionHighlighted);
        assertFalse(isOtherFeedbackSessionHighlighted);

    }

    @Test
    public void testInit() {

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");

        ______TS("typical success case with existing fs passed in");

        InstructorFeedbackSessionsPageData data = new InstructorFeedbackSessionsPageData(instructorAccount,
                                                                                         dummySessionToken);

        Map<String, InstructorAttributes> courseInstructorMap = new HashMap<>();
        List<InstructorAttributes> instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }

        List<InstructorAttributes> instructorsForUser = new ArrayList<>(courseInstructorMap.values());
        List<CourseAttributes> courses = getCoursesForInstructor(instructorsForUser);

        List<FeedbackSessionAttributes> fsList = getFeedbackSessionsListForInstructor(instructorsForUser);

        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");

        data.init(courses, null, fsList, courseInstructorMap, fsa, null, null);

        ______TS("typical success case with existing fs passed in: test new fs form");
        // Test new fs form model
        FeedbackSessionsForm formModel = data.getNewFsForm();

        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Team peer evaluation session", formModel.getFeedbackSessionTypeOptions().get(1).getContent());
        assertNull(formModel.getFeedbackSessionTypeOptions().get(1).getAttributes().get("selected"));
        assertTrue(formModel.getFeedbackSessionTypeOptions().get(1).getAttributes().containsKey("selected"));

        assertEquals("Fri, 30 Apr, 2027", formModel.getFsEndDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsEndTimeOptions().size());
        assertEquals("First feedback session", formModel.getFsName());

        assertEquals("Sun, 01 Apr, 2012", formModel.getFsStartDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsStartTimeOptions().size());

        assertEquals(7, formModel.getGracePeriodOptions().size());

        int expectedDefaultGracePeriodOptionsIndex = 2;
        assertNull(formModel.getGracePeriodOptions()
                            .get(expectedDefaultGracePeriodOptionsIndex)
                            .getAttributes().get("selected"));
        assertTrue(formModel.getGracePeriodOptions()
                            .get(expectedDefaultGracePeriodOptionsIndex)
                            .getAttributes().containsKey("selected"));

        assertEquals("Please please fill in the following questions.", formModel.getInstructions());
        assertEquals("Sat, 01 May, 2027", formModel.getAdditionalSettings().getResponseVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getResponseVisibleTimeOptions().size());
        assertEquals("Wed, 28 Mar, 2012", formModel.getAdditionalSettings().getSessionVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getSessionVisibleTimeOptions().size());

        assertFalse(formModel.getAdditionalSettings().isResponseVisiblePublishManuallyChecked());
        assertTrue(formModel.getAdditionalSettings().isResponseVisibleDateChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleImmediatelyChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleNeverChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleDateDisabled());

        assertFalse(formModel.getAdditionalSettings().isSessionVisibleAtOpenChecked());
        assertFalse(formModel.getAdditionalSettings().isSessionVisibleDateDisabled());
        assertTrue(formModel.getAdditionalSettings().isSessionVisibleDateButtonChecked());
        assertFalse(formModel.getAdditionalSettings().isSessionVisiblePrivateChecked());

        ______TS("typical success case with existing fs passed in: session rows");
        FeedbackSessionsTable fsTableModel = data.getFsList();

        List<FeedbackSessionsTableRow> fsRows = fsTableModel.getExistingFeedbackSessions();
        assertEquals(6, fsRows.size());

        String firstFsName = "Grace Period Session";
        assertEquals(firstFsName, fsRows.get(0).getName());
        String lastFsName = "First feedback session";
        assertEquals(lastFsName, fsRows.get(fsRows.size() - 1).getName());

        ______TS("typical success case with existing fs passed in: copy modal");
        FeedbackSessionsCopyFromModal copyModalModel = data.getCopyFromModal();

        assertEquals(1, copyModalModel.getCoursesSelectField().size());
        assertEquals("First feedback session", copyModalModel.getFsName());
        assertEquals(6, copyModalModel.getExistingFeedbackSessions().size());
    }

    @Test
    public void testInitWithoutHighlighting() {

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor2OfCourse1");

        ______TS("typical success case with existing fs passed in");

        InstructorFeedbackSessionsPageData data = new InstructorFeedbackSessionsPageData(instructorAccount,
                                                                                         dummySessionToken);

        Map<String, InstructorAttributes> courseInstructorMap = new HashMap<>();
        List<InstructorAttributes> instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }

        List<InstructorAttributes> instructorsForUser = new ArrayList<>(courseInstructorMap.values());
        List<CourseAttributes> courses = getCoursesForInstructor(instructorsForUser);

        List<FeedbackSessionAttributes> fsList = getFeedbackSessionsListForInstructor(instructorsForUser);

        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");

        data.initWithoutHighlightedRow(courses, "idOfTypicalCourse1", fsList, courseInstructorMap, fsa, "STANDARD");

        FeedbackSessionsForm formModel = data.getNewFsForm();

        assertEquals("idOfTypicalCourse1", formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Session with your own questions", formModel.getFeedbackSessionTypeOptions().get(0).getContent());
        assertNull(formModel.getFeedbackSessionTypeOptions().get(0).getAttributes().get("selected"));
        assertTrue(formModel.getFeedbackSessionTypeOptions().get(0).getAttributes().containsKey("selected"));

        FeedbackSessionsCopyFromModal modal = data.getCopyFromModal();
        assertEquals("First feedback session", modal.getFsName());

    }

    private List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean isOmitArchived) {
        List<InstructorAttributes> instructors = new ArrayList<>(dataBundle.instructors.values());

        instructors.removeIf(instructor -> {
            boolean isGoogleIdSame = instructor.googleId != null
                    && instructor.googleId.equals(googleId);
            boolean isOmittedDueToArchiveStatus = isOmitArchived
                    && instructor.isArchived != null
                    && instructor.isArchived;
            return !isGoogleIdSame || isOmittedDueToArchiveStatus;
        });

        return instructors;
    }

    private List<CourseAttributes> getCoursesForInstructor(List<InstructorAttributes> instructorsForUser) {
        Set<String> courseIdsOfUser = getSetOfCourseIdsFromInstructorAttributes(instructorsForUser);

        List<CourseAttributes> courses = new ArrayList<>(dataBundle.courses.values());

        courses.removeIf(course -> !courseIdsOfUser.contains(course.getId()));

        return courses;
    }

    private List<FeedbackSessionAttributes>
            getFeedbackSessionsListForInstructor(List<InstructorAttributes> instructorsForUser) {
        Set<String> courseIdsOfUser = getSetOfCourseIdsFromInstructorAttributes(instructorsForUser);

        List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<>(dataBundle.feedbackSessions.values());

        feedbackSessions.removeIf(fs -> !courseIdsOfUser.contains(fs.getCourseId()));

        return feedbackSessions;
    }

    private Set<String> getSetOfCourseIdsFromInstructorAttributes(
                                    List<InstructorAttributes> instructorsForUser) {
        Set<String> courseIdsOfUser = new HashSet<>();
        for (InstructorAttributes instructor : instructorsForUser) {
            courseIdsOfUser.add(instructor.courseId);
        }
        return courseIdsOfUser;
    }

}
