package teammates.testing.object;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import teammates.testing.lib.SharedLib;

public class Scenario {
	public Coordinator coordinator;
	public ArrayList<Student> students;
	public ArrayList<Student> students2;
	public Course course;
	public Course course2;
	public Evaluation evaluation;
	public Evaluation evaluation2;
	public Evaluation evaluation3;
	public Evaluation evaluation4;
	public Evaluation evaluation5;
	public HashMap<String, Team> teams;
	public HashMap<String, Team> teams2;
	public TeamFormingSession teamFormingSession;
	public String[] submissionPoints;

	/**
	 * Initialize the scenario
	 */
	public Scenario() {
	}

	/**
	 * 
	 */
	public void randomizeCourseId() {
		Random g = new Random();
		Integer n = g.nextInt(99999);
		String suffix = "r" + n.toString();

		String newCourseID = this.course.courseId + suffix;

		System.out.println(Thread.currentThread().getName() + " - " + newCourseID);

		// Changing to the new course name
		this.course.courseId = newCourseID;
		if(this.teamFormingSession != null)
            this.teamFormingSession.courseID = newCourseID;
		
		if (this.evaluation != null) {
			this.evaluation.courseID = newCourseID;
		}
		if (this.evaluation2 != null) {
			this.evaluation2.courseID = newCourseID;
		}
		if (this.evaluation3 != null) {
			this.evaluation3.courseID = newCourseID;
		}
		if (this.evaluation4 != null) {
			this.evaluation4.courseID = newCourseID;
		}if (this.evaluation5 != null) {
			this.evaluation5.courseID = newCourseID;
		}
			 

		for (Student student : this.students) {
			student.courseID = newCourseID;
		}

	}

