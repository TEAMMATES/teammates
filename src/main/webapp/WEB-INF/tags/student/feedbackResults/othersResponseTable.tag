<%@ tag description="questionWithOthersResponses.tag - Others-responses given to a particular recipient" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="othersResponseTable" type="teammates.ui.template.FeedbackResultsResponseTable" required="true" %>

<c:set value="panel-primary" var="panelHeaderClass"/>

<div class="panel ${panelHeaderClass}">
  <div class="panel-heading">
    <b>To:</b> ${fn:escapeXml(othersResponseTable.recipientName)}
  </div>
  <table class="table">
    <tbody>
    <c:forEach items="${othersResponseTable.responses}" var="othersResponse">
      <feedbackResults:othersResponse othersResponse="${othersResponse}"/>
    </c:forEach>
    </tbody>
  </table>
</div>
