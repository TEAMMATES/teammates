package teammates.sqllogic.core;

import teammates.storage.sqlapi.FeedbackSessionsDb;

/**
 * Handles operations related to feedback sessions.
 *
 * @see FeedbackSession
 * @see FeedbackSessionsDb
 */
public final class FeedbackSessionsLogic {

    private static final FeedbackSessionsLogic instance = new FeedbackSessionsLogic();

    // private FeedbackSessionsDb fsDb;

    // private CoursesLogic coursesLogic;

    private FeedbackSessionsLogic() {
        // prevent initialization
    }

    public static FeedbackSessionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(FeedbackSessionsDb fsDb, CoursesLogic coursesLogic) {
        // this.fsDb = fsDb;
        // this.coursesLogic = coursesLogic;
    }

}
