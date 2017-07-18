package teammates.storage.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;

/**
 * The {@link SearchDocument} object that defines how we store {@link Document} for response comments.
 */
public class FeedbackResponseCommentSearchDocument extends SearchDocument {

    private FeedbackResponseCommentAttributes comment;
    private FeedbackResponseAttributes relatedResponse;
    private String responseGiverName;
    private String responseRecipientName;
    private FeedbackQuestionAttributes relatedQuestion;
    private FeedbackSessionAttributes relatedSession;
    private CourseAttributes course;
    private InstructorAttributes giverAsInstructor; // comment giver
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
        relatedInstructors = new ArrayList<>();
        relatedStudents = new ArrayList<>();

        // prepare the response giver name and recipient name
        Set<String> addedEmailSet = new HashSet<>();
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
                responseRecipientName = relatedResponse.recipient; // it's actually a team name here
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

        // populate related Students/Instructors information
        StringBuilder relatedPeopleBuilder = new StringBuilder("");
        String delim = ",";
        int counter = 0;
        for (StudentAttributes student : relatedStudents) {
            if (counter == 25) {
                break; // in case of exceeding size limit for document
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

        // produce searchableText for this feedback comment document:
        // it contains courseId, courseName, feedback session name, question number, question title,
        // response answer commentGiverEmail, commentGiverName, related people's information, and commentText
        String searchableText = comment.courseId + delim
                                + (course == null ? "" : course.getName()) + delim
                                + relatedSession.getFeedbackSessionName() + delim
                                + "question " + relatedQuestion.questionNumber + delim
                                + relatedQuestion.getQuestionDetails().getQuestionText() + delim
                                + relatedResponse.getResponseDetails().getAnswerString() + delim
                                + comment.giverEmail + delim
                                + (giverAsInstructor == null ? "" : giverAsInstructor.name) + delim
                                + relatedPeopleBuilder.toString() + delim
                                + comment.commentText.getValue();

        // for data-migration use
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
        return Document.newBuilder()
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
                                            .setText(searchableText))
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
    }

