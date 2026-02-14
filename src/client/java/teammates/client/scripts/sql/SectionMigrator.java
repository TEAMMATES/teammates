package teammates.client.scripts.sql;

import java.time.Instant;
import java.util.function.Consumer;

import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;

/**
 * Shared migration logic for Section entities. Used by {@link DataMigrationForCourseEntitySql}
 * and {@link DataMigrationForSectionSql}.
 */
public final class SectionMigrator {

    private static final int MAX_SECTION_NAME_LENGTH = 255;

    private SectionMigrator() {
        // utility class
    }

    /**
     * Creates and saves Section entities for the given course and section names.
     * Caller is responsible for transaction boundaries when using {@code HibernateUtil::persist}.
     *
     * @param newCourse the SQL course (must already exist)
     * @param sectionNames distinct section names derived from CourseStudent data
     * @param saveAction called for each created section (e.g. {@code HibernateUtil::persist} or
     *        {@code this::saveEntityDeferred})
     */
    public static void migrate(
            Course newCourse,
            Iterable<String> sectionNames,
            Consumer<Section> saveAction) {
        for (String sectionName : sectionNames) {
            String truncatedName = truncate(sectionName, MAX_SECTION_NAME_LENGTH);
            Section section = new Section(newCourse, truncatedName);
            section.setCreatedAt(Instant.now());
            saveAction.accept(section);
        }
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
