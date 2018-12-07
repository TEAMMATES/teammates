<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Status message" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>

<%@ attribute name="statusMessagesToUser" type="java.util.Collection" %>
<%@ attribute name="doNotFocusToStatus" %>

<c:choose>
  <c:when test="${fn:length(statusMessagesToUser) gt 0}">
    <div id="statusMessagesToUser">
      <c:forEach items="${statusMessagesToUser}" var="statusMessage">
        <div class="overflow-auto alert alert-${statusMessage.color} icon-${statusMessage.color} statusMessage">
          ${statusMessage.text}
        </div>
      </c:forEach>
    </div>
    <c:if test="${not doNotFocusToStatus}">
      <script type="text/javascript" src="/js/statusMessage.js" defer></script>
    </c:if>
  </c:when>
  <c:otherwise>
    <div id="statusMessagesToUser" style="display: none;">
    </div>
  </c:otherwise>
</c:choose>
