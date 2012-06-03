package teammates.datatransfer;

import teammates.api.APIServlet.UserType;

public class AdminData extends UserData {
	public AdminData(String googleId){
		super.id = googleId;
		super.type = UserType.ADMIN;
	}

}
