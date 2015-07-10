package teammates.ui.template;


public class InstructorResultsParticipantPanel implements Comparable<InstructorResultsParticipantPanel>{
    private boolean isGiver; // if false, then participant is a recipient
    private String participantIdentifier; // email, team name, or %GENERAL%
    private String name; 
    
    private boolean isHasResponses;
    private String className;
    private String arrowClass;
    
    String mailtoStyle;
    String profilePictureLink;
    
    
    boolean isModerationButtonDisplayed;
    InstructorResultsModerationButton moderationButton;
    
    public String getParticipantIdentifier() {
        return participantIdentifier;
    }
    
    public void setParticipantIdentifier(String participantIdentifier) {
        this.participantIdentifier = participantIdentifier;
    }

    public boolean isGiver() {
        return isGiver;
    }

    public void setGiver(boolean isGiver) {
        this.isGiver = isGiver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasResponses() {
        return isHasResponses;
    }

    public void setHasResponses(boolean isHasResponses) {
        this.isHasResponses = isHasResponses;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getArrowClass() {
        return arrowClass;
    }

    public void setArrowClass(String arrowClass) {
        this.arrowClass = arrowClass;
    }

    public String getMailtoStyle() {
        return mailtoStyle;
    }

    public void setMailtoStyle(String mailtoStyle) {
        this.mailtoStyle = mailtoStyle;
    }

    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    public InstructorResultsModerationButton getModerationButton() {
        return moderationButton;
    }

    public void setModerationButton(InstructorResultsModerationButton moderationButton) {
        this.moderationButton = moderationButton;
    }

    public boolean isModerationButtonDisplayed() {
        return isModerationButtonDisplayed;
    }

    public void setModerationButtonDisplayed(boolean isModerationButtonDisplayed) {
        this.isModerationButtonDisplayed = isModerationButtonDisplayed;
    }

    @Override
    public int compareTo(InstructorResultsParticipantPanel o) {
        if (isHasResponses != o.isHasResponses) {
            return isHasResponses ? 1 : 0;
        } else {
            return name.compareTo(o.name);
        }
    }
    
}
