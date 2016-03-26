package teammates.client.scripts;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import javax.jdo.PersistenceManager;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Student;

public class RepairTeamNameInStudentResponseAndCommentAttributes extends RemoteApiClient {
    private final boolean isPreview = true;
    
    private StudentsDb studentsDb = new StudentsDb();
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    private FeedbackResponsesLogic responsesLogic = new FeedbackResponsesLogic();
    private CommentsLogic commentsLogic = new CommentsLogic();
    
    
    public static void main(String[] args) throws IOException {
        RepairTeamNameInStudentResponseAndCommentAttributes migrator = new RepairTeamNameInStudentResponseAndCommentAttributes();
        migrator.doOperationRemotely();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void doOperation() {
        Datastore.initialize();
        List<Student> allStudents = studentsDb.getStudentEntities();
        List<FeedbackResponseAttributes> allResponses = responsesLogic.getAllFeedbackResponses();
        List<CommentAttributes> allComments = commentsLogic.getAllComments();
        int totalNumberOfStudents = allStudents.size();
        int totalNumberOfResponses = allResponses.size();
        int totalNumberOfComments = allComments.size();
        
        if (isPreview) {
            System.out.println("Checking extra spaces in team name...");
        } else {
            System.out.println("Removing extra spaces in team name...");
        }
        
        try {
            int numberOfStudentsWithExtraSpacesInTeamName = removeExtraSpacesInStudents(allStudents);
            int numberOfReponsesWithExtraSpacesInRecipient = removeExtraSpacesInResponses(allResponses);
            int numberOfCommentsWithExtraSpacesInRecipient = removeExtraSpacesInComments(allComments);
            
            if (isPreview) {
                System.out.println("There are/is " + numberOfStudentsWithExtraSpacesInTeamName
                                   + "/" + totalNumberOfStudents + " student(s) with extra spaces in team name!");
                System.out.println("There are/is " + numberOfReponsesWithExtraSpacesInRecipient
                                   + "/" + totalNumberOfResponses + " response(s) with extra spaces in recipient!");
                System.out.println("There are/is " + numberOfCommentsWithExtraSpacesInRecipient
                                   + "/" + totalNumberOfComments + " comment(s) with extra spaces in recipient!");
                             
            } else {
                System.out.println("" + numberOfStudentsWithExtraSpacesInTeamName 
                                   + "/" + totalNumberOfStudents + " student(s) have been fixed!");
                System.out.println("" + numberOfReponsesWithExtraSpacesInRecipient 
                                   + "/" + totalNumberOfResponses + " response(s) have been fixed!");
                System.out.println("" + numberOfCommentsWithExtraSpacesInRecipient 
                                   + "/" + totalNumberOfComments + " comment(s) have been fixed!");
                System.out.println("Extra space removing done!");
            }
        } catch (InvalidParametersException | EntityDoesNotExistException | EntityAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    private String extractStringsWithExtraSpace(Set<String> set) {
        StringBuilder result = new StringBuilder();
        for (String s : set) {
            if (hasExtraSpaces(s)) {
                result.append(s + " ");
            }
        }
        return result.toString();
    }
    
    private int removeExtraSpacesInComments(List<CommentAttributes> allComments) 
                    throws InvalidParametersException, EntityDoesNotExistException {
        int numberOfCommentWithExtraSpacesInRecipient = 0;
        for (CommentAttributes comment : allComments) {
            if (hasExtraSpaces(comment.recipients)) {
                numberOfCommentWithExtraSpacesInRecipient++;
                if (isPreview) {
                    String recipientsWithExtraSpace = extractStringsWithExtraSpace(comment.recipients);
                    System.out.println("" + numberOfCommentWithExtraSpacesInRecipient 
                                       + ". \"" + recipientsWithExtraSpace + "\""
                                       + "courseId: " + comment.courseId);
                } else {
                    comment.recipients = StringHelper.removeExtraSpace(comment.recipients);
                    commentsLogic.updateComment(comment);
                }
            }
        }
        return numberOfCommentWithExtraSpacesInRecipient;
    }

    private int removeExtraSpacesInResponses(List<FeedbackResponseAttributes> allResponses) 
                    throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        int numberOfReponsesWithExtraSpacesInRecipient = 0;
        for (FeedbackResponseAttributes response : allResponses) {
            if (hasExtraSpaces(response.recipientEmail)) {
                numberOfReponsesWithExtraSpacesInRecipient++;
                if (isPreview) {
                    System.out.println("" + numberOfReponsesWithExtraSpacesInRecipient 
                                       + ". \"" + response.recipientEmail + "\" "
                                       + "courseId: " + response.courseId + " sessionName: "
                                       + response.feedbackSessionName);
                } else {
                    response.recipientEmail = StringHelper.removeExtraSpace(response.recipientEmail);
                    responsesLogic.updateFeedbackResponse(response);
                }
            }
        }
        return numberOfReponsesWithExtraSpacesInRecipient;
    }

    private int removeExtraSpacesInStudents(List<Student> allStudents) 
                    throws InvalidParametersException, EntityDoesNotExistException {
        int numberOfStudentsWithExtraSpacesInTeamName = 0;
        for (Student studentEntity : allStudents) {
            if (hasExtraSpaces(studentEntity.getTeamName())) {
                numberOfStudentsWithExtraSpacesInTeamName++;
                if (isPreview) {
                    System.out.println("" + numberOfStudentsWithExtraSpacesInTeamName 
                                       + ". \"" + studentEntity.getTeamName() + "\" "
                                       + "courseId: " + studentEntity.getCourseId());
                } else {
                    StudentAttributes student = new StudentAttributes(studentEntity);
                    updateStudent(student.email, student);
                }
            }
        }
        return numberOfStudentsWithExtraSpacesInTeamName;
    }

    /**
     * Check if there is extra space in the string.
     */
    private boolean hasExtraSpaces(String s) {
        return !s.equals(StringHelper.removeExtraSpace(s));
    }
    
    private boolean hasExtraSpaces(Set<String> stringSet) {
        for (String s : stringSet) {
            if (hasExtraSpaces(s)) {
                return true;
            }
        }
        return false;
    }
    
    protected PersistenceManager getPM() {
        return Datastore.getPersistenceManager();
    }
    
    public void updateStudent(String originalEmail, StudentAttributes student) throws InvalidParametersException,
                                                                                      EntityDoesNotExistException {
        studentsDb.verifyStudentExists(student.course, originalEmail);
        StudentAttributes originalStudent = studentsLogic.getStudentForEmail(student.course, originalEmail);
        
        // prepare new student
        student.updateWithExistingRecord(originalStudent);
        
        if (!student.isValid()) {
            throw new InvalidParametersException(student.getInvalidityInfo());
        }
        
        studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.section, student.email, student.googleId, student.comments, true);    
    }
}