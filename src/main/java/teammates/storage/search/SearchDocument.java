package teammates.storage.search;

import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

import com.google.appengine.api.search.Document;

/**
 * The SearchDocument object that defines how we store {@link Document}
 */
public abstract class SearchDocument {
    
    protected static final CommentsDb commentsDb = new CommentsDb();
    protected static final CoursesDb coursesDb = new CoursesDb();
    protected static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    protected static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    protected static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    protected static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    protected static final InstructorsDb instructorsDb = new InstructorsDb();
    protected static final StudentsDb studentsDb = new StudentsDb();
    
    public Document build() {
        prepareData();
        return toDocument();
    }
    
    protected abstract void prepareData();
    
    protected abstract Document toDocument();
}
