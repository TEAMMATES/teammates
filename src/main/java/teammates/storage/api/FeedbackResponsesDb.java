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

    public void createFeedbackResponses(Collection<FeedbackResponseAttributes> responsesToAdd) throws InvalidParametersException{
        List<EntityAttributes> responsesToUpdate = createEntities(responsesToAdd);
        for(EntityAttributes entity : responsesToUpdate){
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
    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
        
        FeedbackResponse fr = 
                getFeedbackResponseEntity(feedbackResponseId);
        
        if (fr == null) {
            log.info("Trying to get non-existent response: " +
                    feedbackResponseId + ".");
            return null;
        }
        
        return new FeedbackResponseAttributes(fr);    
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public FeedbackResponseAttributes getFeedbackResponse (
            String feedbackQuestionId, String giverEmail, String receiverEmail) {
        
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
        return new FeedbackResponseAttributes(fr);        
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection (
            String feedbackQuestionId, String section) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;        
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion (
            String feedbackQuestionId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestion(feedbackQuestionId);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
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
            String feedbackQuestionId, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestionWithinRange(feedbackQuestionId, range);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;   
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;        
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSessionWithinRange(feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;        
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionInSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            fraList.add(new FeedbackResponseAttributes(fr));
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionFromSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            fraList.add(new FeedbackResponseAttributes(fr));
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionToSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            fraList.add(new FeedbackResponseAttributes(fr));
        }

        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionInSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            fraList.add(new FeedbackResponseAttributes(fr));
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionFromSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            fraList.add(new FeedbackResponseAttributes(fr));
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionToSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList){
            fraList.add(new FeedbackResponseAttributes(fr));
        }

        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion (
            String feedbackQuestionId, String receiver) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestionInSection (
            String feedbackQuestionId, String receiver, String section) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForQuestionInSection(feedbackQuestionId, receiver, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion (
            String feedbackQuestionId, String giverEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForQuestion(feedbackQuestionId, giverEmail);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestionInSection (
            String feedbackQuestionId, String giverEmail, String section) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForQuestionInSection(feedbackQuestionId, giverEmail, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse (
            String courseId, String receiver) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse (
            String courseId, String giverEmail) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForCourse(courseId, giverEmail);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();
        
        for (FeedbackResponse fr : frList) {
                fraList.add(new FeedbackResponseAttributes(fr));
        }
        
        return fraList;
    }
    
    /**
     * Updates the feedback response identified by {@code newAttributes.getId()} 
     * For the remaining parameters, the existing value is preserved 
     *   if the parameter is null (due to 'keep existing' policy).<br> 
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     */
    public void updateFeedbackResponse(FeedbackResponseAttributes newAttributes) 
        throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, 
                newAttributes);
        
        //TODO: Sanitize values and update tests accordingly
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        
        FeedbackResponse fr = (FeedbackResponse) getEntity(newAttributes);
        
        if (fr == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        
        fr.setAnswer(newAttributes.responseMetaData);
        fr.setRecipientEmail(newAttributes.recipientEmail);
        fr.setGiverSection(newAttributes.giverSection);
        fr.setRecipientSection(newAttributes.recipientSection);
                
        getPM().close();
    }
    
    public void deleteFeedbackResponsesForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<FeedbackResponse> feedbackResponseList = getFeedbackResponseEntitiesForCourses(courseIds);
        
        getPM().deletePersistentAll(feedbackResponseList);
        getPM().flush();
    }
    
    public List<FeedbackResponse> getFeedbackResponseEntitiesForCourses(List<String> courseIds) {
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponseList = (List<FeedbackResponse>) q.execute(courseIds);
        return feedbackResponseList;
    }
    
    
    private FeedbackResponse getFeedbackResponseEntity(String feedbackResponseId) {
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackResponseIdParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackResponseId);
        
        if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
            return null;
        }
    
        return FeedbackResponseList.get(0);
    }

        
    private FeedbackResponse getFeedbackResponseEntity(
            String feedbackQuestionId, String giverEmail, String receiver) {
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, " +
                "String giverEmailParam, String receiverParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && " +
                "giverEmail == giverEmailParam && " +
                "receiver == receiverParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, receiver);
        
        if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
            return null;
        }
    
        return FeedbackResponseList.get(0);
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionInSection(    
                String feedbackQuestionId, String section) {
        
        List<FeedbackResponse> FeedbackResponseList = new ArrayList<FeedbackResponse>();
       
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverSectionParam, String receiverSectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverSection == giverSectionParam && receiverSection == receiverSectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, section, section);
        FeedbackResponseList.addAll(firstQueryResponses);
         
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, section, "None");
        FeedbackResponseList.addAll(secondQueryResponses);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> thirdQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, "None", section);
        FeedbackResponseList.addAll(thirdQueryResponses);
       
        return FeedbackResponseList;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(    
                String feedbackQuestionId) {
    
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId);
        
        return FeedbackResponseList;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionWithinRange(    
                String feedbackQuestionId, long range) {
    
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId);
        
        return FeedbackResponseList;
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(
            String feedbackSessionName, String courseId) {
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);
        
        return FeedbackResponseList;
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
        q.setRange(0, range + 1);
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);
        
        return FeedbackResponseList;
    }
 
    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {

        Map<String, FeedbackResponse> FeedbackResponseList = new HashMap<String,FeedbackResponse>();

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for(FeedbackResponse response : firstQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for(FeedbackResponse response : secondQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        return FeedbackResponseList.values();   
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return  queryResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return  queryResponses;
    }
    
    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Map<String, FeedbackResponse> FeedbackResponseList = new HashMap<String,FeedbackResponse>();

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && giverSection == sectionParam");
        q.setRange(0, range + 1);
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for(FeedbackResponse response : firstQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam && receiverSection == sectionParam");
        q.setRange(0, range + 1);
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for(FeedbackResponse response : secondQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        return FeedbackResponseList.values();   
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

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
            String feedbackSessionName, String courseId, String section, long range) {

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
            String feedbackQuestionId, String receiver) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String receiverParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver);
        
        return FeedbackResponseList;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestionInSection(
            String feedbackQuestionId, String receiver, String section) {
        
        Map<String, FeedbackResponse> FeedbackResponseList = new HashMap<String,FeedbackResponse>();
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String receiverParam, String sectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam "
                    + "&& giverSection == sectionParam");
    
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver, section);
        for(FeedbackResponse response : firstQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam "
                + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver, section);
        for(FeedbackResponse response : secondQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        return FeedbackResponseList.values();   
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail);
        
        return FeedbackResponseList;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestionInSection(
            String feedbackQuestionId, String giverEmail, String section) {
        
        Map<String, FeedbackResponse> FeedbackResponseList = new HashMap<String,FeedbackResponse>();
        
        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam, String sectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam "
                    + "&& giverSection == sectionParam");
    
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, section);
        for(FeedbackResponse response : firstQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam "
                + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
            (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, section);
        for(FeedbackResponse response : secondQueryResponses){
            FeedbackResponseList.put(response.getId(), response);
        }
        
        return FeedbackResponseList.values(); 
    }
    
    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForCourse(
            String courseId, String receiver) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam, String receiverParam");
        q.setFilter("courseId == courseIdParam && receiver == receiverParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(courseId, receiver);
        
        return FeedbackResponseList;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForCourse(
            String courseId, String giverEmail) {

        Query q = getPM().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> FeedbackResponseList =
            (List<FeedbackResponse>) q.execute(courseId, giverEmail);
        
        return FeedbackResponseList;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        
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