    /**
     * Produces a {@link FeedbackResponseCommentSearchResultBundle} from the {@code Results<ScoredDocument>} collection.
     * The list of {@link InstructorAttributes} is used to filter out the search result.
     */
    public static FeedbackResponseCommentSearchResultBundle fromResults(
            Results<ScoredDocument> results, List<InstructorAttributes> instructors) {
        FeedbackResponseCommentSearchResultBundle bundle = new FeedbackResponseCommentSearchResultBundle();
        if (results == null) {
            return bundle;
        }

        // get instructor's information
        bundle.instructorEmails = new HashSet<>();
        Set<String> instructorCourseIdList = new HashSet<>();
        for (InstructorAttributes ins : instructors) {
            bundle.instructorEmails.add(ins.email);
            instructorCourseIdList.add(ins.courseId);
        }

        Set<String> isAdded = new HashSet<>();

        List<ScoredDocument> filteredResults = filterOutCourseId(results, instructors);
        for (ScoredDocument doc : filteredResults) {
            // get FeedbackResponseComment from results
            FeedbackResponseCommentAttributes comment = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText(),
                    FeedbackResponseCommentAttributes.class);
            if (frcDb.getFeedbackResponseComment(comment.getId()) == null) {
                frcDb.deleteDocument(comment);
                continue;
            }
            List<FeedbackResponseCommentAttributes> commentList = bundle.comments.get(comment.feedbackResponseId);
            if (commentList == null) {
                commentList = new ArrayList<>();
                bundle.comments.put(comment.feedbackResponseId, commentList);
            }
            commentList.add(comment);

            // get related response from results
            FeedbackResponseAttributes response = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_ATTRIBUTE).getText(),
                    FeedbackResponseAttributes.class);
            if (frDb.getFeedbackResponse(response.getId()) == null) {
                frcDb.deleteDocument(comment);
                continue;
            }
            List<FeedbackResponseAttributes> responseList = bundle.responses.get(response.feedbackQuestionId);
            if (responseList == null) {
                responseList = new ArrayList<>();
                bundle.responses.put(response.feedbackQuestionId, responseList);
            }
            if (!isAdded.contains(response.getId())) {
                isAdded.add(response.getId());
                responseList.add(response);
            }

            // get related question from results
            FeedbackQuestionAttributes question = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_QUESTION_ATTRIBUTE).getText(),
                    FeedbackQuestionAttributes.class);
            if (fqDb.getFeedbackQuestion(question.getId()) == null) {
                frcDb.deleteDocument(comment);
                continue;
            }
            List<FeedbackQuestionAttributes> questionList = bundle.questions.get(question.feedbackSessionName);
            if (questionList == null) {
                questionList = new ArrayList<>();
                bundle.questions.put(question.feedbackSessionName, questionList);
            }
            if (!isAdded.contains(question.getId())) {
                isAdded.add(question.getId());
                questionList.add(question);
            }

            // get related session from results
            FeedbackSessionAttributes session = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_SESSION_ATTRIBUTE).getText(),
                    FeedbackSessionAttributes.class);
            if (fsDb.getFeedbackSession(session.getCourseId(), session.getSessionName()) == null) {
                frcDb.deleteDocument(comment);
                continue;
            }
            if (!isAdded.contains(session.getFeedbackSessionName())) {
                isAdded.add(session.getFeedbackSessionName());
                bundle.sessions.put(session.getSessionName(), session);
            }

            // get giver and recipient names
            String responseGiverName = extractContentFromQuotedString(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_GIVER_NAME).getText());
            bundle.responseGiverTable.put(response.getId(),
                    getFilteredGiverName(bundle, instructorCourseIdList, response, responseGiverName));

            String responseRecipientName = extractContentFromQuotedString(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_RECEIVER_NAME).getText());
            bundle.responseRecipientTable.put(response.getId(),
                    getFilteredRecipientName(bundle, instructorCourseIdList, response, responseRecipientName));

            String commentGiverName = extractContentFromQuotedString(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_GIVER_NAME).getText());
            bundle.commentGiverTable.put(comment.getId().toString(),
                    getFilteredCommentGiverName(bundle, instructorCourseIdList, response, comment, commentGiverName));
            bundle.instructorEmailNameTable.put(comment.giverEmail, commentGiverName);
            boolean isLastEditorEmailInMap = !comment.lastEditorEmail.isEmpty()
                    && bundle.instructorEmailNameTable.containsKey(comment.lastEditorEmail);
            if (!isLastEditorEmailInMap) {
                InstructorAttributes instructor =
                        instructorsDb.getInstructorForEmail(response.courseId, comment.lastEditorEmail);
                String commentLastEditorName = instructor.displayedName + " " + instructor.name;
                bundle.instructorEmailNameTable.put(comment.lastEditorEmail, commentLastEditorName);
            }
            bundle.numberOfResults++;
        }
        for (List<FeedbackQuestionAttributes> questions : bundle.questions.values()) {
            Collections.sort(questions);
        }

        for (List<FeedbackResponseAttributes> responses : bundle.responses.values()) {
            FeedbackResponseAttributes.sortFeedbackResponses(responses);
        }

        for (List<FeedbackResponseCommentAttributes> responseComments : bundle.comments.values()) {
            FeedbackResponseCommentAttributes.sortFeedbackResponseCommentsByCreationTime(responseComments);
        }

        bundle.numberOfResults =
                filterFeedbackResponseCommentResults(bundle, instructors, bundle.numberOfResults);
        removeQuestionsAndResponsesWithoutComments(bundle);
        return bundle;
    }

    private static String getFilteredCommentGiverName(FeedbackResponseCommentSearchResultBundle bundle,
                                                      Set<String> instructorCourseIdList,
                                                      FeedbackResponseAttributes response,
                                                      FeedbackResponseCommentAttributes comment, String name) {
        return isCommentGiverNameVisibleToInstructor(
                bundle.instructorEmails, instructorCourseIdList, response, comment) ? name : "Anonymous";
    }

    private static String getFilteredGiverName(FeedbackResponseCommentSearchResultBundle bundle,
                                               Set<String> instructorCourseIdList,
                                               FeedbackResponseAttributes response, String name) {
        FeedbackQuestionAttributes question = getFeedbackQuestion(bundle.questions, response);
        if (!isNameVisibleToInstructor(bundle.instructorEmails, instructorCourseIdList,
                                       response, question.showGiverNameTo)
                && question.giverType != FeedbackParticipantType.SELF) {
            return FeedbackSessionResultsBundle.getAnonName(question.giverType, name);
        }
        return name;
    }

    private static String getFilteredRecipientName(FeedbackResponseCommentSearchResultBundle bundle,
                                                   Set<String> instructorCourseIdList,
                                                   FeedbackResponseAttributes response, String name) {
        FeedbackQuestionAttributes question = getFeedbackQuestion(bundle.questions, response);
        if (!isNameVisibleToInstructor(bundle.instructorEmails, instructorCourseIdList,
                                       response, question.showRecipientNameTo)
                && question.recipientType != FeedbackParticipantType.SELF
                && question.recipientType != FeedbackParticipantType.NONE) {
            return FeedbackSessionResultsBundle.getAnonName(question.recipientType, name);
        }
        return name;
    }

    private static FeedbackQuestionAttributes getFeedbackQuestion(
            Map<String, List<FeedbackQuestionAttributes>> questions, FeedbackResponseAttributes response) {
        FeedbackQuestionAttributes question = null;
        for (FeedbackQuestionAttributes qn : questions.get(response.feedbackSessionName)) {
            if (qn.getId().equals(response.feedbackQuestionId)) {
                question = qn;
                break;
            }
        }
        return question;
    }

    private static boolean isCommentGiverNameVisibleToInstructor(
            Set<String> instructorEmails, Set<String> instructorCourseIdList,
            FeedbackResponseAttributes response, FeedbackResponseCommentAttributes comment) {
        // in the old ver, name is always visible
        if (comment.isVisibilityFollowingFeedbackQuestion) {
            return true;
        }

        // comment giver can always see
        if (instructorEmails.contains(comment.giverEmail)) {
            return true;
        }
        List<FeedbackParticipantType> showNameTo = comment.showGiverNameTo;
        for (FeedbackParticipantType type : showNameTo) {
            if (type == FeedbackParticipantType.GIVER
                    && instructorEmails.contains(response.giver)) {
                return true;
            } else if (type == FeedbackParticipantType.INSTRUCTORS
                    && instructorCourseIdList.contains(response.courseId)) {
                return true;
            } else if (type == FeedbackParticipantType.RECEIVER
                    && instructorEmails.contains(response.recipient)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNameVisibleToInstructor(
            Set<String> instructorEmails, Set<String> instructorCourseIdList,
            FeedbackResponseAttributes response, List<FeedbackParticipantType> showNameTo) {
        // giver can always see
        if (instructorEmails.contains(response.giver)) {
            return true;
        }
        for (FeedbackParticipantType type : showNameTo) {
            if (type == FeedbackParticipantType.INSTRUCTORS
                    && instructorCourseIdList.contains(response.courseId)) {
                return true;
            } else if (type == FeedbackParticipantType.RECEIVER
                    && instructorEmails.contains(response.recipient)) {
                return true;
            }
        }
        return false;
    }

    private static int filterFeedbackResponseCommentResults(
            FeedbackResponseCommentSearchResultBundle frCommentSearchResults,
            List<InstructorAttributes> instructors, int totalResultsSize) {

        Iterator<Entry<String, List<FeedbackResponseAttributes>>> iterFr =
                frCommentSearchResults.responses.entrySet().iterator();

        int filteredResultsSize = totalResultsSize;
        while (iterFr.hasNext()) {
            List<FeedbackResponseAttributes> frs = iterFr.next().getValue();
            Iterator<FeedbackResponseAttributes> fr = frs.iterator();

            while (fr.hasNext()) {
                FeedbackResponseAttributes response = fr.next();
                InstructorAttributes instructor = getInstructorForCourseId(response.courseId, instructors);

                boolean isVisibleResponse = true;
                boolean isNotAllowedForInstructor =
                        instructor == null
                        || !instructor.isAllowedForPrivilege(
                                response.giverSection, response.feedbackSessionName,
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)
                        || !instructor.isAllowedForPrivilege(
                                response.recipientSection, response.feedbackSessionName,
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);

                if (isNotAllowedForInstructor) {
                    isVisibleResponse = false;
                }
                if (!isVisibleResponse) {
                    int sizeOfCommentList = frCommentSearchResults.comments.get(response.getId()).size();
                    filteredResultsSize -= sizeOfCommentList;
                    // TODO: also need to decrease the size for (fr)CommentSearchResults|studentSearchResults
                    frCommentSearchResults.comments.remove(response.getId());
                    fr.remove();
                }
            }
        }

        Set<String> emailList = frCommentSearchResults.instructorEmails;
        Iterator<Entry<String, List<FeedbackQuestionAttributes>>> iterQn =
                frCommentSearchResults.questions.entrySet().iterator();
        while (iterQn.hasNext()) {
            String fsName = iterQn.next().getKey();
            List<FeedbackQuestionAttributes> questionList = frCommentSearchResults.questions.get(fsName);

            for (int i = questionList.size() - 1; i >= 0; i--) {
                FeedbackQuestionAttributes question = questionList.get(i);
                List<FeedbackResponseAttributes> responseList = frCommentSearchResults.responses.get(question.getId());

                for (int j = responseList.size() - 1; j >= 0; j--) {
                    FeedbackResponseAttributes response = responseList.get(j);
                    List<FeedbackResponseCommentAttributes> commentList =
                            frCommentSearchResults.comments.get(response.getId());

                    for (int k = commentList.size() - 1; k >= 0; k--) {
                        FeedbackResponseCommentAttributes comment = commentList.get(k);

                        if (emailList.contains(comment.giverEmail)) {
                            continue;
                        }

                        boolean isVisibilityFollowingFeedbackQuestion = comment.isVisibilityFollowingFeedbackQuestion;
                        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                                || comment.isVisibleTo(FeedbackParticipantType.GIVER);

                        if (isVisibleToGiver && emailList.contains(response.giver)) {
                            continue;
                        }

                        boolean isVisibleToReceiver = isVisibilityFollowingFeedbackQuestion
                                ? question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)
                                : comment.isVisibleTo(FeedbackParticipantType.RECEIVER);

                        if (isVisibleToReceiver && emailList.contains(response.recipient)) {
                            continue;
                        }

                        boolean isVisibleToInstructor = isVisibilityFollowingFeedbackQuestion
                                ? question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)
                                : comment.isVisibleTo(FeedbackParticipantType.INSTRUCTORS);

                        if (isVisibleToInstructor) {
                            continue;
                        }
                        commentList.remove(k);
                    }
                    if (commentList.isEmpty()) {
                        responseList.remove(j);
                    }
                }
                if (responseList.isEmpty()) {
                    questionList.remove(i);
                }
            }
            if (questionList.isEmpty()) {
                iterQn.remove();
            }
        }

        return filteredResultsSize;
    }

    private static InstructorAttributes getInstructorForCourseId(String courseId, List<InstructorAttributes> instructors) {
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId.equals(courseId)) {
                return instructor;
            }
        }

        return null;
    }

    private static void removeQuestionsAndResponsesWithoutComments(
            FeedbackResponseCommentSearchResultBundle frCommentSearchResults) {
        Iterator<Entry<String, List<FeedbackQuestionAttributes>>> fqsIter =
                frCommentSearchResults.questions.entrySet().iterator();

        while (fqsIter.hasNext()) {
            Iterator<FeedbackQuestionAttributes> fqIter = fqsIter.next().getValue().iterator();

            while (fqIter.hasNext()) {
                FeedbackQuestionAttributes fq = fqIter.next();
                if (frCommentSearchResults.responses.get(fq.getId()).isEmpty()) {
                    fqIter.remove();
                }
            }
        }
    }
}
