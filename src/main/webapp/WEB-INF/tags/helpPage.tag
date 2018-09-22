<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic TEAMMATES Help Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ attribute name="jsIncludes" %>
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
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_UI %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.ELASTICLUNR%>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.MARK_JS%>"></script>
    ${jsIncludes}
  </body>
</html>
