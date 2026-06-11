package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Institute;

/**
 * Builder for Institute entities used in test scenarios.
 */
public final class GivenInstitute extends GivenBase<Institute> {
    public GivenInstitute(GivenData given, UUID instituteId) {
        super(given);
        this.entity = defaultInstitute(instituteId);
    }

    /**
     * Sets the name for the institute.
     */
    public GivenInstitute name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the country (ISO 3166-1 alpha-2 code) for the institute.
     */
    public GivenInstitute country(String country) {
        entity.setCountry(country);
        return this;
    }

    @Override
    void ensureConsistent() {
        // No mandatory relationships
    }

    private Institute defaultInstitute(UUID instituteId) {
        Institute institute = new Institute("Institute " + instituteId, "SG");
        institute.setId(instituteId);
        return institute;
    }
}
