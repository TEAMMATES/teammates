package teammates.ui.template;

public class AdminSearchStudentFeedbackSession {
    private String fsName;
    private String link;

    public AdminSearchStudentFeedbackSession(String fsName, String link) {
        this.fsName = fsName;
        this.link = link;
    }

    public String getFsName() {
        return fsName;
    }

    public String getLink() {
        return link;
    }
}
