package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Test if students have received the results and the comments are in right order.
 * @author Kalpit
 */
public class TestStudentReceiveResults extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		//setupScenario();
		setupScenarioForBumpRatioTest(4);
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}
	
	
	/**
	 * Each student should receive their respective results.
	 * @throws Exception 
	 */
	@Test
	public void testStudentViewResults() throws Exception {
		StudentsSubmitFeedbacks();
		for (Student s : sc.students) {
			String team = s.teamName;
			if(team.equalsIgnoreCase("Team Point")){
				// First we need to login
				studentLogin(s.email, s.password);
	
				// Click Evaluations
				waitAndClick(By.className("t_evaluations"));
				justWait();
		
				// Click View Results
				waitAndClick(By.id("viewEvaluation0"));	
				justWait();
				
				//to check the comments for different students
				if(s.name=="Alice"){
					assertEquals("b", getElementText(By.id("com0")));
					assertEquals("c", getElementText(By.id("com1")));
					assertEquals("d", getElementText(By.id("com2")));
				}
				if(s.name=="Benny"){
					assertEquals("a", getElementText(By.id("com0")));
					assertEquals("c", getElementText(By.id("com1")));
					assertEquals("d", getElementText(By.id("com2")));
				}
				if(s.name=="Charlie"){
					assertEquals("a", getElementText(By.id("com0")));
					assertEquals("b", getElementText(By.id("com1")));
					assertEquals("d", getElementText(By.id("com2")));
				}
				if(s.name=="Danny"){
					assertEquals("a", getElementText(By.id("com0")));
					assertEquals("b", getElementText(By.id("com1")));
					assertEquals("c", getElementText(By.id("com2")));
				}
				
				//selenium.mouseOver("com0");
				
		
				logout();
			}
		}
	}

	public void StudentsSubmitFeedbacks() throws Exception {
		cout("Students submitting feedback.");

		for (Student s : sc.students) {
			
			String comment = "";
			String team = s.teamName;
			if(team.equalsIgnoreCase("Team Point")){
				// First we need to login
				studentLogin(s.email, s.password);
				if(s.name.equalsIgnoreCase("Alice"))
					comment = "a";
				if(s.name.equalsIgnoreCase("Benny"))
					comment = "b";
				if(s.name.equalsIgnoreCase("Charlie"))
					comment = "c";
				if(s.name.equalsIgnoreCase("Danny"))
					comment = "d";
				

				System.out.println("Submitting feedback for student " + s.email);

				// To evaluation page
				waitAndClick(By.className("t_evaluations"));
				justWait();

				// Select the first evaluation available
				if (isElementPresent(By.id("doEvaluation0"))) {
					wdClick(By.id("doEvaluation0"));
				} else {
					wdClick(By.id("editEvaluation0"));
				}
				justWait();
	
				for (int i = 0; i < s.team.students.size(); i++) {
					selectDropdownByValue(By.id("points" + i), "30");
					wdFillString(By.name("justification" + i), String.format(
							"abcd", s.email,
							s.team.students.get(i).email));
					wdFillString(
							By.name("commentstostudent" + i),
							String.format(comment, s.email,
									s.team.students.get(i).email));
				}
	
				// Submit the evaluation
				wdClick(By.name("submitEvaluation"));
				justWait();
	
				// Check to see evaluation status is "Submitted"
				waitForElementText(By.className("t_eval_status"), "SUBMITTED");
	
				logout();
				justWait();
			}
		}
		TMAPI.publishEvaluation(sc.course.courseId, sc.evaluation.name);
	}	
}
