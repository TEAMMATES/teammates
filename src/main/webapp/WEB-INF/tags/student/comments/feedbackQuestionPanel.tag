<%@ tag description="Feedback question panel" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/student/comments" prefix="comments" %>
<%@ attribute name="feedbackQuestionTable" type="teammates.ui.template.QuestionTable" required="true" %>
<%@ attribute name="fsIdx" required="true" %>
<%@ attribute name="qnIdx" required="true" %>
<div class="panel panel-info">
    <div class="panel-heading">
        <b>Question ${feedbackQuestionTable.questionNumber}</b>:
        ${feedbackQuestionTable.questionText}
    </div>
    <table class="table">
        <tbody>
            <c:forEach items="${feedbackQuestionTable.responseRows}" var="feedbackResponseRow" varStatus="j">
                <c:set var="responseIndex" value="${j.index + 1}" />
                <comments:feedbackResponsePanel feedbackResponseRow="${feedbackResponseRow}"
                 fsIdx="${fsIdx}" qnIdx="${qnIdx}" responseIndex="${responseIndex}"/>
            </c:forEach>
        </tbody>
    </table>
</div>