<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="searchCommentFeedbackQuestion.tag - Feedback response" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<%@ attribute name="fsIndx" required="true" %>
<%@ attribute name="qnIndx" required="true" %>
<%@ attribute name="responseIndex" required="true" %>
<%@ attribute name="responseRow" type="teammates.ui.template.ResponseRow" required="true"%>

<tr>
  <td>
    <b>From:</b> ${fn:escapeXml(responseRow.giverName)}
    <b>To:</b> ${fn:escapeXml(responseRow.recipientName)}
  </td>
</tr>

<tr>
  <td>
    <strong>Response:</strong> ${responseRow.response}
  </td>
</tr>

<tr class="active">
  <td>Comment(s):</td>
</tr>

<tr>
  <td>
    <ul class="list-group comments" id="responseCommentTable-${fsIndx}-${qnIndx}-${responseIndex}"
        <c:if test="${empty responseRow.feedbackResponseComments}">style="display:none"</c:if>>

      <c:forEach items="${responseRow.feedbackResponseComments}" var="frc" varStatus="i">
        <shared:feedbackResponseCommentRow frc="${frc}" firstIndex="${fsIndx}" secondIndex="${qnIndx}" thirdIndex="${responseIndex}" frcIndex="${i.count}" />
      </c:forEach>

    </ul>
  </td>
</tr>
