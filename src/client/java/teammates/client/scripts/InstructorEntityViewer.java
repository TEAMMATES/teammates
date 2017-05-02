package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Instructor;

public class InstructorEntityViewer extends RemoteApiClient {

    private static String googleId = "GoogleId";
    private static String courseId = "CourseId";

    @Override
    protected void doOperation() {
        Query q = PM.newQuery(Instructor.class);
        q.declareParameters("String googleIdParam, String courseIdParam");
        q.setFilter("googleId == googleIdParam && courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<Instructor> instructorList = (List<Instructor>) q.execute(googleId, courseId);

        System.out.println("Instructors:");
        for (Instructor ins : instructorList) {
            System.out.println(ins.getGoogleId() + "@" + ins.getCourseId());
            System.out.println(ins.getInstructorPrivilegesAsText());
            System.out.println("");
        }
        System.out.println("End of output");

        PM.close();
    }

    public static void main(String[] args) throws IOException {
        InstructorEntityViewer viewer = new InstructorEntityViewer();
        viewer.doOperationRemotely();
    }

}
