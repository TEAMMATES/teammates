<%@ tag description="StudentComments - Feedback question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ attribute name="feedbackQuestionTable" type="teammates.ui.template.QuestionTable" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="qnIdx" required="true" %>
<div class="panel panel-info">
    <div class="panel-heading">
        <b>Question ${feedbackQuestionTable.questionNumber}</b>: ${feedbackQuestionTable.questionText}
    </div>
    <table class="table">
        <tbody>
            <c:forEach items="${feedbackQuestionTable.responseRows}" var="feedbackResponseRow" varStatus="i">
                <comments:feedbackResponsePanel feedbackResponseRow="${feedbackResponseRow}"
                 fsIdx="${fsIdx}" qnIdx="${qnIdx}" responseIndex="${i.index + 1}"/>
            </c:forEach>
        </tbody>
    </table>
</div>