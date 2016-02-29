<%@ tag description="instructorSearch.jsp - Search comments for responses" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>
<%@ attribute name="commentsForResponsesTables" type="java.util.Collection" required="true" %>

<br>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong><jsp:doBody /></strong>
        <div class='display-icon pull-right'>
            <span class="glyphicon ${'glyphicon-chevron-up'} pull-right"></span>
        </div>
    </div>

    <div class="panel-collapse collapse in">
        <c:forEach items="${commentsForResponsesTables}"
            var="searchCommentsForResponsesTable" varStatus="i">
            <c:forEach
                items="${searchCommentsForResponsesTable.feedbackSessionRows}"
                var="fsRow">
                <c:set var="fsName" value="${fsRow.feedbackSessionName}" />
                <search:searchCommentFeedbackSession
                    feedbackSessionRow="${fsRow}" fsIndx="${i.count}" />
            </c:forEach>
        </c:forEach>
    </div>
</div>