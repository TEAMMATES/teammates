package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.api.CoursesDb;

/**
 * Handles operations related to courses.
 *
 * @see CourseAttributes
 * @see CoursesDb
 */
public final class CoursesLogic {

    private static final Logger log = Logger.getLogger();

    private static CoursesLogic instance = new CoursesLogic();

    /* Explanation: This class depends on CoursesDb class but no other *Db classes.
     * That is because reading/writing entities from/to the datastore is the
     * responsibility of the matching *Logic class.
     * However, this class can talk to other *Logic classes. That is because
     * the logic related to one entity type can involve the logic related to
     * other entity types.
     */

    private static final CoursesDb coursesDb = new CoursesDb();

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private CoursesLogic() {
        // prevent initialization
    }

    public static CoursesLogic inst() {
        return instance;
    }

    /**
     * Creates a course.
     *
     * @return the created course
     * @throws InvalidParametersException if the course is not valid
     * @throws EntityAlreadyExistsException if the course already exists in the Datastore.
     */
    CourseAttributes createCourse(CourseAttributes courseToCreate)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesDb.createEntity(courseToCreate);
    }

    /**
     * Creates a course and an associated instructor for the course.
     *
     * <br/>Preconditions: <br/>
     * * {@code instructorGoogleId} already has an account and instructor privileges.
     */
    public void createCourseAndInstructor(String instructorGoogleId, CourseAttributes courseToCreate)
            throws InvalidParametersException, EntityAlreadyExistsException {

        AccountAttributes courseCreator = accountsLogic.getAccount(instructorGoogleId);
        Assumption.assertNotNull(
                "Trying to create a course for a non-existent instructor :" + instructorGoogleId, courseCreator);
        Assumption.assertTrue(
                "Trying to create a course for a person who doesn't have instructor privileges :" + instructorGoogleId,
                courseCreator.isInstructor());

        CourseAttributes createdCourse = createCourse(courseToCreate);

        /* Create the initial instructor for the course */
        InstructorPrivileges privileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorAttributes instructor = InstructorAttributes
                .builder(createdCourse.getId(), courseCreator.getEmail())
                .withName(courseCreator.getName())
                .withGoogleId(instructorGoogleId)
                .withPrivileges(privileges)
                .build();

        try {
            instructorsLogic.createInstructor(instructor);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            // roll back the transaction
            coursesDb.deleteCourse(createdCourse.getId());
            String errorMessage = "Unexpected exception while trying to create instructor for a new course "
                                  + System.lineSeparator() + instructor.toString() + System.lineSeparator()
                                  + TeammatesException.toStringWithStackTrace(e);
            Assumption.fail(errorMessage);
        }
    }

    /**
     * Gets the course with the specified ID.
     */
    public CourseAttributes getCourse(String courseId) {
        return coursesDb.getCourse(courseId);
    }

    /**
     * Returns true if the course with ID courseId is present.
     */
    public boolean isCoursePresent(String courseId) {
        return coursesDb.getCourse(courseId) != null;
    }

    /**
     * Used to trigger an {@link EntityDoesNotExistException} if the course is not present.
     */
    void verifyCourseIsPresent(String courseId) throws EntityDoesNotExistException {
        if (!isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException("Course does not exist: " + courseId);
        }
    }

    /**
     * Returns a list of section names for the course with valid ID courseId.
     *
     * @param courseId Course ID of the course
     */
    public List<String> getSectionsNameForCourse(String courseId) throws EntityDoesNotExistException {
        verifyCourseIsPresent(courseId);

        List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(courseId);

        Set<String> sectionNameSet = new HashSet<>();
        for (StudentAttributes sd : studentDataList) {
            if (!sd.section.equals(Const.DEFAULT_SECTION)) {
                sectionNameSet.add(sd.section);
            }
        }

        List<String> sectionNameList = new ArrayList<>(sectionNameSet);
        sectionNameList.sort(null);

        return sectionNameList;
    }

    /**
     * Returns team names for a particular courseId.
     *
     * <p>Note: This method does not returns any Loner information presently.
     * Loner information must be returned as we decide to support loners in future.
     */
    public List<String> getTeamsForCourse(String courseId) throws EntityDoesNotExistException {

        if (getCourse(courseId) == null) {
            throw new EntityDoesNotExistException("The course " + courseId + " does not exist");
        }

        return studentsLogic.getStudentsForCourse(courseId)
                .stream()
                .map(StudentAttributes::getTeam)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of {@link CourseAttributes} for all courses a given student is enrolled in.
     *
     * @param googleId The Google ID of the student
     */
    public List<CourseAttributes> getCoursesForStudentAccount(String googleId) {
        List<StudentAttributes> studentDataList = studentsLogic.getStudentsForGoogleId(googleId);

        List<String> courseIds = studentDataList.stream()
                .filter(student -> !getCourse(student.course).isCourseDeleted())
                .map(StudentAttributes::getCourse)
                .collect(Collectors.toList());

        return coursesDb.getCourses(courseIds);
    }

    /**
     * Returns a list of {@link CourseAttributes} for all courses for a given list of instructors
     * except for courses in Recycle Bin.
     */
    public List<CourseAttributes> getCoursesForInstructor(List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(instructorList);

        List<String> courseIdList = instructorList.stream()
                .filter(instructor -> !coursesDb.getCourse(instructor.courseId).isCourseDeleted())
                .map(InstructorAttributes::getCourseId)
                .collect(Collectors.toList());

        List<CourseAttributes> courseList = coursesDb.getCourses(courseIdList);

        // Check that all courseIds queried returned a course.
        if (courseIdList.size() > courseList.size()) {
            for (CourseAttributes ca : courseList) {
                courseIdList.remove(ca.getId());
            }
            log.severe("Course(s) was deleted but the instructor still exists: " + System.lineSeparator()
                    + courseIdList.toString());
        }

        return courseList;
    }

    /**
     * Returns a list of {@link CourseAttributes} for soft-deleted courses for a given list of instructors.
     */
    public List<CourseAttributes> getSoftDeletedCoursesForInstructors(List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(instructorList);

        List<String> softDeletedCourseIdList = instructorList.stream()
                .filter(instructor -> coursesDb.getCourse(instructor.courseId).isCourseDeleted())
                .map(InstructorAttributes::getCourseId)
                .collect(Collectors.toList());

        List<CourseAttributes> softDeletedCourseList = coursesDb.getCourses(softDeletedCourseIdList);

        if (softDeletedCourseIdList.size() > softDeletedCourseList.size()) {
            for (CourseAttributes ca : softDeletedCourseList) {
                softDeletedCourseIdList.remove(ca.getId());
            }
            log.severe("Course(s) was deleted but the instructor still exists: " + System.lineSeparator()
                    + softDeletedCourseIdList.toString());
        }

        return softDeletedCourseList;
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * <p>If the {@code timezone} of the course is changed, cascade the change to its corresponding feedback sessions.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public CourseAttributes updateCourseCascade(CourseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        CourseAttributes oldCourse = coursesDb.getCourse(updateOptions.getCourseId());
        CourseAttributes updatedCourse = coursesDb.updateCourse(updateOptions);

        if (!updatedCourse.getTimeZone().equals(oldCourse.getTimeZone())) {
            feedbackSessionsLogic
                    .updateFeedbackSessionsTimeZoneForCourse(updatedCourse.getId(), updatedCourse.getTimeZone());
        }

        return updatedCourse;
    }

    /**
     * Deletes a course cascade its students, instructors, sessions, responses and comments.
     *
     * <p>Fails silently if no such course.
     */
    public void deleteCourseCascade(String courseId) {
        if (getCourse(courseId) == null) {
            return;
        }

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build();
        frcLogic.deleteFeedbackResponseComments(query);
        frLogic.deleteFeedbackResponses(query);
        fqLogic.deleteFeedbackQuestions(query);
        feedbackSessionsLogic.deleteFeedbackSessions(query);
        studentsLogic.deleteStudents(query);
        instructorsLogic.deleteInstructors(query);

        coursesDb.deleteCourse(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     * @return the time when the course is moved to the recycle bin
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {

        return coursesDb.softDeleteCourse(courseId);
    }

    /**
     * Restores a course from Recycle Bin by its given corresponding ID.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        coursesDb.restoreDeletedCourse(courseId);
    }

}
