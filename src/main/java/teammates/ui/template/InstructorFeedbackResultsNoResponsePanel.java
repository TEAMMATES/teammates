package teammates.ui.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

public class InstructorFeedbackResultsNoResponsePanel {
    private List<String> emails;
    private Map<String, String> names;
    private Map<String, String> teams;
    private Map<String, Boolean> instructorStatus;
    private InstructorFeedbackResultsRemindButton remindButton;
    private String remindParticularStudentsLink;
    private Map<String, InstructorFeedbackResultsModerationButton> moderationButtons;

    public InstructorFeedbackResultsNoResponsePanel(FeedbackSessionResponseStatus responseStatus,
            Map<String, InstructorFeedbackResultsModerationButton> moderationButtons,
            InstructorFeedbackResultsRemindButton remindButton,
            String remindParticularStudentsLink) {
        this.instructorStatus = new HashMap<>();
        this.names = Collections.unmodifiableMap(responseStatus.emailNameTable);
        this.emails = getFilteredEmails(responseStatus.getStudentsWhoDidNotRespondSorted());
        this.teams = getTeamsWithInstructorTeam(responseStatus.emailTeamNameTable,
                                                Const.USER_TEAM_FOR_INSTRUCTOR);
        this.moderationButtons = moderationButtons;
        this.remindButton = remindButton;
        this.remindParticularStudentsLink = remindParticularStudentsLink;
    }

    public List<String> getEmails() {
        return emails;
    }

    public Map<String, String> getNames() {
        return names;
    }

    public Map<String, String> getTeams() {
        return teams;
    }

    public Map<String, Boolean> getInstructorStatus() {
        return instructorStatus;
    }

    private List<String> getFilteredEmails(List<String> allEmails) {
        Assumption.assertNotNull(allEmails);
        Assumption.assertNotNull(names);

        List<String> emails = new ArrayList<>();
        emails.addAll(allEmails);
        emails.retainAll(names.keySet());
        return emails;
    }

    private Map<String, String> getTeamsWithInstructorTeam(Map<String, String> studentTeams,
                                                           String instructorTeamName) {
        Assumption.assertNotNull(emails);
        Assumption.assertNotNull(studentTeams);

        Map<String, String> teams = new HashMap<>();
        teams.putAll(studentTeams);

        // TODO: Support for users who are both instructor and student
        List<String> instructorEmails = new ArrayList<>();
        instructorEmails.addAll(emails);
        instructorEmails.removeAll(studentTeams.keySet());

        for (String email : instructorEmails) {
            teams.put(email, instructorTeamName);
            instructorStatus.put(email, true);
        }

        return Collections.unmodifiableMap(teams);
    }

    public Map<String, InstructorFeedbackResultsModerationButton> getModerationButtons() {
        return moderationButtons;
    }

    public InstructorFeedbackResultsRemindButton getRemindButton() {
        return remindButton;
    }

    public String getRemindParticularStudentsLink() {
        return remindParticularStudentsLink;
    }
}
