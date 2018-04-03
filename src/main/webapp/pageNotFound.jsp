<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<% response.setStatus(404);%>
<t:errorPage>
  <div class="row">
    <div class="alert alert-warning col-md-4 col-md-offset-4">
      <img src="/images/error.png" style="float: left; margin: 0 10px 10px 0; height: 90px;">
      <p>
        The page you are looking for is not there.<br><br>
        Make sure that the URL is correct, or go to <a href="/">main page</a><br><br>
      </p>
    </div>
  </div>
</t:errorPage>
