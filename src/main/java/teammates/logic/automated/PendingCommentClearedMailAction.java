package teammates.logic.automated;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.Emails;

public class PendingCommentClearedMailAction extends EmailAction {
    private String courseId;
    private String recipientEmailsKey;
    private Cache cache;
    
    public PendingCommentClearedMailAction(HttpServletRequest req) {
        super(req);
        initializeNameAndDescription();
        
        courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
        recipientEmailsKey = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.RECIPIENTS);
        Assumption.assertNotNull(recipientEmailsKey);
    }

    public PendingCommentClearedMailAction(HashMap<String, String> paramMap) {
        super(paramMap);
        initializeNameAndDescription();
        
        courseId = paramMap.get(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
        recipientEmailsKey = paramMap.get(ParamsNames.RECIPIENTS);
        Assumption.assertNotNull(recipientEmailsKey);
    }

    @Override
    protected void doPostProcessingForSuccesfulSend() {
        //
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<MimeMessage> prepareMailToBeSent()
            throws MessagingException, IOException, EntityDoesNotExistException {
        Emails emailManager = new Emails();
        List<MimeMessage> preparedEmails = null;
        Set<String> recipients = null;
        
        try{
            initializeCache();
            log.info("Fetching recipient emails for pending comments in course : "
                    + courseId);
            recipients = (Set<String>) cache.get(recipientEmailsKey);
        } catch (CacheException e) {
            log.severe("Recipient emails for pending comments in course : " + courseId +
                    " could not be fetched");
        }
        
        if(recipients != null) {
            preparedEmails = emailManager
                            .generatePendingCommentsClearedEmails(courseId, recipients);
        } else {
            log.severe("Recipient emails for pending comments in course : " + courseId +
                       " could not be fetched");
        }
        return preparedEmails;
    }

    @SuppressWarnings("unchecked")
    private void initializeCache() throws CacheException {
        @SuppressWarnings("rawtypes")
        Map cacheProps = new HashMap();
        cacheProps.put(GCacheFactory.EXPIRATION_DELTA, 1800);
        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
        cache = cacheFactory.createCache(cacheProps);
    }

    private void initializeNameAndDescription() {
        actionName = "pendingCommentClearedMailAction";
        actionDescription = "clear pending comments";
    }
}
