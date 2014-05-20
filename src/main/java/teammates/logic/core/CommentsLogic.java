package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.CommentsDb;

public class CommentsLogic {

    private static CommentsLogic instance;

    @SuppressWarnings("unused") //used by test
    private static final Logger log = Utils.getLogger();

    private static final CommentsDb commentsDb = new CommentsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    public static CommentsLogic inst() {
        if (instance == null)
            instance = new CommentsLogic();
        return instance;
    }

    public void createComment(CommentAttributes comment)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        verifyIsCoursePresentForCreateComment(comment.courseId);
        verifyIsInstructorOfCourse(comment.courseId, comment.giverEmail);
        verifyIsStudentOfCourse(comment.courseId, comment.receiverEmail);
        
        commentsDb.createEntity(comment);
    }

    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForGiver(courseId, giverEmail);
    }

    public List<CommentAttributes> getCommentsForReceiver(String courseId, String receiverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForReceiver(courseId, receiverEmail);
    }

    public List<CommentAttributes> getCommentsForGiverAndReceiver(
            String courseId, String giverEmail, String receiverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForGiverAndReceiver(courseId, giverEmail,
                receiverEmail);
    }
    
    public void updateComment(CommentAttributes comment)
            throws InvalidParametersException, EntityDoesNotExistException{
        commentsDb.updateComment(comment);
    }
    
    public void deleteComment(CommentAttributes comment){
        commentsDb.deleteEntity(comment);
    }
    
    private void verifyIsCoursePresentForCreateComment(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to create comments for a course that does not exist.");
        }
    }
    
    private void verifyIsCoursePresentForGetComments(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to get comments for a course that does not exist.");
        }
    }
    
    private void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException{
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if(instructor == null){
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered instructor for course "+ courseId + ".");
        }
    }
    
    private void verifyIsStudentOfCourse(String courseId, String email) throws EntityDoesNotExistException{
        StudentAttributes student = studentsLogic.getStudentForEmail(courseId, email);
        if(student == null){
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered student for course "+ courseId + ".");
        }
    }
}
