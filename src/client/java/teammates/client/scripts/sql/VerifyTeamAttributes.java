package teammates.client.scripts.sql;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Team;

/**
 * Class for verifying team attributes.
 */
public class VerifyTeamAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifyTeamAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyTeamAttributes script = new VerifyTeamAttributes();
        script.doOperationRemotely();
    }

    private static String normalizeSectionName(String value) {
        return value == null || value.isEmpty() ? Const.DEFAULT_SECTION : value;
    }

    private static String normalizeTeamName(String value) {
        return value == null || value.isEmpty() ? Const.DEFAULT_TEAM : value;
    }

    private Map<String, Set<String>> getSectionNameToTeamNamesMap(Course course) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .collect(Collectors.groupingBy(stu -> normalizeSectionName(stu.getSectionName()),
                        Collectors.mapping(stu -> normalizeTeamName(stu.getTeamName()), Collectors.toSet())));
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        Map<String, Set<String>> sectionNameToTeamNamesMap = getSectionNameToTeamNamesMap(datastoreEntity);

        return sqlEntity.getSections().stream().map(section -> {
            Set<String> oldTeamNames = sectionNameToTeamNamesMap.getOrDefault(section.getName(), Collections.emptySet());
            Set<String> newTeamNames = new HashSet<>(
                    section.getTeams().stream().map(Team::getName).collect(Collectors.toList()));

            return section.getTeams().size() == newTeamNames.size()
                    && newTeamNames.equals(oldTeamNames);
        }).allMatch(b -> b);
    }

}
