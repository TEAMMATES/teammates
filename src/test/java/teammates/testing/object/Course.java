package teammates.testing.object;

import java.util.ArrayList;

import org.json.JSONArray;
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

	public Course(String courseId, String courseName) {
		this.courseId = courseId;
		this.courseName = courseName;
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

	public static ArrayList<Course> fromJSONArray(JSONArray arr) {
		ArrayList<Course> ls = new ArrayList<Course>();

		try {
			for (int i = 0; i < arr.length(); i++)
				ls.add(Course.fromJSONObject(arr.getJSONObject(i)));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ls;
	}

}
