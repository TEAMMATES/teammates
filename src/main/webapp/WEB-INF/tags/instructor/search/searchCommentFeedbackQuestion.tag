<%@ tag description="SearchCommentFeedbackSession.tag - Feedback question when instructor searches for a keyword in feedback response comments"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search"%>
<%@ attribute name="fsIndx" required="true" %>
<%@ attribute name="qnIndx" required="true" %>
<%@ attribute name="questionTable" type="teammates.ui.template.QuestionTable" required="true"%>

<div class="panel panel-info">
    <div class="panel-heading">
        <b>Question ${questionTable.questionNumber}</b>: ${questionTable.questionText} ${questionTable.additionalInfo}
    </div>
    <table class="table">
        <tbody>       
            <c:forEach items="${questionTable.responseRows}" var="responseRow" varStatus="i">
                <search:feedbackResponse qnIndx="${qnIndx}" responseRow="${responseRow}" 
                                         responseIndex="${i.count}" fsIndx="${fsIndx}" />
            </c:forEach>    
        </tbody>
    </table>
</div>