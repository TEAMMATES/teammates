<%@ tag description="instructorSearch.jsp - Search comments for responses" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>

<br>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong><jsp:doBody/></strong>
    </div>
    <c:set var="fsIndx" value="${0}" />
    
    <c:forEach items="${data.searchCommentsForResponsesTables}" var="searchCommentsForResponsesTable">
        <c:forEach items="${searchCommentsForResponsesTable.feedbackSessionRows}" var="fsRow">
            <c:set var="fsIndx" value="${fsIndx + 1}" />
            <c:set var="fsName" value="${fsRow.feedbackSessionName}" />
            
            <search:searchCommentFeedbackSession feedbackSessionRow="${fsRow}" fsIndx="${fsIndx}"/>
        </c:forEach>
    </c:forEach>
</div>