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
<%@ page import="java.util.Date"%>

<%
    AdminSessionsPageData data = (AdminSessionsPageData) request.getAttribute("data");
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>TEAMMATES - Administrator Sessions</title>
    <link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
    <link href="/stylesheets/teammatesCommon.css" rel="stylesheet">
    <link rel="stylesheet" href="/stylesheets/datepicker.css" type="text/css" media="screen">
    
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
                      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
                      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
                      <![endif]-->
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script type="text/javascript" src="/js/administrator.js"></script>
    <script type="text/javascript" src="/js/date.js"></script>
    <script type="text/javascript" src="/js/datepicker.js"></script>
    <script type="text/javascript"
        src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/js/adminSessions.js"></script>
    <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>

    <jsp:include page="../enableJS.jsp"></jsp:include>

</head>


<body>


    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />
    <div class="container theme-showcase" id="frameBodyWrapper"
        role="main">

        <div id="topOfPage"></div>

        <div id="headerOperation" class="page-header">


            <h1 id="headTitle">
                Ongoing Sessions<small> Total: <%=data.totalOngoingSessions%>
                    <br> <%=TimeHelper.formatTime(data.rangeStart)%>&nbsp;&nbsp;<span
                    class="glyphicon glyphicon-resize-horizontal"></span>&nbsp;&nbsp;<%=TimeHelper.formatTime(data.rangeEnd)%>
                    &nbsp;<%=data.getTimeZoneAsString()%>
                </small><br> <a href="#" class="btn btn-info"
                    onclick="openAllSections(<%=data.tableCount%>)">
                    Open All</a> <a href="#" class="btn btn-warning"
                    onclick="closeAllSections(<%=data.tableCount%>)">
                    Collapse All</a>
            </h1>


        </div>

        <div class="form-group">
            <a href="#" class="btn btn-link center-block"
                onclick="toggleFilter()"><span id="referenceText">
                    Show filter</span><br> <span
                class="glyphicon glyphicon-chevron-down"
                id="detailButton"></span> </a>

        </div>

        <form method="get"
            action="<%=Const.ActionURIs.ADMIN_SESSIONS_PAGE%>">

            <div class="panel panel-primary" id="timeFramePanel">
                <div class="panel-body">
                    <div class="row">
                        <div class="col-md-4"
                            title="<%=Const.Tooltips.FEEDBACK_SESSION_STARTDATE%>"
                            data-toggle="tooltip" data-placement="top">
                            <div class="row">
                                <div class="col-md-6">
                                    <label
                                        for="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                        class="label-control">
                                        From</label>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <input class="form-control col-sm-2"
                                        type="text"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTDATE%>"
                                        value=<%=TimeHelper.formatDate(data.rangeStart) %>
                                        placeholder="Date">
                                </div>
                                <div class="col-md-3">
                                    <select class="form-control"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTHOUR%>">
                                        <%
                                            for (String opt : data.getHourOptionsAsHtml(data.rangeStart))
                                                out.println(opt);
                                        %>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <select class="form-control"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_STARTMINUTE%>">
                                        <%
                                            for (String opt : data.getMinuteOptionsAsHtml(data.rangeStart))
                                                out.println(opt);
                                        %>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 border-left-gray"
                            title="<%=Const.Tooltips.FEEDBACK_SESSION_ENDDATE%>"
                            data-toggle="tooltip" data-placement="top">
                            <div class="row">
                                <div class="col-md-6">
                                    <label
                                        for="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                        class="label-control">To</label>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6">
                                    <input class="form-control col-sm-2"
                                        type="text"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDDATE%>"
                                        value="<%=TimeHelper.formatDate(data.rangeEnd)%>"
                                        placeHolder="Date">
                                </div>
                                <div class="col-md-3">
                                    <select class="form-control"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDHOUR%>">
                                        <%
                                            for (String opt : data.getHourOptionsAsHtml(data.rangeEnd))
                                                out.println(opt);
                                        %>
                                    </select>
                                </div>

                                <div class="col-md-3">
                                    <select class="form-control"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_ENDMINUTE%>">
                                        <%
                                            for (String opt : data.getMinuteOptionsAsHtml(data.rangeEnd))
                                                out.println(opt);
                                        %>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-4 border-left-gray">
                            <div class="row">
                                <div class="col-md-12">
                                    <label
                                        for="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                                        class="control-label">
                                        Time Zone</label>
                                </div>
                            </div>
                            <div class="row">


                                <div class="col-sm-6">
                                    <select class="form-control"
                                        name="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>"
                                        id="<%=Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE%>">
                                        <%
                                            for (String opt : data.getTimeZoneOptionsAsHtml())
                                                out.println(opt);
                                        %>
                                    </select>
                                </div>

                                <div class="col-sm-6">
                                    <button type="submit"
                                        class="btn btn-primary btn-block">Filter
                                        by Range</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

            </div>

        </form>







        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />

        <%
            int tableIndex = 1;

            for (String key : data.map.keySet()) {

                int index = 1;

                if (key.contentEquals("Unknown")) {
                    continue;
                }
                
                if (!data.isShowAll && key.contains("TEAMMATES Test Institute")){
                	continue;   
                }
        %>

        <div class="panel panel-primary">
            <ul class="nav nav-pills nav-stacked">
                <li id="pill_<%=tableIndex%>" class="active"><a
                    href="#"
                    onclick="toggleContent(<%=tableIndex%>); return false;"><span
                        class="badge pull-right"
                        id="badge_<%=tableIndex%>" style="display: none"><%=data.map.get(key).size()%></span><Strong><%=key%>
                    </Strong></a></li>
            </ul>


            <div class="table-responsive" id="table_<%=tableIndex%>">
                <table class="table table-striped dataTable">
                    <thead>
                        <tr>
                            <th>Status
                           </th>
                            <th onclick="toggleSort(this,2)"
                            	class="button-sort-non">[Course ID]Session Name &nbsp; <span
                                class="icon-sort unsorted"></span>
                            </th>
                            <th> Response Rate 
                            </th>
                            <th onclick="toggleSort(this,4,sortDate)"
                                class="button-sort-non">Start Time&nbsp;
                                <span class="icon-sort unsorted"></span>
                            </th>
                            <th onclick="toggleSort(this,5,sortDate)"
                                class="button-sort-non">End Time&nbsp; <span
                                class="icon-sort unsorted"></span></th>
                            <th onclick="toggleSort(this,6)"
                                class="button-sort-non">Creator</th>
                        </tr>
                    </thead>

                    <tbody>

                        <%
                            List<FeedbackSessionAttributes> curList = data.map.get(key);

                                for (FeedbackSessionAttributes fs : curList) {
                                    
                                    if(!data.isShowAll && fs.creatorEmail.endsWith(".tmt")){
                                    	   continue;   
                                    }
                        %>

                        <tr>
                             <td><%=data.getSessionStatusForShow(fs)%>  
                            </td>
                            <td><strong>[<%=fs.courseId%>]</strong>&nbsp;<%=fs.feedbackSessionName%></td>
                            
                            <%
                            String googleId = data.sessionToInstructorIdMap.get(fs.getIdentificationString());
                            if(!googleId.isEmpty()){               
                            %>
                            <td class="session-response-for-test">
                               <a oncontextmenu="return false;" href="<%=data.getFeedbackSessionStatsLink(fs.courseId, fs.feedbackSessionName, googleId)%>">Show</a>
                            </td>
                            
                            <%    
                            } else {
                            %>
                             <td class="session-response-for-test">
                                <p>Not Available</p>
                            </td>
                            
                            <%
                            }
                            %>
                            <td><%=TimeHelper.formatTime(fs.getSessionStartTime())%></td>
                            <td><%=TimeHelper.formatTime(fs.getSessionEndTime())%></td>
                            <td><a target="blank"
                                <%=data
                            .getInstructorHomePageViewLink(fs.creatorEmail)%>><%=fs.creatorEmail%></a></td>
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


            <ul class="nav nav-pills nav-stacked">
                <li id="pill_<%=tableIndex%>" class="active"><a
                    href="#"
                    onclick="toggleContent(<%=tableIndex%>); return false;"><span
                        class="badge pull-right"
                        id="badge_<%=tableIndex%> style="display:none"><%=data.map.get(key).size()%></span>
                        <Strong><%=key%> </Strong></a></li>
            </ul>

            <div class="table-responsive" id="table_<%=tableIndex%>">
                <table class="table table-striped dataTable">
                    <thead>
                        <tr>
                           <th>Status
                           </th>
                           <th onclick="toggleSort(this,2)"
                                class="button-sort-non">Session Name &nbsp; <span
                                class="icon-sort unsorted"></span>
                            </th>
                             <th> Response Rate 
                            </th>
                            <th onclick="toggleSort(this,4,sortDate)"
                                class="button-sort-non">Start Time&nbsp;
                                <span class="icon-sort unsorted"></span>
                            </th>
                            <th onclick="toggleSort(this,5,sortDate)"
                                class="button-sort-non">End Time&nbsp; <span
                                class="icon-sort unsorted"></span></th>
                            <th onclick="toggleSort(this,6)"
                                class="button-sort-non">Creator</th>
                        </tr>
                    </thead>

                    <tbody>

                        <%
                            List<FeedbackSessionAttributes> curList = data.map.get(key);

                                for (FeedbackSessionAttributes fs : curList) {
                                    
                                	if(!data.isShowAll && fs.creatorEmail.endsWith(".tmt")){
                                        continue;   
                                    }
                        %>

                        <tr>
                            <td><%=data.getSessionStatusForShow(fs)%> 
                            </td>
                            <td><%=fs.feedbackSessionName%></td>
                             <%
                            String googleId = data.sessionToInstructorIdMap.get(fs.getIdentificationString());
                            if(!googleId.isEmpty()){               
                            %>
                            <td class="session-response-for-test">
                               <a oncontextmenu="return false;" href="<%=data.getFeedbackSessionStatsLink(fs.courseId, fs.feedbackSessionName, googleId)%>">Show</a>
                            </td>
                            
                            <%    
                            } else {
                            %>
                             <td class="session-response-for-test">
                                <p>Not Available</p>
                            </td>
                            
                            <%
                            }
                            %>
                            <td><%=TimeHelper.formatTime(fs.getSessionStartTime())%></td>
                            <td><%=TimeHelper.formatTime(fs.getSessionEndTime())%></td>
                            <td><a
                                <%=data
                            .getInstructorHomePageViewLink(fs.creatorEmail)%>><%=fs.creatorEmail%></a></td>
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


        <a href="#" class="back-to-top-left"><span
            class="glyphicon glyphicon-arrow-up"></span>&nbsp;Top</a> <a
            href="#" class="back-to-top-right">Top&nbsp;<span
            class="glyphicon glyphicon-arrow-up"></span></a>




    </div>




    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />



</body>






</html>