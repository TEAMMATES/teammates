package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchQueryException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
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

    public static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create an entity that exists: %s";
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
    public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT_PROFILE = "Trying to update non-existent Student Profile: ";

    protected static final Logger log = Logger.getLogger();

    /**
     * Creates the entity in the Datastore.
     *
     * @return created entity
     * @throws InvalidParametersException if the entity to create is invalid
     * @throws EntityAlreadyExistsException if the entity to create already exists
     */
    public A createEntity(A entityToCreate) throws InvalidParametersException, EntityAlreadyExistsException {
        return createEntity(entityToCreate, true);
    }

    private A createEntity(A entityToAdd, boolean shouldCheckExistence)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);

        entityToAdd.sanitizeForSaving();

        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }

        if (shouldCheckExistence && hasExistingEntities(entityToAdd)) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.toString());
            throw new EntityAlreadyExistsException(error);
        }

        E entity = entityToAdd.toEntity();

        ofy().save().entity(entity).now();
        log.info("Entity created: " + JsonUtils.toJson(entityToAdd));

        return makeAttributes(entity);
    }

    /**
     * Checks whether there are existing entities in the Datastore.
     */
    protected abstract boolean hasExistingEntities(A entityToCreate);

    /**
     * Puts an entity in the datastore without existence checking.
     *
     * <p>The document of the associated entity (if applicable) WILL NOT be updated.
     *
     * @return created entity
     * @throws InvalidParametersException if entity to put is not valid
     */
    public A putEntity(A entityToAdd) throws InvalidParametersException {
        try {
            return createEntity(entityToAdd, false);
        } catch (EntityAlreadyExistsException e) {
            Assumption.fail("Unreachable branch");
            return null;
        }
    }

    /**
     * Puts a collection of entity in the datastore without existence checking.
     *
     * <p>The documents of the associated entities (if applicable) WILL NOT be updated.
     *
     * @return created entities
     * @throws InvalidParametersException if any of entity to add is not valid
     */
    public List<A> putEntities(Collection<A> entitiesToAdd) throws InvalidParametersException {
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

        for (A attributes : entitiesToAdd) {
            log.info("Entity created: " + JsonUtils.toJson(attributes));
        }
        ofy().save().entities(entities).now();

        return makeAttributes(entities);
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

    /**
     * Deletes entity by key.
     */
    protected void deleteEntity(Key<?>... keys) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, (Object) keys);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, (Object[]) keys);

        for (Key<?> key : keys) {
            log.info(String.format("Delete entity %s of key (id: %d, name: %s)",
                    key.getKind(), key.getId(), key.getName()));
        }
        ofy().delete().keys(keys).now();
    }

    protected abstract LoadType<E> load();

    /**
     * NOTE: This method must be overriden for all subclasses such that it will return the
     * Entity matching the EntityAttributes in the parameter.
     * @return    the Entity which matches the given {@link EntityAttributes} {@code attributes}
     *             based on the default key identifiers.
     */
    protected abstract E getEntity(A attributes);

    protected abstract A makeAttributes(E entity);

    protected List<A> makeAttributes(Collection<E> entities) {
        List<A> attributes = new LinkedList<>();
        for (E entity : entities) {
            attributes.add(makeAttributes(entity));
        }
        return attributes;
    }

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

    /**
     * Deletes document by documentId(s).
     */
    protected void deleteDocument(String indexName, String... documentIds) {
        try {
            SearchManager.deleteDocument(indexName, documentIds);
        } catch (Exception e) {
            log.info("Unable to delete document in the index: " + indexName
                    + " with document Ids " + String.join(", ", documentIds));
        }
    }

}
