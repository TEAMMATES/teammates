package teammates.logic.core;

import java.util.UUID;

import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.InstitutesDb;
import teammates.storage.entity.Institute;

/**
 * Handles operations related to institutes.
 *
 * @see Institute
 * @see InstitutesDb
 */
public final class InstitutesLogic {

    private static final InstitutesLogic instance = new InstitutesLogic();

    private InstitutesDb institutesDb;

    private InstitutesLogic() {
        // prevent initialization
    }

    public static InstitutesLogic inst() {
        return instance;
    }

    void initLogicDependencies(InstitutesDb institutesDb) {
        this.institutesDb = institutesDb;
    }

    /**
     * Gets the institute with the given {@code id}, or null if it does not exist.
     */
    public Institute getInstitute(UUID id) {
        return institutesDb.getInstitute(id);
    }

    /**
     * Returns the shared institute matching {@code name} and {@code country}, creating and
     * persisting it if it does not yet exist.
     */
    public Institute getOrCreateInstitute(String name, String country) throws InvalidParametersException {
        Institute candidate = new Institute(name, country);
        if (!candidate.isValid()) {
            throw new InvalidParametersException(candidate.getInvalidityInfo());
        }

        Institute existing = institutesDb.getInstituteByNameAndCountry(candidate.getName(), candidate.getCountry());
        if (existing != null) {
            return existing;
        }

        return institutesDb.persistInstitute(candidate);
    }

    /**
     * Deletes the institute with the given {@code id}.
     * 
     * <p>Fails silently if the institute does not exist.
     */
    public void deleteInstitute(UUID id) {
        Institute institute = institutesDb.getInstitute(id);
        if (institute == null) {
            return;
        }
        
        institutesDb.removeInstitute(institute);
    }
}
