package teammates.testing.object;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import teammates.testing.lib.SharedLib;

public class Scenario {
	public Course course;
	public Course course2;
	public Coordinator coordinator;
	public ArrayList<Student> students;
	public Evaluation evaluation;
	public Evaluation evaluation2;
	public Evaluation evaluation3;
	public Evaluation evaluation4;
	public String[] submissionPoints;
	public HashMap<String, Team> teams;
	
	/**
	 * Initialize the scenario
	 */
	public Scenario() { }

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
			sc.coordinator = Coordinator.fromJSONObject( json.getJSONObject("coordinator") );
			sc.course = Course.fromJSONObject(json.getJSONObject("course") );
			sc.evaluation = Evaluation.fromJSONObject( json.getJSONObject( "evaluation") );
			sc.evaluation2 = Evaluation.fromJSONObject(json.getJSONObject("evaluation2"));
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

			return sc;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scenario newScenario(String filepath){
		
		String s = SharedLib.getFileContents(filepath);
		try {
			JSONObject json = new JSONObject(s);
			return newScenario(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scenario newScenario(JSONObject json){
		Scenario sc = new Scenario();
		
		try {
			sc.coordinator = Coordinator.fromJSONObject( json.getJSONObject("coordinator") );
			sc.course2 = Course.fromJSONObject(json.getJSONObject("course2") );
			sc.evaluation3 = Evaluation.fromJSONObject( json.getJSONObject( "evaluation3") );
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
			for (Student s : sc.students) {
				s.team = sc.teams.get(s.teamName);
				s.team.students.add(s);
				s.courseID = sc.course2.courseId;
				s.comments = "This student's name is " + s.name;
			}
			
			sc.evaluation3.courseID = sc.course2.courseId;
			sc.evaluation4.courseID = sc.course2.courseId;
			sc.course2.students = sc.students;

			
			return sc;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public JSONObject toJSONObject() {
		
		JSONObject json = new JSONObject();
		try {
			json.put("coordinator", coordinator.toJSONObject());
			json.put("course", course.toJSONObject());
			json.put("evaluation", evaluation.toJSONObject());
			json.put("evaluation2", evaluation2.toJSONObject());
			json.put("students", Student.toJSONArray( students ));
			
			ArrayList<Team> arr_teams = new ArrayList<Team>( teams.values() );
			json.put("teams", Team.toJSONArray( arr_teams ) );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}

	
	
	/*
	public void toJSONFile(String filepath) {
		
		FileWriter fout;
		try {
			fout = new FileWriter(new File( filepath ));
			fout.write( toJSONObject().toString(2) );
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	*/
	
	public static Scenario scenarioForBumpRatioTest(String filepath, int index){
		String s = SharedLib.getFileContents(filepath);
		try {
			JSONObject json = new JSONObject(s);
			return scenarioForBumpRatioTest(json, index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Scenario scenarioForBumpRatioTest(JSONObject json, int index){
		Scenario sc = new Scenario();
		try {
			sc.coordinator = Coordinator.fromJSONObject( json.getJSONObject("coordinator") );
			sc.course = Course.fromJSONObject(json.getJSONObject("course") );
			sc.evaluation = Evaluation.fromJSONObject( json.getJSONObject( "evaluation") );
			sc.evaluation2 = Evaluation.fromJSONObject(json.getJSONObject("evaluation2"));
			JSONArray json_points = json.getJSONArray("submissionPoints"+index);
			int teamSize = json_points.length();//scenario depends on how many students submit evaluation
			JSONArray json_students = json.getJSONArray("students2");
			sc.students = new ArrayList<Student>();
			for(int i = 0; i < teamSize; i++){
				Student s = Student.fromJSONObject(json_students.getJSONObject(i));
				sc.students.add(s);
			}
			sc.submissionPoints = new String[json_points.length()];
			for (int i = 0; i < json_points.length(); i++){
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
}