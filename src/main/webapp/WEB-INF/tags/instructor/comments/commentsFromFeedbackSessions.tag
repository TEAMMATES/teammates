<%@ tag description="Comments from feedback sessions" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="fsName"%>
<%@ attribute name="fsIdx" %>
<%@ attribute name="panelIdx"%>
<div class="panel panel-primary">
    <div class="panel-heading" onclick="loadFeedbackResponseComments('${data.account.googleId}','${data.courseId}','${fsName}', '${fsIdx}', this);"
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