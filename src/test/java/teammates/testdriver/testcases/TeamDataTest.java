package teammates.testdriver.testcases;

import static org.junit.Assert.*;

import org.junit.Test;

import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamData;
import teammates.logic.api.InvalidParametersException;

public class TeamDataTest {
	
	@Test
	public void testSortByStudentNameAscending() throws InvalidParametersException{
		TeamData team = new TeamData();
		//try to sort empty team. Should fail silently.
		team.sortByStudentNameAscending();
		
		//sort typical team
		team.students.add(new StudentData("|Benny|x@e|", "dummyCourse"));
		team.students.add(new StudentData("|Alice|y@e|", "dummyCourse"));
		team.students.add(new StudentData("|Frank|z@e|", "dummyCourse"));
		team.sortByStudentNameAscending();
		assertEquals("Alice",team.students.get(0).name);
		assertEquals("Benny",team.students.get(1).name);
		assertEquals("Frank",team.students.get(2).name);
	}

}
