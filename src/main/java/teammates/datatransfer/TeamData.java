package teammates.datatransfer;

import java.util.ArrayList;

import teammates.api.NotImplementedException;

public class TeamData {
	public String name;
	public TeamProfileData profile;
	public ArrayList<StudentData> students = new ArrayList<StudentData>();
	
	public void sortByStudentNameAscending() throws NotImplementedException{
		//TODO:
		throw new NotImplementedException("to be implemented soon");
	}

}
