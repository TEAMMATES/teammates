<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<% response.setStatus(500);%>
<t:errorPage>
  <br><br>
  <div class="row">
    <div class="alert alert-warning col-md-4 col-md-offset-4">
      <img src="/images/error.png" style="float: left; margin: 0 10px 10px 0; height: 90px;">
      <p>
        There was an error in our server.<br>
        Please try again in a few moments. <br><br>
        If the error persists, please <a class="link" href="contact.jsp" target="_blank" rel="noopener noreferrer"> let us know </a>
      </p>
    </div>
  </div>
</t:errorPage>
