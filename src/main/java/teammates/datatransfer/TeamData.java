package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import teammates.api.NotImplementedException;

public class TeamData {
	public String name;
	public TeamProfileData profile;
	public ArrayList<StudentData> students = new ArrayList<StudentData>();
	
	public void sortByStudentNameAscending() {
		Collections.sort(students, new Comparator<StudentData>() {
			public int compare(StudentData s1, StudentData s2) {
				return s1.name.compareTo(s2.name);
			}
		});
	}

}
