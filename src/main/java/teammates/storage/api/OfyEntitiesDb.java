package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchQueryException;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Logger;
import teammates.storage.entity.BaseEntity;
import teammates.storage.search.SearchDocument;
import teammates.storage.search.SearchManager;
import teammates.storage.search.SearchQuery;

/**
 * Base class for all classes performing CRUD operations against the Datastore.
 * @param <E> Specific entity class
 * @param <A> Specific attributes class
 */
public abstract class OfyEntitiesDb<E extends BaseEntity, A extends EntityAttributes> {

    public static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create a %s that exists: ";
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_ADMIN_EMAIL = "Trying to update non-existent Admin Email: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE = "Trying to update non-existent Student Profile: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update non-existent Course: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_INSTRUCTOR_PERMISSION =
            "Trying to update non-existing InstructorPermission: ";
    public static final String ERROR_UPDATE_TO_EXISTENT_INSTRUCTOR_PERMISSION =
            "Trying to update to existent InstructorPermission: ";
    public static final String ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS = "Trying to create a Instructor that exists: ";
    public static final String ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR =
            "Trying to make an non-existent account an Instructor :";

    private static final Logger log = Logger.getLogger();

    /**
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public E createEntity(A entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {

        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);

        entityToAdd.sanitizeForSaving();

        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }

        // TODO: Do we really need special identifiers? Can just use ToString()?
        // Answer: Yes. We can use toString.
        if (hasEntity(entityToAdd)) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.getEntityTypeAsString())
                    + entityToAdd.getIdentificationString();
            log.info(error);
            throw new EntityAlreadyExistsException(error);
        }

        E entity = entityToAdd.toEntity();

        ofy().save().entity(entity).now();

        log.info(entityToAdd.getBackupIdentifier());

        return entity;
    }

    public List<A> createEntities(Collection<? extends A> entitiesToAdd)
            throws InvalidParametersException {

        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToAdd);

        List<A> entitiesToUpdate = new ArrayList<A>();
        List<E> entities = new ArrayList<E>();

        for (A entityToAdd : entitiesToAdd) {
            entityToAdd.sanitizeForSaving();

            if (!entityToAdd.isValid()) {
                throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
            }

            if (hasEntity(entityToAdd)) {
                entitiesToUpdate.add(entityToAdd);
            } else {
                E entity = entityToAdd.toEntity();
                entities.add(entity);
            }

            log.info(entityToAdd.getBackupIdentifier());
        }

        ofy().save().entities(entities).now();

        return entitiesToUpdate;

    }

    /**
     * Warning: Do not use this method unless a previous update might cause
     * adding of the new entity to fail due to EntityAlreadyExists exception
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public E createEntityWithoutExistenceCheck(A entityToAdd)
            throws InvalidParametersException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);

        entityToAdd.sanitizeForSaving();

        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }

        E entity = entityToAdd.toEntity();
        ofy().save().entity(entity).now();

        log.info(entityToAdd.getBackupIdentifier());

        return entity;
    }

    // TODO: use this method for subclasses.
    /**
     * Note: This is a non-cascade delete.<br>
     *   <br> Fails silently if there is no such object.
     */
    public void deleteEntity(A entityToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToDelete);
        ofy().delete().entity(entityToDelete.toEntity()).now();
        log.info(entityToDelete.getBackupIdentifier());
    }

    protected void deleteEntityDirect(E entityToDelete, A entityToDeleteAttributesForLogging) {
        ofy().delete().entity(entityToDelete).now();
        log.info(entityToDeleteAttributesForLogging.getBackupIdentifier());
    }

    public void deleteEntities(Iterable<A> entityAttributesToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityAttributesToDelete);

        ArrayList<E> entitiesToDelete = new ArrayList<E>();
        for (A entityAttributeToDelete : entityAttributesToDelete) {
            E entityToDelete = entityAttributeToDelete.toEntity();
            entitiesToDelete.add(entityToDelete);
            log.info(entityAttributeToDelete.getBackupIdentifier());
        }

        ofy().delete().entities(entitiesToDelete).now();
    }

    protected void deleteEntitiesDirect(Iterable<E> entitiesToDelete, Iterable<A> entitiesToDeleteAttributesForLogging) {
        for (A entityAttributesToDelete : entitiesToDeleteAttributesForLogging) {
            log.info(entityAttributesToDelete.getBackupIdentifier());
        }
        ofy().delete().entities(entitiesToDelete).now();
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
    protected abstract E getEntity(A attributes);

    public abstract boolean hasEntity(A attributes);

    //the followings APIs are used by Teammates' search engine
    protected void putDocument(String indexName, SearchDocument document) {
        try {
            SearchManager.putDocument(indexName, document.build());
        } catch (Exception e) {
            log.severe("Failed to put searchable document in " + indexName + " for " + document.toString());
        }
    }

    protected void putDocuments(String indexName, List<SearchDocument> documents) {
        List<Document> searchDocuments = new ArrayList<Document>();
        for (SearchDocument document : documents) {
            searchDocuments.add(document.build());
        }
        try {
            SearchManager.putDocuments(indexName, searchDocuments);
        } catch (Exception e) {
            log.severe("Failed to batch put searchable documents in " + indexName + " for " + documents.toString());
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
