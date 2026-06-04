package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Course;
import teammates.storage.entity.Section;

/**
 * Builder for Section entities used in test scenarios.
 */
public final class SectionData {
    private GivenData given;
    private Section section;

    public SectionData(GivenData given, UUID sectionId) {
        this.given = given;
        this.section = defaultSection(sectionId);
    }

    public Section build() {
        return section;
    }

    /**
     * Sets the name for the section.
     */
    public SectionData name(String name) {
        section.setName(name);
        return this;
    }

    /**
     * Sets the course for the section.
     */
    public SectionData course(String courseAlias) {
        assert section.getCourse() == null : "Course has already been set for this section";
        Course c = given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        c.addSection(section);
        return this;
    }

    void ensureConsistent() {
        if (section.getCourseId() == null) {
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
