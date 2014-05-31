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
    <br><br>
        <div class="row">
            <div class="alert alert-warning col-md-4 col-md-offset-4">
                <img src="/images/embarrassed.png"
                    style="float: left; margin: 0 10px 10px 0; height: 90px;">
                <p>
                    TEAMMATES is currently unavailable due to routine maintenance activities.<br>
                    It will be back within ONE HOUR. We apologize for the inconvenience.<br><br><br>
                </p>
            </div>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>