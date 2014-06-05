package teammates.client.scripts;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Comment;

public class DataMigrationOfReceiverEmailForComments extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        DataMigrationOfReceiverEmailForComments migrator = new DataMigrationOfReceiverEmailForComments();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        Datastore.initialize();

        List<InstructorAttributes> allInstructors = getAllInstructors();
        for (InstructorAttributes instructor : allInstructors) {
            updateCommentForInstructor(instructor);
        }
    }

    private void updateCommentForInstructor(InstructorAttributes instructor) {
        List<Comment> comments = getCommentEntitiesForInstructor(instructor);
        for (Comment c : comments) {
            @SuppressWarnings("deprecation")
            String receiverEmail = c.getReceiverEmail();
            if (receiverEmail != null) {
                Set<String> recipients = c.getRecipients();
                if (recipients != null) {
                    recipients.add(receiverEmail);
                } else {
                    recipients = new HashSet<String>();
                    recipients.add(receiverEmail);
                }
                c.setRecipients(recipients);
                c.setRecipientType(CommentRecipientType.PERSON);
            }
        }
        getPM().close();
    }
    
    protected List<Comment> getCommentEntitiesForInstructor(
            InstructorAttributes instructor) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(
                instructor.courseId, instructor.email);
        return commentList;
    }

    protected PersistenceManager getPM() {
        return Datastore.getPersistenceManager();
    }

    @SuppressWarnings("deprecation")
    protected List<InstructorAttributes> getAllInstructors() {
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        return instructorsLogic.getAllInstructors();
    }
}
