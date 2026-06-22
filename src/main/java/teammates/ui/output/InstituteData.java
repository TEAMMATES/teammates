package teammates.ui.output;

import java.util.UUID;

import teammates.storage.entity.Institute;

/**
 * Output data for an institute.
 */
public class InstituteData implements ApiOutput {
    private final UUID id;
    private final String name;
    private final String country;

    public InstituteData(Institute institute) {
        this.id = institute.getId();
        this.name = institute.getName();
        this.country = institute.getCountry();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
}
