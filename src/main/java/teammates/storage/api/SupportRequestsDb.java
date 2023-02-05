package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.SupportRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.SupportRequest;

/**
 * Handles CRUD operations for support requests.
 *
 * @see SupportRequest
 * @see SupportRequestAttributes
 */
public final class SupportRequestsDb extends EntitiesDb<SupportRequest, SupportRequestAttributes> {

    private static final SupportRequestsDb instance = new SupportRequestsDb();

    private SupportRequestsDb() {
        // prevent initialisation
    }

    public static SupportRequestsDb inst() {
        return instance;
    }

    /**
     * Gets a support request by its unique ID.
     */
    public SupportRequestAttributes getSupportRequest(String id) {
        assert id != null;

        return id.isEmpty() ? null : makeAttributesOrNull(getSupportRequestEntity(id));
    }

    /**
     * Gets all support requests
     */
    public List<SupportRequestAttributes> getAllSupportRequests() {
        List<SupportRequest> supportRequests = load().list();
        List<SupportRequestAttributes> supportRequestAttributes = makeAttributes(supportRequests);
        SupportRequestAttributes.sortByUpdatedTime(supportRequestAttributes);
        return supportRequestAttributes;
    }

    /**
     * Updates a support request with {@link SupportRequestAttributes.UpdateOptions}
     * 
     * @return updated support request
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if support request cannot be found
     */
    public SupportRequestAttributes updateSupportRequest(SupportRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        SupportRequest supportRequest = getSupportRequestEntity(updateOptions.getId());
        if (supportRequest == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        SupportRequestAttributes newAttributes = makeAttributes(supportRequest);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        putEntity(newAttributes);

        return newAttributes;
    }

    /**
     * Deletes a support request by its unique ID.
     * 
     * <p>Fails silently if there is no such support request.
     */
    public void deleteSupportRequest(String id) {
        assert id != null;

        Key<SupportRequest> keyToDelete = Key.create(SupportRequest.class, id);
        deleteEntity(keyToDelete);
    }

    /**
     * Checks if a support request associated with {@code id} exists.
     */
    public boolean doesSupportRequestExists(String id) {
        Key<SupportRequest> keyToFind = Key.create(SupportRequest.class, id);
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    private SupportRequest getSupportRequestEntity(String id) {
        return load().id(id).now();
    }

    @Override
    LoadType<SupportRequest> load() {
        return ofy().load().type(SupportRequest.class);
    }

    @Override
    boolean hasExistingEntities(SupportRequestAttributes entityToCreate) {
        Key<SupportRequest> keyToFind = Key.create(SupportRequest.class, entityToCreate.getId());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    SupportRequestAttributes makeAttributes(SupportRequest entity) {
        assert entity != null;

        return SupportRequestAttributes.valueOf(entity);
    }
}