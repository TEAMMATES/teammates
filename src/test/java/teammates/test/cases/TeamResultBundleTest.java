package teammates.test.cases;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.exception.InvalidParametersException;

public class TeamResultBundleTest {
	
	@Test
	public void testSortByStudentNameAscending() throws InvalidParametersException{
		TeamResultBundle teamEvalResultBundle = new TeamResultBundle();
		//try to sort empty team. Should fail silently.
		teamEvalResultBundle.sortByStudentNameAscending();
		
		//sort typical team
		teamEvalResultBundle.team.students.add(new StudentData("|Benny|x@e|", "dummyCourse"));
		teamEvalResultBundle.team.students.add(new StudentData("|Alice|y@e|", "dummyCourse"));
		teamEvalResultBundle.team.students.add(new StudentData("|Frank|z@e|", "dummyCourse"));
		teamEvalResultBundle.sortByStudentNameAscending();
		AssertJUnit.assertEquals("Alice",teamEvalResultBundle.team.students.get(0).name);
		AssertJUnit.assertEquals("Benny",teamEvalResultBundle.team.students.get(1).name);
		AssertJUnit.assertEquals("Frank",teamEvalResultBundle.team.students.get(2).name);
	}

}
