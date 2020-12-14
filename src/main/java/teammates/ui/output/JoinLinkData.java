package teammates.ui.output;

/**
 * Output format for join link.
 */
public class JoinLinkData extends ApiOutput {
    private final String joinLink;

    public JoinLinkData(String joinLink) {
        this.joinLink = joinLink;
    }

    public String getJoinLink() {
        return joinLink;
    }
}
