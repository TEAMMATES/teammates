package teammates.common.datatransfer;

public enum FeedbackParticipantType { 
	// booleans represent: isValidGiver?, isValidRecipient?
	// Strings represents: option shown in giver select box, option shown in recipient select box, 
	// text displayed during feedback submission.
	SELF (true, true, "Me (Session creator)", "Giver (Self feedback)", ""),
	STUDENTS (true, true, "Students in this course", "Other students in the course", "Other students in the course"),
	INSTRUCTORS (true, true, "Instructors in this course", "Instructors in the course", "Instructors in this course"),
	// TODO: This is a special condition that needs to be taken care of somewhere (fieldValidator?),
	// i.e. if giver is TEAMS, recipients cannot be OWN_TEAM_MEMBERS
	TEAMS (true, true, "Teams in this course", "Other teams in the course", ""),
	OWN_TEAM (false, true, "", "Giver's team" ,"Your team"),
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
	
	/**
	 * Formats the participant type as a giver for display to user.
	 * @return A user-friendly {@code String} representing this participant as a feedback giver.
	 */
	public String toDisplayGiverName(){
		return displayNameGiver;
	}
	
	/**
	 * Formats the participant type as a recipient for display to user.
	 * @return A user-friendly {@code String} representing this participant as a feedback recipient.
	 */
	public String toDisplayRecipientName(){
		return displayNameRecipient;
	}
	
	/**
	 * Formats the participant type for display to user in the response visibility section.
	 * @return A user-friendly {@code String} representing this participant directed to users who are
	 * responding to a feedback.
	 */
	public String toVisibilityString(){
		return displayNameVisibility;
	}
	
	/**
	 * Formats the participant type as a singular noun.
	 * @return A user-friendly {@code String} representing this participant in singular form.
	 */
	public String toSingularFormString() {
		switch (this) {
		case INSTRUCTORS:
			return "instructor";
		case STUDENTS:
			return "student";
		case TEAMS:
			return "team";
		case OWN_TEAM:
			return "team";
		default:
			return super.toString();
		}
	}
	
}
