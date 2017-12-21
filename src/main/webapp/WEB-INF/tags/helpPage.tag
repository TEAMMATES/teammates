<%@ tag description="Generic TEAMMATES Help Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES</title>
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" type="text/css">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
  </head>
  <body style="padding-top: 0;">
    <div class="navbar navbar-inverse navbar-static-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <t:teammatesLogo/>
        </div>
        <!-- Navigation links added below for help pages -->
        <div class="collapse navbar-collapse" id="navbar-collapse-1">
            <ul class="nav navbar-nav">
              <li class="${currentPage == 'index' ? 'active' : ''}"><a href="/">Home</a></li>
              <li class="${currentPage == 'features' ? 'active' : ''}"><a href="features.jsp">Features</a></li>
              <li class="${currentPage == 'about' ? 'active' : ''}"><a href="about.jsp">About Us</a></li>
              <li class="${currentPage == 'contact' ? 'active' : ''}"><a href="contact.jsp">Contact</a></li>
              <li class="${currentPage == 'terms' ? 'active' : ''}"><a href="terms.jsp">Terms of Use</a></li>
            </ul>
            <form class="navbar-form navbar-right" action="/login" name="login">
              <input type="submit" name="student" class="btn btn-login " id="btnStudentLogin" value="Student Login" label="studentLogin">
              <input type="submit" name="instructor" class="btn btn-login" id="btnInstructorLogin" value="Instructor Login" label="instructorLogin">
            </form>
        </div>
      </div>
    </div>
    <div class="container" id="mainContent">
      <jsp:doBody />
    </div>
    <div id="footerComponent" class="container-fluid">
      <div class="container">
        <div class="row">
          <div class="col-md-2">
            <span>[<a href="/">TEAMMATES</a>]</span>
          </div>
          <div class="col-md-8">
            [hosted on <a href="https://cloud.google.com/appengine/" target="_blank" rel="noopener noreferrer">Google App Engine</a>]
          </div>
          <div class="col-md-2">
            <span>[Send <a class="link" href="/contact.jsp" target="_blank" rel="noopener noreferrer">Feedback</a>]</span>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
