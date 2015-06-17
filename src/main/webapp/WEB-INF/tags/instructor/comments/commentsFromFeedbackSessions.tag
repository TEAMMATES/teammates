<%@ tag description="Comments from feedback sessions" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="fsName" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="panelIdx" required="true" %>
<div class="panel panel-primary">
    <div class="panel-heading" onclick="loadFeedbackResponseComments('${data.account.googleId}','${courseId}','${fsName}', '${fsIdx}', this);"
        style="cursor: pointer;">
        <strong>Comments in session: ${fsName}</strong>
        <div class="placeholder-img-loading pull-right"></div>
    </div>
    <div class="panel-body hidden">
        <div class="placeholder-error-msg-${panelIdx} hidden">
            <div class="panel panel-info">
                <ul class="list-group comments">
                    <li class="list-group-item list-group-item-danger">
                        Failed to load response comments for this session. Please try again later.
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>