package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;
import teammates.storage.search.SearchDocument;
import teammates.storage.search.SearchManager;
import teammates.storage.search.SearchQuery;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchQueryException;

/**
 * Base class for all classes performing CRUD operations against the Datastore.
 */
public abstract class EntitiesDb {

    public static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create a %s that exists: ";
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_ADMIN_EMAIL = "Trying to update non-existent Admin Email: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE = "Trying to update non-existent Student Profile: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update non-existent Course: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_INSTRUCTOR_PERMISSION =
            "Trying to update non-existing InstructorPermission: ";
    public static final String ERROR_UPDATE_TO_EXISTENT_INTRUCTOR_PERMISSION =
            "Trying to update to existent IntructorPermission: ";
    public static final String ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS = "Trying to create a Instructor that exists: ";
    public static final String ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR =
            "Trying to make an non-existent account an Instructor :";

    protected static final Logger log = Logger.getLogger();
    
    private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
    private static final ThreadLocal<PersistenceManager> PER_THREAD_PM = new ThreadLocal<PersistenceManager>();
    
    /**
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public Object createEntity(EntityAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);
        
        entityToAdd.sanitizeForSaving();
        
        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }
        
        // TODO: Do we really need special identifiers? Can just use ToString()?
        // Answer: Yes. We can use toString.
        Object existingEntity = getEntity(entityToAdd);
        if (existingEntity != null) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.getEntityTypeAsString())
                    + entityToAdd.getIdentificationString();
            log.info(error);
            throw new EntityAlreadyExistsException(error, existingEntity);
        }
        
        Object entity = entityToAdd.toEntity();
        getPm().makePersistent(entity);
        getPm().flush();

        // Wait for the operation to persist
        int elapsedTime = 0;
        Object createdEntity = getEntity(entityToAdd);
        if (Config.PERSISTENCE_CHECK_DURATION > 0) {
            while (createdEntity == null
                   && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                ThreadHelper.waitBriefly();
                createdEntity = getEntity(entityToAdd);
                //check before incrementing to avoid boundary case problem
                if (createdEntity == null) {
                    elapsedTime += ThreadHelper.WAIT_DURATION;
                }
            }
            if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                log.info("Operation did not persist in time: create"
                        + entityToAdd.getEntityTypeAsString() + "->"
                        + entityToAdd.getIdentificationString());
            }
        }
        
        log.info(entityToAdd.getBackupIdentifier());
        
        return entity;
    }
    
    public List<EntityAttributes> createEntities(Collection<? extends EntityAttributes> entitiesToAdd)
            throws InvalidParametersException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToAdd);
        
        List<EntityAttributes> entitiesToUpdate = new ArrayList<EntityAttributes>();
        List<Object> entities = new ArrayList<Object>();
        
        for (EntityAttributes entityToAdd : entitiesToAdd) {
            entityToAdd.sanitizeForSaving();
            
            if (!entityToAdd.isValid()) {
                throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
            }
            
            if (getEntity(entityToAdd) == null) {
                entities.add(entityToAdd.toEntity());
            } else {
                entitiesToUpdate.add(entityToAdd);
            }
            
            log.info(entityToAdd.getBackupIdentifier());
        }
       
        getPm().makePersistentAll(entities);
        getPm().flush();
 
        return entitiesToUpdate;

    }
    
    public List<Object> createAndReturnEntities(Collection<? extends EntityAttributes> entitiesToAdd)
            throws InvalidParametersException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToAdd);
        
        List<EntityAttributes> entitiesToUpdate = new ArrayList<EntityAttributes>();
        List<Object> entities = new ArrayList<Object>();
        
        for (EntityAttributes entityToAdd : entitiesToAdd) {
            entityToAdd.sanitizeForSaving();
            
            if (!entityToAdd.isValid()) {
                throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
            }
            
            if (getEntity(entityToAdd) == null) {
                entities.add(entityToAdd.toEntity());
            } else {
                entitiesToUpdate.add(entityToAdd);
            }
            
            log.info(entityToAdd.getBackupIdentifier());
        }
        
        getPm().makePersistentAll(entities);
        getPm().flush();
 
        return entities;

    }

    
    /**
     * Warning: Do not use this method unless a previous update might cause
     * adding of the new entity to fail due to EntityAlreadyExists exception
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public Object createEntityWithoutExistenceCheck(EntityAttributes entityToAdd)
            throws InvalidParametersException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);
        
        entityToAdd.sanitizeForSaving();
        
        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }
        
        Object entity = entityToAdd.toEntity();
        getPm().makePersistent(entity);
        getPm().flush();

        // Wait for the operation to persist
        if (Config.PERSISTENCE_CHECK_DURATION > 0) {
            int elapsedTime = 0;
            Object entityCheck = getEntity(entityToAdd);
            while (entityCheck == null
                   && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                ThreadHelper.waitBriefly();
                entityCheck = getEntity(entityToAdd);
                //check before incrementing to avoid boundary case problem
                if (entityCheck == null) {
                    elapsedTime += ThreadHelper.WAIT_DURATION;
                }
            }
            if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                log.info("Operation did not persist in time: create"
                         + entityToAdd.getEntityTypeAsString() + "->"
                         + entityToAdd.getIdentificationString());
            }
        }
        log.info(entityToAdd.getBackupIdentifier());
        
        return entity;
    }
    
    // TODO: use this method for subclasses.
    /**
     * Note: This is a non-cascade delete.<br>
     *   <br> Fails silently if there is no such object.
     * <br> Preconditions:
     * <br> * {@code courseId} is not null.
     */
    public void deleteEntity(EntityAttributes entityToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToDelete);

