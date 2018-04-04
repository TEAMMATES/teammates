<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<% response.setStatus(401);%>
<t:errorPage>
  <br><br>
  <div class="row">
    <div class="alert alert-warning col-md-4 col-md-offset-4">
      <img src="/images/angry.png" style="float: left; height: 90px; margin: 0 10px 10px 0;">
      <p>
        You are not authorized to view this page. <br> <br>
        <a href="/logout">Logout and return to main page.</a>
      </p>
      <br>
    </div>
  </div>
</t:errorPage>
