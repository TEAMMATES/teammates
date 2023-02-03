package teammates.logic.core;

import java.util.List;

import com.google.cloud.datastore.Entity;

import teammates.common.datatransfer.SupportRequestStatus;
import teammates.common.datatransfer.SupportRequestType;
import teammates.common.datatransfer.attributes.SupportRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.SupportRequestsDb;

/**
 * Handles the logic related to support requests.
 */
public class SupportRequestsLogic {
    
    private static final SupportRequestsLogic instance = new SupportRequestsLogic();

    private final SupportRequestsDb supportRequestsDb = SupportRequestsDb.inst();

    private SupportRequestsLogic() {
        // prevent initialisation
    }

    public static SupportRequestsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // No dependency to other logic class
    }

    /**
     * Gets support request associated with the {@code id}.
     * 
     * @return null if no match found.
     */
    public SupportRequestAttributes getSupportRequest(String id) {
        return supportRequestsDb.getSupportRequest(id);
    }

    /**
     * Gets all notifications.
     */
    public List<SupportRequestAttributes> getAllSupportRequests() {
        return supportRequestsDb.getAllSupportRequests();
    }

    // We can add in methods here to get support requests based on type, status, etc.

    /**
     * Creates a support request.
     * 
     * @return the created support request.
     * @throws InvalidParametersException if the support request is not valid.
     * @throws EntityAlreadyExistsException if the support request already exists in the database.
     */
    public SupportRequestAttributes createSupportRequest(SupportRequestAttributes supportRequest) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        return supportRequestsDb.createEntity(supportRequest);
    }

    /**
     * Updates/Creates the support request using {@link SupportRequestAttributes.UpdateOptions}.
     * 
     * @return updated support request.
     * @throws InvalidParameterException if attributes to update are not valid.
     * @throws EntityDoesNotExistException if support request cannot be found with given id.
     */
    public SupportRequestAttributes updateSupportRequest(SupportRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        return supportRequestsDb.updateSupportRequest(updateOptions);
    }

    /**
     * Deletes support request associated with the {@code id}.
     * 
     * <p>Fails silently if the support request doesn't exist.</p>
     */
    public void deleteSupportRequest(String id) {
        supportRequestsDb.deleteSupportRequest(id);
    }

    /**
     * Checks if a support request associated the {@code id} exists.
     */
    public boolean doesSupportRequestExists(String id) {
        return supportRequestsDb.doesSupportRequestExists(id);
    }
}
