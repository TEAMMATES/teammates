<%@ tag description="StudentComments - Feedback response comment" %>
<%@ attribute name="feedbackResponseCommentRow" type="teammates.ui.template.FeedbackResponseComment" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="qnIdx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="responseCommentIndex" required="true" %>
<li class="list-group-item list-group-item-warning" id="responseCommentRow-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">
    <div id="commentBar-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">
        <span class="text-muted">
            From: ${feedbackResponseCommentRow.giverDisplay} [${feedbackResponseCommentRow.createdAt}] ${feedbackResponseCommentRow.editedAt}
        </span>
    </div>
    <div id="plainCommentText-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">${feedbackResponseCommentRow.commentText}</div>
</li>