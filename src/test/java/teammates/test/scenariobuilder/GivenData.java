package teammates.test.scenariobuilder;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import teammates.common.datatransfer.DataBundle;
import teammates.storage.entity.Course;
import teammates.storage.entity.Section;
import teammates.storage.entity.Team;

public final class GivenData {
    private final String testName;
    final DataBundle dataBundle = new DataBundle();

    public GivenData(String testName) {
        this.testName = testName;
    }

    public String course(String alias) {
        return course(alias, c -> {});
    }

    public String course(String alias, Consumer<CourseData> options) {
        CourseData courseData = new CourseData(stringId(alias));
        options.accept(courseData);
        Course course = courseData.build();
        dataBundle.courses.put(alias, course);
        return course.getId();
    }

    public UUID section(String alias) {
        return section(alias, s -> {});
    }

    public UUID section(String alias, Consumer<SectionData> options) {
        SectionData sectionData = new SectionData(this, uuid(alias));
        options.accept(sectionData);
        sectionData.ensureConsistent();
        Section section = sectionData.build();
        dataBundle.sections.put(alias, section);
        return section.getId();
    }

    public UUID team(String alias) {
        return team(alias, t -> {});
    }

    public UUID team(String alias, Consumer<TeamData> options) {
        TeamData teamData = new TeamData(this, uuid(alias));
        options.accept(teamData);
        teamData.ensureConsistent();
        Team team = teamData.build();
        dataBundle.teams.put(alias, team);
        return team.getId();
    }

    public DataBundle getDataBundle() {
        return dataBundle;
    }

    <T> T getOrCreate(String alias, Map<String, T> map, Consumer<String> create) {
        T entity = map.get(alias);
        if (entity != null) {
            return entity;
        }
        create.accept(alias);
        return map.get(alias);
    }

    public String stringId(String alias) {
        String prefix = alias.substring(0, Math.min(alias.length(), 27));
        UUID uuid = uuid(alias);
        return prefix + "-" + uuid.toString();
    }

    public UUID uuid(String alias) {
        return UUID.nameUUIDFromBytes((testName + ":" + alias).getBytes());
    }
}
