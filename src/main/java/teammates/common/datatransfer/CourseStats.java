package teammates.common.datatransfer;

/**
 * Represents the course statistics for a course.
 * <br> Contains:
 * <br> * The total number of teams in the course.
 * <br> * The total number of students in the course.
 * <br> * The total number of unregistered students in the course.
 */
public class CourseStats {
	public int sectionsTotal = 0;
    public int teamsTotal = 0;
    public int studentsTotal = 0;
    public int unregisteredTotal = 0;
    
    public int getSectionsTotal() {
        return this.sectionsTotal;
    }
    
    public int getTeamsTotal() {
        return this.teamsTotal;
    }
    
    public int getStudentsTotal() {
        return this.studentsTotal;
    }
    
    public int getUnregisteredTotal() {
        return this.unregisteredTotal;
    }
}
