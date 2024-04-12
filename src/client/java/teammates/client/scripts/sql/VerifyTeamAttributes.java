package teammates.client.scripts.sql;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Team;

/**
 * Class for verifying section attributes.
 */
@SuppressWarnings("PMD")
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

    private Set<String> getAllTeamNames(Course course) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .map(stu -> stu.getTeamName())
                .distinct()
                .collect(Collectors.toCollection(HashSet::new));

    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        List<Team> teams = sqlEntity.getSections().stream()
                .flatMap(section -> section.getTeams().stream())
                .collect(Collectors.toList());
        Set<String> newTeamNames = new HashSet<>(
                teams.stream().map(Team::getName).collect(Collectors.toList()));
        Set<String> oldTeamNames = getAllTeamNames(datastoreEntity);

        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && teams.size() == newTeamNames.size()
                && newTeamNames.equals(oldTeamNames);
    }

}
