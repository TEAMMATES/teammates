package teammates.ui.webapi.output;

import teammates.ui.webapi.action.CreateAccountAction;

/**
 * Output format for {@link CreateAccountAction}.
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
