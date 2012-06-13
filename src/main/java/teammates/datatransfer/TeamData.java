package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TeamData {
	public String name;
	public TeamProfileData profile;
	public ArrayList<StudentData> students = new ArrayList<StudentData>();
	
	public void sortByStudentNameAscending() {
		Collections.sort(students, new Comparator<StudentData>() {
			public int compare(StudentData s1, StudentData s2) {
				//email is prefixed to avoid mix ups due to two students with
				//same name.
				return (s1.name+s1.email).compareTo(s2.name+s2.email);
			}
		});
	}

}
