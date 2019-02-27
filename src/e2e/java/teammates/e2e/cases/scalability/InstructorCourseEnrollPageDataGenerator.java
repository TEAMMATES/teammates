package teammates.e2e.cases.scalability;

import java.io.File;
import java.io.IOException;

import teammates.e2e.util.StudentEnrollmentGenerator;
import teammates.test.driver.FileHelper;

/**
 * Generates test data for {@link InstructorCourseEnrollPageScalabilityTest}.
 */
public final class InstructorCourseEnrollPageDataGenerator {

    private static final String FILENAME = "InstructorCourseEnrollPageScaleTestData";

    private InstructorCourseEnrollPageDataGenerator() {
        // script-like; not meant to be instantiated
    }

    public static void main(String[] args) throws IOException {
        //Number of students to be added for each data set
        int[] studentGroups = {10, 20, 50, 75, 100, 150};
        String folderPath = "src/e2e/java/teammates/e2e/cases/scalability/data/";
        new File(folderPath).mkdir();
        for (int studentGroup : studentGroups) {
            FileHelper.saveFile(
                    folderPath + FILENAME + studentGroup,
                    StudentEnrollmentGenerator.generateStudents(studentGroup));
        }
    }
}
