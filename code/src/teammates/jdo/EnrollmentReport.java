package teammates.jdo;

public class EnrollmentReport 
{
	private String name;
	private String email;
	
	
	private EnrollmentStatus status;
	
	private boolean nameEdited;
	private boolean teamNameEdited;
	private boolean commentsEdited;
	
	public EnrollmentReport(String name, String email, EnrollmentStatus status, boolean nameEdited, boolean teamNameEdited, boolean commentsEdited)
	{
		this.name = name;
		this.email = email;
		this.status = status;
		this.nameEdited = nameEdited;
		this.teamNameEdited = teamNameEdited;
		this.commentsEdited = commentsEdited;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return email;
	}
	public void setStatus(EnrollmentStatus status) {
		this.status = status;
	}
	public EnrollmentStatus getStatus() {
		return status;
	}
	public void setNameEdited(boolean nameEdited) {
		this.nameEdited = nameEdited;
	}
	public boolean isNameEdited() {
		return nameEdited;
	}
	public void setTeamNameEdited(boolean teamNameEdited) {
		this.teamNameEdited = teamNameEdited;
	}
	public boolean isTeamNameEdited() {
		return teamNameEdited;
	}
	public void setCommentsEdited(boolean commentsEdited) {
		this.commentsEdited = commentsEdited;
	}
	public boolean isCommentsEdited() {
		return commentsEdited;
	}
	
	

}
