package teammates.logic.automated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FeedbackSubmissionAdjustmentAction extends TaskQueueWorkerAction {
    private String courseId;
    private String sessionName;
    private String enrollmentDetails;
    
    public FeedbackSubmissionAdjustmentAction(
            HttpServletRequest request) {
        super(request);
        
        this.courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        this.sessionName = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(sessionName);
        
        this.enrollmentDetails = HttpRequestHelper
                .getValueFromRequestParameterMap(request, ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
    }

    public FeedbackSubmissionAdjustmentAction(HashMap<String,String> paramMap) {    
        super(null);
        
        this.courseId = paramMap.get(ParamsNames.COURSE_ID); 
        Assumption.assertNotNull(courseId);
        
        this.sessionName = paramMap.get(ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertNotNull(sessionName);
        
        this.enrollmentDetails = paramMap.get(ParamsNames.ENROLLMENT_DETAILS);
        Assumption.assertNotNull(enrollmentDetails);
    }
    
    @Override
    public boolean execute() {
        
        Gson gsonParser = Utils.getTeammatesGson();
        ArrayList<StudentEnrollDetails> enrollmentList = gsonParser
                .fromJson(enrollmentDetails, new TypeToken<ArrayList<StudentEnrollDetails>>(){}
                .getType());
        
        log.info("Adjusting submissions for feedback session :" + sessionName +
                 "in course : " + courseId);
        
        FeedbackSessionAttributes feedbackSession = FeedbackSessionsLogic.inst()
                .getFeedbackSession(sessionName, courseId);
        StudentsLogic stLogic = StudentsLogic.inst();
        String errorString = "Error encountered while adjusting feedback session responses " +
                "of %s in course : %s : %s\n%s";
        
        if(feedbackSession != null) {
            List<FeedbackResponseAttributes> allResponses = FeedbackResponsesLogic.inst()
                    .getFeedbackResponsesForSession(feedbackSession.feedbackSessionName,
                            feedbackSession.courseId);
            
            for (FeedbackResponseAttributes response : allResponses) {
                try {
                    stLogic.adjustFeedbackResponseForEnrollments(enrollmentList, response);
                } catch (Exception e) {
                    log.severe(String.format(errorString, sessionName, courseId, e.getMessage(),
                            ActivityLogEntry.generateServletActionFailureLogMessage(request, e)));
                    return false;
                }
            } 
            adjustResponsesForTeamChange(enrollmentList, feedbackSession.feedbackSessionName);
            
            return true;
        } else {
            log.severe(String.format(errorString, sessionName, courseId, "feedback session is null", ""));
            return false;
        }    
    }

    private void adjustResponsesForTeamChange(ArrayList<StudentEnrollDetails> enrolmentList, String feedbackSessionName) {
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        StudentsLogic studentsLogic = StudentsLogic.inst();
        
        // check if all members of the same team got moved to the same team
        // if it is, then the team is renamed
        Map<String, String> oldTeamToNewTeamMap = new HashMap<String, String>();
        Map<String, Boolean> isGivenTeamRenamed = new HashMap<String, Boolean>();
        Set<String> modifiedStudents = new HashSet<String>();
        
        // initialise isGivenTeamRenamed 
        for (StudentEnrollDetails details : enrolmentList) {
            String originalSectionTeam = constructOriginalSectionTeamStr(details);
            
            if (details.updateStatus == UpdateStatus.UNMODIFIED || 
                (details.oldSection == null && details.oldTeam == null) ) {
                // if someone from the same team is not modified, 
                // or if the team and section is not modified,
                // then the team is not renamed
                isGivenTeamRenamed.put(originalSectionTeam, false);
            } else {
                modifiedStudents.add(details.email);
                
                if (!isGivenTeamRenamed.containsKey(originalSectionTeam)) {
                    isGivenTeamRenamed.put(originalSectionTeam, true);
                }
            }
        }
        
        Set<String> renamedTeams = new HashSet<String>();
        for (Entry<String, Boolean> entry : isGivenTeamRenamed.entrySet()) {
            String team = entry.getKey();
            boolean isRenamed = entry.getValue();
            
            if (isRenamed) {
                renamedTeams.add(team);
            }
        }
        
        // For every team, check that all team members is in the enrollment
        Set<String> sectionTeams = new HashSet<String>(renamedTeams);
        for (String sectionTeam : sectionTeams) {
            String team = sectionTeam.split("\\|")[1];
            List<StudentAttributes> studentsInTeam = studentsLogic.getStudentsForTeam(team, courseId);

            boolean isAllStudentsModified = true;
            for (StudentAttributes student : studentsInTeam) {
                
                if (!modifiedStudents.contains(student.email)) {
                    isAllStudentsModified = false;
                }
            }
            
            if (!isAllStudentsModified) {
                renamedTeams.remove(sectionTeam);
            }
        }
        
        
        // For every old team, check if there exists another member of the old team
        // who got a different new team
        for (StudentEnrollDetails details : enrolmentList) {
            String originalSectionTeam = constructOriginalSectionTeamStr(details);
            String newSectionTeam = details.newSection + "|" + details.newTeam;
            
            if (oldTeamToNewTeamMap.containsKey(originalSectionTeam)) {
                if (!oldTeamToNewTeamMap.get(originalSectionTeam).equals(newSectionTeam)) {
                    renamedTeams.remove(originalSectionTeam);
                }
            } else {
                oldTeamToNewTeamMap.put(originalSectionTeam, newSectionTeam);
            }
        }
        
        for (String sectionTeamToRename : renamedTeams) {
            
            String[] splitsectionTeamToRename = sectionTeamToRename.split("\\|");
            String oldSection = splitsectionTeamToRename[0];
            String oldTeam = splitsectionTeamToRename[1];
            
            String newSectionTeam = oldTeamToNewTeamMap.get(sectionTeamToRename);
            String[] splitNewSectionTeam = newSectionTeam.split("\\|");
            String newSection = splitNewSectionTeam[0];
            String newTeam = splitNewSectionTeam[1];
            
            frLogic.updateFeedbackResponsesForRenamingTeam(courseId, feedbackSessionName, oldTeam, oldSection, newTeam, newSection);
        }
    }

    private String constructOriginalSectionTeamStr(StudentEnrollDetails details) {
        boolean isSectionChanged = details.oldSection != null;
        String oldSectionName;
        if (isSectionChanged) { 
            oldSectionName = details.oldSection;
        } else {
            // if the section is not changed, details.oldSection is null
            // and newSection contains the original value
            oldSectionName = details.newSection;
        }
        
        boolean isTeamChanged = details.oldTeam != null;
        String oldTeamName;
        if (isTeamChanged) {
            oldTeamName = details.oldTeam;
        } else {
            // if the team is not changed, details.oldteam is null
            // and newTeam contains the original value
            oldTeamName = details.newTeam;
        }
        
        return oldSectionName + "|" + oldTeamName;
    }

}
