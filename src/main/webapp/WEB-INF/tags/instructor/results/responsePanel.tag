<%@ tag description="instructorFeedbackResults - participant > participant > question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>

<%@ attribute name="responsePanel" type="teammates.ui.template.InstructorFeedbackResultsResponsePanel" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>


<div class="panel panel-info">
    <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
    <div class="panel-heading">
    Question ${responsePanel.question.questionNumber}: <span class="text-preserve-space">${responsePanel.questionText}${responsePanel.additionalInfoText}</span>
    </div>
    <div class="panel-body">
        <div style="clear:both; overflow: hidden">
            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
            <div class="pull-left text-preserve-space">${responsePanel.displayableResponse}</div>
            
            <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" 
                onclick="showResponseCommentAddForm(${responsePanel.recipientIndex},${responsePanel.giverIndex},${responsePanel.qnIndex})"
                data-toggle="tooltip" data-placement="top" title="<%=Const.Tooltips.COMMENT_ADD%>"
                <c:if test="${!responsePanel.allowedToAddComment}">
                        disabled="disabled"
                </c:if>
                >
                <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
            </button>
        </div>
        <ul class="list-group" id="responseCommentTable-${responsePanel.recipientIndex}-${responsePanel.giverIndex}-${responsePanel.qnIndex}"
            style="${not empty responsePanel.comments ? 'margin-top:15px;': 'display:none'}">
            <c:forEach items="${responsePanel.comments}" var="responseComment" varStatus="status">
                <shared:feedbackResponseComment frc="${responseComment}" firstIndex="${responsePanel.recipientIndex}" 
                                                secondIndex="${responsePanel.giverIndex}" thirdIndex="${responsePanel.qnIndex}" 
                                                frcIndex="${status.count}"/>
            </c:forEach>
        </ul>
        
    </div>

</div>