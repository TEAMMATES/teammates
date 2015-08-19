package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.datastore.Datastore;

/**
 * Script to go through either every feedback session, or a specified feedback session,
 * and verifies that the non-respondents do not have a response in the feedback session
 * 
 * If isPreview is false, whenever an inconsistency is found, logic.updateRespondants will
 * be used to recompute the respondents' set.
 * 
 */
public class RepairFeedbackSessionResponseRate extends RemoteApiClient {

    private Logic logic = new Logic();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    
    // if isPreview is true, then no writes will be done 
    private boolean isPreview = false;
    
    // if either of courseId or feedbackSessionName is null,
    // then all feedback sessions will be checked
    // otherwise the feedback session specified will be operated upon.
    private String courseId = null;
    private String feedbackSessionName = null;
    
    private Map<String, Set<String>> emailsInCourse = new HashMap<>();
    
    
    public static void main(String[] args) throws IOException {
        RepairFeedbackSessionResponseRate migrator = new RepairFeedbackSessionResponseRate();
        migrator.doOperationRemotely();
    }
    
    @Override
    protected void doOperation() {
        Datastore.initialize();
        
        List<FeedbackSessionAttributes> feedbackSessions;
        if (courseId == null || feedbackSessionName == null) {
            feedbackSessions = getAllFeedbackSessions();
        } else {
            feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
            feedbackSessions.add(logic.getFeedbackSession(feedbackSessionName, courseId));
        }
        
        try {
            for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
                System.out.println(feedbackSession.getIdentificationString());
                
                Set<String> nonRespondants = getNonRespondentsForFeedbackSession(feedbackSession);
                findAndFixInconsistentNonRespondentList(feedbackSession, nonRespondants);
            }
        } catch (EntityDoesNotExistException | InvalidParametersException e) {
            e.printStackTrace();
        }
    }

    private void findAndFixInconsistentNonRespondentList(FeedbackSessionAttributes feedbackSession,
                                    Set<String> nonRespondants) throws EntityDoesNotExistException,
                                    InvalidParametersException {
        boolean isRepairRequired = false;
        for (String nonRespondentEmail : nonRespondants) {
            boolean isRespondentWithResponses = logic.hasGiverRespondedForSession(
                                                        nonRespondentEmail, 
                                                        feedbackSession.feedbackSessionName, 
                                                        feedbackSession.courseId);
            if (isRespondentWithResponses) {
                System.out.println("Inconsistent data for " + feedbackSession.getIdentificationString() 
                                 + nonRespondentEmail);
                isRepairRequired = true;
            } 
        }
        
        if (!isPreview && isRepairRequired) {
            System.out.println("fixing " + feedbackSession.getIdentificationString());
            logic.updateRespondants(feedbackSession.feedbackSessionName, feedbackSession.courseId);
        }
    }

    
    private Set<String> getNonRespondentsForFeedbackSession(
                                    FeedbackSessionAttributes feedbackSession) throws EntityDoesNotExistException {
        
        // obtain the respondents first
        Set<String> respondingStudentsEmail = feedbackSession.respondingStudentList;                
        Set<String> respondingInstructorsEmail = feedbackSession.respondingInstructorList;
        
        Set<String> respondents = new HashSet<>(respondingInstructorsEmail);
        respondents.addAll(respondingStudentsEmail);
        
        
        Set<String> nonRespondentsEmails;
        // obtain emails of every student and instructor in the course
        if (emailsInCourse.containsKey(feedbackSession.courseId)) {
            nonRespondentsEmails = emailsInCourse.get(feedbackSession.courseId);
        } else {
            List<InstructorAttributes> allInstructors = 
                                            logic.getInstructorsForCourse(feedbackSession.courseId);
            List<StudentAttributes> allStudents = 
                                            logic.getStudentsForCourse(feedbackSession.courseId);
            List<EntityAttributes> allPossibleRespondents = new ArrayList<>();
            allPossibleRespondents.addAll(allInstructors);
            allPossibleRespondents.addAll(allStudents);
            
            nonRespondentsEmails = new HashSet<String>();
            for (EntityAttributes possibleRespondent : allPossibleRespondents) {
                if (possibleRespondent instanceof StudentAttributes) {
                    StudentAttributes student = (StudentAttributes)possibleRespondent;
                    nonRespondentsEmails.add(student.email);
                } else if (possibleRespondent instanceof InstructorAttributes){
                    InstructorAttributes instructor = (InstructorAttributes)possibleRespondent;
                    nonRespondentsEmails.add(instructor.email);
                }
            }
            
            emailsInCourse.put(feedbackSession.courseId, nonRespondentsEmails);
        }
        
        // non-respondents = all students and instructors - respondents
        nonRespondentsEmails.removeAll(respondents);
        return nonRespondentsEmails;
    }
    
    @SuppressWarnings("deprecation")
    private List<FeedbackSessionAttributes> getAllFeedbackSessions() {
        return fsDb.getAllFeedbackSessions();
    }
}
