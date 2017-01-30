package teammates.storage.search;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

public class StudentSearchDocument extends SearchDocument {

    private StudentAttributes student;
    private CourseAttributes course;
    
    public StudentSearchDocument(StudentAttributes student) {
        this.student = student;
    }
    
    @Override
    protected void prepareData() {
        if (student == null) {
            return;
        }
        
        course = coursesDb.getCourse(student.course);
    }

    @Override
    public Document toDocument() {
        
        String delim = ",";
        
        // produce searchableText for this student document:
        // it contains courseId, courseName, studentEmail, studentName studentTeam and studentSection
        String searchableText = student.course + delim
                                + (course == null ? "" : course.getName()) + delim
                                + student.email + delim
                                + student.name + delim
                                + student.team + delim
                                + student.section;
        
        return Document.newBuilder()
                // this is used to filter documents visible to certain instructor
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID)
                                            .setText(student.course))
                // searchableText and createdDate are used to match the query string
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                            .setText(searchableText))
                // attribute field is used to convert a doc back to attribute
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.STUDENT_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(student)))
                .setId(student.key)
                .build();
    }
    
    /**
     * This method should be used by admin only since the previous searching does not restrict the
     * visibility according to the logged-in user's google ID. Therefore,This fromResults method
     * does not require a googleID as a parameter. Returned results bundle will contain information
     * related to matched students only.
     * @param results
     * @return studentResultBundle containing information related to matched students only.
     */
    public static StudentSearchResultBundle fromResults(Results<ScoredDocument> results) {
        StudentSearchResultBundle bundle = new StudentSearchResultBundle();
        if (results == null) {
            return bundle;
        }
        
        for (ScoredDocument doc : results) {
            StudentAttributes student = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.STUDENT_ATTRIBUTE).getText(),
                    StudentAttributes.class);
            if (student.key == null
                    || studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(student.key)) == null) {
                studentsDb.deleteDocument(student);
                continue;
            }
            
            bundle.studentList.add(student);
            bundle.numberOfResults++;
        }
        
        sortStudentResultList(bundle.studentList);
        
        return bundle;
    }
    
    /**
     * Produce a StudentSearchResultBundle from the Results<ScoredDocument> collection.
     * The list of InstructorAttributes is used to filter out the search result.
     */
    public static StudentSearchResultBundle fromResults(Results<ScoredDocument> results,
                                                        List<InstructorAttributes> instructors) {
        StudentSearchResultBundle bundle = new StudentSearchResultBundle();
        if (results == null) {
            return bundle;
        }
        
        for (InstructorAttributes ins : instructors) {
            bundle.courseIdInstructorMap.put(ins.courseId, ins);
        }
        
        List<ScoredDocument> filteredResults = filterOutCourseId(results, instructors);
        for (ScoredDocument doc : filteredResults) {
            StudentAttributes student = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.STUDENT_ATTRIBUTE).getText(),
                    StudentAttributes.class);
            if (student.key == null
                    || studentsDb.getStudentForRegistrationKey(StringHelper.encrypt(student.key)) == null) {
                studentsDb.deleteDocument(student);
                continue;
            }
            
            bundle.studentList.add(student);
            bundle.numberOfResults++;
        }
        
        sortStudentResultList(bundle.studentList);
        
        return bundle;
    }

    private static void sortStudentResultList(List<StudentAttributes> studentList) {
        
        Collections.sort(studentList, new Comparator<StudentAttributes>() {
            @Override
            public int compare(StudentAttributes s1, StudentAttributes s2) {
                int compareResult = s1.course.compareTo(s2.course);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = s1.section.compareTo(s2.section);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = s1.team.compareTo(s2.team);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                compareResult = s1.name.compareTo(s2.name);
                if (compareResult != 0) {
                    return compareResult;
                }
                
                return s1.email.compareTo(s2.email);
            }
        });
    }
    
}
