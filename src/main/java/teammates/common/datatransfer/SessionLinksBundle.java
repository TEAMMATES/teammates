package teammates.common.datatransfer;

import java.util.List;

/**
 * Holds the links for a user's feedback sessions, including the course join
 * link, submission links, and results links.
 */
public class SessionLinksBundle {

    private final String courseJoinLink;
    private final List<SessionSubmissionLink> submissionLinks;
    private final List<SessionResultLink> resultsLinks;

    public SessionLinksBundle(String courseJoinLink, List<SessionSubmissionLink> submissionLinks,
            List<SessionResultLink> resultsLinks) {
        this.courseJoinLink = courseJoinLink;
        this.submissionLinks = submissionLinks;
        this.resultsLinks = resultsLinks;
    }

    public String getCourseJoinLink() {
        return courseJoinLink;
    }

    public List<SessionSubmissionLink> getSubmissionLinks() {
        return submissionLinks;
    }

    public List<SessionResultLink> getResultsLinks() {
        return resultsLinks;
    }
}
