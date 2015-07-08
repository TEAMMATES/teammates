package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.util.Sanitizer;

public class InstructorStudentListAjaxTeamData {

    private String teamName;
    private List<InstructorStudentListAjaxStudentData> students;

    public InstructorStudentListAjaxTeamData(TeamDetailsBundle team, Map<String, String> emailPhotoUrlMapping,
                                             String googleId) {
        this.teamName = Sanitizer.sanitizeForHtml(team.name);
        List<InstructorStudentListAjaxStudentData> studentsDetails =
                                        new ArrayList<InstructorStudentListAjaxStudentData>();
        for (StudentAttributes student: team.students) {
            studentsDetails.add(new InstructorStudentListAjaxStudentData(googleId, student.name,
                                                                         student.email, student.course,
                                                             emailPhotoUrlMapping.get(student.email)));
        }
        this.students = studentsDetails;
    }

    public String getTeamName() {
        return teamName;
    }

    public List<InstructorStudentListAjaxStudentData> getStudents() {
        return students;
    }

}
