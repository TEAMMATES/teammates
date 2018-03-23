<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic TEAMMATES Static Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="currentPage" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="application-name" content="TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects">
    <meta name="keywords" content="Teammates, peer feedback, peer evaluation, student, team, project, free">
    <meta name="description" content="TEAMMATES is an Online Peer Feedback System for student team projects. It is completely free to use. Get your students to evaluate their performance in team projects, and view reports and summaries of their feedback and evaluations.">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" rel="stylesheet">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" rel="stylesheet">
    <link type="text/css" href="stylesheets/teammatesCommon.css" rel="stylesheet">
    <link rel="shortcut icon" href="/favicon.png">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <title>TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects</title>
  </head>
  <body>
    <div id="mainContainer">
      <nav class="navbar navbar-inverse navbar-fixed-top">
        <div class="container">
          <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-1" aria-expanded="false">
              <span class="sr-only">Toggle navigation</span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <t:teammatesLogo/>
          </div>
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
      </nav>
      <div id="mainContent" class="container">
        <jsp:doBody />
      </div>
    </div>
    <footer id="footerComponent">
      <div class="container text-nowrap">
        <div class="row">
          <div class="col-xs-12 col-sm-4 col-sm-offset-8 col-md-3 col-md-offset-9">
            Hosted on <a class="footer" href="https://cloud.google.com/appengine/" target="_blank" rel="noopener noreferrer">Google App Engine</a>
          </div>
        </div>
      </div>
    </footer>
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_UI %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    ${jsIncludes}
  </body>
</html>
