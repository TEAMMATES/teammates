package teammates.storage.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

/**
 * The SearchDocument object that defines how we store {@link Document} for response comments
 */
public class FeedbackResponseCommentSearchDocument extends SearchDocument {
    
    private FeedbackResponseCommentAttributes comment;
    private FeedbackResponseAttributes relatedResponse;
    private String responseGiverName;
    private String responseRecipientName;
    private FeedbackQuestionAttributes relatedQuestion;
    private FeedbackSessionAttributes relatedSession;
    private CourseAttributes course;
    private InstructorAttributes giverAsInstructor; //comment giver
    private List<InstructorAttributes> relatedInstructors;
    private List<StudentAttributes> relatedStudents;
    
    public FeedbackResponseCommentSearchDocument(FeedbackResponseCommentAttributes comment) {
        this.comment = comment;
    }
    
    @Override
    protected void prepareData() {
        if (comment == null) {
            return;
        }
        
        relatedSession = fsDb.getFeedbackSession(comment.courseId, comment.feedbackSessionName);
        
        relatedQuestion = fqDb.getFeedbackQuestion(comment.feedbackQuestionId);
        
        relatedResponse = frDb.getFeedbackResponse(comment.feedbackResponseId);
        
        course = coursesDb.getCourse(comment.courseId);
        
        giverAsInstructor = instructorsDb.getInstructorForEmail(comment.courseId, comment.giverEmail);
        
        relatedInstructors = new ArrayList<InstructorAttributes>();
        relatedStudents = new ArrayList<StudentAttributes>();
        
        //prepare the response giver name and recipient name
        Set<String> addedEmailSet = new HashSet<String>();
        if (relatedQuestion.giverType == FeedbackParticipantType.INSTRUCTORS
                || relatedQuestion.giverType == FeedbackParticipantType.SELF) {
            InstructorAttributes ins = instructorsDb.getInstructorForEmail(comment.courseId, relatedResponse.giver);
            if (ins == null || addedEmailSet.contains(ins.email)) {
                responseGiverName = Const.USER_UNKNOWN_TEXT;
            } else {
                relatedInstructors.add(ins);
                addedEmailSet.add(ins.email);
                responseGiverName = ins.name + " (" + ins.displayedName + ")";
            }
        } else {
            StudentAttributes stu = studentsDb.getStudentForEmail(comment.courseId, relatedResponse.giver);
            if (stu == null || addedEmailSet.contains(stu.email)) {
                responseGiverName = Const.USER_UNKNOWN_TEXT;
            } else {
                relatedStudents.add(stu);
                addedEmailSet.add(stu.email);
                responseGiverName = stu.name + " (" + stu.team + ")";
            }
        }
        
        if (relatedQuestion.recipientType == FeedbackParticipantType.INSTRUCTORS) {
            InstructorAttributes ins = instructorsDb.getInstructorForEmail(comment.courseId, relatedResponse.recipient);
            if (ins != null && !addedEmailSet.contains(ins.email)) {
                relatedInstructors.add(ins);
                addedEmailSet.add(ins.email);
                responseRecipientName = ins.name + " (" + ins.displayedName + ")";
            }
        } else if (relatedQuestion.recipientType == FeedbackParticipantType.SELF) {
            responseRecipientName = responseGiverName;
        } else if (relatedQuestion.recipientType == FeedbackParticipantType.NONE) {
            responseRecipientName = Const.USER_NOBODY_TEXT;
        } else {
            StudentAttributes stu = studentsDb.getStudentForEmail(comment.courseId, relatedResponse.recipient);
            if (stu != null && !addedEmailSet.contains(stu.email)) {
                relatedStudents.add(stu);
                addedEmailSet.add(stu.email);
                responseRecipientName = stu.name + " (" + stu.team + ")";
            }
            List<StudentAttributes> team = studentsDb.getStudentsForTeam(relatedResponse.recipient, comment.courseId);
            if (team != null) {
                responseRecipientName = relatedResponse.recipient; //it's actually a team name here
                for (StudentAttributes studentInTeam : team) {
                    if (!addedEmailSet.contains(studentInTeam.email)) {
                        relatedStudents.add(studentInTeam);
                        addedEmailSet.add(studentInTeam.email);
                    }
                }
            }
            if (stu == null || team == null) {
                responseRecipientName = Const.USER_UNKNOWN_TEXT;
            }
        }
    }

