package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;

public class StudentListSectionData {

    private String sectionName;
    private boolean allowedToViewStudentInSection;
    private boolean allowedToModifyStudent;
    private boolean allowedToGiveCommentInSection;
    private List<StudentListTeamData> teams;

    public StudentListSectionData(SectionDetailsBundle section, boolean isAllowedToViewStudentInSection,
                                  boolean isAllowedToModifyStudent, boolean isAllowedToGiveCommentInSection,
                                  Map<String, String> emailPhotoUrlMapping, String googleId) {
        this.sectionName = section.name;
        this.allowedToViewStudentInSection = isAllowedToViewStudentInSection;
        this.allowedToModifyStudent = isAllowedToModifyStudent;
        this.allowedToGiveCommentInSection = isAllowedToGiveCommentInSection;
        List<StudentListTeamData> teamsDetails = new ArrayList<StudentListTeamData>();
        for (TeamDetailsBundle team : section.teams) {
            teamsDetails.add(new StudentListTeamData(team, emailPhotoUrlMapping, googleId));
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

    public List<StudentListTeamData> getTeams() {
        return teams;
    }

}
