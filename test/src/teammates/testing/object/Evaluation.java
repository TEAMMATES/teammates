package teammates.testing.object;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import teammates.testing.lib.SharedLib;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;


public class Evaluation {

	
	@SerializedName("course_id")
	public String courseID;
	
	@SerializedName("name")
	public String name;
	
	//@SerializedName("start_date")
	//public String startDate;
	
	@SerializedName("start_time")
	public Date startTime;
	
	//@SerializedName("deadline_date")
	//public String endDate;
	
	@SerializedName("end_time")
	public Date endTime;
	
	@SerializedName("timezone")
	public double timezone;
	
	@SerializedName("grace")
	public Integer gracePeriod;
	
	@SerializedName("comments_on")
	public String p2pcomments;
	
	@SerializedName("instr")
	public String instructions;
	
	public String dateValue;
	public String nextTimeValue;
	
	public static Evaluation fromJSONObject(JSONObject json) {
		Evaluation evaluation = new Evaluation();
		try {
			evaluation.name = json.getString("name");
			evaluation.p2pcomments = json.getString("p2pcomments");
			evaluation.instructions = json.getString("instructions");
			evaluation.gracePeriod = json.getInt("graceperiod");
			
			evaluation.dateValue = SharedLib.getDateValue();
			evaluation.nextTimeValue = SharedLib.getNextTimeValue();
			
			//evaluation.startDate = SharedLib.getDateString();
			evaluation.startTime = new Date(System.currentTimeMillis());
			evaluation.endTime = new Date(System.currentTimeMillis() + 24*60*60*1000);
			//evaluation.endTime = 23;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return evaluation;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("name", name);
			json.put("p2pcomments", p2pcomments);
			json.put("instructions", instructions);
			json.put("graceperiod", gracePeriod);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public String toJSON() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
