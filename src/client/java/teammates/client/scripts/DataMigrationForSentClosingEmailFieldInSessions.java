package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const.SystemParams;
import teammates.logic.api.Logic;
import teammates.storage.entity.FeedbackSession;

public class DataMigrationForSentClosingEmailFieldInSessions extends RemoteApiClient {

    private static final Logic logic = new Logic();

    private boolean isPreview = true;

    public static void main(String[] args) throws IOException {
        new DataMigrationForSentClosingEmailFieldInSessions().doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<FeedbackSessionAttributes> sessions = getNonPrivateFeedbackSessions();
        for (FeedbackSessionAttributes session : sessions) {
            populateClosingEmailField(session);
        }
    }

    private void populateClosingEmailField(FeedbackSessionAttributes session) {
        session.setSentClosedEmail(session.isClosed());
        session.setSentClosingEmail(
                session.isClosed()
                || !session.isClosedAfter(SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT));

        if (isPreview) {
            System.out.println("sentClosingEmail and sentClosedEmail for " + session.getSessionName()
                               + " in course " + session.getCourseId() + " to be set to " + session.isClosed());
            return;
        }

        try {
            logic.updateFeedbackSession(session);
        } catch (Exception e) {
            System.out.println("Failed to set attribute sentClosingEmail and sentClosedEmail for session "
                               + session.getSessionName() + " in course " + session.getCourseId() + ".");
            e.printStackTrace();
        }
    }

    private List<FeedbackSessionAttributes> getNonPrivateFeedbackSessions() {
        List<FeedbackSessionAttributes> sessions = new ArrayList<FeedbackSessionAttributes>();
        List<FeedbackSession> sessionEntities = getNonPrivateFeedbackSessionEntities();
        for (FeedbackSession sessionEntity : sessionEntities) {
            sessions.add(new FeedbackSessionAttributes(sessionEntity));
        }
        return sessions;
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackSession> getNonPrivateFeedbackSessionEntities() {
        Query q = PM.newQuery(FeedbackSession.class);
        q.declareParameters("Enum private");
        q.setFilter("feedbackSessionType != private");

        return (List<FeedbackSession>) q.execute(FeedbackSessionType.PRIVATE);
    }

}