	public JSONObject toJSONObject() {

		JSONObject json = new JSONObject();
		try {
			json.put("coordinator", coordinator.toJSONObject());
			json.put("course", course.toJSONObject());
			json.put("evaluation", evaluation.toJSONObject());
			json.put("evaluation2", evaluation2.toJSONObject());
			json.put("students", Student.toJSONArray(students));

			ArrayList<Team> arr_teams = new ArrayList<Team>(teams.values());
			json.put("teams", Team.toJSONArray(arr_teams));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public void toJSONFile(String filepath) {

		FileWriter fout;
		try {
			fout = new FileWriter(new File(filepath));
			fout.write(toJSONObject().toString(2));
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// basic scenario
	public static Scenario fromJSONFile(String filepath) {
		String s = SharedLib.getFileContents(filepath);
		try {
			JSONObject json = new JSONObject(s);
			return fromJSONObject(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Scenario fromJSONObject(JSONObject json) {
		Scenario sc = new Scenario();
		try {
			sc.coordinator = Coordinator.fromJSONObject(json.getJSONObject("coordinator"));
			sc.course = Course.fromJSONObject(json.getJSONObject("course"));
			sc.evaluation = Evaluation.fromJSONObject(json.getJSONObject("evaluation"));
			sc.evaluation2 = Evaluation.fromJSONObject(json.getJSONObject("evaluation2"));
			sc.teamFormingSession = TeamFormingSession.fromJSONObject(json.getJSONObject("teamFormingSession"));
			sc.students = Student.fromJSONArray(json.getJSONArray("students"));

			// Teams
			sc.teams = new HashMap<String, Team>();
			JSONArray json_teams = json.getJSONArray("teams");
			for (int i = 0; i < json_teams.length(); i++) {
				String teamname = json_teams.getString(i);
				Team team = new Team();
				team.teamname = teamname;
				sc.teams.put(teamname, team);
			}
			for (Student s : sc.students) {
				s.team = sc.teams.get(s.teamName);
				s.team.students.add(s);
				s.courseID = sc.course.courseId;
				s.comments = "This student's name is " + s.name;
			}

			sc.evaluation.courseID = sc.course.courseId;
			sc.evaluation2.courseID = sc.course.courseId;
			sc.course.students = sc.students;
			sc.teamFormingSession.courseID = sc.course.courseId;

			return sc;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// new scenario
	public static Scenario newScenario(String filepath) {

		String s = SharedLib.getFileContents(filepath);
		try {
			JSONObject json = new JSONObject(s);
			return newScenario(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Scenario newScenario(JSONObject json) {
		Scenario sc = new Scenario();

		try {
			sc.coordinator = Coordinator.fromJSONObject(json.getJSONObject("coordinator"));
			sc.course = Course.fromJSONObject(json.getJSONObject("course2"));
			sc.evaluation = Evaluation.fromJSONObject(json.getJSONObject("evaluation"));
			sc.evaluation2 = Evaluation.fromJSONObject(json.getJSONObject("evaluation2"));
			sc.evaluation3 = Evaluation.fromJSONObject(json.getJSONObject("evaluation3"));
			sc.evaluation4 = Evaluation.fromJSONObject(json.getJSONObject("evaluation4"));
			sc.students = Student.fromJSONArray(json.getJSONArray("students"));
			// Teams
			sc.teams = new HashMap<String, Team>();
			JSONArray json_teams = json.getJSONArray("teams");
			for (int i = 0; i < json_teams.length(); i++) {
				String teamname = json_teams.getString(i);
				Team team = new Team();
				team.teamname = teamname;
				sc.teams.put(teamname, team);
			}


			// Students
			for (Student s : sc.students) {
				s.team = sc.teams.get(s.teamName);
				s.team.students.add(s);
				s.courseID = sc.course.courseId;
				s.comments = "This is comment for " + s.name;
			}

			sc.evaluation.courseID = sc.course.courseId;
			sc.evaluation2.courseID = sc.course.courseId;
			sc.evaluation3.courseID = sc.course.courseId;
			sc.evaluation4.courseID = sc.course.courseId;
			sc.course.students = sc.students;

			return sc;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	// scenario for bump ratio test
	public static Scenario scenarioForBumpRatioTest(String filepath, int index) {
		String s = SharedLib.getFileContents(filepath);
		try {
			JSONObject json = new JSONObject(s);
			return scenarioForBumpRatioTest(json, index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Scenario scenarioForBumpRatioTest(JSONObject json, int index) {
		Scenario sc = new Scenario();
		try {
			sc.coordinator = Coordinator.fromJSONObject(json.getJSONObject("coordinator"));
			sc.course = Course.fromJSONObject(json.getJSONObject("course"));
			sc.evaluation = Evaluation.fromJSONObject(json.getJSONObject("evaluation"));
			sc.evaluation2 = Evaluation.fromJSONObject(json.getJSONObject("evaluation2"));
			JSONArray json_points = json.getJSONArray("submissionPoints" + index);
			int teamSize = json_points.length();// scenario depends on how many students submit evaluation
			JSONArray json_students = json.getJSONArray("students2");
			sc.students = new ArrayList<Student>();
			for (int i = 0; i < teamSize; i++) {
				Student s = Student.fromJSONObject(json_students.getJSONObject(i));
				sc.students.add(s);
			}
			sc.submissionPoints = new String[json_points.length()];
			for (int i = 0; i < json_points.length(); i++) {
				String points = json_points.getString(i);
				sc.submissionPoints[i] = points;
			}

			// Teams
			sc.teams = new LinkedHashMap<String, Team>();
			JSONArray json_teams = json.getJSONArray("teams2");
			for (int i = 0; i < json_teams.length(); i++) {
				String teamname = json_teams.getString(i);
				Team team = new Team();
				team.teamname = teamname;
				sc.teams.put(teamname, team);
			}
			for (Student s : sc.students) {
				s.team = sc.teams.get(s.teamName);
				s.team.students.add(s);
				s.courseID = sc.course.courseId;
				s.comments = "This student's name is " + s.name;
			}

			sc.evaluation.courseID = sc.course.courseId;
			sc.evaluation2.courseID = sc.course.courseId;
			sc.course.students = sc.students;

			return sc;

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	// static scenario for page verification
	public static Scenario scenarioForPageVerification(String filepath) {
		String s = SharedLib.getFileContents(filepath);
		try {
			JSONObject json = new JSONObject(s);
			return scenarioForPageVerification(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Scenario scenarioForPageVerification(JSONObject json) {
		Scenario sc = new Scenario();
		try {
			sc.coordinator = Coordinator.fromJSONObject(json.getJSONObject("coordinator"));

			ArrayList<Course> courses = Course.fromJSONArray(json.getJSONArray("courses"));
			sc.course = courses.get(0);
			sc.course2 = courses.get(1);

			ArrayList<Evaluation> evaluations = Evaluation.fromJSONArray(json.getJSONArray("evaluations"));
			sc.evaluation = evaluations.get(0);
			sc.evaluation2 = evaluations.get(1);
			sc.evaluation3 = evaluations.get(2);
			sc.evaluation4 = evaluations.get(3);
			sc.evaluation5 = evaluations.get(4);
			sc.students = Student.fromJSONArray(json.getJSONArray("students"));
			sc.students2 = Student.fromJSONArray(json.getJSONArray("students2"));
			sc.teams = new HashMap<String, Team>();
			JSONArray json_teams = json.getJSONArray("teams");
			for (int i = 0; i < json_teams.length(); i++) {
				String teamname = json_teams.getString(i);
				Team team = new Team();
				team.teamname = teamname;
				sc.teams.put(teamname, team);
			}			
			sc.teams2 = new HashMap<String, Team>();
			JSONArray json_teams2 = json.getJSONArray("teams2");
			for (int i = 0; i < json_teams2.length(); i++) {
				String teamname = json_teams2.getString(i);
				Team team = new Team();
				team.teamname = teamname;
				sc.teams2.put(teamname, team);
			}
			for (Student s : sc.students) {
				s.team = sc.teams.get(s.teamName);
				s.team.students.add(s);
				s.courseID = sc.course.courseId;
				s.comments = "This student's name is " + s.name;
			}
			for (Student s : sc.students2) {
				s.team = sc.teams2.get(s.teamName);
				s.team.students.add(s);
				s.courseID = sc.course2.courseId;
				s.comments = "This student's name is " + s.name;
			}
			sc.evaluation.courseID = sc.course.courseId;
			sc.evaluation2.courseID = sc.course.courseId;			
			sc.evaluation3.courseID = sc.course2.courseId;
			sc.evaluation4.courseID = sc.course2.courseId;
			sc.evaluation5.courseID = sc.course2.courseId;
			sc.course.students = sc.students;
			sc.course2.students = sc.students2;
			return sc;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}