package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchQueryException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

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
public abstract class EntitiesDb<E extends BaseEntity, A extends EntityAttributes<E>> {

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

    protected static final Logger log = Logger.getLogger();

    /**
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public E createEntity(A entityToAdd) throws InvalidParametersException, EntityAlreadyExistsException {
        return createEntity(entityToAdd, true);
    }

    public List<A> createEntities(Collection<A> entitiesToAdd) throws InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToAdd);

        List<A> entitiesToUpdate = new ArrayList<>();
        List<E> entities = new ArrayList<>();

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
        }

        saveEntities(entities, entitiesToAdd);

        return entitiesToUpdate;
    }

    /**
     * Creates multiple entities without checking for existence. Also calls {@link #flush()},
     * leading to any previously deferred operations being written immediately.
     *
     * @return list of created entities.
     */
    @SuppressWarnings("PMD.UnnecessaryLocalBeforeReturn") // Needs to flush before returning
    public List<E> createEntitiesWithoutExistenceCheck(Collection<A> entitiesToAdd) throws InvalidParametersException {
        List<E> createdEntities = createEntitiesDeferred(entitiesToAdd);
        flush();
        return createdEntities;
    }

    /**
     * Queues creation of multiple entities. No actual writes are done until {@link #flush()} is called.
     * Note that there is no check for existence - existing entities will be overwritten.
     * If multiple entities with the same key are queued, only the last one queued will be created.
     *
     * @return list of created entities.
     */
    public List<E> createEntitiesDeferred(Collection<A> entitiesToAdd) throws InvalidParametersException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToAdd);

        List<E> entities = new ArrayList<>();

        for (A entityToAdd : entitiesToAdd) {
            entityToAdd.sanitizeForSaving();

            if (!entityToAdd.isValid()) {
                throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
            }

            E entity = entityToAdd.toEntity();
            entities.add(entity);
        }

        saveEntitiesDeferred(entities, entitiesToAdd);

        return entities;
    }

    /**
     * Warning: Do not use this method unless a previous update might cause
     * adding of the new entity to fail due to EntityAlreadyExists exception
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    public E createEntityWithoutExistenceCheck(A entityToAdd) throws InvalidParametersException {
        try {
            return createEntity(entityToAdd, false);
        } catch (EntityAlreadyExistsException e) {
            Assumption.fail("Caught exception thrown by existence check even with existence check disabled");
            return null;
        }
    }

    private E createEntity(A entityToAdd, boolean shouldCheckExistence)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);

        entityToAdd.sanitizeForSaving();

        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }

        // TODO: Do we really need special identifiers? Can just use ToString()?
        // Answer: Yes. We can use toString.
        if (shouldCheckExistence && hasEntity(entityToAdd)) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.getEntityTypeAsString())
                    + entityToAdd.getIdentificationString();
            log.info(error);
            throw new EntityAlreadyExistsException(error);
        }

        E entity = entityToAdd.toEntity();

        saveEntity(entity, entityToAdd);

        return entity;
    }

    public void saveEntity(E entityToSave) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToSave);

        saveEntity(entityToSave, makeAttributes(entityToSave));
    }

    protected void saveEntity(E entityToSave, A entityToSaveAttributesForLogging) {
        ofy().save().entity(entityToSave).now();
        log.info(entityToSaveAttributesForLogging.getBackupIdentifier());
    }

    protected void saveEntities(Collection<E> entitiesToSave) {
        saveEntities(entitiesToSave, makeAttributes(entitiesToSave));
    }

    protected void saveEntities(Collection<E> entitiesToSave, Collection<A> entitiesToSaveAttributesForLogging) {
        for (A attributes : entitiesToSaveAttributesForLogging) {
            log.info(attributes.getBackupIdentifier());
        }
        ofy().save().entities(entitiesToSave).now();
    }

    protected void saveEntitiesDeferred(Collection<E> entitiesToSave) {
        saveEntitiesDeferred(entitiesToSave, makeAttributes(entitiesToSave));
    }

    protected void saveEntitiesDeferred(Collection<E> entitiesToSave, Collection<A> entitiesToSaveAttributesForLogging) {
        for (A attributes : entitiesToSaveAttributesForLogging) {
            log.info(attributes.getBackupIdentifier());
        }
        ofy().defer().save().entities(entitiesToSave);
    }

    public static void flush() {
        ofy().flush();
    }

    // TODO: use this method for subclasses.
    /**
     * Note: This is a non-cascade delete.<br>
     *   <br> Fails silently if there is no such object.
     */
    public void deleteEntity(A entityToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToDelete);

        ofy().delete().keys(getEntityQueryKeys(entityToDelete)).now();
        log.info(entityToDelete.getBackupIdentifier());
    }

    public void deleteEntities(Collection<A> entitiesToDelete) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entitiesToDelete);

        List<Key<E>> keysToDelete = new ArrayList<>();
        for (A entityToDelete : entitiesToDelete) {
            Key<E> keyToDelete = getEntityQueryKeys(entityToDelete).first().now();
            if (keyToDelete == null) {
                continue;
            }
            keysToDelete.add(keyToDelete);
            log.info(entityToDelete.getBackupIdentifier());
        }

        ofy().delete().keys(keysToDelete).now();
    }

    protected void deleteEntityDirect(E entityToDelete) {
        deleteEntityDirect(entityToDelete, makeAttributes(entityToDelete));
    }

    protected void deleteEntityDirect(E entityToDelete, A entityToDeleteAttributesForLogging) {
        ofy().delete().entity(entityToDelete).now();
        log.info(entityToDeleteAttributesForLogging.getBackupIdentifier());
    }

    protected void deleteEntitiesDirect(Collection<E> entitiesToDelete) {
        deleteEntitiesDirect(entitiesToDelete, makeAttributes(entitiesToDelete));
    }

    protected void deleteEntitiesDirect(Collection<E> entitiesToDelete, Collection<A> entitiesToDeleteAttributesForLogging) {
        for (A attributes : entitiesToDeleteAttributesForLogging) {
            log.info(attributes.getBackupIdentifier());
        }
        ofy().delete().entities(entitiesToDelete).now();
    }

    public void deletePicture(BlobKey key) {
        GoogleCloudStorageHelper.deleteFile(key);
    }

    protected abstract LoadType<E> load();

    /**
     * NOTE: This method must be overriden for all subclasses such that it will return the
     * Entity matching the EntityAttributes in the parameter.
     * @return    the Entity which matches the given {@link EntityAttributes} {@code attributes}
     *             based on the default key identifiers.
     */
    protected abstract E getEntity(A attributes);

    /**
     * NOTE: This method must be overriden for all subclasses such that it will return the key query for the
     * Entity matching the EntityAttributes in the parameter.
     * @return    the key query for the Entity which matches the given {@link EntityAttributes} {@code attributes}
     *             based on the default key identifiers.
     */
    protected abstract QueryKeys<E> getEntityQueryKeys(A attributes);

    public boolean hasEntity(A attributes) {
        return getEntityQueryKeys(attributes).first().now() != null;
    }

    protected abstract A makeAttributes(E entity);

    protected A makeAttributesOrNull(E entity) {
        return makeAttributesOrNull(entity, null);
    }

    protected A makeAttributesOrNull(E entity, String logMessage) {
        if (entity != null) {
            return makeAttributes(entity);
        }
        if (logMessage != null) {
            log.info(logMessage);
        }
        return null;
    }

    protected List<A> makeAttributes(Collection<E> entities) {
        List<A> attributes = new LinkedList<>();
        for (E entity : entities) {
            attributes.add(makeAttributes(entity));
        }
        return attributes;
    }

    protected Key<E> makeKeyOrNullFromWebSafeString(String webSafeString) {
        if (webSafeString == null) {
            return null;
        }
        try {
            return Key.create(webSafeString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    //the followings APIs are used by Teammates' search engine
    protected void putDocument(String indexName, SearchDocument document) {
        try {
            SearchManager.putDocument(indexName, document.build());
        } catch (Exception e) {
            log.severe("Failed to put searchable document in " + indexName + " for " + document.toString());
        }
    }

    protected void putDocuments(String indexName, List<SearchDocument> documents) {
        List<Document> searchDocuments = new ArrayList<>();
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
