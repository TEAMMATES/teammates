package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Document.Builder;
import com.google.gson.Gson;

import teammates.common.datatransfer.BaseCommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

public abstract class BaseCommentSearchDocument extends SearchDocument {

    protected CourseAttributes course;
    protected InstructorAttributes giverAsInstructor; // comment giver
    protected List<StudentAttributes> relatedStudents;

    protected void prepareFieldsForDataPreparation(BaseCommentAttributes comment) {
        course = logic.getCourse(comment.courseId);
        giverAsInstructor = logic.getInstructorForEmail(comment.courseId, comment.giverEmail);
        relatedStudents = new ArrayList<StudentAttributes>();
    }

    protected StringBuilder populateRelatedStudentsInformation(int limit) {
        StringBuilder relatedPeopleBuilder = new StringBuilder("");
        String delim = ",";
        int counter = 0;
        for (StudentAttributes student : relatedStudents) {
            if (counter == limit) {
                break;// in case of exceeding size limit for document
            }
            relatedPeopleBuilder.append(student.email).append(delim)
                                .append(student.name).append(delim)
                                .append(student.team).append(delim)
                                .append(student.section).append(delim);
            counter++;
        }
        return relatedPeopleBuilder;
    }

    protected StringBuilder prepareCommonSearchableTextBuilder(BaseCommentAttributes comment,
                                                               String delim) {
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(comment.courseId).append(delim);
        searchableTextBuilder.append(course != null ? course.name : "").append(delim);
        searchableTextBuilder.append(comment.giverEmail).append(delim);
        searchableTextBuilder.append(giverAsInstructor != null ? giverAsInstructor.name : "").append(delim);
        searchableTextBuilder.append(comment.commentText.getValue()).append(delim);
        return searchableTextBuilder;
    }

    protected Builder prepareCommonDocumentPart(BaseCommentAttributes comment, String giverEmail,
                                                StringBuilder searchableTextBuilder, String instructor,
                                                String commentAttrField, String commentGiverNameField) {
        Builder docBuilder = Document.newBuilder().setId(comment.getId().toString());
        docBuilder.addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID)
                                              .setText(comment.courseId))
                  .addField(Field.newBuilder().setName(Const.SearchDocumentField.GIVER_EMAIL)
                                              .setText(giverEmail))
                  // searchableText and createdDate are used to match the query string
                  .addField(Field.newBuilder().setName(Const.SearchDocumentField.CREATED_DATE)
                                              .setDate(comment.createdAt))
                  .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                              .setText(searchableTextBuilder.toString()))
                  // this is used to filter documents visible to certain instructor
                  .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR)
                                              .setText(instructor))
                  // attribute field is used to convert a doc back to attribute
                  .addField(Field.newBuilder().setName(commentAttrField)
                                              .setText(new Gson().toJson(comment)))
                  .addField(Field.newBuilder().setName(commentGiverNameField)
                                              .setText(new Gson().toJson(giverAsInstructor != null
                                                                             ? giverAsInstructor.displayedName + " "
                                                                                   + giverAsInstructor.name
                                                                             : comment.giverEmail)));
        return docBuilder;
    }

}
