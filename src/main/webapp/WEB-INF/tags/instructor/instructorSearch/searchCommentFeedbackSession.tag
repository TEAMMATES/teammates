<%@ tag description="commentsForResponsesSearchResults.tag - Feedback session when instructor searches for a keyword in feedback response comments"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/instructor/instructorSearch" prefix="search"%>
<%@ attribute name="fsIndx" required="true" %>
<%@ attribute name="feedbackSessionRow" type="teammates.ui.template.FeedbackSessionRow" required="true"%>

<div class="panel-body">
    <div class="row <c:if test="${fsIndx != 1}">border-top-gray</c:if>">
        <div class="col-md-2">
            <strong>
                Session: ${feedbackSessionRow.feedbackSessionName} (${feedbackSessionRow.courseId})
            </strong>
        </div>
        <div class="col-md-10">
            <c:set var="qnIndx" value="${0}" />
            
            <c:forEach items="${feedbackSessionRow.questionTables}" var="questionTable">
                <c:set var="qnIndx" value="${qnIndx + 1}" />
                <search:searchCommentFeedbackQuestion questionTable="${questionTable}" qnIndx="${qnIndx}" fsIndx="${fsIndx}" />
            </c:forEach>  
        </div>
    </div>
</div>