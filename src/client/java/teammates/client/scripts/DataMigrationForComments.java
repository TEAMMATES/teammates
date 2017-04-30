package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.entity.Comment;

public class DataMigrationForComments extends RemoteApiClient {

    public static void main(String[] args) throws IOException {
        DataMigrationForComments migrator = new DataMigrationForComments();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
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
                if (recipients == null) {
                    recipients = new HashSet<String>();
                    recipients.add(receiverEmail);
                } else {
                    recipients.add(receiverEmail);
                }
                //map receiverEmail to recipients
                c.setRecipients(recipients);
                //set default recipientType to PERSON
                c.setRecipientType(CommentParticipantType.PERSON);
                //set default comment status to FINAL (sent/published)
                c.setStatus(CommentStatus.FINAL);
                //set default visibility option of showCommentTo to private
                c.setShowCommentTo(new ArrayList<CommentParticipantType>());
                //set default visibility option of showGiverNameTo to private
                c.setShowGiverNameTo(new ArrayList<CommentParticipantType>());
                //set default visibility option of showRecipientNameTo to private
                c.setShowRecipientNameTo(new ArrayList<CommentParticipantType>());
            }
        }
        PM.close();
    }

    private List<Comment> getCommentEntitiesForInstructor(
            InstructorAttributes instructor) {
        Query q = PM.newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(
                instructor.courseId, instructor.email);
        return commentList;
    }

    @SuppressWarnings("deprecation")
    private List<InstructorAttributes> getAllInstructors() {
        InstructorsLogic instructorsLogic = InstructorsLogic.inst();
        return instructorsLogic.getAllInstructors();
    }
}