    @Override
    public Document toDocument() {
        
        //populate related Students/Instructors information
        StringBuilder relatedPeopleBuilder = new StringBuilder("");
        String delim = ",";
        int counter = 0;
        for (StudentAttributes student : relatedStudents) {
            if (counter == 25) {
                break; //in case of exceeding size limit for document
            }
            relatedPeopleBuilder.append(student.email).append(delim)
                .append(student.name).append(delim)
                .append(student.team).append(delim)
                .append(student.section).append(delim);
            counter++;
        }
        counter = 0;
        for (InstructorAttributes instructor : relatedInstructors) {
            if (counter == 25) {
                break;
            }
            relatedPeopleBuilder.append(instructor.email).append(delim)
                .append(instructor.name).append(delim)
                .append(instructor.displayedName).append(delim);
            counter++;
        }
        
        //produce searchableText for this feedback comment document:
        //it contains
        //courseId, courseName, feedback session name, question number, question title
        //response answer
        //commentGiverEmail, commentGiverName,
        //related people's information, and commentText
        StringBuilder searchableTextBuilder = new StringBuilder("");
        searchableTextBuilder.append(comment.courseId).append(delim)
                             .append(course == null ? "" : course.getName()).append(delim)
                             .append(relatedSession.getFeedbackSessionName()).append(delim)
                             .append("question ").append(relatedQuestion.questionNumber).append(delim)
                             .append(relatedQuestion.getQuestionDetails().getQuestionText()).append(delim)
                             .append(relatedResponse.getResponseDetails().getAnswerString()).append(delim)
                             .append(comment.giverEmail).append(delim)
                             .append(giverAsInstructor == null ? "" : giverAsInstructor.name).append(delim)
                             .append(relatedPeopleBuilder.toString()).append(delim)
                             .append(comment.commentText.getValue());
        
        //for data-migration use
        boolean isVisibilityFollowingFeedbackQuestion = comment.isVisibilityFollowingFeedbackQuestion;
        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                                   || comment.isVisibleTo(FeedbackParticipantType.GIVER);
        boolean isVisibleToReceiver = isVisibilityFollowingFeedbackQuestion
                                    ? relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                                    : comment.isVisibleTo(FeedbackParticipantType.RECEIVER);
        boolean isVisibleToInstructor = isVisibilityFollowingFeedbackQuestion
                                      ? relatedQuestion.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                                      : comment.isVisibleTo(FeedbackParticipantType.INSTRUCTORS);
        
        String displayedName = giverAsInstructor == null
                             ? comment.giverEmail
                             : giverAsInstructor.displayedName + " " + giverAsInstructor.name;
        Document doc = Document.newBuilder()
                // these are used to filter documents visible to certain instructor
                // TODO: some of the following fields are not used anymore
                // (refer to {@link FeedbackResponseCommentSearchQuery}), can remove them
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.COURSE_ID)
                                            .setText(comment.courseId))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_GIVER_EMAIL)
                                            .setText(comment.giverEmail))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.GIVER_EMAIL)
                                            .setText(relatedResponse.giver))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.GIVER_SECTION)
                                            .setText(relatedResponse.giverSection))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.RECIPIENT_EMAIL)
                                            .setText(relatedResponse.recipient))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.RECIPIENT_SECTION)
                                            .setText(relatedResponse.recipientSection))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_GIVER)
                                            .setText(Boolean.toString(isVisibleToGiver)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_RECEIVER)
                                            .setText(Boolean.toString(isVisibleToReceiver)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.IS_VISIBLE_TO_INSTRUCTOR)
                                            .setText(Boolean.toString(isVisibleToInstructor)))
                // searchableText and createdDate are used to match the query string
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.SEARCHABLE_TEXT)
                                            .setText(searchableTextBuilder.toString()))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.CREATED_DATE)
                                            .setDate(comment.createdAt))
                // attribute field is used to convert a doc back to attribute
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(comment)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_RESPONSE_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(relatedResponse)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_RESPONSE_GIVER_NAME)
                                            .setText(JsonUtils.toJson(responseGiverName)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_RESPONSE_RECEIVER_NAME)
                                            .setText(JsonUtils.toJson(responseRecipientName)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_QUESTION_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(relatedQuestion)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_SESSION_ATTRIBUTE)
                                            .setText(JsonUtils.toJson(relatedSession)))
                .addField(Field.newBuilder().setName(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_GIVER_NAME)
                                            .setText(JsonUtils.toJson(displayedName)))
                .setId(comment.getId().toString())
                .build();
        return doc;
    }

}
