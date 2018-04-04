<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/admin" prefix="ta" %>
<%@ taglib tagdir="/WEB-INF/tags/admin/email/log" prefix="adminEmailLog" %>

<div>
  <table id="email-logs-table">
    <c:forEach items="${data.logs}" var="log">
      <adminEmailLog:emailLogTableRow log="${log}" />
    </c:forEach>
  </table>

  <div id="status-message">${data.statusForAjax}</div>
</div>
