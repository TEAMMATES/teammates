<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.Sanitizer"%>
<%@ page import="teammates.ui.controller.PageData"%>
<%@ page import="teammates.ui.controller.InstructorCourseJoinConfirmationPageData"%>
<%
    InstructorCourseJoinConfirmationPageData data = (InstructorCourseJoinConfirmationPageData) request
            .getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <title>TEAMMATES - Instructor</title>

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
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    
    <jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER%>" />

    <div class="container" id="mainContent">
        <br>
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
        <div id="topOfPage"></div>

        <div class="panel panel-primary panel-narrow">
            <div class="panel-heading">
                <h3>Confirm your Google account</h3>
            </div>
            <div class="panel-body">
                <p class="lead">
                    You are currently logged in as <span><strong><%=data.account.googleId%></strong></span>.
                    <br>If this is not you please <a
                        href="/logout.jsp">log out</a> and re-login using your own Google account. 
                        <br>If this is you, please confirm below to complete your registration. <br>
                <div class="align-center">
                    <%
                        String ref = "";
                        if (data.institute == null) {

                            ref = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED
                                    + "?key=" + data.regkey;
                        } else {
                            ref = Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED
                                    + "?key=" + data.regkey + "&"
                                    + Const.ParamsNames.INSTRUCTOR_INSTITUTION + "="
                                    + Sanitizer.sanitizeForUri(data.institute);
                        }
                    %>
                    <a href="<%=ref%>" id="button_confirm"
                        class="btn btn-success">Yes, this is my account</a> <a href="/logout.jsp" id="button_cancel"
                        class="btn btn-danger">No, this is not my account</a>
                </div>
                </p>
            </div>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>