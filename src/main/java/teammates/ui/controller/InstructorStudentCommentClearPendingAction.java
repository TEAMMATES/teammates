package teammates.ui.controller;

import java.util.Collections;
import java.util.Set;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails;
import teammates.logic.core.Emails.EmailType;

public class InstructorStudentCommentClearPendingAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        Set<String> recipientEmails = logic.getRecipientEmailsForPendingComments(courseId);
        String recipientEmailsKey = Const.ParamsNames.RECIPIENTS + recipientEmails.hashCode();
        
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            Cache cache = cacheFactory.createCache(Collections.emptyMap());
            cache.put(recipientEmailsKey, recipientEmails);
            
            Emails emails = new Emails();
            emails.addCommentReminderToEmailsQueue(courseId, recipientEmailsKey, EmailType.PENDING_COMMENT_CLEARED);
            
            logic.clearPendingComments(courseId);
            logic.clearPendingFeedbackResponseComments(courseId);
        } catch (CacheException e) {
            isError = true;
            statusToUser.add(Const.StatusMessages.COMMENT_CLEARED_UNSUCCESSFULLY);
            statusToAdmin = account.googleId + " cleared pending comments for course " + courseId + " unsuccessfully";
        }
        
        if(!isError){
            statusToUser.add(Const.StatusMessages.COMMENT_CLEARED);
            statusToAdmin = account.googleId + " cleared pending comments for course " + courseId;
        }
        
        return createRedirectResult((new PageData(account).getInstructorCommentsLink()) + "&" + Const.ParamsNames.COURSE_ID + "=" + courseId);
    }
}
