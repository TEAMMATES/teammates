<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Generic TEAMMATES Page" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="cssIncludes" fragment="true" %>
<%@ attribute name="jsIncludes" fragment="true" %>
<%@ attribute name="navBar" required="true" fragment="true" %>
<%@ attribute name="bodyTitle" required="true" %>
<%@ attribute name="bodyFooter" required="true" fragment="true" %>
<!DOCTYPE html>
<html>
  <head>
    <title>${pageTitle}</title>
    <link rel="shortcut icon" href="/favicon.png">
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" rel="stylesheet">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" rel="stylesheet">
    <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet">
    <jsp:invoke fragment="cssIncludes" />
  </head>
  <body>
    <noscript>
      <jsp:include page="<%= Const.ViewURIs.ENABLE_JS %>" />
    </noscript>
    <jsp:invoke fragment="navBar" />
    <div class="container" id="mainContent">
      <t:bodyHeader title="${bodyTitle}" />
      <jsp:doBody />
    </div>
    <jsp:invoke fragment="bodyFooter" />
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY_UI %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTBOX %>"></script>
    <jsp:invoke fragment="jsIncludes" />
  </body>
</html>
