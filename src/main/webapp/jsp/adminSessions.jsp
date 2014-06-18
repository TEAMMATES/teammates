<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="teammates.ui.controller.AdminSessionsPageData"%>
<%@ page
    import="teammates.common.datatransfer.FeedbackSessionAttributes"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="teammates.common.util.TimeHelper"%>


<%
	AdminSessionsPageData data = (AdminSessionsPageData) request
			.getAttribute("data");
%><!DOCTYPE html>
<html lang="en">
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEAMMATES - Administrator Sessions</title>
<link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/stylesheets/teammatesCommon.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
          <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
          <![endif]-->
<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/administrator.js"></script>
<script type="text/javascript"
    src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/adminSessions.js"></script>

<jsp:include page="../enableJS.jsp"></jsp:include>

</head>


<body>


    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
    <div class="container theme-showcase" id="frameBodyWrapper"
        role="main">

        <div id="topOfPage"></div>

        <div id="headerOperation" class="page-header">
            <h1 id="headTitle">
                Ongoing Sessions<small> Total: <%=data.totalOngoingSessions%></small>
            </h1>
            <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        </div>

        <%
        	int tableIndex = 1;

        	for (String key : data.map.keySet()) {

        		int index = 1;

        		if (key.contentEquals("Unknown")) {
        			continue;
        		}
        %>

        <div class="panel panel-primary">
            <ul class="nav nav-pills nav-stacked"
                onclick="toggleContent(<%=tableIndex%>); return false;">
                <li id="pill_<%=tableIndex%>"><a href="#"><span
                        class="badge pull-right"><%=data.map.get(key).size()%></span><Strong><%=key%>
                    </Strong></a></li>
            </ul>


            <div class="table-responsive" id="table_<%=tableIndex%>">
                <table class="table table-striped dataTable">
                    <thead>
                        <tr>
                           <!--  <th>No</th> -->
                            <th onclick="sessionToggleSort(this,2)"
                                class="non-sorted">Session Name</th>
                            <th onclick="sessionToggleSort(this,3)"
                                class="non-sorted">Start Time</th>
                            <th onclick="sessionToggleSort(this,4)"
                                class="non-sorted">End Time</th>
                            <th onclick="sessionToggleSort(this,5)"
                                class="non-sorted">Creator</th>
                            <!-- <th width="20%" onclick="toggleSort(this,3)"
                                class="button-sort-ascending">
                                Institute <span
                                class="icon-sort unsorted"
                                id="button_sort_institute"></span>
                            </th>
                            <th width="30%"
                                onclick="toggleSort(this,4);"
                                class="button-sort-ascending">Create
                                At <span class="icon-sort unsorted"
                                id="button_sort_createat"></span>
                            </th> -->
                            <!-- <th width="5%">Options</th> -->
                        </tr>
                    </thead>

                    <tbody>

                        <%
                        	List<FeedbackSessionAttributes> curList = data.map.get(key);

                        		for (FeedbackSessionAttributes fs : curList) {
                        %>

                        <tr>
                           <%--  <td><%=index%></td> --%>
                            <td><%=fs.feedbackSessionName%></td>
                            <td><%=TimeHelper.formatTime(fs.getSessionStartTime())%></td>
                            <td><%=TimeHelper.formatTime(fs.getSessionEndTime())%></td>
                            <td><a
                                href="<%=data
							.getInstructorHomePageViewLink(fs.creatorEmail)%>"
                                target="blank"><%=fs.creatorEmail%></a></td>
                        </tr>

                        <%
                        	index++;
                        		}

                        		tableIndex++;
                        %>
                    </tbody>

                </table>
            </div>


        </div>

        <%
        	}

        	if (data.hasUnknown) {

        		String key = "Unknown";
        		int index = 1;
        %>


        <div class="panel panel-primary">


            <ul class="nav nav-pills nav-stacked"
                onclick="toggleContent(<%=tableIndex%>); return false;">
                <li id="pill_<%=tableIndex%>"><a href="#"><span
                        class="badge pull-right"><%=data.map.get(key).size()%></span><Strong><%=key%>
                    </Strong></a></li>
            </ul>

            <div class="table-responsive" id="table_<%=tableIndex%>">
                <table class="table table-striped dataTable">
                    <thead>
                        <tr>
                            <!-- <th>No</th> -->
                            <th>Session Name</th>
                            <th>Start Time</th>
                            <th>End Time</th>
                            <th>Creator</th>
                            <!-- <th width="20%" onclick="toggleSort(this,3)"
                                class="button-sort-ascending">
                                Institute <span
                                class="icon-sort unsorted"
                                id="button_sort_institute"></span>
                            </th>
                            <th width="30%"
                                onclick="toggleSort(this,4);"
                                class="button-sort-ascending">Create
                                At <span class="icon-sort unsorted"
                                id="button_sort_createat"></span>
                            </th> -->
                            <!-- <th width="5%">Options</th> -->
                        </tr>
                    </thead>

                    <tbody>

                        <%
                        	List<FeedbackSessionAttributes> curList = data.map.get(key);

                        		for (FeedbackSessionAttributes fs : curList) {
                        %>

                        <tr>
                            <%-- <td><%=index%></td> --%>
                            <td><%=fs.feedbackSessionName%></td>
                            <td><%=TimeHelper.formatTime(fs.getSessionStartTime())%></td>
                            <td><%=TimeHelper.formatTime(fs.getSessionEndTime())%></td>
                            <td><a
                                href="<%=data
							.getInstructorHomePageViewLink(fs.creatorEmail)%>"
                                target="blank"><%=fs.creatorEmail%></a></td>
                        </tr>

                        <%
                        	index++;
                        		}
                        %>
                    </tbody>

                </table>
            </div>


        </div>



        <%
        	}
        %>




        <div id="bottomButton">

            <a href="#" class="btn btn-info btn-xs" ><span
                class="glyphicon glyphicon-arrow-up"></span> Back to Top</a>
        </div>

    </div>




    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />



</body>






</html>