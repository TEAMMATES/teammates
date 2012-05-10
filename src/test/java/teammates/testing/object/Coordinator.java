package teammates.testing.object;

import org.json.JSONException;
import org.json.JSONObject;

import teammates.testing.config.Config;


public class Coordinator {
	public String username;
	public String name;
	public String email;
	public String password;

	public Coordinator() {}
	
	public static Coordinator fromJSONObject(JSONObject json)  {
		Coordinator coord = new Coordinator();
		try {
			coord.username = json.getString("username");
			coord.name = json.getString("name");
			coord.email = json.getString("email");
		} catch (JSONException e) {
			//TODO: put a warning instead of stack trace?
			e.printStackTrace();
		}
		//TODO: password should be part of Json? -damith
		coord.password = Config.inst().TEAMMATES_APP_PASSWD;
		return coord;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		try {
			json.put("username", username);
			json.put("name", name);
			json.put("email", email);
			//TODO: password? -damith
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
}
