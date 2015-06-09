package teammates.ui.controller;

public class SessionResultsResponseRow {
    private String questionId;
    private String giverId;
    private String recipientId;

    private String giverTeam;
    private String recipientTeam;
    private String responseText;
    
    public boolean isModerationEnabled = true;
    
    public String getGiverTeam() {
        return giverTeam;
    }

    public String getRecipientTeam() {
        return recipientTeam;
    }

    public String getResponseText() {
        return responseText;
    }
    
    public SessionResultsResponseRow(String questionId, String giverId, String recipientId) {
        this.questionId = questionId;
        this.giverId = giverId;
        this.recipientId = recipientId;
    }
    
    public String getQuestionId() {
        return questionId;
    }
    
    public String getGiverId() {
        return giverId;
    }
    
    public String getRecipientId() {
        return recipientId;
    }
    
}
