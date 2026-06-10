package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Course;
import teammates.storage.entity.Section;

/**
 * Builder for Section entities used in test scenarios.
 */
public final class GivenSection extends GivenBase<Section> {
    public GivenSection(GivenData given, UUID sectionId) {
        super(given);
        this.given = given;
        this.entity = defaultSection(sectionId);
    }

    /**
     * Sets the name for the section.
     */
    public GivenSection name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the course for the section.
     */
    public GivenSection course(String courseAlias) {
        assert entity.getCourse() == null : "Course has already been set for this section";
        Course c = given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        c.addSection(entity);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getCourseId() == null) {
            this.course("default");
        }
    }

    /**
     * Generates a default alias for a section based on the course alias.
     */
    public static String getDefaultAlias(String courseAlias) {
        return "default:" + courseAlias;
    }

    private Section defaultSection(UUID sectionId) {
        Section s = new Section(sectionId.toString());
        s.setId(sectionId);
        return s;
    }
}
