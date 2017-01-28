package teammates.storage.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

public class InstructorSearchDocument extends SearchDocument {
    
    private InstructorAttributes instructor;
    private CourseAttributes course;
    
    public InstructorSearchDocument(InstructorAttributes instructor) {
        this.instructor = instructor;
    }
    
    @Override
    protected void prepareData() {
        if (instructor == null) {
            return;
        }
        
        course = coursesDb.getCourse(instructor.courseId);
    }

    @Override
    protected Document toDocument() {
        
        String delim = ",";
        
        // produce searchableText for this instructor document:
        // it contains courseId, courseName, instructorName, instructorEmail, instructorGoogleId, instructorRole
        String searchableText = instructor.courseId + delim
                                + (course == null ? "" : course.getName()) + delim
                                + instructor.name + delim
                                + instructor.email + delim
                                + (instructor.googleId == null ? "" : instructor.googleId) + delim
                                + instructor.role + delim
                                + instructor.displayedName;
        
        return Document.newBuilder()
                // searchableText is used to match the query string
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                            .setText(searchableText))
                // attribute field is used to convert a doc back to attribute
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.INSTRUCTOR_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(instructor)))
                .setId(StringHelper.encrypt(instructor.key))
                .build();
    }

    /**
     * This method should be used by admin only since the previous searching does not restrict the
     * visibility according to the logged-in user's google ID. Therefore,This fromResults method
     * does not require a googleID as a parameter. Returned results bundle will contain information
     * related to matched instructors only.
     * @param results
     * @return studentResultBundle containing information related to matched students only.
     */
    public static InstructorSearchResultBundle fromResults(Results<ScoredDocument> results) {
        InstructorSearchResultBundle bundle = new InstructorSearchResultBundle();
        if (results == null) {
            return bundle;
        }
        
        bundle.cursor = results.getCursor();
        
        for (ScoredDocument doc : results) {
            InstructorAttributes instructor = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.INSTRUCTOR_ATTRIBUTE).getText(),
                    InstructorAttributes.class);
            
            if (instructorsDb.getInstructorForRegistrationKey(StringHelper.encrypt(instructor.key)) == null) {
                instructorsDb.deleteDocument(instructor);
                continue;
            }
            
            bundle.instructorList.add(instructor);
            bundle.numberOfResults++;
        }
        
        sortInstructorResultList(bundle.instructorList);
        
        return bundle;
    }
    
    private static void sortInstructorResultList(List<InstructorAttributes> instructorList) {
        
        Collections.sort(instructorList, new Comparator<InstructorAttributes>() {
            @Override
            public int compare(InstructorAttributes ins1, InstructorAttributes ins2) {
                int compareResult = ins1.courseId.compareTo(ins2.courseId);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = ins1.role.compareTo(ins2.role);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = ins1.name.compareTo(ins2.name);
                if (compareResult != 0) {
                    return compareResult;
                }
                      
                return ins1.email.compareTo(ins2.email);
            }
        });
    }
    
}
