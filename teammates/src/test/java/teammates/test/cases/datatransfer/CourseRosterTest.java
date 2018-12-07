package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link CourseRoster}.
 */
public class CourseRosterTest extends BaseTestCase {

    @Test
    public void allTests() {

        ______TS("No students");

        CourseRoster roster = new CourseRoster(null, null);
        assertFalse(roster.isStudentInCourse("studentEmail"));

        ______TS("only 1 student, no instructors");

        roster = new CourseRoster(createStudentList("team 1", "s1@gmail.com"), null);
        assertFalse(roster.isStudentInCourse("non-existent@gmail.com"));
        assertTrue(roster.isStudentInCourse("s1@gmail.com"));

        assertFalse(roster.isStudentInTeam("non-existent@gmail.com", "team 1"));
        assertFalse(roster.isStudentInTeam("s1@gmail.com", "team 123"));
        assertTrue(roster.isStudentInTeam("s1@gmail.com", "team 1"));

        assertFalse(roster.isStudentsInSameTeam("non-existent@gmail.com", "s1@gmail.com"));
        assertFalse(roster.isStudentsInSameTeam("s1@gmail.com", "non-existent@gmail.com"));
        assertTrue(roster.isStudentsInSameTeam("s1@gmail.com", "s1@gmail.com"));

        assertEquals(roster.getStudentForEmail("s1@gmail.com").email, "s1@gmail.com");
        assertEquals(roster.getStudentForEmail("s1@gmail.com").team, "team 1");
        assertNull(roster.getInstructorForEmail("ins@email.com"));

        ______TS("only 1 instructor, no students");

        roster = new CourseRoster(null, createInstructorList("John", "ins1@email.com"));
        assertEquals(roster.getInstructorForEmail("ins1@email.com").email, "ins1@email.com");
        assertEquals(roster.getInstructorForEmail("ins1@email.com").name, "John");

        assertNull(roster.getInstructorForEmail("non-existent@email.com"));

        ______TS("multiple students, multiple instructors");

        roster = new CourseRoster(createStudentList("team 1", "s1@gmail.com",
                                                        "team 1", "s2@gmail.com",
                                                        "team 2", "s3@gmail.com"),
                                   createInstructorList("John", "ins1@email.com",
                                                          "Jean", "ins2@email.com"));

        assertFalse(roster.isStudentInCourse("non-existent@gmail.com"));
        assertTrue(roster.isStudentInCourse("s2@gmail.com"));

        assertFalse(roster.isStudentInTeam("non-existent@gmail.com", "team 1"));
        assertFalse(roster.isStudentInTeam("s3@gmail.com", "team 1"));
        assertTrue(roster.isStudentInTeam("s1@gmail.com", "team 1"));
        assertTrue(roster.isStudentInTeam("s2@gmail.com", "team 1"));
        assertTrue(roster.isStudentInTeam("s3@gmail.com", "team 2"));

        assertFalse(roster.isStudentsInSameTeam("non-existent@gmail.com", "s1@gmail.com"));
        assertFalse(roster.isStudentsInSameTeam("s1@gmail.com", "s3@gmail.com"));
        assertTrue(roster.isStudentsInSameTeam("s2@gmail.com", "s1@gmail.com"));

        assertEquals(roster.getInstructorForEmail("ins1@email.com").email, "ins1@email.com");
        assertEquals(roster.getInstructorForEmail("ins1@email.com").name, "John");
        assertEquals(roster.getInstructorForEmail("ins2@email.com").email, "ins2@email.com");
        assertEquals(roster.getInstructorForEmail("ins2@email.com").name, "Jean");

    }

    @Test
    public void testGetEmailToNameTableFromRoster() {
        Map<String, String> emailToNameTableExpected = new HashMap<>();

        emailToNameTableExpected.put("ins1@email.com", "Jess");
        emailToNameTableExpected.put("s1@gmail.com", "student 1");
        emailToNameTableExpected.put("s2@gmail.com", "student 2");

        List<StudentAttributes> students = new ArrayList<>();
        StudentAttributes student1 = StudentAttributes
                .builder("", "student 1", "s1@gmail.com")
                .build();
        StudentAttributes student2 = StudentAttributes
                .builder("", "student 2", "s2@gmail.com")
                .build();
        students.add(student1);
        students.add(student2);

        CourseRoster roster = new CourseRoster(students, createInstructorList("Jess", "ins1@email.com"));
        Map<String, String> emailToNameTableActual = roster.getEmailToNameTableFromRoster();
        assertEquals(emailToNameTableExpected, emailToNameTableActual);
    }

    private List<StudentAttributes> createStudentList(String... studentData) {
        List<StudentAttributes> students = new ArrayList<>();
        for (int i = 0; i < studentData.length; i += 2) {
            StudentAttributes student = StudentAttributes
                    .builder("", "", "")
                    .build();
            student.team = studentData[i];
            student.email = studentData[i + 1];
            students.add(student);
        }
        return students;
    }

    private List<InstructorAttributes> createInstructorList(String... instructorData) {
        List<InstructorAttributes> instructors = new ArrayList<>();
        for (int i = 0; i < instructorData.length; i += 2) {
            @SuppressWarnings("deprecation")
            InstructorAttributes instructor = InstructorAttributes
                    .builder("googleId", "courseId", instructorData[i], instructorData[i + 1])
                    .build();
            instructors.add(instructor);
        }
        return instructors;
    }

}
