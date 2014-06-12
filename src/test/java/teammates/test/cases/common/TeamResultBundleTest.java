package teammates.test.cases.common;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.exception.EnrollException;
import teammates.test.cases.BaseTestCase;

public class TeamResultBundleTest extends BaseTestCase{
    
    @Test
    public void testSortByStudentNameAscending() throws EnrollException{
        //try to sort empty team. Should fail silently.
        TeamResultBundle teamResultBundle = new TeamResultBundle(new TeamDetailsBundle().students);
        teamResultBundle.sortByStudentNameAscending();
        
        //sort typical team
        TeamDetailsBundle teamDetails = new TeamDetailsBundle();
        StudentAttributes s1 = new StudentAttributes("sect1", "team1", "Benny", "x@e", "", "dummyCourse");
        teamDetails.students.add(s1);
        StudentAttributes s2 = new StudentAttributes("sect1", "team1", "Alice", "y@e", "", "dummyCourse");
        teamDetails.students.add(s2);
        StudentAttributes s3 = new StudentAttributes("sect1", "team1", "Frank", "z@e", "", "dummyCourse");
        teamDetails.students.add(s3);
        teamResultBundle = new TeamResultBundle(teamDetails.students);
        
        teamResultBundle.sortByStudentNameAscending();
        AssertJUnit.assertEquals("Alice",teamResultBundle.studentResults.get(0).student.name);
        AssertJUnit.assertEquals("Benny",teamResultBundle.studentResults.get(1).student.name);
        AssertJUnit.assertEquals("Frank",teamResultBundle.studentResults.get(2).student.name);
    }

}
