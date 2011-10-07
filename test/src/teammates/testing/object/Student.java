package teammates.testing.object;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import teammates.testing.Config;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Student {

	@SerializedName("google_id")
	public String google_id;
	
	@SerializedName("email")
	public String email;
	
	public transient String password;
	
	@SerializedName("coursename")
	public String courseID;
	
	@SerializedName("name")
	public String name;
	
	@SerializedName("comments")
	public String comments;
	
	public transient String courseKey = "";

	@SerializedName("teamname")
	public String teamName;
	
	
	public transient Team team;

	public Student() { }
	
	public static Student fromJSONObject(JSONObject json) {
		Student student = new Student();
		try {
			student.email = json.getString("email");
			student.name = json.getString("name");
			student.teamName = json.getString("team");
			student.google_id = json.getString("google_id");
			if (json.has("coursekey")) {
				student.courseKey = json.getString("coursekey");
			}
			student.password = Config.TEAMMATES_APP_PASSWD;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return student;
	}

	

	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	public static ArrayList<Student> fromJSONArray( JSONArray arr ) {
		ArrayList<Student> ls = new ArrayList<Student>();

		try {
			for (int i = 0; i < arr.length(); i++) {

				ls.add( Student.fromJSONObject( arr.getJSONObject(i) ) );
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ls;
	}

	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();

		try {
			obj.put("email", email);
			obj.put("name", name);
			obj.put("team", teamName);
			obj.put("coursekey", courseKey);
			obj.put("google_id", google_id);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return obj;
	}

	public static JSONArray toJSONArray(ArrayList<Student> students) {
		JSONArray jsonarr = new JSONArray();
		for (Student s : students) {
			jsonarr.put(s.toJSONObject());
		}
		return jsonarr;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("name: " + name);
		sb.append("\ncourseid: " + courseID);
		sb.append("\nemail:" + email);
		sb.append("\nteam:" + teamName);
		sb.append("\nkey: " + courseKey);
		sb.append("\ncomments" + comments);
		
		return sb.toString();
		
	}
	

}
