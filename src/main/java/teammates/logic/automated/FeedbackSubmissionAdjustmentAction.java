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
        
        // Initialise oldSectionTeamToNewSectionTeamMap, modifiedStudents
        Map<String, String> oldSectionTeamToNewSectionTeamMap = extractOldTeamToNewTeamMapFromEnrollmentList(enrolmentList);
        Set<String> modifiedStudents = getStudentsWithModifiedTeamOrSectionFromEnrollment(enrolmentList);
        
        // For every possible renamed team, check that all team members are in the enrollment
        // remove the teams that have team members missing from the enrollment data
        removeTeamsWithMembersMissingFromEnrollmentData(logic, modifiedStudents, oldSectionTeamToNewSectionTeamMap);

        for (Map.Entry<String, String> mapEntry : oldSectionTeamToNewSectionTeamMap.entrySet()) {
            String sectionTeamToRename = mapEntry.getKey();
            String[] splitsectionTeamToRename = sectionTeamToRename.split("\\|");
            String oldSection = splitsectionTeamToRename[0];
            String oldTeam = splitsectionTeamToRename[1];
            
            String newSectionTeam = mapEntry.getValue();
            
            String[] splitNewSectionTeam = newSectionTeam.split("\\|");
            String newSection = splitNewSectionTeam[0];
            String newTeam = splitNewSectionTeam[1];
            
            logic.updateFeedbackResponsesForRenamingTeam(courseId, feedbackSessionName, oldTeam, oldSection, newTeam, newSection);
        }
    }

    /**
     * Using the enrollment list, return the mapping of old teams to new teams.
     * If a team was not renamed (due to members going to different teams), 
     * or an unmodified student in the enrollment list, it is not included in the map.
     * This does not consider cases where not every student in a team is included in the enrollment
     * @param enrolmentList
     */
    private Map<String, String> extractOldTeamToNewTeamMapFromEnrollmentList(
            ArrayList<StudentEnrollDetails> enrolmentList) {
        
        Map<String, Boolean> isGivenTeamRenamed = new HashMap<String, Boolean>();
        
        for (StudentEnrollDetails details : enrolmentList) {
            String originalSectionTeam = constructOriginalSectionTeamStr(details);
            boolean isTeamUpdated = (details.newTeam != null) && (details.oldTeam != null) 
                                    && (!details.oldTeam.equals(details.newTeam));
            boolean isSectionUpdated = (details.newSection != null) && (details.oldSection != null) 
                                        && (!details.oldSection.equals(details.newSection));
            
            if (details.updateStatus == UpdateStatus.UNMODIFIED || 
                (!isTeamUpdated && !isSectionUpdated) ) {
                // if someone from the same team is not modified, or if the team and section is not modified,
                // then the team is not renamed
                isGivenTeamRenamed.put(originalSectionTeam, false);
            } else {
                if (!isGivenTeamRenamed.containsKey(originalSectionTeam)) {
                    isGivenTeamRenamed.put(originalSectionTeam, true);
                }
            }
        }
        
        
        // Obtains a mapping of old sectionTeam name to new sectionTeam name.
        // This does not include teams where the entire team did not move to the same team
        Map<String, String> oldSectionTeamToNewSectionTeamMap = generateOldToNewTeamMappingWithConsistencyCheck(enrolmentList, isGivenTeamRenamed);
        
        return oldSectionTeamToNewSectionTeamMap;
    }
    
    
    Set<String> getStudentsWithModifiedTeamOrSectionFromEnrollment(List<StudentEnrollDetails> enrolmentList) {
        Set<String> modifiedStudents = new HashSet<String>();
        
        for (StudentEnrollDetails details : enrolmentList) {
            boolean isTeamUpdated = (details.newTeam != null) && (details.oldTeam != null) 
                                    && (!details.oldTeam.equals(details.newTeam));
            boolean isSectionUpdated = (details.newSection != null) && (details.oldSection != null) 
                                        && (!details.oldSection.equals(details.newSection));
            
            if (details.updateStatus == UpdateStatus.UNMODIFIED || 
               (!isTeamUpdated && !isSectionUpdated) ) {
                continue;
            } 
            
            modifiedStudents.add(details.email);
        }
        
        return modifiedStudents;
    }

    /**
     * Updates renamedTeams by removing teams where not every member was modified in the enrollment
     * @param logic
     * @param modifiedStudents
     * @param oldToNewSectionTeamMapping
     */
    private void removeTeamsWithMembersMissingFromEnrollmentData(Logic logic,
            Set<String> modifiedStudents, Map<String, String> oldToNewSectionTeamMapping) {
        
        Set<String> sectionTeams = new HashSet<String>(oldToNewSectionTeamMapping.keySet());
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
                oldToNewSectionTeamMapping.keySet().remove(sectionTeam);
            }
        }
    }

    /**
     * Creates a map of old section Team name, to new section team name.
     * If an old name maps to 2 different team names, it is removed from the mapping
     * @param enrolmentList
     * @param oldTeamToNewTeamMap 
     */
    private Map<String, String> generateOldToNewTeamMappingWithConsistencyCheck(ArrayList<StudentEnrollDetails> enrolmentList,
            Map<String, Boolean> isGivenTeamRenamed) {
        
        Map<String, String> oldTeamToNewTeamMap = new HashMap<String, String>();
        Set<String> renamedTeams = new HashSet<String>();
        
        for (Map.Entry<String, Boolean> entry : isGivenTeamRenamed.entrySet()) {
            String team = entry.getKey();
            Boolean isRenamed = entry.getValue();
            if (isRenamed) {
                renamedTeams.add(team);
            }
        }
        
        // Create the old team to new team mapping.
        // If not every student in a team maps to the same new team,
        // then the team is not renamed, and is not included in the mapping
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
        
        oldTeamToNewTeamMap.keySet().retainAll(renamedTeams);
        
        return oldTeamToNewTeamMap;
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
