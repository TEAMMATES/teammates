package teammates.client.scripts;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.jdo.PersistenceManager;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.StudentsDb;
import teammates.storage.datastore.Datastore;

public class RepairTeamNameInStudentAttributesAndResponses extends RemoteApiClient {
    private final boolean isPreview = true;
    
    private StudentsDb studentsDb = new StudentsDb();
    private StudentsLogic studentsLogic = StudentsLogic.inst();
    private FeedbackResponsesLogic responsesLogic = new FeedbackResponsesLogic();
    
    public static void main(String[] args) throws IOException {
        RepairTeamNameInStudentAttributesAndResponses migrator = new RepairTeamNameInStudentAttributesAndResponses();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();
        List<StudentAttributes> allStudents = studentsLogic.getAllStudents();
        @SuppressWarnings("deprecation")
        List<FeedbackResponseAttributes> allResponses = responsesLogic.getAllFeedbackResponses();
        
        if (isPreview) {
            System.out.println("Checking extra spaces in team name...");
        } else {
            System.out.println("Removing extra spaces in team name...");
        }
        
        int numberOfStudentsWithExtraSpacesInTeamName = 0;
        for (StudentAttributes student : allStudents) {
            if (hasExtraSpaces(student.team)) {
                numberOfStudentsWithExtraSpacesInTeamName++;
                if (!isPreview) {
                    removeExtraSpaceForStudent(student);
                } else {
                    System.out.println("" + numberOfStudentsWithExtraSpacesInTeamName 
                                       + ". \"" + student.team + "\"");
                }
            }
        }
        
        int numberOfReponsesWithExtraSpacesInRecipient = 0;
        for (FeedbackResponseAttributes response : allResponses) {
            if (hasExtraSpaces(response.recipientEmail)) {
                numberOfReponsesWithExtraSpacesInRecipient++;
                if (!isPreview) {
                    removeExtraSpaceForResponse(response);
                } else {
                    System.out.println("" + numberOfReponsesWithExtraSpacesInRecipient 
                                       + ". \"" + response.recipientEmail + "\"");
                }
            }
        }
        
        if (isPreview) {
            System.out.println("There are/is " + numberOfReponsesWithExtraSpacesInRecipient 
                               + " student(s) with extra spaces in team name!");
            System.out.println("There are/is " + numberOfStudentsWithExtraSpacesInTeamName 
                                            + " response(s) with extra spaces in recipient!");
        } else {
            System.out.println("" + numberOfReponsesWithExtraSpacesInRecipient 
                               + " student(s) have been fixed!");
            System.out.println("" + numberOfStudentsWithExtraSpacesInTeamName 
                               + " response(s) have been fixed!");
            System.out.println("Extra space removing done!");
        }
    }

    private void removeExtraSpaceForResponse(FeedbackResponseAttributes response) {
        if (hasExtraSpaces(response.recipientEmail)) {
            response.recipientEmail = StringHelper.removeExtraSpace(response.recipientEmail);
            updateResponse(response);
        }
    }

    private void updateResponse(FeedbackResponseAttributes response) {
        try {
            responsesLogic.updateFeedbackResponse(response);
        } catch (InvalidParametersException e) {
            Utils.getLogger().log(Level.INFO, "Response " + response.getId() + " invalid!");
            e.printStackTrace();
        } catch (EntityAlreadyExistsException e) {
            Utils.getLogger().log(Level.INFO, "New Response has already existed!");
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            Utils.getLogger().log(Level.INFO, "Old Response does not exist!");
            e.printStackTrace();
        }
    }

    /**
     * Check if there is extra space in the string.
     */
    private boolean hasExtraSpaces(String s) {
        return !s.equals(StringHelper.removeExtraSpace(s));
    }
    
    private void removeExtraSpaceForStudent(StudentAttributes student) {
        try {
            boolean hasExtraSpaces = hasExtraSpaces(student.team);
            if (hasExtraSpaces) {
                student.team = StringHelper.removeExtraSpace(student.team);
                updateStudent(student.email, student);
            }
        } catch (InvalidParametersException e) {
            Utils.getLogger().log(Level.INFO, "Student " + student.email + " invalid!");
            e.printStackTrace();
        } catch (EntityDoesNotExistException e) {
            Utils.getLogger().log(Level.INFO, "Student " + student.email + " does not exist!");
            e.printStackTrace();
        }
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