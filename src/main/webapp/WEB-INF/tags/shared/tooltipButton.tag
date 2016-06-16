<%@ tag description="instructorSearch / instructorStudentList - Student List" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="isEnabled" required="true" %>

<span class="tooltip-button-wrapper"
      <c:if test="${not isEnabled}">
      title="<%= Const.Tooltips.ACTION_NOT_ALLOWED %>"
      data-toggle="tooltip"
      data-placement="top"
      </c:if>>
    <jsp:doBody />
</span>