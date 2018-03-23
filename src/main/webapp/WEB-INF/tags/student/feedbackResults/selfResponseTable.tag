<%@ tag description="questionWithSelfResponses.tag - Self-responses given to a particular recipient" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/student/feedbackResults" prefix="feedbackResults" %>
<%@ attribute name="selfResponseTable" type="teammates.ui.template.FeedbackResultsResponseTable" required="true" %>

<c:set value="panel-default" var="panelHeaderClass"/>

<div class="panel ${panelHeaderClass}">
  <div class="panel-heading">
    <b>To:</b> ${fn:escapeXml(selfResponseTable.recipientName)}
  </div>
  <table class="table">
    <tbody>
    <c:forEach items="${selfResponseTable.responses}" var="selfResponse">
      <feedbackResults:selfResponse selfResponse="${selfResponse}"/>
    </c:forEach>
    </tbody>
  </table>
</div>
