package teammates.testing.concurrent;

import teammates.Common;
import teammates.testing.object.Scenario;

/**
 * Base class for all testing classes.
 * 
 * TODO: Rename to something nicer
 * TODO: should this be parent class? does not look like a good inheritance relationship
 * 
 */
public class TestCase {

	/**
	 * Each class groups similar tests that revolve around 1 Scenario
	 */
	protected static Scenario scn = null;
	
	protected static void setupScenario(String name) {
		scn = Scenario.fromJSONFile(Common.TEST_DATA_FOLDER + name + ".json");
		scn.randomizeCourseId();
	}
	
	protected static void setupNewScenarioForMultipleCourses(String name) {
		scn = Scenario.newScenario(Common.TEST_DATA_FOLDER + name + ".json");
		scn.randomizeCourseId();
	}
	
	protected static void setupScenarioBumpRatioTest(int index) {
		scn = Scenario.scenarioForBumpRatioTest(Common.TEST_DATA_FOLDER+"bump_ratio_scenario.json", index);
		scn.randomizeCourseId();
	}
	
	protected static Scenario setupScenarioInstance(String name) {
		Scenario s = Scenario.fromJSONFile(Common.TEST_DATA_FOLDER + name + ".json");
		s.randomizeCourseId();
		return s;
	}

	protected static Scenario setupNewScenarioInstance(String name) {
		Scenario s = Scenario.newScenario(Common.TEST_DATA_FOLDER + name + ".json");
		s.randomizeCourseId();
		return s;
	}
	
	protected static Scenario setupBumpRatioScenarioInstance(String name, int index) {
		Scenario s = Scenario.scenarioForBumpRatioTest(Common.TEST_DATA_FOLDER + name + ".json", index);
		s.randomizeCourseId();
		return s;
	}
	
	public static void printTestCaseHeader(String testCaseName){
		System.out.println("[TestCase]---:"+testCaseName);
	}
	
	public static void printTestClassHeader(String testClassName){
		System.out.println("[============================="+testClassName);
	}
	
	public static void printTestClassFooter(String testClassName){
		System.out.println(testClassName+"=============================]");
	}

	// Does Java support late static binding?

}
