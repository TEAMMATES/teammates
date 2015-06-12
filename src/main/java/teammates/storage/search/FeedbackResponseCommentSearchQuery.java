package teammates.storage.search;

import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

import com.google.appengine.api.search.Document;

/**
 * The SearchQuery object that defines how we query {@link Document} for response comments
 */
public class FeedbackResponseCommentSearchQuery extends BaseCommentSearchQuery {

    public FeedbackResponseCommentSearchQuery(String googleId, String queryString, String cursorString) {
        super(googleId, queryString, cursorString);
    }

    protected void finishVisibilityQueryString(StringBuilder courseIdLimit,
                                               List<InstructorAttributes> instructorRoles) {
        visibilityQueryString = Const.SearchDocumentField.COURSE_ID + ":" + courseIdLimit.toString();
    }

}
