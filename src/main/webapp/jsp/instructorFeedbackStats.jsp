<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.FeedbackSessionDetailsBundle" %>
<%@ page import="teammates.ui.controller.FeedbackSessionStatsPageData" %>
<%
    FeedbackSessionStatsPageData data = (FeedbackSessionStatsPageData)request.getAttribute("data");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>TEAMMATES-Feedback Stats</title>

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
    <div id="frameTop">
        <jsp:include page="<%= Const.ViewURIs.INSTRUCTOR_HEADER %>" />
    </div>
    
    <div class="container" id="mainContent">
            <div id="topOfPage"></div>
            <h1 class="align-center"><%= data.sessionDetails.feedbackSession.feedbackSessionName %></h1>
            <br>
            <p class="align-center">Expected : <%= data.sessionDetails.stats.expectedTotal %></p>
            <br />
            
            <p class="align-center">Submitted : <%= data.sessionDetails.stats.submittedTotal %></p>
            <br />
    </div>
</body>
</html>