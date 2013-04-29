package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static teammates.common.Common.EOL;
import teammates.common.Common;
import teammates.logic.TeamEvalResult;

public class TeamData {
	public String name;
	public ArrayList<StudentData> students = new ArrayList<StudentData>();
	public TeamEvalResult result;
	
	public void sortByStudentNameAscending() {
		Collections.sort(students, new Comparator<StudentData>() {
			public int compare(StudentData s1, StudentData s2) {
				//email is prefixed to avoid mix ups due to two students with
				//same name.
				return (s1.name+s1.email).compareTo(s2.name+s2.email);
			}
		});
	}
	
	public String toString(){
		return toString(0);
	}
	
	public String toString(int indent){
		String indentString = Common.getIndent(indent);
		StringBuilder sb = new StringBuilder();
		sb.append(indentString+"Team:"+name+EOL);
		sb.append(indentString+result.toString(indent+1));
		for(StudentData student: students){
			sb.append(indentString+student.toString(indent+2)+EOL);
		}
		return sb.toString();
	}

}
