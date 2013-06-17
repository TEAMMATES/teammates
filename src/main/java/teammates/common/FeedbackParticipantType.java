package teammates.common;

public enum FeedbackParticipantType {
	// booleans represent: isValidGiver?, isValidRecipient?
	// Strings represents: option shown in giver select box, option shown in recipient select box, 
	// text displayed during feedback submission.
	SELF (true, true, "Me (Session creator)", "Giver (Self feedback)", "You"),
	STUDENTS (true, true, "Students in this course", "Other students in the course", "Other students in the course"),
	INSTRUCTORS (true, true, "Instructors in this course", "Instructors in the course", "Instructors"),
	// TODO: This is a special condition that needs to be taken care of somewhere (fieldValidator?),
	// i.e. if giver is TEAMS, recipients cannot be OWN_TEAM_MEMBERS
	TEAMS (true, true, "Teams in this course", "Other teams in the course", ""),
	OWN_TEAM (false, true, "", "Giver's team" ,""),
	OWN_TEAM_MEMBERS (false, true, "", "Giver's team members", "Your team members"),
	// NOTE: The following two are only for answer visibility purposes.
	// They are "participants" only in the sense that they are able to
	// participate in the viewing of responses.
	RECEIVER (false, false, "", "", "The receiving"),
	RECEIVER_TEAM_MEMBERS (false, false, "", "", "The recipient's team members"),
	NONE (false, true, "", "Nobody specific (For general class feedback)", "");
	
	private final boolean validGiver;
	private final boolean validRecipient;
	private String displayNameGiver;
	private String displayNameRecipient;
	private String displayNameVisibility;
	
	FeedbackParticipantType(boolean isGiver, boolean isRecipient,
			String displayNameGiver, String displayNameRecipient, String displayNameVisibility) {
		this.validGiver = isGiver;
		this.validRecipient = isRecipient;
		this.displayNameGiver = displayNameGiver;
		this.displayNameRecipient = displayNameRecipient;
		this.displayNameVisibility = displayNameVisibility;
	}
	
	public boolean isValidGiver() {
		return validGiver;
	}
	
	public boolean isValidRecipient() {
		return validRecipient;
	}
	
	public String toDisplayGiverName(){
		return displayNameGiver;
	}
	
	public String toDisplayRecipientName(){
		return displayNameRecipient;
	}
	
	public String toDisplayNameVisibility(){
		return displayNameVisibility;
	}
}
