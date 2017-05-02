package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;

public class RepairStudentsWithDuplicateEmail extends RemoteApiClient {

    // TODO: This class contains lot of code copy-pasted from the Logic and
    // Storage layer. This duplication can be removed if we figure out
    // to reuse the Logic API from here.

    private int duplicateEmailCount;

    public static void main(String[] args) throws IOException {
        RepairStudentsWithDuplicateEmail repairman = new RepairStudentsWithDuplicateEmail();
        repairman.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<CourseAttributes> allCourses = getAllCourses();

        duplicateEmailCount = 0;
        for (CourseAttributes course : allCourses) {
            repairCourseStudents(course);
        }
        print("Total students with duplicate emails in all courses: " + duplicateEmailCount);
    }

    private void repairCourseStudents(CourseAttributes course) {
        List<StudentAttributes> studentList = getStudentsForCourse(course.getId());

        Map<String, String> emailNameMap = new TreeMap<String, String>();
        Set<String> duplicateEmailRecord = new TreeSet<String>();
        for (StudentAttributes student : studentList) {
            String duplicateEmailOwner =
                    emailNameMap.put(student.email, student.name);

            if (duplicateEmailOwner != null) {
                duplicateEmailRecord.add("<" + student.email + "> owner: " + duplicateEmailOwner);
                duplicateEmailRecord.add("<" + student.email + "> owner: " + student.name);
            }
        }

        for (String entry : duplicateEmailRecord) {
            print(entry);
            //TODO: delete duplicate records if possible
        }

        duplicateEmailCount += duplicateEmailRecord.size();
        print("[" + duplicateEmailRecord.size() + ": " + course.getId() + "]");
    }

    private void print(String string) {
        System.out.println(string);
    }

    private List<CourseAttributes> getAllCourses() {

        Query q = PM.newQuery(Course.class);

        @SuppressWarnings("unchecked")
        List<Course> courseList = (List<Course>) q.execute();

        List<CourseAttributes> courseDataList = new ArrayList<CourseAttributes>();
        for (Course c : courseList) {
            courseDataList.add(new CourseAttributes(c));
        }

        return courseDataList;
    }

    private List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<CourseStudent> studentList = getStudentEntitiesForCourse(courseId);

        List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();

        for (CourseStudent s : studentList) {
            if (!JDOHelper.isDeleted(s)) {
                studentDataList.add(new StudentAttributes(s));
            }
        }

        return studentDataList;
    }

    @SuppressWarnings("unchecked")
    private List<CourseStudent> getStudentEntitiesForCourse(String courseId) {
        Query q = PM.newQuery(CourseStudent.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseID == courseIdParam");

        return (List<CourseStudent>) q.execute(courseId);
    }
}
