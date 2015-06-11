package teammates.ui.template;

import java.util.List;

public class ActiveCoursesTableRow {
    private String courseId;
    private String courseName;
    private int sectionNum;
    private int teamNum;
    private int totalStudentNum;
    private int unregisteredStudentNum;
    private List<ElementTag> actions;
    
    public ActiveCoursesTableRow(String courseIdParam, String courseNameParam, int sectionNumParam,
            int teamNumParam, int totalStudentParam, int unregisteredStudentNumParam, List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.sectionNum = sectionNumParam;
        this.teamNum = teamNumParam;
        this.totalStudentNum = totalStudentParam;
        this.unregisteredStudentNum = unregisteredStudentNumParam;
        this.actions = actionsParam;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public int getSectionNum() {
        return sectionNum;
    }
    
    public int getTeamNum() {
        return teamNum;
    }
    
    public int getTotalStudentNum() {
        return totalStudentNum;
    }
    
    public int getUnregisteredStudentNum() {
        return unregisteredStudentNum;
    }
    
    public List<ElementTag> getActions() {
        return actions;
    }
    
}
