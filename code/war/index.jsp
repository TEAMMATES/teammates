<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.google.appengine.api.utils.SystemProperty"%>
<html>
<head>
        <link rel="shortcut icon" href="/favicon.png" />
        <meta http-equiv="X-UA-Compatible" content="IE=8" />
        <title>Teammates</title>
        <link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
        <script language="JavaScript" src="js/index.js"></script>
		<script type="text/javascript" src="js/blockUnsupportedBrowsers.js"></script>
</head>

<body>
        <div id="frameTop">
                <div id="frameTopWrapper">
                        <div id="logo">
                                <img alt="Teammates" height="47px"
                                        src="images/teammateslogo.jpg"
                                        width="150px" />
                        </div>
                </div>
        </div>

        <div id="frameBody">
                <div id="frameBodyWrapper">
                        <div id="loginTableHolder">
                                <table id="login">
                                        <tr>
                                                <td class="loginCell">
                                                        <input type="button" name="STUDENT_LOGIN" class="button" value="Student" onclick="studentLogin();" />
                                                </td>
                                        </tr>
                                        <tr>
                                                <td class="loginCell">
                                                        <input type="button" name="COORDINATOR_LOGIN" class="button" value="Coordinator" onclick="coordinatorLogin();" />
                                                </td>
                                        </tr>
                                </table>
                        </div>
                </div>
        </div>

        <div id="frameBottom">
        <jsp:include page="./footer.jsp" />
        </div>
</body>
</html>