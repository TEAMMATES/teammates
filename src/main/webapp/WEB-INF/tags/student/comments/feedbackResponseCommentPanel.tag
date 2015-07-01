<%@ tag description="StudentComments - Feedback response comment" %>
<%@ attribute name="feedbackResponseCommentRow" type="teammates.ui.template.FeedbackResponseCommentRow" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="qnIdx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="responseCommentIndex" required="true" %>
<li class="list-group-item list-group-item-warning" id="responseCommentRow-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">
    <div id="commentBar-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">
        <span class="text-muted">From:
            <b>${feedbackResponseCommentRow.giverDetails}</b>
            on ${feedbackResponseCommentRow.creationTime} ${feedbackResponseCommentRow.editedAt}
        </span>
    </div>
    <div id="plainCommentText-${fsIdx}-${qnIdx}-${responseIndex}-${responseCommentIndex}">${feedbackResponseCommentRow.comment}</div>
</li>