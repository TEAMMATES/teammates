package teammates.testing.object;

import java.util.ArrayList;

import org.json.JSONArray;

public class Team {
	public String teamname;
	public ArrayList<Student> students;
	
	public Team() {
		students = new ArrayList<Student>();
	}
	
	public static JSONArray toJSONArray(ArrayList<Team> teams) {
		JSONArray jsonarr = new JSONArray();
		for (Team t : teams) {
			jsonarr.put(t.teamname);
		}
		return jsonarr;
	}

	
	
}
