package teammates.testing.object;

import org.json.JSONException;
import org.json.JSONObject;

import teammates.testing.Config;


public class Coordinator {
	public String username;
	public String password;

	public Coordinator() {}
	
	public static Coordinator fromJSONObject(JSONObject json)  {
		Coordinator coord = new Coordinator();
		try {
			coord.username = json.getString("username");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		coord.password = Config.TEAMMATES_APP_PASSWD;
		return coord;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("username", username);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
}
