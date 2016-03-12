<%@ tag description="StudentComments - Feedback session" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ attribute name="feedbackSessionRow" type="teammates.ui.template.FeedbackSessionRow" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Comments in session: ${feedbackSessionRow.feedbackSessionName}</strong>
    </div> 
    <div class="panel-body"> 
        <c:forEach items="${feedbackSessionRow.questionTables}" var="feedbackQuestionTable" varStatus="i">
            <comments:feedbackQuestionPanel feedbackQuestionTable="${feedbackQuestionTable}"
             fsIdx="${fsIdx}" qnIdx="${i.index + 1}"/>
        </c:forEach>
    </div>
</div>