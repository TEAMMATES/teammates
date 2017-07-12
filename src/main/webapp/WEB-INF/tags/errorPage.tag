<%@ tag description="Generic TEAMMATES Error Page" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES</title>
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" type="text/css">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://cdn.jsdelivr.net/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://cdn.jsdelivr.net/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
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
        <jsp:doBody />
        <div class="row">
            <div class="col-md-6 col-md-offset-3 align-center">
                <h2>Error Feedback Collection</h2>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-md-12">
                <p>You seem to have encountered an error! If you think you were incorrectly shown this error page then we request you to take a moment and fill in the email below detailing your steps before you encountered it.</p>
            </div>
        </div>
        <t:errorPageEmailCompose />
    </div>
    <t:bodyFooter />
</body>
<script type="text/javascript" src="https://unpkg.com/jquery@1.12.4/dist/jquery.min.js"></script>
<script type="text/javascript" src="/js/errorPageEmailComposer.js"></script>
</html>
