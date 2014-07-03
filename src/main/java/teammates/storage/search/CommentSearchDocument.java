package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.gson.Gson;

public class CommentSearchDocument implements SearchDocument {
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    
    private CommentAttributes comment;
    private CourseAttributes course;
    private InstructorAttributes giverAsInstructor;
    private List<StudentAttributes> relatedStudents;
    
    public CommentSearchDocument(CommentAttributes comment){
        prepareDate(comment);
    }
    
    private void prepareDate(CommentAttributes comment) {
        if(comment == null) return;
        this.comment = comment;
        course = coursesLogic.getCourse(comment.courseId);
        
        giverAsInstructor = instructorsLogic.
                getInstructorForEmail(comment.courseId, comment.giverEmail);
        
        relatedStudents = new ArrayList<StudentAttributes>();
        switch (comment.recipientType) {
        case PERSON:
            for(String email:comment.recipients){
                StudentAttributes student = studentsLogic.getStudentForEmail(comment.courseId, email);
                if(student != null){
                    relatedStudents.add(student);
                }
            }
            break;
        case TEAM:
            for(String team:comment.recipients){
                List<StudentAttributes> students = studentsLogic.getStudentsForTeam(team, comment.courseId);
                if(students != null){
                    relatedStudents.addAll(students);
                }
            }
            break;
        case SECTION:
            for(String section:comment.recipients){
                List<StudentAttributes> students = studentsLogic.getStudentsForSection(section, comment.courseId);
                if(students != null){
                    relatedStudents.addAll(students);
                }
            }
        case COURSE:
            break;
        default:
            break;
        }
    }

    @Override
    public Document toDocument() {

        //populate recipients information
        StringBuilder recipientsBuilder = new StringBuilder("");
        String delim = ",";
        int counter = 0;
        for(StudentAttributes student:relatedStudents){
            if(counter == 50) break;//in case of exceeding size limit for document
            recipientsBuilder.append(student.email).append(delim)
                .append(student.name).append(delim)
                .append(student.team).append(delim)
                .append(student.section).append(delim);
            counter++;
        }
        
        //produce searchableText for this comment document:
        //it contains
        //courseId, courseName, giverEmail, giverName, 
        //recipientEmails/Teams/Sections, and commentText
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(comment.courseId).append(delim);
        searchableTextBuilder.append(course != null? course.name: "").append(delim);
        searchableTextBuilder.append(comment.giverEmail).append(delim);
        searchableTextBuilder.append(giverAsInstructor != null? giverAsInstructor.name: "").append(delim);
        searchableTextBuilder.append(recipientsBuilder.toString()).append(delim);
        searchableTextBuilder.append(comment.commentText.getValue());
        
        Document doc = Document.newBuilder()
            //this is used to filter documents visible to certain instructor
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID).setText(comment.courseId))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.GIVER_EMAIL).setText(comment.giverEmail))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR).setText(
                    comment.isVisibleTo(CommentRecipientType.INSTRUCTOR).toString()))
            //searchableText and createdDate are used to match the query string
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT).setText(searchableTextBuilder.toString()))
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.CREATED_DATE).setDate(comment.createdAt))
            //attribute field is used to convert a doc back to attribute
            .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_ATTRIBUTE).setText(new Gson().toJson(comment)))
            .setId(comment.getCommentId().toString())
            .build();
        return doc;
    }

}
