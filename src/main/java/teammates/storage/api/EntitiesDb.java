package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Objects;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.entity.BaseEntity;

/**
 * Base class for all classes performing CRUD operations against the database.
 *
 * @param <E> Specific entity class
 * @param <A> Specific attributes class
 */
abstract class EntitiesDb<E extends BaseEntity, A extends EntityAttributes<E>> {

    /**
     * Error message when trying to create entity that already exist.
     */
    static final String ERROR_CREATE_ENTITY_ALREADY_EXISTS = "Trying to create an entity that exists: %s";

    /**
     * Error message when trying to update entity that does not exist.
     */
    static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Entity: ";

    /**
     * Info message when entity is not saved because it does not change.
     */
    static final String OPTIMIZED_SAVING_POLICY_APPLIED =
            "Saving request is not issued because entity %s does not change by the update (%s)";

    static final Logger log = Logger.getLogger();

    /**
     * Creates the entity in the database.
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
        assert entityToAdd != null;

        entityToAdd.sanitizeForSaving();

        if (!entityToAdd.isValid()) {
            throw new InvalidParametersException(entityToAdd.getInvalidityInfo());
        }

        if (shouldCheckExistence && hasExistingEntities(entityToAdd)) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, entityToAdd.toString());
            throw new EntityAlreadyExistsException(error);
        }

        E entity = convertToEntityForSaving(entityToAdd);

        ofy().save().entity(entity).now();
        log.info("Entity created: " + JsonUtils.toJson(entityToAdd));

        return makeAttributes(entity);
    }

    E convertToEntityForSaving(A entityAttributes) throws EntityAlreadyExistsException {
        return entityAttributes.toEntity();
    }

    /**
     * Checks whether there are existing entities in the database.
     */
    abstract boolean hasExistingEntities(A entityToCreate);

    /**
     * Puts an entity in the database without existence checking.
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
            assert false : "Unreachable branch";
            return null;
        }
    }

    /**
     * Puts a collection of entity in the database without existence checking.
     *
     * <p>The documents of the associated entities (if applicable) WILL NOT be updated.
     *
     * @return created entities
     * @throws InvalidParametersException if any of entity to add is not valid
     */
    public List<A> putEntities(Collection<A> entitiesToAdd) throws InvalidParametersException {
        assert entitiesToAdd != null;

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

    /**
     * Checks whether two values are the same.
     */
    <T> boolean hasSameValue(T oldValue, T newValue) {
        return Objects.equal(oldValue, newValue);
    }

    /**
     * Saves an entity.
     */
    void saveEntity(E entityToSave) {
        assert entityToSave != null;

        log.info("Entity saved: " + JsonUtils.toJson(entityToSave));

        ofy().save().entity(entityToSave).now();
    }

    /**
     * Saves a collection of entities.
     */
    void saveEntities(Collection<E> entitiesToSave) {
        for (E entityToSave : entitiesToSave) {
            log.info("Entity saved: " + JsonUtils.toJson(entityToSave));
        }

        ofy().save().entities(entitiesToSave).now();
    }

    /**
     * Deletes entity by key.
     */
    void deleteEntity(Key<E> key) {
        assert key != null;
        deleteEntity(Collections.singletonList(key));
    }

    /**
     * Deletes entities by keys.
     */
    void deleteEntity(List<Key<E>> keys) {
        assert keys != null;
        assert !keys.contains(null);

        for (Key<E> key : keys) {
            log.info(String.format("Delete entity %s of key (id: %d, name: %s)",
                    key.getKind(), key.getRaw().getId(), key.getName()));
        }
        ofy().delete().keys(keys).now();
    }

    abstract LoadType<E> load();

    /**
     * Converts from entity to attributes.
     */
    abstract A makeAttributes(E entity);

    /**
     * Converts a collection of entities to a list of attributes.
     */
    List<A> makeAttributes(Collection<E> entities) {
        List<A> attributes = new LinkedList<>();
        for (E entity : entities) {
            attributes.add(makeAttributes(entity));
        }
        return attributes;
    }

    /**
     * Converts from entity to attributes.
     *
     * @return null if the original entity is null
     */
    A makeAttributesOrNull(E entity) {
        if (entity != null) {
            return makeAttributes(entity);
        }
        return null;
    }

    /**
     * Creates a key from a web safe string.
     */
    Optional<Key<E>> makeKeyFromWebSafeString(String webSafeString) {
        if (webSafeString == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Key.create(webSafeString));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

}
