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
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.api.Logic;
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
            adjustResponsesForRenamedTeam(enrollmentList, feedbackSession.feedbackSessionName);
            
            return true;
        } else {
            log.severe(String.format(errorString, sessionName, courseId, "feedback session is null", ""));
            return false;
        }    
    }

    private void adjustResponsesForRenamedTeam(ArrayList<StudentEnrollDetails> enrolmentList, String feedbackSessionName) {
        Logic logic = new Logic();
        
        Set<String> modifiedStudents = new HashSet<String>();
        Map<String, String> oldSectionTeamToNewSectionTeamMap = new HashMap<String, String>();
        
        // Initialise modifiedStudents, oldSectionTeamToNewSectionTeamMap and renamedTeams 
        Set<String> renamedTeams = extractDataFromEnrollmentList(
                enrolmentList, modifiedStudents, oldSectionTeamToNewSectionTeamMap);
        
        // For every possible renamed team, check that all team members are in the enrollment
        removeTeamsWithMembersMissingFromEnrollmentData(logic, modifiedStudents, renamedTeams);

        for (String sectionTeamToRename : renamedTeams) {
            String[] splitsectionTeamToRename = sectionTeamToRename.split("\\|");
            String oldSection = splitsectionTeamToRename[0];
            String oldTeam = splitsectionTeamToRename[1];
            
            String newSectionTeam = oldSectionTeamToNewSectionTeamMap.get(sectionTeamToRename);
            
            String[] splitNewSectionTeam = newSectionTeam.split("\\|");
            String newSection = splitNewSectionTeam[0];
            String newTeam = splitNewSectionTeam[1];
            
            logic.updateFeedbackResponsesForRenamingTeam(courseId, feedbackSessionName, oldTeam, oldSection, newTeam, newSection);
        }
    }

    /**
     * Using the enrollment list, return the list of teams which are renamed.
     * Updates the set of students who are modified by the enrollment.
     * Updates the mapping from old section and team names to new section and team names.
     * @param enrolmentList
     * @param modifiedStudents
     * @param oldSectionTeamToNewSectionTeamMap
     * @return
     */
    private Set<String> extractDataFromEnrollmentList(
            ArrayList<StudentEnrollDetails> enrolmentList,
            Set<String> modifiedStudents,
            Map<String, String> oldSectionTeamToNewSectionTeamMap) {
        
        Map<String, Boolean> isGivenTeamRenamed = new HashMap<String, Boolean>();
        
        for (StudentEnrollDetails details : enrolmentList) {
            String originalSectionTeam = constructOriginalSectionTeamStr(details);
            boolean isTeamUpdated = (details.newTeam != null) && (details.oldTeam != null) 
                                    && (!details.oldTeam.equals(details.newTeam));
            boolean isSectionUpdated = (details.newSection != null) && (details.oldSection != null) 
                                        && (!details.oldSection.equals(details.newSection));
            
            if (details.updateStatus == UpdateStatus.UNMODIFIED || 
                (!isTeamUpdated && !isSectionUpdated) ) {
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
        
        // Obtains a mapping of old sectionTeam name to new sectionTeam name.
        // This also removes teams where not the entire team moved to the same team
        generateOldToNewTeamMappingWithConsistencyCheck(oldSectionTeamToNewSectionTeamMap, enrolmentList,
                renamedTeams);
        
        return renamedTeams;
    }

    /**
     * Updates renamedTeams by removing teams where not every member was modified
     * in the enrollment
     * @param logic
     * @param modifiedStudents
     * @param renamedTeams
     */
    private void removeTeamsWithMembersMissingFromEnrollmentData(Logic logic,
            Set<String> modifiedStudents, Set<String> renamedTeams) {
        Set<String> sectionTeams = new HashSet<String>(renamedTeams);
        for (String sectionTeam : sectionTeams) {
            String team = sectionTeam.split("\\|")[1];
            List<StudentAttributes> studentsInTeam = logic.getStudentsForTeam(team, courseId);

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
    }

    /**
     * Creates a map of old section Team name, to new section team name.
     * If an old name maps to 2 different team names, it is removed from the 
     * set of teams that are renamed.
     * @param enrolmentList
     * @param oldTeamToNewTeamMap 
     * @param renamedTeams Teams that are renamed.  
     */
    private void generateOldToNewTeamMappingWithConsistencyCheck(Map<String, String> oldTeamToNewTeamMap,
            ArrayList<StudentEnrollDetails> enrolmentList, Set<String> renamedTeams) {
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
    }

    /**
     * Construct a string of sectionName|teamName
     * @param details
     * @return
     */
    private String constructOriginalSectionTeamStr(StudentEnrollDetails details) {
        boolean isSectionChanged = details.oldSection != null;
        String oldSectionName;
        if (isSectionChanged) { 
            oldSectionName = details.oldSection;
        } else {
            // if the section is not changed, details.oldSection is null
            // and newSection contains the original value
            oldSectionName = details.newSection != null? 
                             details.newSection : 
                             Const.DEFAULT_SECTION;
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
