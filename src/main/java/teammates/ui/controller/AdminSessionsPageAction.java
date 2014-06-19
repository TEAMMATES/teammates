package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class AdminSessionsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        Logic logic = new Logic();

        new GateKeeper().verifyAdminPrivileges(account);

        AdminSessionsPageData data = new AdminSessionsPageData(account);

        @SuppressWarnings("deprecation")
        // This method is deprecated to prevent unintended usage. This is an
        // intended usage.
        List<FeedbackSessionAttributes> allOpenFeedbackSessionsList = logic.getAllOpenFeedbackSessions();

        HashMap<String, List<FeedbackSessionAttributes>> map = new HashMap<String, List<FeedbackSessionAttributes>>();

        if (allOpenFeedbackSessionsList.isEmpty()) {

            isError = false;
            statusToUser.add("Currently No Ongoing Sessions");
            statusToAdmin = "Admin Sessions Page Load<br>" +
                            "<span class=\"bold\"> No Ongoing Sessions</span>";

            data.map = map;
            data.totalOngoingSessions = 0;
            data.hasUnknown = false;

            return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
        }

        data.totalOngoingSessions = allOpenFeedbackSessionsList.size();
        data.hasUnknown = false;

        for (FeedbackSessionAttributes fs : allOpenFeedbackSessionsList) {

            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(fs.courseId);

            if (!instructors.isEmpty()) {

                InstructorAttributes instructor = instructors.get(0);
               
                AccountAttributes account = logic.getAccount(instructor.googleId);

                if (account == null) {
                    putIntoUnknownList(map, fs);
                    data.hasUnknown = true;
                    continue;
                }

                if (map.get(account.institute) == null) {
                    List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
                    newList.add(fs);
                    map.put(account.institute, newList);
                } else {
                    map.get(account.institute).add(fs);
                }

            } else {
                putIntoUnknownList(map, fs);
                data.hasUnknown = true;
            }
        }

        data.map = map;
        statusToAdmin = "Admin Sessions Page Load<br>" +
                "<span class=\"bold\">Total Ongoing Sessions:</span> " +
                data.totalOngoingSessions;

        return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
    }

    private void putIntoUnknownList(HashMap<String, List<FeedbackSessionAttributes>> map,
                                    FeedbackSessionAttributes fs) {

        if (map.get("Unknown") == null) {
            List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
            newList.add(fs);
            map.put("Unknown", newList);
        } else {
            map.get("Unknown").add(fs);
        }
    }

}
