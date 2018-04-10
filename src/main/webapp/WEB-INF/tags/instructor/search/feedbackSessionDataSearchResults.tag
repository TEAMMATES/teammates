<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorSearch.jsp - Search comments for responses" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/search" prefix="search" %>
<%@ attribute name="feedbackSessionDataTables" type="java.util.Collection" required="true" %>

<br>
<div class="panel panel-primary">
  <div class="panel-heading">
    <strong><jsp:doBody/></strong>
  </div>

  <c:forEach items="${feedbackSessionDataTables}" var="searchFeedbackSessionDataTable" varStatus="i">
    <c:forEach items="${searchFeedbackSessionDataTable.feedbackSessionRows}" var="fsRow">
      <c:set var="fsName" value="${fsRow.feedbackSessionName}" />
      <search:searchCommentFeedbackSession feedbackSessionRow="${fsRow}" fsIndx="${i.count}"/>
    </c:forEach>
  </c:forEach>
</div>
