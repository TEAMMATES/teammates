package teammates.common;

public enum FeedbackParticipantType {
	// booleans represent: isValidGiver?, isValidRecipient?
	SELF (true, true),
	STUDENTS (true, true),
	INSTRUCTORS (true, true),
	TEAMS (true, true),	// TODO: This is a special condition that needs to be taken care of somewhere (fieldValidator?),
						// i.e. if giver is TEAMS, recipients cannot be OWN_TEAM_MEMBERS
	OWN_TEAM (false, true),
	OWN_TEAM_MEMBERS (false, true),
	RECEIVER (false, false),	// This is only for answer visibility purposes.
	NONE (false, true);
	
	private final boolean validGiver;
	private final boolean validRecipient;
	
	FeedbackParticipantType(boolean isGiver, boolean isRecipient) {
		this.validGiver = isGiver;
		this.validRecipient = isRecipient;
	}
	
	public boolean isValidGiver() {
		return validGiver;
	}
	
	public boolean isValidRecipient() {
		return validRecipient;
	}
}
