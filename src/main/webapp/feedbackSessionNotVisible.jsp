<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="teammates.common.util.Const"%>
<% response.setStatus(403);%>
<%
  String startTimeString = (String)session.getAttribute(Const.ParamsNames.FEEDBACK_SESSION_NOT_VISIBLE);
  session.removeAttribute(Const.ParamsNames.FEEDBACK_SESSION_NOT_VISIBLE);
  pageContext.setAttribute("startTimeString", startTimeString);
%>
<t:errorPage>
  <br><br>
  <div class="row">
    <div class="alert alert-warning col-md-4 col-md-offset-4">
      <img src="/images/angry.png" style="float: left; height: 90px; margin: 0 10px 10px 0;">
      <p>
        Sorry, this session is currently not open for submission.
        <c:if test="${not empty startTimeString}">
          It will be open from ${startTimeString}<br><br>
        </c:if>
      </p>
      <br>
    </div>
  </div>
</t:errorPage>
