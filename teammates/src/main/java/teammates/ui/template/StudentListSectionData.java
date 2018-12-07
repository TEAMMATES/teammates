package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;

public class StudentListSectionData {

    private String sectionName;
    private boolean isAllowedToViewStudentInSection;
    private boolean isAllowedToModifyStudent;

    private List<StudentListTeamData> teams;

    public StudentListSectionData(SectionDetailsBundle section, boolean isAllowedToViewStudentInSection,
                                  boolean isAllowedToModifyStudent,
                                  Map<String, String> emailPhotoUrlMapping, String googleId, String sessionToken,
                                  String previousPage) {
        this.sectionName = section.name;
        this.isAllowedToViewStudentInSection = isAllowedToViewStudentInSection;
        this.isAllowedToModifyStudent = isAllowedToModifyStudent;
        List<StudentListTeamData> teamsDetails = new ArrayList<>();
        for (TeamDetailsBundle team : section.teams) {
            teamsDetails.add(new StudentListTeamData(team, emailPhotoUrlMapping, googleId, sessionToken, previousPage));
        }
        this.teams = teamsDetails;
    }

    public String getSectionName() {
        return sectionName;
    }

    public boolean isAllowedToViewStudentInSection() {
        return isAllowedToViewStudentInSection;
    }

    public boolean isAllowedToModifyStudent() {
        return isAllowedToModifyStudent;
    }

    public List<StudentListTeamData> getTeams() {
        return teams;
    }

}
