package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackRemindEmailParticularUsersWorkerServlet extends
        WorkerServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String feedbackSessionName = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_FEEDBACK);
        Assumption.assertNotNull(feedbackSessionName);
        
        String courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_COURSE);
        Assumption.assertNotNull(courseId);
        
        String[] usersToRemind = HttpRequestHelper
                .getValuesFromRequestParameterMap(req, ParamsNames.SUBMISSION_REMIND_USERLIST);
        
        try {
            FeedbackSessionsLogic.inst()
                    .sendReminderForFeedbackSessionParticularUsers(courseId,
                            feedbackSessionName, usersToRemind);
        } catch (EntityDoesNotExistException e) {
            log.severe("Unexpected error while sending emails " + e.getMessage());
        }
    }

}
