package teammates.testing.object;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Course {

	@SerializedName("id")
	public String courseId;
	
	@SerializedName("name")
	public String courseName;
	
	public transient ArrayList<Student> students;

	public Course() {
	}

	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public static Course fromJSONObject(JSONObject json) throws JSONException {
		Course course = new Course();
		try {
			course.courseName = json.getString("name");
			course.courseId = json.getString("id");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return course;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", courseId);
			json.put("name", courseName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}

}
