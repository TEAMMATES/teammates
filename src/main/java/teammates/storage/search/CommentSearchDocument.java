package teammates.storage.search;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

/**
 * The SearchDocument object that defines how we store {@link Document} for student comments
 */
public class CommentSearchDocument extends SearchDocument {
    
    private CommentAttributes comment;
    private CourseAttributes course;
    private InstructorAttributes giverAsInstructor;
    private List<StudentAttributes> relatedStudents;
    private String commentRecipientName;
    
    public CommentSearchDocument(CommentAttributes comment) {
        this.comment = comment;
    }
    
    @Override
    protected void prepareData() {
        if (comment == null) {
            return;
        }
        course = coursesDb.getCourse(comment.courseId);
        giverAsInstructor = instructorsDb.getInstructorForEmail(comment.courseId, comment.giverEmail);
        relatedStudents = new ArrayList<StudentAttributes>();
        commentRecipientName = buildCommentRecipientName();
    }
    
    private String buildCommentRecipientName() {
        StringBuilder commentRecipientNameBuilder = new StringBuilder(100);
        String delim = "";
        switch (comment.recipientType) {
        case PERSON:
            for (String email : comment.recipients) {
                StudentAttributes student = studentsDb.getStudentForEmail(comment.courseId, email);
                if (student == null) {
                    commentRecipientNameBuilder.append(delim).append(email);
                } else {
                    relatedStudents.add(student);
                    commentRecipientNameBuilder.append(delim)
                                               .append(student.name)
                                               .append(" (" + student.team + ", " + student.email + ")");
                }
                delim = ", ";
            }
            break;
        case TEAM:
            for (String team : comment.recipients) {
                List<StudentAttributes> students =
                        studentsDb.getStudentsForTeam(StringHelper.recoverFromSanitizedText(team), comment.courseId);
                if (students != null) {
                    relatedStudents.addAll(students);
                }
                commentRecipientNameBuilder.append(delim).append(team);
                delim = ", ";
            }
            break;
        case SECTION:
            for (String section : comment.recipients) {
                List<StudentAttributes> students = studentsDb.getStudentsForSection(section, comment.courseId);
                if (students != null) {
                    relatedStudents.addAll(students);
                }
                commentRecipientNameBuilder.append(delim).append(section);
                delim = ", ";
            }
            break;
        case COURSE:
            for (String course : comment.recipients) {
                commentRecipientNameBuilder.append(delim).append("All students in Course ").append(course);
                delim = ", ";
            }
            break;
        default:
            break;
        }
        return commentRecipientNameBuilder.toString();
    }

    @Override
    public Document toDocument() {
        
        // populate recipients information
        StringBuilder recipientsBuilder = new StringBuilder("");
        String delim = ",";
        int counter = 0;
        for (StudentAttributes student : relatedStudents) {
            if (counter == 50) {
                break; // in case of exceeding size limit for document
            }
            recipientsBuilder.append(student.email).append(delim)
                             .append(student.name).append(delim)
                             .append(student.team).append(delim)
                             .append(student.section).append(delim);
            counter++;
        }
        
        // produce searchableText for this comment document:
        // it contains courseId, courseName, giverEmail, giverName, recipientEmails/Teams/Sections, and commentText
        String searchableText = comment.courseId + delim
                                + (course == null ? "" : course.getName()) + delim
                                + comment.giverEmail + delim
                                + (giverAsInstructor == null ? "" : giverAsInstructor.name) + delim
                                + recipientsBuilder.toString() + delim
                                + comment.commentText.getValue();
        
        String displayedName = giverAsInstructor == null
                             ? comment.giverEmail
                             : giverAsInstructor.displayedName + " " + giverAsInstructor.name;
        return Document.newBuilder()
                // this is used to filter documents visible to certain instructor
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID)
                                            .setText(comment.courseId))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.GIVER_EMAIL)
                                            .setText(comment.giverEmail))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR)
                                            .setText(comment.isVisibleTo(CommentParticipantType.INSTRUCTOR).toString()))
                // searchableText and createdDate are used to match the query string
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                            .setText(searchableText))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.CREATED_DATE)
                                            .setDate(comment.createdAt))
                // attribute field is used to convert a doc back to attribute
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(comment)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_GIVER_NAME)
                                            .setText(JsonUtils.toJson(displayedName)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME)
                                            .setText(JsonUtils.toJson(commentRecipientName)))
                .setId(comment.getCommentId().toString())
                .build();
    }

    /**
     * Produce a CommentSearchResultBundle from the Results<ScoredDocument> collection.
     * The list of InstructorAttributes is used to filter out the search result.
     */
    public static CommentSearchResultBundle fromResults(Results<ScoredDocument> results,
                                                        List<InstructorAttributes> instructors) {
        CommentSearchResultBundle bundle = new CommentSearchResultBundle();
        if (results == null) {
            return bundle;
        }
        
        List<String> giverEmailList = new ArrayList<String>();
        for (InstructorAttributes ins : instructors) {
            giverEmailList.add(ins.email);
        }
        
        List<ScoredDocument> filteredResults = filterOutCourseId(results, instructors);
        for (ScoredDocument doc : filteredResults) {
            CommentAttributes comment = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.COMMENT_ATTRIBUTE).getText(),
                    CommentAttributes.class);
            if (commentsDb.getComment(comment.getCommentId()) == null) {
                commentsDb.deleteDocument(comment);
                continue;
            }
            comment.sendingState = CommentSendingState.SENT;
            String giverName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_GIVER_NAME).getText();
            String recipientName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME).getText();
            
            boolean isGiver = giverEmailList.contains(comment.giverEmail);
            String giverAsKey = comment.giverEmail + comment.courseId;
            
            if (isGiver) {
                giverName = "You (" + comment.courseId + ")";
            } else if (comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR)) {
                giverName = extractContentFromQuotedString(giverName) + " (" + comment.courseId + ")";
            } else {
                giverAsKey = "Anonymous" + comment.courseId;
                giverName = "Anonymous" + " (" + comment.courseId + ")";
            }
            
            if (isGiver || comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR)) {
                recipientName = extractContentFromQuotedString(recipientName);
            } else {
                recipientName = "Anonymous";
            }
            
            List<CommentAttributes> commentList = bundle.giverCommentTable.get(giverAsKey);
            if (commentList == null) {
                commentList = new ArrayList<CommentAttributes>();
                bundle.giverCommentTable.put(giverAsKey, commentList);
            }
            commentList.add(comment);
            bundle.giverTable.put(giverAsKey, giverName);
            bundle.recipientTable.put(comment.getCommentId().toString(), recipientName);
            bundle.numberOfResults++;
        }
        
        for (List<CommentAttributes> comments : bundle.giverCommentTable.values()) {
            CommentAttributes.sortCommentsByCreationTime(comments);
        }
        
        return bundle;
    }
    
}
