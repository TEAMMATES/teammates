<%@ page import="teammates.common.util.Const"%>
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

    <div id="frameBodyWrapper" class="container">
            <div style="margin: 30px auto; border: 1px solid #333; padding: 10px; text-align: left; width: 550px; background: #FFFFCC; height: 100px;">
                <img src="/images/error.png"
                    style="float: left; margin: 0 10px 10px 0; height: 90px;">
                <p>
                    There was an error in our server.<br> <br>
                    Please try again in a few moments.
                </p>
            </div>
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>