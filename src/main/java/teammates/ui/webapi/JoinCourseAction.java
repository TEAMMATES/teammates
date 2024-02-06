package teammates.ui.webapi;

import java.util.Optional;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * Action: joins a course for a student/instructor.
 */
public class JoinCourseAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Any user can use a join link as long as its parameters are valid
    }

    @Override
    public JsonResult execute() throws InvalidOperationException {
        String regKey = getNonNullRequestParamValue(Const.ParamsNames.REGKEY);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        String courseId = getCourseId(regKey, entityType);

        // courseId is null when the registration key does not exist, this case is handled in the AccountsLogic.
        // Hence default to not migrated. Getting the courseId in the action layer is not needed once migration is done.
        if (courseId == null || !isCourseMigrated(courseId)) {
            switch (entityType) {
            case Const.EntityType.STUDENT:
                return joinCourseForStudentDatastore(regKey);
            case Const.EntityType.INSTRUCTOR:
                return joinCourseForInstructorDatastore(regKey);
            default:
                throw new InvalidHttpParameterException("Error: invalid entity type");
            }
        }

        switch (entityType) {
        case Const.EntityType.STUDENT:
            return joinCourseForStudent(regKey);
        case Const.EntityType.INSTRUCTOR:
            return joinCourseForInstructor(regKey);
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
    }

    private JsonResult joinCourseForStudent(String regkey) throws InvalidOperationException {
        Student student;

        try {
            student = sqlLogic.joinCourseForStudent(regkey, userInfo.id);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        } catch (InvalidParametersException ipe) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", ipe);
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmail(student.getCourseId(), student.getName(), student.getEmail(), false);

        return new JsonResult("Student successfully joined course");
    }

    private JsonResult joinCourseForInstructor(String regkey) throws InvalidOperationException {
        Instructor instructor;

        try {
            instructor = sqlLogic.joinCourseForInstructor(regkey, userInfo.id);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        } catch (InvalidParametersException ipe) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", ipe);
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmail(instructor.getCourseId(), instructor.getName(), instructor.getEmail(), true);

        return new JsonResult("Instructor successfully joined course");
    }

    private JsonResult joinCourseForStudentDatastore(String regkey) throws InvalidOperationException {
        StudentAttributes student;

        try {
            student = logic.joinCourseForStudent(regkey, userInfo.id);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        } catch (InvalidParametersException ipe) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", ipe);
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmailDatastore(student.getCourse(), student.getName(), student.getEmail(), false);

        return new JsonResult("Student successfully joined course");
    }

    private JsonResult joinCourseForInstructorDatastore(String regkey) throws InvalidOperationException {
        InstructorAttributes instructor;

        try {
            instructor = logic.joinCourseForInstructor(regkey, userInfo.id);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        } catch (EntityAlreadyExistsException eaee) {
            throw new InvalidOperationException(eaee);
        } catch (InvalidParametersException ipe) {
            // There should not be any invalid parameter here
            log.severe("Unexpected error", ipe);
            return new JsonResult(ipe.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }

        sendJoinEmailDatastore(instructor.getCourseId(), instructor.getName(), instructor.getEmail(), true);

        return new JsonResult("Instructor successfully joined course");
    }

    private void sendJoinEmailDatastore(String courseId, String userName, String userEmail, boolean isInstructor) {
        CourseAttributes course = logic.getCourse(courseId);
        EmailWrapper email = emailGenerator.generateUserCourseRegisteredEmail(
                userName, userEmail, userInfo.id, isInstructor, course);
        emailSender.sendEmail(email);
    }

    private void sendJoinEmail(String courseId, String userName, String userEmail, boolean isInstructor) {
        Course course = sqlLogic.getCourse(courseId);
        EmailWrapper email = sqlEmailGenerator.generateUserCourseRegisteredEmail(
                userName, userEmail, userInfo.id, isInstructor, course);
        emailSender.sendEmail(email);
    }

    private String getCourseId(String regKey, String entityType) {
        String courseIdSql;
        String courseIdDatastore;
        switch (entityType) {
        case Const.EntityType.STUDENT:
            courseIdSql = Optional.ofNullable(sqlLogic.getStudentByRegistrationKey(regKey))
                                            .map(Student::getCourseId)
                                            .orElse(null);
            courseIdDatastore = Optional.ofNullable(logic.getStudentForRegistrationKey(regKey))
                                                .map(StudentAttributes::getCourse)
                                                .orElse(null);
            break;
        case Const.EntityType.INSTRUCTOR:
            courseIdSql = Optional.ofNullable(sqlLogic.getInstructorByRegistrationKey(regKey))
                                            .map(Instructor::getCourseId)
                                            .orElse(null);
            courseIdDatastore = Optional.ofNullable(logic.getInstructorForRegistrationKey(regKey))
                                                .map(InstructorAttributes::getCourseId)
                                                .orElse(null);
            break;
        default:
            throw new InvalidHttpParameterException("Error: invalid entity type");
        }
        return courseIdDatastore != null ? courseIdDatastore : courseIdSql;
    }
}
