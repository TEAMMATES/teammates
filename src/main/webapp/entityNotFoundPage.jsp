<%@ page import="teammates.common.util.Const"%>
<% response.setStatus(500);%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css">
    <link rel="stylesheet" href="stylesheets/teammatesCommon.css" type="text/css">
</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="/index.html">TEAMMATES</a>
            </div>
        </div>
    </div>
    <div id="mainContent" class="container">
        <div class="row">
            <div class="alert alert-warning col-md-6 col-md-offset-3">
                <img src="/images/puzzled.png"
                    style="float: left; margin: 0 10px 10px 0; height: 90px;">
                <p>
                    <br><br>
                    TEAMMATES could not locate what you were trying to access. <br><br>
                    <br><br>
                    Possible reasons include:
                </p>
                <ul>
                    <li>
                        You clicked on a link received in email, but the link was mangled by the email software. Try copy-pasting the entire link into the Browser address bar.
                    </li>
                    <li>
                        The entity (e.g. course, session) you were trying to access was deleted by an instructor after the link was sent to you by TEAMMATES.
                        <br><br>
                    </li>
                </ul>
                If the problem persists, contact TEAMMATES support at <a>teammates@comp.nus.edu.sg</a>
                <br><br>
                <b>Note: </b>If the problematic link was received via email, remember to copy-paste that email content when contacting TEAMMATES support.
                <br><br>
                If you are a registered user you can go back to the <a href="/page/studentHomePage">home page</a>
                <br><br>
                <p>
                </p>
            </div>
        </div>
    </div>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>