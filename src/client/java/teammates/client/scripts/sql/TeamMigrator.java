package teammates.client.scripts.sql;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;

/**
 * Shared migration logic for Team entities. Used by {@link DataMigrationForCourseEntitySql}
 * and {@link DataMigrationForTeamSql}.
 */
public final class TeamMigrator {

    private static final int MAX_TEAM_NAME_LENGTH = 255;

    private TeamMigrator() {
        // utility class
    }

    /**
     * Creates and saves Team entities for the given course. Course must have sections loaded
     * (e.g. via fetch join). Caller is responsible for transaction boundaries when using
     * {@code HibernateUtil::persist}.
     *
     * @param newCourse the SQL course with sections loaded
     * @param sectionNameToTeamNames map from section name to set of team names in that section
     * @param saveAction called for each created team
     */
    public static void migrate(
            Course newCourse,
            Map<String, Set<String>> sectionNameToTeamNames,
            Consumer<Team> saveAction) {
        Map<String, Section> sectionByName =
                newCourse.getSections().stream()
                        .collect(java.util.stream.Collectors.toMap(Section::getName, s -> s));

        for (Map.Entry<String, Set<String>> entry : sectionNameToTeamNames.entrySet()) {
            String sectionName = entry.getKey();
            Section section = sectionByName.get(sectionName);
            if (section == null) {
                continue;
            }
            for (String teamName : entry.getValue()) {
                String normalizedName = normalizeTeamName(teamName);
                String truncatedName = truncate(normalizedName, MAX_TEAM_NAME_LENGTH);
                Team team = new Team(section, truncatedName);
                team.setCreatedAt(Instant.now());
                saveAction.accept(team);
            }
        }
    }

    /**
     * Normalizes null/empty team names to {@link Const#DEFAULT_TEAM} so that
     * Team.name (non-null) receives a valid value. Matches caller normalization
     * (e.g. DataMigrationForCourseEntitySql, DataMigrationForTeamSql).
     */
    private static String normalizeTeamName(String name) {
        return name == null || name.isEmpty() ? Const.DEFAULT_TEAM : name;
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
