<%@ tag description="Generic TEAMMATES Page" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="jsIncludes" fragment="true" %>
<%@ attribute name="navBar" required="true" fragment="true" %>
<%@ attribute name="bodyTitle" required="true" %>
<%@ attribute name="bodyFooter" required="true" fragment="true" %>
<%@ attribute name="bodyOnload" %>
<!DOCTYPE html>
<html>
<head>
    <title>${pageTitle}</title>

    <link rel="shortcut icon" href="/favicon.png" />

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link type="text/css" href="/bootstrap/css/bootstrap.min.css" rel="stylesheet"/>
    <link type="text/css" href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet"/>
    <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet"/>

    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="${data.jQueryFilePath}"></script>
    <script type="text/javascript" src="${data.jQueryUiFilePath}"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>

    <t:enableJS />

    <jsp:invoke fragment="jsIncludes" />
</head>
<body ${bodyOnload}>
    <jsp:invoke fragment="navBar" />
    <div class="container" id="mainContent">
        <t:bodyHeader title="${bodyTitle}" />
        <jsp:doBody />
    </div>
    <jsp:invoke fragment="bodyFooter" />
</body>
</html>