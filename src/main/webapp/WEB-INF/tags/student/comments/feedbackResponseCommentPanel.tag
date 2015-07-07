<%@ tag description="StudentComments - Feedback response comment" %>
<%@ attribute name="feedbackResponseComment" type="teammates.ui.template.FeedbackResponseComment" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="qnIdx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="responseCommentIndex" required="true" %>
<li class="list-group-item list-group-item-warning" id="responseCommentRow-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">
    <div id="commentBar-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">
        <span class="text-muted">
            From: ${feedbackResponseComment.giverDisplay} [${feedbackResponseComment.createdAt}] ${feedbackResponseComment.editedAt}
        </span>
    </div>
    <div id="plainCommentText-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">${feedbackResponseComment.commentText}</div>
</li>