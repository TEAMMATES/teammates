<%@ tag description="selfResponseTable.tag - Self-responses given to a particular recipient" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared"%>
<%@ attribute name="selfResponse" type="teammates.ui.template.FeedbackResultsResponse" required="true" %>

<tr class="resultSubheader">
  <td>
    <span class="bold"><b>From:</b></span> ${fn:escapeXml(selfResponse.giverName)}
  </td>
</tr>

<tr>
  <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
  <td class="text-preserve-space">${selfResponse.answer}</td>
</tr>

<c:if test="${not empty selfResponse.comments}">
  <tr>
    <td>
      <ul class="list-group comment-list">
        <c:forEach items="${selfResponse.comments}" var="comment">
          <shared:feedbackResponseCommentRow frc="${comment}" />
        </c:forEach>
      </ul>
    </td>
  </tr>
</c:if>
