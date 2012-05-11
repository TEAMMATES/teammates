package teammates.testing.concurrent;

import teammates.testing.object.Scenario;

/**
 * Base class for all testing classes.
 * 
 * TODO: Rename to something nicer
 * TODO: should this be parent class? does not look like a good inheritance relationship
 * 
 */
public class TestCase {

	private static final String TEST_DATA_FOLDER = "target/test-classes/data/";
	/**
	 * Each class groups similar tests that revolve around 1 Scenario
	 */
	protected static Scenario scn = null;
	
	protected static void setupScenario(String name) {
		scn = Scenario.fromJSONFile(TEST_DATA_FOLDER + name + ".json");
		scn.randomizeCourseId();
	}
	
	protected static void setupNewScenarioForMultipleCourses(String name) {
		scn = Scenario.newScenario(TEST_DATA_FOLDER + name + ".json");
		scn.randomizeCourseId();
	}
	
	protected static void setupScenarioBumpRatioTest(int index) {
		scn = Scenario.scenarioForBumpRatioTest("target/test-classes/data/bump_ratio_scenario.json", index);
		scn.randomizeCourseId();
	}
	
	protected static Scenario setupScenarioInstance(String name) {
		Scenario s = Scenario.fromJSONFile(TEST_DATA_FOLDER + name + ".json");
		s.randomizeCourseId();
		return s;
	}
	
	//TODO: modify other setupScenario.... methods to follow this one -damith
		//i.e. 1. method name should be changed, 
		//     2. '.json' should be part of the parameter
		//     3. parameter name should be jsonFileName
	protected static Scenario loadTestData(String jsonFileName) {
		Scenario s = Scenario.fromJSONFile(TEST_DATA_FOLDER + jsonFileName );
		s.randomizeCourseId();
		return s;
	}

	protected static Scenario setupNewScenarioInstance(String name) {
		Scenario s = Scenario.newScenario(TEST_DATA_FOLDER + name + ".json");
		s.randomizeCourseId();
		return s;
	}
	
	protected static Scenario setupBumpRatioScenarioInstance(String name, int index) {
		Scenario s = Scenario.scenarioForBumpRatioTest(TEST_DATA_FOLDER + name + ".json", index);
		s.randomizeCourseId();
		return s;
	}

	// Does Java support late static binding?

}
