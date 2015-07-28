<%@ tag description="instructorFeedbackResults - participant > participant > question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

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
        </div>
    </div>

</div>