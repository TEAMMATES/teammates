package teammates.datatransfer;

import teammates.api.APIServlet.UserType;

public class AdminData extends UserData {
	public String name;
	public String email;

	public AdminData(String googleId){
		super.id = googleId;
		super.isAdmin = true;
	}

}
