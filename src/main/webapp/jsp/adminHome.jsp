<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.ui.controller.AdminHomePageData"%>
<%@ page import="static teammates.ui.controller.PageData.sanitizeForHtml" %>

<%
    AdminHomePageData data = (AdminHomePageData)request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
    <title>TEAMMATES - Administrator</title>

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

    <script type="text/javascript" src="/js/administrator.js"></script>
</head>

<body>
    <div id="frameTop">
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
    </div>
    <div class="container" id="mainContent">
            <div id="topOfPage"></div>
            <h1>Add New Instructor</h1>
            <br>
            <div class="well well-plain">
                <form method="post" action="<%=Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD%>" name="form_addinstructoraccount">
                    <div>
                        <label class="label-control">Short Name:</label>
                       <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_SHORT_NAME%>" value="<%=sanitizeForHtml(data.instructorShortName)%>">
                    </div><br />
                    <div>
                        <label class="label-control">Name:</label>
                        <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_NAME%>" value="<%=sanitizeForHtml(data.instructorName)%>">
                    </div><br />
                    <div>
                        <label class="label-control">Email: </label>
                        <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_EMAIL%>" value="<%=sanitizeForHtml(data.instructorEmail)%>">
                    </div><br />
                    <div>
                        <label class="label-control">Institution: </label>
                        <input class="form-control" type="text" name="<%=Const.ParamsNames.INSTRUCTOR_INSTITUTION%>" value="<%=sanitizeForHtml(data.instructorInstitution)%>">
                    </div><br />
                    
                    <div>
                        <input id="btnAddInstructor" class="btn btn-primary" type="submit" value="Add Instructor">
                    </div>
                </form>
            </div>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            <br>
            <br>
        </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>