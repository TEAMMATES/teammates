<%@ tag description="Generic TEAMMATES Page" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="jsIncludes" fragment="true" %>
<%@ attribute name="navBar" required="true" fragment="true" %>
<%@ attribute name="bodyTitle" required="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>
    <link rel="shortcut icon" href="/favicon.png" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css"/>
    
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript"  src="/bootstrap/js/bootstrap.min.js"></script>
    
    <jsp:invoke fragment="jsIncludes" />
    
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <t:enableJS />
</head>
<body>
    <jsp:invoke fragment="navBar" />
    <div id="frameBodyWrapper" class="container theme-showcase">
        <t:bodyHeader title="${bodyTitle}" />
        <jsp:doBody />
    </div>
    <t:bodyFooter version="tempVal"/>
</body>
</html>