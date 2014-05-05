package teammates.common.datatransfer;

import java.util.ArrayList;

/** 
 * Represents details of students in a team.
 * <br> Contains: 
 * <br> * The team name .
 * <br> * {@link StudentAttributes} objects for all students in the team.
 */
public class TeamDetailsBundle {
    
    public String name;
    public ArrayList<StudentAttributes> students = new ArrayList<StudentAttributes>();

}
