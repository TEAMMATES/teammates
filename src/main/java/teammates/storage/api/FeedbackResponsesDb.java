package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponsesDb extends EntitiesDb {

    private static final Logger log = Utils.getLogger();

    public void createFeedbackResponses(final Collection<FeedbackResponseAttributes> responsesToAdd) throws InvalidParametersException{
        List<EntityAttributes> responsesToUpdate = createEntities(responsesToAdd);
        for (EntityAttributes entity : responsesToUpdate){
            FeedbackResponseAttributes response = (FeedbackResponseAttributes) entity;
            try {
                updateFeedbackResponse(response);
            } catch (EntityDoesNotExistException e) {
             // This situation is not tested as replicating such a situation is 
             // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponseAttributes getFeedbackResponse(final String feedbackResponseId) {
        FeedbackResponse feedbackResponse = getFeedbackResponseEntityWithCheck(feedbackResponseId);
        if (feedbackResponse == null) {
            return null;
        }
        return new FeedbackResponseAttributes(feedbackResponse);    
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseAttributes getFeedbackResponse (
            final String feedbackQuestionId, final String giverEmail, final String receiverEmail) {
        FeedbackResponse feedbackResponse = 
                getFeedbackResponseEntityWithCheck (feedbackQuestionId, giverEmail, receiverEmail);
        if (feedbackResponse == null) {
            return null;
        }
        return new FeedbackResponseAttributes(feedbackResponse);        
    }
    

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityWithCheck(final String feedbackResponseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        
        FeedbackResponse fr = 
                getFeedbackResponseEntity(feedbackResponseId);
        
        if (fr == null) {
            log.info("Trying to get non-existent response: " +
                    feedbackResponseId + ".");
            return null;
        }
        
        return fr;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityWithCheck (
            final String feedbackQuestionId, final String giverEmail, final String receiverEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);
        
        FeedbackResponse fr = 
                getFeedbackResponseEntity(feedbackQuestionId, giverEmail, receiverEmail);
        
        if (fr == null) {
            log.warning("Trying to get non-existent response: " +
                    feedbackQuestionId + "/" + "from: " +
                    giverEmail + " to: " + receiverEmail );
            return null;
        }
        return fr;        
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityOptimized (final FeedbackResponseAttributes response) {
         return (FeedbackResponse) getEntity(response); 
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection (
            final String feedbackQuestionId, final String section) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;        
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion (
            final String feedbackQuestionId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestion(feedbackQuestionId);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;        
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.This function will find the responses for a
     * specified question within a given range
     * 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(
            final String feedbackQuestionId, final long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestionWithinRange(feedbackQuestionId, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;   
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            final String feedbackSessionName, final String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;        
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            final String feedbackSessionName, final String courseId, final long range) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSessionWithinRange(feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;        
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            final String feedbackSessionName, final String courseId, final String section) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionInSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            final String feedbackSessionName, final String courseId, final String section) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionFromSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            final String feedbackSessionName, final String courseId, final String section) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionToSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            final String feedbackSessionName, final String courseId, final String section, final long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionInSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            final String feedbackSessionName, final String courseId, final String section, final long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionFromSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            final String feedbackSessionName, final String courseId, final String section, final long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionToSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion (
            final String feedbackQuestionId, final String receiver) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestionInSection (
            final String feedbackQuestionId, final String receiver, final String section) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForQuestionInSection(feedbackQuestionId, receiver, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion (
            final String feedbackQuestionId, final String giverEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForQuestion(feedbackQuestionId, giverEmail);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestionInSection (
            final String feedbackQuestionId, final String giverEmail, final String section) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForQuestionInSection(feedbackQuestionId, giverEmail, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;
    }

    /**
     *  Preconditions: <br>
     * * All parameters are non-null.
     *  @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForSessionWithinRange (final String giverEmail, final String feedbackSessionName, final String courseId, final long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        Collection<FeedbackResponse> frList = 
                getFeedbackResponseEntitiesFromGiverForSessionWithinRange(giverEmail, feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> fraList = 
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse (
            final String courseId, final String receiver) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse (
            final String courseId, final String giverEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForCourse(courseId, giverEmail);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        
        return fraList;
    }
    
    /**
     * Updates the feedback response identified by {@code newAttributes.getId()} and 
     *   changes the {@code updatedAt} timestamp to be the time of update.
     * For the remaining parameters, the existing value is preserved 
     *   if the parameter is null (due to 'keep existing' policy).<br> 
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     * @throws EntityDoesNotExistException 
     * @throws InvalidParametersException 
     */
    public void updateFeedbackResponse(final FeedbackResponseAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        updateFeedbackResponse(newAttributes, false);
    }
    
    /**
     * Updates the feedback response identified by {@code newAttributes.getId()} 
     * For the remaining parameters, the existing value is preserved 
     *   if the parameter is null (due to 'keep existing' policy).<br> 
     * The timestamp for {@code updatedAt} is independent of the {@code newAttributes}
     *   and depends on the value of {@code keepUpdateTimestamp}
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     */
    public void updateFeedbackResponse(final FeedbackResponseAttributes newAttributes, final boolean keepUpdateTimestamp) 
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, 
                newAttributes);
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        
        FeedbackResponse fr = (FeedbackResponse) getEntity(newAttributes);
        
        updateFeedbackResponseOptimized(newAttributes, fr, keepUpdateTimestamp);
    }
    
    /**
     * Optimized to take in FeedbackResponse entity if available, to prevent reading the entity again.
     * Updates the feedback response identified by {@code newAttributes.getId()} 
     * For the remaining parameters, the existing value is preserved 
     *   if the parameter is null (due to 'keep existing' policy).<br> 
     * The timestamp for {@code updatedAt} is independent of the {@code newAttributes}
     *   and depends on the value of {@code keepUpdateTimestamp}
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     */
    private void updateFeedbackResponseOptimized(final FeedbackResponseAttributes newAttributes, final FeedbackResponse fr,
            final boolean keepUpdateTimestamp) throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, 
                newAttributes);
        
        //TODO: Sanitize values and update tests accordingly
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        
        if (fr == null || JDOHelper.isDeleted(fr)) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        
        fr.keepUpdateTimestamp = keepUpdateTimestamp;
        fr.setAnswer(newAttributes.responseMetaData);
        fr.setRecipientEmail(newAttributes.recipientEmail);
        fr.setGiverSection(newAttributes.giverSection);
        fr.setRecipientSection(newAttributes.recipientSection);
                
        log.info(newAttributes.getBackupIdentifier());
        getPM().close();
    }
    
    public void updateFeedbackResponseOptimized(final FeedbackResponseAttributes newAttributes, final FeedbackResponse fr) 
            throws InvalidParametersException, EntityDoesNotExistException {
        updateFeedbackResponseOptimized(newAttributes, fr, false);
    }
    
    public void deleteFeedbackResponsesForCourse(final String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<String> courseIds = new ArrayList<String>();
        courseIds.add(courseId);
        deleteFeedbackResponsesForCourses(courseIds);
        
    }
    
    public void deleteFeedbackResponsesForCourses(final List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<FeedbackResponse> feedbackResponses = getFeedbackResponseEntitiesForCourses(courseIds);
        
        getPM().deletePersistentAll(feedbackResponses);
        getPM().flush();
    }
    
    public List<FeedbackResponse> getFeedbackResponseEntitiesForCourses(final List<String> courseIds) {
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(courseIds);
        return feedbackResponses;
    }
    
    public List<FeedbackResponseAttributes> getFeedbackResponsesForCourse(final String courseId) {
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForCourse(courseId);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        return fraList;
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForCourse(final String courseId) {
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(courseId);
        return feedbackResponses;
    }
    
    private FeedbackResponse getFeedbackResponseEntity(final String feedbackResponseId) {
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackResponseIdParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackResponseId);
        
        if (feedbackResponses.isEmpty() || JDOHelper.isDeleted(feedbackResponses.get(0))) {
            return null;
        }
    
        return feedbackResponses.get(0);
    }

        
    private FeedbackResponse getFeedbackResponseEntity(
            final String feedbackQuestionId, final String giverEmail, final String receiver) {
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, " +
                "String giverEmailParam, String receiverParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && " +
                "giverEmail == giverEmailParam && " +
                "receiver == receiverParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, receiver);
        
        if (feedbackResponses.isEmpty() || JDOHelper.isDeleted(feedbackResponses.get(0))) {
            return null;
        }
    
        return feedbackResponses.get(0);
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionInSection(    
                final String feedbackQuestionId, final String section) {
        
        List<FeedbackResponse> feedbackResponses = new ArrayList<FeedbackResponse>();
       
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverSectionParam, String receiverSectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverSection == giverSectionParam && receiverSection == receiverSectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, section, section);
        feedbackResponses.addAll(firstQueryResponses);
         
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, section, "None");
        feedbackResponses.addAll(secondQueryResponses);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> thirdQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, "None", section);
        feedbackResponses.addAll(thirdQueryResponses);
       
        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(    
                final String feedbackQuestionId) {
    
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId);
        
        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionWithinRange(    
                final String feedbackQuestionId, final long range) {
    
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId);
        
        return feedbackResponses;
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(
            final String feedbackSessionName, final String courseId) {
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);
        
        return feedbackResponses;
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionWithinRange(
            final String feedbackSessionName, final String courseId, final long range) {
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
        q.setRange(0, range + 1);
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);
        
        return feedbackResponses;
    }
 
    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSection(
            final String feedbackSessionName, final String courseId, final String section) {

        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : firstQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : secondQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        return feedbackResponses.values();   
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSection(
            final String feedbackSessionName, final String courseId, final String section) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return  queryResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSection(
            final String feedbackSessionName, final String courseId, final String section) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return  queryResponses;
    }
    
    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSectionWithinRange(
            final String feedbackSessionName, final String courseId, final String section, final long range) {

        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        q.setRange(0, range + 1);
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : firstQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        q.setRange(0, range + 1);
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : secondQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        return feedbackResponses.values();   
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSectionWithinRange(
            final String feedbackSessionName, final String courseId, final String section, final long range) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return  queryResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSectionWithinRange(
            final String feedbackSessionName, final String courseId, final String section, final long range) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return  queryResponses;
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestion(
            final String feedbackQuestionId, final String receiver) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String receiverParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver);
        
        return feedbackResponses;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestionInSection(
            final String feedbackQuestionId, final String receiver, final String section) {
        
        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String receiverParam, String sectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam "
                    + "&& giverSection == sectionParam");
    
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver, section);
        for (FeedbackResponse response : firstQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam "
                + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver, section);
        for (FeedbackResponse response : secondQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        return feedbackResponses.values();   
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestion(
            final String feedbackQuestionId, final String giverEmail) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail);
        
        return feedbackResponses;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestionInSection(
            final String feedbackQuestionId, final String giverEmail, final String section) {
        
        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam, String sectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam "
                    + "&& giverSection == sectionParam");
    
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, section);
        for (FeedbackResponse response : firstQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam "
                + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, section);
        for (FeedbackResponse response : secondQueryResponses){
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }
        
        return feedbackResponses.values(); 
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForSessionWithinRange(
            final String giverEmail, final String feedbackSessionName, final String courseId, final long range) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String giverEmailParam, String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("giverEmail == giverEmailParam && feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
        q.setRange(0, range + 1);
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(giverEmail, feedbackSessionName, courseId);
        
        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForCourse(
            final String courseId, final String receiver) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam, String receiverParam");
        q.setFilter("courseId == courseIdParam && receiver == receiverParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(courseId, receiver);
        
        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForCourse(
            final String courseId, final String giverEmail) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
            (List<FeedbackResponse>) q.execute(courseId, giverEmail);
        
        return feedbackResponses;
    }
    
    @Override
    protected Object getEntity(final EntityAttributes attributes) {
        
        FeedbackResponseAttributes FeedbackResponseToGet =
                (FeedbackResponseAttributes) attributes;
        
        if (FeedbackResponseToGet.getId() != null) {
            return getFeedbackResponseEntity(FeedbackResponseToGet.getId());
        } else { 
            return getFeedbackResponseEntity(
                FeedbackResponseToGet.feedbackQuestionId,
                FeedbackResponseToGet.giverEmail,
                FeedbackResponseToGet.recipientEmail);
        }
    }
}
