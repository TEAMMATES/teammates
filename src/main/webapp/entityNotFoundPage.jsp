<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<% response.setStatus(500);%>
<t:errorPage>
  <div class="row">
    <div class="alert alert-warning col-md-6 col-md-offset-3">
      <img src="/images/puzzled.png" style="float: left; margin: 0 10px 10px 0; height: 90px;">
      <p>
        <br><br>
        TEAMMATES could not locate what you were trying to access. <br><br>
        <br><br>
        Possible reasons include:
      </p>
      <ul>
        <li>
          You clicked on a link received in email, but the link was mangled by the email software. Try copy-pasting the entire link into the Browser address bar.
        </li>
        <li>
          The entity (e.g. course, session) you were trying to access was deleted by an instructor after the link was sent to you by TEAMMATES.
          <br><br>
        </li>
      </ul>
      If the problem persists, please inform <a class="link" href="contact.jsp" target="_blank" rel="noopener noreferrer">TEAMMATES support team</a>.
      <br><br>
      <b>Note: </b>If the problematic link was received via email, please also forward us the original email containing the link you clicked, to help us with the troubleshooting.
      <br><br>
      If you are a registered user you can go back to the <a href="/page/studentHomePage">home page</a>
      <br><br>
    </div>
  </div>
</t:errorPage>
