<%@ page import="teammates.common.util.Const"%>
<% response.setStatus(401);%>
<!DOCTYPE html>

<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css"/>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>
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
        <br><br>
        <div class="row">
            <div class="alert alert-warning col-md-4 col-md-offset-4">
                <img src="/images/angry.png"
                    style="float: left; height: 90px; margin: 0 10px 10px 0;">
                <p>
                    You are not authorized to view this page. <br> <br>
                    <a href="/logout.jsp">Logout and return to main page.</a>
                </p>
                <br>
            </div>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>