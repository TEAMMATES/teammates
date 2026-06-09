package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Contains a list of students and instructors in a course. Useful for caching
 * a copy of student and instructor details of a course instead of reading
 * them from the database multiple times.
 */
public class CourseRoster {

    private final List<Student> students;
    private final List<Instructor> instructors;

    public CourseRoster(List<Student> students, List<Instructor> instructors) {
        this.students = students == null ? new ArrayList<>() : students;
        this.instructors = instructors == null ? new ArrayList<>() : instructors;
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
    }

    public List<Instructor> getInstructors() {
        return Collections.unmodifiableList(instructors);
    }

    /**
     * Gets all teams that have at least one student in the course.
     */
    public Collection<Team> getTeams() {
        Map<UUID, Team> teams = new LinkedHashMap<>();
        for (Student student : students) {
            teams.putIfAbsent(student.getTeamId(), student.getTeam());
        }
        return teams.values();
    }

    /**
     * Gets the students belonging to the team with the given ID.
     */
    public List<Student> getTeamMembers(UUID teamId) {
        return students.stream()
                .filter(student -> Objects.equals(student.getTeamId(), teamId))
                .collect(Collectors.toList());
    }
}
