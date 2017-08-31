package teammates.storage.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

/**
 * Defines how we store {@link Document} for indexing/searching.
 */
public abstract class SearchDocument {

    protected static final CoursesDb coursesDb = new CoursesDb();
    protected static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    protected static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    protected static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    protected static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    protected static final InstructorsDb instructorsDb = new InstructorsDb();
    protected static final StudentsDb studentsDb = new StudentsDb();

    /**
     * Builds the search document.
     */
    public Document build() {
        prepareData();
        return toDocument();
    }

    protected abstract void prepareData();

    protected abstract Document toDocument();

    /**
     * This method must be called to filter out the search result for course Id.
     */
    protected static List<ScoredDocument> filterOutCourseId(Results<ScoredDocument> results,
                                                            List<InstructorAttributes> instructors) {
        Set<String> courseIdSet = new HashSet<>();
        for (InstructorAttributes ins : instructors) {
            courseIdSet.add(ins.courseId);
        }

        List<ScoredDocument> filteredResults = new ArrayList<>();
        for (ScoredDocument document : results) {
            String resultCourseId = document.getOnlyField(Const.SearchDocumentField.COURSE_ID).getText();
            if (courseIdSet.contains(resultCourseId)) {
                filteredResults.add(document);
            }
        }
        return filteredResults;
    }

}
