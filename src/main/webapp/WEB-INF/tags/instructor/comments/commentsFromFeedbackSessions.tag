<%@ tag description="Comments from feedback sessions" %>
<%@ attribute name="courseId" required="true" %>
<%@ attribute name="fsName" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="panelIdx" required="true" %>
<div class="panel panel-primary">
    <div class="panel-heading" style="cursor: pointer;"
         onclick="loadFeedbackResponseComments('${data.account.googleId}','${courseId}','${fsName}', '${fsIdx}', this, true);">
        <strong>Comments in session: ${fsName}</strong>
        <div class="pull-right">
            <div class="placeholder-img-loading" style="display:inline-block;"></div>
            <div class="display-icon" style="display:inline-block; margin-left:5px;">
                <span class="glyphicon glyphicon-chevron-down"></span>
            </div>
        </div>
    </div>
    
    <div class="panel-collapse collapse">
        <div class="panel-body">
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
</div>