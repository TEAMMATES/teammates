package teammates.testing.object;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import teammates.api.Common;
import teammates.testing.lib.SharedLib;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;


public class TeamFormingSession {

	
	@SerializedName("course_id")
	public String courseID;
	
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
	
	@SerializedName("instr")
	public String instructions;
	
	@SerializedName("profile_template")
	public String profileTemplate;
	
	public String dateValue;
	public String nextTimeValue;
	
	public static TeamFormingSession createTeamFormingSession(String courseID, int gracePeriod, String instructions, String profileTemplate) {
		TeamFormingSession teamFormingSession = new TeamFormingSession();
		teamFormingSession.courseID = courseID;
		teamFormingSession.startTime = new Date(System.currentTimeMillis());
		teamFormingSession.endTime = new Date(System.currentTimeMillis() + 24*60*60*1000);
		
		teamFormingSession.gracePeriod = gracePeriod;
		teamFormingSession.profileTemplate = profileTemplate;
		teamFormingSession.instructions = instructions;
		
		teamFormingSession.nextTimeValue = Common.getNextTimeValue();
		teamFormingSession.dateValue = Common.getDateValue();
		
		
		return teamFormingSession;
	}
	
	public static TeamFormingSession fromJSONObject(JSONObject json) {
		TeamFormingSession teamFormingSession = new TeamFormingSession();
		try {
			teamFormingSession.profileTemplate = json.getString("profileTemplate");
			teamFormingSession.instructions = json.getString("instructions");
			teamFormingSession.gracePeriod = json.getInt("graceperiod");
			
			teamFormingSession.dateValue = Common.getDateValue();
			teamFormingSession.nextTimeValue = Common.getNextTimeValue();
			
			//teamFormingSession.startDate = SharedLib.getDateString();
			teamFormingSession.startTime = new Date(System.currentTimeMillis());
			teamFormingSession.endTime = new Date(System.currentTimeMillis() + 24*60*60*1000);
			//teamFormingSession.endTime = 23;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return teamFormingSession;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("profileTemplate", profileTemplate);
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
