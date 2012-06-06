package teammates.datatransfer;


public class AdminData extends UserData {
	public String name;
	public String email;

	public AdminData(String googleId){
		super.id = googleId;
		super.isAdmin = true;
	}

}
