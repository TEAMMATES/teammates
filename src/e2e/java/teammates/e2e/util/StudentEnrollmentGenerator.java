package teammates.e2e.util;

import java.util.HashSet;
import java.util.Set;

import org.kohsuke.randname.RandomNameGenerator;

/**
 * Helper class to generate student enrollment string.
 */
public final class StudentEnrollmentGenerator {

    private static final String HEADER = "Section | Team | Name | Email | Comments\n";

    private StudentEnrollmentGenerator() {
        // utility class
    }

    /**
     * Generates students to be enrolled, as a string.
     */
    public static String generateStudents(int num) {
        RandomNameGenerator generator = new RandomNameGenerator(num);
        Set<String> studentNames = new HashSet<>();
        StringBuffer students = new StringBuffer(HEADER);
        for (int i = 0; i < num; i++) {
            String curName = generator.next();
            while (studentNames.contains(curName)) {
                curName = generator.next();
            }

            students.append(
                    String.format("Section %d | Team %d | %s | %s.tmms@gmail.tmt | %n",
                            i / 100 + 1, i / 100 + 1, curName.replace("_", " "), curName.replace("_", ".")));
        }
        return students.toString();
    }

}
