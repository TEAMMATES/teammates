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

	/**
	 * Each class groups similar tests that revolve around 1 Scenario
	 */
	protected static Scenario scn = null;
	
	protected static void setupScenario(String name) {
		scn = Scenario.fromJSONFile("target/test-classes/data/" + name + ".json");
		scn.scrambleScenario();
	}
	
	protected static void setupNewScenarioForMultipleCourses(String name) {
		scn = Scenario.newScenario("target/test-classes/data/" + name + ".json");
		scn.scrambleScenario();
	}
	
	protected static void setupScenarioBumpRatioTest(int index) {
		scn = Scenario.scenarioForBumpRatioTest("target/test-classes/data/bump_ratio_scenario.json", index);
		scn.scrambleScenario();
	}
	
	protected static Scenario setupScenarioInstance(String name) {
		Scenario s = Scenario.fromJSONFile("target/test-classes/data/" + name + ".json");
		s.scrambleScenario();
		return s;
	}

	protected static Scenario setupNewScenarioInstance(String name) {
		Scenario s = Scenario.newScenario("target/test-classes/data/" + name + ".json");
		s.scrambleScenario();
		return s;
	}
	
	protected static Scenario setupBumpRatioScenarioInstance(String name, int index) {
		Scenario s = Scenario.scenarioForBumpRatioTest("target/test-classes/data/" + name + ".json", index);
		s.scrambleScenario();
		return s;
	}

	// Does Java support late static binding?

}
