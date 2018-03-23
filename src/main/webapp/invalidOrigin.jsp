<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<% response.setStatus(403);%>
<t:errorPage>
  <br><br>
  <div class="row">
    <div class="alert alert-warning col-md-4 col-md-offset-4">
      <img src="/images/angry.png" style="float: left; height: 90px; margin: 0 10px 10px 0;">
      <p>
        The request origin could not be validated. For your security, this action has been blocked.<br><br>
        Here are a few things you can try:
      </p>
      <ul class="small narrow-slight">
        <li>
          Click on the link from the relevant page instead of typing in the URL.
        </li>
        <li>
          Disable HTTP referrer spoofing in your browser if you previously enabled it.
        </li>
        <li>
          Enable cookies in your browser.
        </li>
      </ul>
      <br>
    </div>
  </div>
</t:errorPage>
