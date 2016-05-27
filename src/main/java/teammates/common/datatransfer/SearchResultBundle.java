package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.util.Const;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

/**
 * The search result bundle object.
 */
public abstract class SearchResultBundle {
    protected String extractContentFromQuotedString(String quotedString) {
        if (quotedString.matches("^\".*\"$")) {
            return quotedString.substring(1, quotedString.length() - 1);
        }
        return quotedString;
    }
    
    /**
     * This method must be called to filter out the search result for course Id.
     */
    protected List<ScoredDocument> filterOutCourseId(Results<ScoredDocument> results,
                                                     List<InstructorAttributes> instructors) {
        Set<String> courseIdSet = new HashSet<String>();
        for (InstructorAttributes ins : instructors) {
            courseIdSet.add(ins.courseId);
        }
        
        List<ScoredDocument> filteredResults = new ArrayList<ScoredDocument>();
        for (ScoredDocument document : results) {
            String resultCourseId = document.getOnlyField(Const.SearchDocumentField.COURSE_ID).getText();
            if (courseIdSet.contains(resultCourseId)) {
                filteredResults.add(document);
            }
        }
        return filteredResults;
    }
    
    public abstract int getResultSize();
}
