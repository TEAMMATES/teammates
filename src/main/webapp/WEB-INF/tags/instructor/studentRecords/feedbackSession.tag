<%@ tag description="instructorStudentRecords - Feedback Session" %>
<%@ attribute name="session" type="teammates.ui.template.InstructorStudentRecordsFeedbackSession" required="true" %>
<%@ attribute name="index" required="true" %>
<div class="well well-plain student_feedback" id="studentFeedback-${index}" 
     onclick="loadFeedbackSession('${session.courseId}', '${session.studentEmail}', '${session.googleId}', '${session.feedbackSessionName}', this)">
    <div class="text-primary">
        <h2 id="feedback_name-${index}">
            <strong>Feedback Session : ${session.sanitizedFsName}</strong>
        </h2>
    </div>
    <div class="placeholder-img-loading"></div>
    <div id="target-feedback-${index}">
    </div>
</div>
<br>