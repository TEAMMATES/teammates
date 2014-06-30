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
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>TEAMMATES - Administrator</title>
    <!-- Bootstrap core CSS -->
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap theme -->
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css" media="screen">
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/administrator.js"></script>
    <jsp:include page="../enableJS.jsp"></jsp:include>
    <!-- Bootstrap core JavaScript ================================================== -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body>
    <div id="frameTop">
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
    </div>
    <div id="frameBody">
        <div id="frameBodyWrapper" class="container">
            <div id="topOfPage"></div>
            <div id="headerOperation" class="page-header">
            <h1>Add New Instructor</h1>
            </div>
            <br />
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
    </div>

    <div id="frameBottom">
        <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
    </div>
</body>
</html>