package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.FeedbackResponseStatisticAttributes;
import teammates.storage.entity.FeedbackResponseStatistic;

public class FeedbackResponseStatisticDb extends EntitiesDb<FeedbackResponseStatistic, FeedbackResponseStatisticAttributes> {
	private static final FeedbackResponseStatisticDb instance = new FeedbackResponseStatisticDb();

	public static FeedbackResponseStatisticDb inst() {
        return instance;
    }

	/**
	 * Checks whether there are existing entities in the database.
	 */
	@Override
	public boolean hasExistingEntities(FeedbackResponseStatisticAttributes feedbackResponseStatistic) {
		return !load()
                .filterKey(Key.create(FeedbackResponseStatistic.class,
                        FeedbackResponse.generateId(entityToCreate.getFeedbackQuestionId(),
                                entityToCreate.getGiver(), entityToCreate.getRecipient())))
                .list()
                .isEmpty();
	}

	@Override
	public LoadType<FeedbackResponseStatistic> load() {
		return ofy().load().type(FeedbackResponseStatistic.class); 
	}
	/**
     * Converts from entity to attributes.
     */
	public FeedbackResponseStatisticAttributes makeAttributes(FeedbackResponseStatistic statistic) {
		assert statistic != null;

        return FeedbackResponseStatisticAttributes.valueOf(statistic);
	}
	
}
