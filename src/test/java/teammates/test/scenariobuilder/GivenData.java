package teammates.test.scenariobuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;

/**
 * Builder for test data. Provides methods to create entities and manage their
 * relationships.
 *
 * <p>
 * Usage example:
 * 1. Create a GivenData instance with a unique test name.
 * This ensures that generated IDs are consistent across test runs and do not
 * collide with other tests.
 *
 * <pre>
 * GivenData given = new GivenData("testName");
 * </pre>
 *
 * <p>
 * 2. Use the provided methods to create entities, providing only the
 * information that is relavant to the test.
 * For example, if the course and section of a team are not important for the
 * test, they will be automatically created with default values.
 *
 * <pre>
 * UUID teamId = given.team("teamAlias");
 * </pre>
 *
 * <p>
 * 3. If specific values are needed for the entities, use the options to
 * customize them.
 *
 * <pre>
 * String courseId = given.course("courseAlias", c -> c.name("Custom Course Name"));
 * String softDeletedCourseId = given.course("softDeletedCourseAlias", c -> {
 *     c.name("Soft Deleted Course");
 *     c.softDeleted();
 * });
 * </pre>
 *
 * <p>
 * 4. To create relationships between entities, pass the appropriate aliases in
 * the options.
 * For example, to create a section that belongs to a particular course, use the
 * course alias in the section options.
 *
 * <pre>
 * String courseId = given.course("courseAlias");
 * UUID sectionId = given.section("sectionAlias", s -> s.course("courseAlias"));
 * </pre>
 *
 * <p>
 * 5. Call persistGivenData to save all created entities to the database.
 *
 * <pre>
 * persistGivenData(given);
 * </pre>
 *
 * <p>
 * 6. Use the generated IDs in the test assertions or further entity creation.
 */
public final class GivenData {
    final DataBundle dataBundle = new DataBundle();
    private final String testName;

    public GivenData(String testName) {
        this.testName = testName;
    }

    /**
     * Creates an account with default values.
     */
    public UUID account(String alias) {
        return account(alias, a -> {});
    }

    /**
     * Creates an account and applies the provided options to customize it.
     */
    public UUID account(String alias, Consumer<AccountData> options) {
        AccountData accountData = new AccountData(uuid(alias));
        options.accept(accountData);
        accountData.ensureConsistent();
        Account account = accountData.build();
        dataBundle.accounts.put(alias, account);
        return account.getId();
    }

    /**
     * Creates a course with default values.
     */
    public String course(String alias) {
        return course(alias, c -> {});
    }

    /**
     * Creates a course and applies the provided options to customize it.
     */
    public String course(String alias, Consumer<CourseData> options) {
        CourseData courseData = new CourseData(stringId(alias));
        options.accept(courseData);
        courseData.ensureConsistent();
        Course course = courseData.build();
        dataBundle.courses.put(alias, course);
        return course.getId();
    }

    /**
     * Creates a section with default values.
     */
    public UUID section(String alias) {
        return section(alias, s -> {});
    }

    /**
     * Creates a section and applies the provided options to customize it.
     */
    public UUID section(String alias, Consumer<SectionData> options) {
        SectionData sectionData = new SectionData(this, uuid(alias));
        options.accept(sectionData);
        sectionData.ensureConsistent();
        Section section = sectionData.build();
        dataBundle.sections.put(alias, section);
        return section.getId();
    }

    /**
     * Creates a team with default values.
     */
    public UUID team(String alias) {
        return team(alias, t -> {});
    }

    /**
     * Creates a team and applies the provided options to customize it.
     */
    public UUID team(String alias, Consumer<TeamData> options) {
        TeamData teamData = new TeamData(this, uuid(alias));
        options.accept(teamData);
        teamData.ensureConsistent();
        Team team = teamData.build();
        dataBundle.teams.put(alias, team);
        return team.getId();
    }

    /**
     * Returns the data bundle containing all created entities.
     */
    public DataBundle getDataBundle() {
        return dataBundle;
    }

    /**
     * Helper method to get an entity from a map by alias, or create it if it does
     * not exist.
     */
    <T> T getOrCreate(String alias, Map<String, T> map, Consumer<String> create) {
        T entity = map.get(alias);
        if (entity != null) {
            return entity;
        }
        create.accept(alias);
        return map.get(alias);
    }

    /**
     * Generates a string ID based on the alias and test name. The ID is
     * deterministic
     * and will be the same across test runs for the same alias and test name.
     */
    public String stringId(String alias) {
        String prefix = alias.substring(0, Math.min(alias.length(), 27));
        UUID uuid = uuid(alias);
        return prefix + "-" + uuid.toString();
    }

    /**
     * Generates a UUID based on the alias and test name. The UUID is deterministic
     * and will be the same across test runs for the same alias and test name.
     */
    public UUID uuid(String alias) {
        return UUID.nameUUIDFromBytes((testName + ":" + alias).getBytes(StandardCharsets.UTF_8));
    }
}
