<%@ tag description="Generic TEAMMATES Page" %>
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
    <script type="text/javascript" src="/js/libs-common.js"></script>
    <jsp:invoke fragment="jsIncludes" />
  </body>
</html>
