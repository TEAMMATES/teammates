package teammates.storage.api;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.datastore.Datastore;

public abstract class EntitiesDb {

	public static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create a %s that exists: ";
	public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
	public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
	public static final String ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS = "Trying to create a Instructor that exists: ";
	public static final String ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR = "Trying to make an non-existent account an Instructor :";

	private static final Logger log = Common.getLogger();
	
	/**
	 * Preconditions: 
	 * <br> * {@code entityToAdd} is not null and has valid data.
	 */
	public void createEntity(EntityAttributes entityToAdd) 
			throws InvalidParametersException, EntityAlreadyExistsException {

		Assumption.assertNotNull(
				Common.ERROR_DBLEVEL_NULL_INPUT, entityToAdd);
		
		if (!entityToAdd.isValid()) {
			throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
		}
		
		// TODO: Do we really need special identifiers? Can just use ToString()?
		if (getEntity(entityToAdd) != null) {
			String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.getEntityTypeAsString())
					+ entityToAdd.getIdentificationString();
			log.info(error);
			throw new EntityAlreadyExistsException(error);
		}
		
		Object entity = entityToAdd.toEntity();
		getPM().makePersistent(entity);
		getPM().flush();

		// Wait for the operation to persist
		int elapsedTime = 0;
		Object objectCheck = getEntity(entityToAdd);
		while ((objectCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			objectCheck = getEntity(entityToAdd);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: create"
					+ entityToAdd.getEntityTypeAsString() + "->"
					+ entityToAdd.getIdentificationString());
		}
	}
	
	// TODO: use this method for subclasses.
	/**
	 * Note: This is a non-cascade delete.<br>
	 *   <br> Fails silently if there is no such object.
	 * <br> Preconditions: 
	 * <br> * {@code courseId} is not null.
	 */
	public void deleteEntity(EntityAttributes entityToDelete) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, entityToDelete);

		Object entity = getEntity(entityToDelete);

		if (entity == null) {
			return;
		}

		getPM().deletePersistent(entity);
		getPM().flush();

		// wait for the operation to persist
		int elapsedTime = 0;
		Object entityCheck = getEntity(entityToDelete);
		while ((entityCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			entityCheck = getEntity(entityToDelete);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: delete"
					+ entityToDelete.getEntityTypeAsString() + "->"
					+ entityToDelete.getIdentificationString());
		}

	}
	
	/**
	 * NOTE: This method must be overriden for all subclasses such that it will return the Entity
	 * matching the EntityAttributes in the parameter.
	 * @return	the Entity which matches the given {@link EntityAttributes} {@code attributes}
	 * 			based on the default key identifiers. Returns null if it 
	 * 			does not already exist in the Datastore. 
	 */
	protected abstract Object getEntity(EntityAttributes attributes) ;
	
	protected PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	};
}
