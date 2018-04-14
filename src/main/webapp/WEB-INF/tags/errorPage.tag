<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic TEAMMATES Error Page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<!DOCTYPE html>
<html>
  <head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES</title>
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" type="text/css">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
  </head>
  <body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <t:teammatesLogo/>
        </div>
      </div>
    </div>
    <div class="container" id="mainContent">
      <t:statusMessage statusMessagesToUser="${data.statusMessagesToUser}" />
      <jsp:doBody />
      <div class="row">
        <div class="col-md-6 col-md-offset-3 align-center">
          <h2>Uh oh! Something went wrong.</h2>
        </div>
      </div>
      <hr>
      <div class="row">
        <div class="col-md-12">
          <p>
            We are sorry this happened. You can safely ignore this error page in the following cases:
          </p>
          <ul>
            <li>
              Retrying the same action a few minutes later succeeds (i.e. no more error page).
            </li>
            <li>
              You loaded an outdated page unintentionally. e.g. some browsers auto-load the pages that were loaded in the previous browsing session.
            </li>
          </ul>
          <p>
            However, if you keep seeing this page on multiple failed attempts when trying to perform some action in TEAMMATES, please help us troubleshoot the problem by providing us some additional details using the form below.
          </p>
        </div>
      </div>
      <t:errorPageEmailCompose />
    </div>
    <t:bodyFooter />
  </body>
  <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
  <script type="text/javascript" src="/js/errorPageEmailComposer.js"></script>
</html>