        Object entity = getEntity(entityToDelete);

        if (entity == null) {
            return;
        }

        getPm().deletePersistent(entity);
        getPm().flush();
        
        // wait for the operation to persist
        if (Config.PERSISTENCE_CHECK_DURATION > 0) {
            int elapsedTime = 0;
            Object entityCheck = getEntity(entityToDelete);
            boolean isEntityDeleted = entityCheck == null || JDOHelper.isDeleted(entityCheck);
            while (!isEntityDeleted
                    && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                ThreadHelper.waitBriefly();
                entityCheck = getEntity(entityToDelete);
                
                isEntityDeleted = entityCheck == null || JDOHelper.isDeleted(entityCheck);
                //check before incrementing to avoid boundary case problem
                if (!isEntityDeleted) {
                    elapsedTime += ThreadHelper.WAIT_DURATION;
                }
            }
            if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                log.info("Operation did not persist in time: delete"
                        + entityToDelete.getEntityTypeAsString() + "->"
                        + entityToDelete.getIdentificationString());
            }
        }
        log.info(entityToDelete.getBackupIdentifier());
    }
    
    public void deleteEntities(Collection<? extends EntityAttributes> entitiesToDelete) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToDelete);
        List<Object> entities = new ArrayList<Object>();
        for (EntityAttributes entityToDelete : entitiesToDelete) {
            Object entity = getEntity(entityToDelete);
            if (entity != null) {
                entities.add(entity);
                log.info(entityToDelete.getBackupIdentifier());
            }
        }
        
        getPm().deletePersistentAll(entities);
        getPm().flush();
    }
    
    public void commitOutstandingChanges() {
        closePm();
    }
    
    protected void closePm() {
        if (!getPm().isClosed()) {
            getPm().close();
        }
    }
    
    public void deletePicture(BlobKey key) {
        GoogleCloudStorageHelper.deleteFile(key);
    }
    
    /**
     * NOTE: This method must be overriden for all subclasses such that it will return the Entity
     * matching the EntityAttributes in the parameter.
     * @return    the Entity which matches the given {@link EntityAttributes} {@code attributes}
     *             based on the default key identifiers. Returns null if it
     *             does not already exist in the Datastore.
     */
    protected abstract Object getEntity(EntityAttributes attributes);
    
    protected PersistenceManager getPm() {
        PersistenceManager pm = PER_THREAD_PM.get();
        if (pm != null && !pm.isClosed()) {
            return pm;
        }
        
        if (pm != null && pm.isClosed()) {
            PER_THREAD_PM.remove();
        }
        pm = PMF.getPersistenceManager();
        PER_THREAD_PM.set(pm);
        return pm;
    }
    
    //the followings APIs are used by Teammates' search engine
    protected void putDocument(String indexName, SearchDocument document) {
        try {
            SearchManager.putDocument(indexName, document.build());
        } catch (Exception e) {
            log.info("Failed to put searchable document in " + indexName + " for " + document.toString());
        }
    }
    
    protected Results<ScoredDocument> searchDocuments(String indexName, SearchQuery query) {
        try {
            if (query.getFilterSize() > 0) {
                return SearchManager.searchDocuments(indexName, query.toQuery());
            }
            return null;
        } catch (SearchQueryException e) {
            log.info("Unsupported query for this query string: " + query.toString());
            return null;
        }
    }
    
    protected void deleteDocument(String indexName, String documentId) {
        try {
            SearchManager.deleteDocument(indexName, documentId);
        } catch (Exception e) {
            log.info("Unable to delete document in the index: " + indexName + " with document id " + documentId);
        }
    }
    
}
