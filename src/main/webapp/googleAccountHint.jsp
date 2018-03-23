<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ page import="teammates.common.util.Const"%>
<%
  pageContext.setAttribute("expectedId", request.getAttribute(Const.ParamsNames.HINT));
  pageContext.setAttribute("actualId", request.getAttribute(Const.ParamsNames.USER_ID));
  pageContext.setAttribute("logoutUrl", request.getAttribute(Const.ParamsNames.NEXT_URL));
  pageContext.setAttribute("homePage", Const.ActionURIs.STUDENT_HOME_PAGE);
%>
<t:errorPage>
  <div class="panel panel-primary panel-narrow">
    <div class="panel-heading">
      <h4>
        Google Account Hint
      </h4>
    </div>
    <div class="panel-body">
      <p>
        The link you provided belongs to a user with Google ID (partially obscured for security) <strong>"${expectedId}"</strong>
        while you are currently logged in as <strong>"${actualId}"</strong>.
      </p>
      <ul class="small narrow-slight">
        <li>
          If the Google ID <strong>"${expectedId}"</strong>
          belongs to you, please proceed to the login page.
        </li>
        <li>
          If that Google ID does not belong to you, please inform
          <a class="link" href="contact.jsp" target="_blank" rel="noopener noreferrer">TEAMMATES support team</a>.
          <br><br>
          <b>Note: </b>If the problematic link was received via email, please also forward us the original email containing the link you clicked, to help us with the troubleshooting.
        </li>
      </ul>
    </div>
    <div class="panel-footer center-block align-center container-fluid">
      <a class="btn btn-primary" href="${logoutUrl}">
        Proceed to Login Page
      </a>
      <a class="btn btn-default" href="${homePage}">
        Go to Home Page
      </a>
    </div>
  </div>
</t:errorPage>
