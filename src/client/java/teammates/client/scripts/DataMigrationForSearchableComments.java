package teammates.client.scripts;

import java.io.IOException;
import java.util.List;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.logic.api.Logic;
import teammates.logic.core.InstructorsLogic;
import teammates.storage.entity.Comment;
import teammates.storage.entity.FeedbackResponseComment;

public class DataMigrationForSearchableComments extends RemoteApiClient {

    private Logic logic = new Logic();

    public static void main(String[] args) throws IOException {
        DataMigrationForSearchableComments migrator = new DataMigrationForSearchableComments();
        migrator.doOperationRemotely();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void doOperation() {
        List<InstructorAttributes> allInstructors = InstructorsLogic.inst().getAllInstructors();
        for (InstructorAttributes instructor : allInstructors) {
            updateCommentsForInstructor(instructor);
        }
    }

    private void updateCommentsForInstructor(InstructorAttributes instructor) {
        List<Comment> comments = getCommentEntitiesForInstructor(instructor);
        for (Comment c : comments) {
            putCommentToSearchableDocument(new CommentAttributes(c));
        }
        List<FeedbackResponseComment> frComments = getFrCommentEntitiesForInstructor(instructor);
        for (FeedbackResponseComment c : frComments) {
            putFrCommentToSearchableDocument(new FeedbackResponseCommentAttributes(c));
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

    private List<FeedbackResponseComment> getFrCommentEntitiesForInstructor(
            InstructorAttributes instructor) {
        Query q = PM.newQuery(FeedbackResponseComment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponseComment> commentList = (List<FeedbackResponseComment>) q.execute(
                instructor.courseId, instructor.email);
        return commentList;
    }

    private void putCommentToSearchableDocument(CommentAttributes comment) {
        logic.putDocument(comment);
    }

    private void putFrCommentToSearchableDocument(FeedbackResponseCommentAttributes comment) {
        logic.putDocument(comment);
    }

}
