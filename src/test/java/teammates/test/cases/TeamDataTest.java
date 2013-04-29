package teammates.test.cases;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamData;
import teammates.common.exception.InvalidParametersException;

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
		AssertJUnit.assertEquals("Alice",team.students.get(0).name);
		AssertJUnit.assertEquals("Benny",team.students.get(1).name);
		AssertJUnit.assertEquals("Frank",team.students.get(2).name);
	}

}
