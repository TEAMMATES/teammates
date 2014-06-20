package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentStatus;
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
        
        commentsDb.createEntity(comment);
    }

    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForGiver(courseId, giverEmail);
    }
    
    public List<CommentAttributes> getCommentsForGiverAndStatus(String courseId, String giverEmail, CommentStatus status)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForGiverAndStatus(courseId, giverEmail, status);
    }
    
    public List<CommentAttributes> getCommentDrafts(String giverEmail)
            throws EntityDoesNotExistException {
        return commentsDb.getCommentDrafts(giverEmail);
    }

    public List<CommentAttributes> getCommentsForReceiver(String courseId, CommentRecipientType recipientType, String receiverEmail)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForReceiver(courseId, recipientType, receiverEmail);
    }
    
    public List<CommentAttributes> getCommentsForCommentViewer(String courseId, CommentRecipientType commentViewerType)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(courseId);
        
        return commentsDb.getCommentsForCommentViewer(courseId, commentViewerType);
    }
    
    public void updateComment(CommentAttributes comment)
            throws InvalidParametersException, EntityDoesNotExistException{
        verifyIsCoursePresentForUpdateComments(comment.courseId);
        commentsDb.updateComment(comment);
    }
    
    public void deleteComment(CommentAttributes comment){
        commentsDb.deleteEntity(comment);
    }
    
    public List<CommentAttributes> getCommentsForStudent(StudentAttributes student)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(student.course);
        
        List<StudentAttributes> teammates = studentsLogic.getStudentsForTeam(student.team, student.course);
        List<String> teammatesEmails = getTeammatesEmails(teammates);

        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        
        List<CommentAttributes> commentsForStudent = getCommentsForReceiver(student.course, CommentRecipientType.PERSON, student.email);
        removeNonVisibleCommentsForStudent(commentsForStudent, commentsVisitedSet, comments);
        
        List<CommentAttributes> commentsForTeam = getCommentsForCommentViewer(student.course, CommentRecipientType.TEAM);
        removeNonVisibleCommentsForTeam(commentsForTeam, student, teammatesEmails, commentsVisitedSet, comments);
        
        //TODO: handle comments for section
        
        List<CommentAttributes> commentsForCourse = getCommentsForCommentViewer(student.course, CommentRecipientType.COURSE);
        removeNonVisibleCommentsForCourse(commentsForCourse, student, commentsVisitedSet, comments);
        
        java.util.Collections.sort(comments);
        
        return comments;
    }
    
    public List<CommentAttributes> getCommentsForInstructor(InstructorAttributes instructor)
            throws EntityDoesNotExistException {
        verifyIsCoursePresentForGetComments(instructor.courseId);
        verifyIsInstructorOfCourse(instructor.courseId, instructor.email);
        HashSet<String> commentsVisitedSet = new HashSet<String>();
        
        List<CommentAttributes> comments = getCommentsForGiverAndStatus(instructor.courseId, instructor.email, CommentStatus.FINAL);
        for(CommentAttributes c: comments){
            commentsVisitedSet.add(c.getCommentId().toString());
        }
        
        List<CommentAttributes> commentsForOtherInstructor = getCommentsForCommentViewer(instructor.courseId, CommentRecipientType.INSTRUCTOR);
        removeNonVisibleCommentsForInstructor(commentsForOtherInstructor, commentsVisitedSet, comments);
        
        java.util.Collections.sort(comments);
        
        return comments;
    }
    
    private void removeNonVisibleCommentsForInstructor(
            List<CommentAttributes> commentsForInstructor,
            HashSet<String> commentsVisitedSet, List<CommentAttributes> comments) {
        Iterator<CommentAttributes> iter = commentsForInstructor.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(!c.showCommentTo.contains(CommentRecipientType.INSTRUCTOR)){
                iter.remove();
            } else {
                removeGiverAndRecipientNameByVisibilityOptions(c, CommentRecipientType.INSTRUCTOR);
                appendCommentsFrom(c, comments, commentsVisitedSet);
            }
        }
    }

    private List<String> getTeammatesEmails(List<StudentAttributes> teammates) {
        List<String> teammatesEmails = new ArrayList<String>();
        for(StudentAttributes teammate : teammates){
            teammatesEmails.add(teammate.email);
        }
        return teammatesEmails;
    }

    private void removeNonVisibleCommentsForCourse(
            List<CommentAttributes> commentsForCourse, StudentAttributes student, HashSet<String> commentsVisitedSet,
            List<CommentAttributes> comments) {
        Iterator<CommentAttributes> iter = commentsForCourse.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(!c.courseId.equals(student.course)){
                iter.remove();
            } else {
                if(c.recipientType != CommentRecipientType.COURSE){
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentRecipientType.COURSE);
                } else {
                    removeGiverNameByVisibilityOptions(c, CommentRecipientType.COURSE);
                }
                appendCommentsFrom(c, comments, commentsVisitedSet);
            }
        }
    }
    
    private void removeNonVisibleCommentsForTeam(List<CommentAttributes> commentsForTeam,
            StudentAttributes student, List<String> teammates, HashSet<String> commentsVisitedSet,
            List<CommentAttributes> comments) {
        Iterator<CommentAttributes> iter = commentsForTeam.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(c.recipientType == CommentRecipientType.PERSON){
                boolean isToRemove = true;
                for(String recipient : c.recipients){
                    if(teammates.contains(recipient)){
                        isToRemove = false;
                        break;
                    }
                }
                if(isToRemove){
                    iter.remove();
                } else {
                    removeGiverAndRecipientNameByVisibilityOptions(c, CommentRecipientType.TEAM);
                    appendCommentsFrom(c, comments, commentsVisitedSet);
                }
            } else if(c.recipientType == CommentRecipientType.TEAM){
                if(!c.recipients.contains(student.team)){
                    iter.remove();
                } else{
                    removeGiverNameByVisibilityOptions(c, CommentRecipientType.TEAM);
                    appendCommentsFrom(c, comments, commentsVisitedSet);
                }
            }
        }
    }

    private void removeNonVisibleCommentsForStudent(List<CommentAttributes> commentsForStudent, HashSet<String> commentsVisitedSet,
            List<CommentAttributes> comments){
        Iterator<CommentAttributes> iter = commentsForStudent.iterator();
        while(iter.hasNext()){
            CommentAttributes c = iter.next();
            if(!c.showCommentTo.contains(CommentRecipientType.PERSON)){
                iter.remove();
            } else {
                removeGiverNameByVisibilityOptions(c, CommentRecipientType.PERSON);
                appendCommentsFrom(c, comments, commentsVisitedSet);
            }
        }
    }
    
    private void removeGiverNameByVisibilityOptions(CommentAttributes c, CommentRecipientType viewerType) {
        if (!c.showGiverNameTo.contains(viewerType)){
            c.giverEmail = "Anonymous";
        }
    }

    private void removeGiverAndRecipientNameByVisibilityOptions(CommentAttributes c, CommentRecipientType viewerType) {
        removeGiverNameByVisibilityOptions(c, viewerType);
        if(!c.showRecipientNameTo.contains(viewerType)){
            c.recipients = new HashSet<String>();
            c.recipients.add("Anonymous");
        }
    }
    
    private void appendCommentsFrom(CommentAttributes c, List<CommentAttributes> thatCommentList, HashSet<String> commentsVisitedSet){
        if(!commentsVisitedSet.contains(c.getCommentId().toString())){
            thatCommentList.add(c);
            commentsVisitedSet.add(c.getCommentId().toString());
        }
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
    
    private void verifyIsCoursePresentForUpdateComments(String courseId)
            throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to update comments for a course that does not exist.");
        }
    }
    
    private void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException{
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if(instructor == null){
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered instructor for course "+ courseId + ".");
        }
    }
}
