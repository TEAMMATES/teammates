package teammates.test.cases;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.exception.InvalidParametersException;

public class TeamResultBundleTest {
	
	@Test
	public void testSortByStudentNameAscending() throws InvalidParametersException{
		//try to sort empty team. Should fail silently.
		TeamResultBundle teamResultBundle = new TeamResultBundle(new TeamDetailsBundle());
		teamResultBundle.sortByStudentNameAscending();
		
		//sort typical team
		TeamDetailsBundle teamDetails = new TeamDetailsBundle();
		StudentData s1 = new StudentData("|Benny|x@e|", "dummyCourse");
		teamDetails.students.add(s1);
		StudentData s2 = new StudentData("|Alice|y@e|", "dummyCourse");
		teamDetails.students.add(s2);
		StudentData s3 = new StudentData("|Frank|z@e|", "dummyCourse");
		teamDetails.students.add(s3);
		teamResultBundle = new TeamResultBundle(teamDetails);
		
		teamResultBundle.sortByStudentNameAscending();
		AssertJUnit.assertEquals("Alice",teamResultBundle.studentResults.get(0).student.name);
		AssertJUnit.assertEquals("Benny",teamResultBundle.studentResults.get(1).student.name);
		AssertJUnit.assertEquals("Frank",teamResultBundle.studentResults.get(2).student.name);
	}

}
