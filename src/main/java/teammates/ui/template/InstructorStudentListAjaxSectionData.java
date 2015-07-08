package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.util.Sanitizer;

public class InstructorStudentListAjaxSectionData {

    private String sectionName;
    private boolean allowedToViewStudentInSection;
    private boolean allowedToModifyStudent;
    private boolean allowedToGiveCommentInSection;
    private List<InstructorStudentListAjaxTeamData> teams;

    public InstructorStudentListAjaxSectionData(SectionDetailsBundle section,
                                                boolean isAllowedToViewStudentInSection,
                                                boolean isAllowedToModifyStudent,
                                                boolean isAllowedToGiveCommentInSection,
                                                Map<String, String> emailPhotoUrlMapping,
                                                String googleId) {
        this.sectionName = Sanitizer.sanitizeForHtml(section.name);
        this.allowedToViewStudentInSection = isAllowedToViewStudentInSection;
        this.allowedToModifyStudent = isAllowedToModifyStudent;
        this.allowedToGiveCommentInSection = isAllowedToGiveCommentInSection;
        List<InstructorStudentListAjaxTeamData> teamsDetails =
                                        new ArrayList<InstructorStudentListAjaxTeamData>();
        for (TeamDetailsBundle team : section.teams) {
            teamsDetails.add(new InstructorStudentListAjaxTeamData(team, emailPhotoUrlMapping, googleId));
        }
        this.teams = teamsDetails;
    }

    public String getSectionName() {
        return sectionName;
    }

    public boolean isAllowedToViewStudentInSection() {
        return allowedToViewStudentInSection;
    }

    public boolean isAllowedToModifyStudent() {
        return allowedToModifyStudent;
    }

    public boolean isAllowedToGiveCommentInSection() {
        return allowedToGiveCommentInSection;
    }

    public List<InstructorStudentListAjaxTeamData> getTeams() {
        return teams;
    }

}
