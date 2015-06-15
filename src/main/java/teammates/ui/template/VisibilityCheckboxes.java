package teammates.ui.template;

public class VisibilityCheckboxes {
    
    private boolean isRecipientAbleToSeeComment;
    private boolean isRecipientAbleToSeeGiverName;
    
    private boolean isRecipientTeamAbleToSeeComment;
    private boolean isRecipientTeamAbleToSeeGiverName;
    private boolean isRecipientTeamAbleToSeeRecipientName;
    
    private boolean isRecipientSectionAbleToSeeComment;
    private boolean isRecipientSectionAbleToSeeGiverName;
    private boolean isRecipientSectionAbleToSeeRecipientName;
    
    private boolean isCourseStudentsAbleToSeeComment;
    private boolean isCourseStudentsAbleToSeeGiverName;
    private boolean isCourseStudentsAbleToSeeRecipientName;
    
    private boolean isInstructorsAbleToSeeComment;
    private boolean isInstructorsAbleToSeeGiverName;
    private boolean isInstructorsAbleToSeeRecipientName;
    
    public VisibilityCheckboxes(boolean iRATSC, boolean iRATSGN,
                                boolean iRTATSC, boolean iRTATSGN, boolean iRTATSRN,
                                boolean iRSATSC, boolean iRSATSGN, boolean iRSATSRN,
                                boolean iCSATSC, boolean iCSATSGN, boolean iCSATSRN,
                                boolean iIATSC, boolean iIATSGN, boolean iIATSRN) {
        this.isRecipientAbleToSeeComment = iRATSC;
        this.isRecipientAbleToSeeGiverName = iRATSGN;
        
        this.isRecipientTeamAbleToSeeComment = iRTATSC;
        this.isRecipientTeamAbleToSeeGiverName = iRTATSGN;
        this.isRecipientTeamAbleToSeeRecipientName = iRTATSRN;
        
        this.isRecipientSectionAbleToSeeComment = iRSATSC;
        this.isRecipientSectionAbleToSeeGiverName = iRSATSGN;
        this.isRecipientSectionAbleToSeeRecipientName = iRSATSRN;
        
        this.isCourseStudentsAbleToSeeComment = iCSATSC;
        this.isCourseStudentsAbleToSeeGiverName = iCSATSGN;
        this.isCourseStudentsAbleToSeeRecipientName = iCSATSRN;
        
        this.isInstructorsAbleToSeeComment = iIATSC;
        this.isInstructorsAbleToSeeGiverName = iIATSGN;
        this.isInstructorsAbleToSeeRecipientName = iIATSRN;
        
    }
    
    public boolean isRecipientAbleToSeeComment() {
        return isRecipientAbleToSeeComment;
    }

    public boolean isRecipientAbleToSeeGiverName() {
        return isRecipientAbleToSeeGiverName;
    }

    public boolean isRecipientTeamAbleToSeeComment() {
        return isRecipientTeamAbleToSeeComment;
    }

    public boolean isRecipientTeamAbleToSeeGiverName() {
        return isRecipientTeamAbleToSeeGiverName;
    }

    public boolean isRecipientTeamAbleToSeeRecipientName() {
        return isRecipientTeamAbleToSeeRecipientName;
    }

    public boolean isRecipientSectionAbleToSeeComment() {
        return isRecipientSectionAbleToSeeComment;
    }

    public boolean isRecipientSectionAbleToSeeGiverName() {
        return isRecipientSectionAbleToSeeGiverName;
    }

    public boolean isRecipientSectionAbleToSeeRecipientName() {
        return isRecipientSectionAbleToSeeRecipientName;
    }

    public boolean isCourseStudentsAbleToSeeComment() {
        return isCourseStudentsAbleToSeeComment;
    }

    public boolean isCourseStudentsAbleToSeeGiverName() {
        return isCourseStudentsAbleToSeeGiverName;
    }

    public boolean isCourseStudentsAbleToSeeRecipientName() {
        return isCourseStudentsAbleToSeeRecipientName;
    }

    public boolean isInstructorsAbleToSeeComment() {
        return isInstructorsAbleToSeeComment;
    }

    public boolean isInstructorsAbleToSeeGiverName() {
        return isInstructorsAbleToSeeGiverName;
    }

    public boolean isInstructorsAbleToSeeRecipientName() {
        return isInstructorsAbleToSeeRecipientName;
    }
}
