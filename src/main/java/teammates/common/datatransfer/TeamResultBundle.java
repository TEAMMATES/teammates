package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import teammates.common.util.StringHelper;

/**
 * Represents detailed results for a team.
 * <br> Contains:
 * <br> * {@link StudentResultBundle} objects for each team member.
 */
public class TeamResultBundle {
    
    public ArrayList<StudentResultBundle> studentResults;
    
    public TeamResultBundle(ArrayList<StudentAttributes> students){
        this.studentResults = new ArrayList<StudentResultBundle>();
        for(StudentAttributes student: students){
            studentResults.add(new StudentResultBundle(student));
        }
    }
    
    //TODO: unit test sort methods
    
    public void sortByStudentNameAscending() {
        Collections.sort(studentResults, new Comparator<StudentResultBundle>() {
            public int compare(StudentResultBundle s1, StudentResultBundle s2) {
                //email is prefixed to avoid mix ups due to two students with
                //same name.
                return (s1.student.name+s1.student.email).compareTo(s2.student.name+s2.student.email);
            }
        });
    }
    
    public StudentResultBundle getStudentResult(String studentEmail){
        for(StudentResultBundle srb: studentResults){
            if(studentEmail.equals(srb.student.email)){
                return srb;
            }
        }
        //not found
        return null;
    }
    
    public String getTeamName(){
        return studentResults.size()==0? "":studentResults.get(0).student.team;
    }
    
    public String toString(){
        return toString(0);
    }
    
    public String toString(int indent){
        String indentString = StringHelper.getIndent(indent);
        StringBuilder sb = new StringBuilder();
        for(StudentResultBundle srb: studentResults){
            sb.append(indentString+srb.toString(indent+1));
        }
        return sb.toString();
    }

}
