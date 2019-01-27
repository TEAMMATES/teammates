<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="commentsForResponsesSearchResults.tag - Feedback session when instructor searches for a keyword in feedback response comments" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search"%>
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
      <c:forEach items="${feedbackSessionRow.questionTables}" var="questionTable" varStatus="i">
        <search:searchCommentFeedbackQuestion questionTable="${questionTable}" qnIndx="${i.count}" fsIndx="${fsIndx}" />
      </c:forEach>
    </div>
  </div>
</div>
