<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.util.ActivityLogEntry"%>
<%@ page import="teammates.ui.controller.AdminActivityLogPageData"%>

<%
    AdminActivityLogPageData data = (AdminActivityLogPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>

<head>
    <link rel="shortcut icon" href="/favicon.png" />
    <meta http-equiv="X-UA-Compatible" content="IE=8" />
    <title>TEAMMATES - Administrator</title>
    
    
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
    <script type="text/javascript" src="/js/adminActivityLog.js"></script>
    <script type="text/javascript"
        src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
    <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
</head>

<body>
    <div id="dhtmltooltip"></div>

    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />

    <div class="container theme-showcase" role="main">
        <div id="frameBody">
            <div id="frameBodyWrapper">
                <div id="topOfPage"></div>
                <div id="headerOperation" class="page-header">
                    <h1>Admin Activity Log</h1>
                </div>

                <div class="well well-plain">
                    <form class="form-horizontal" method="post"
                        action="" id="activityLogFilter" role="form">

                        <div class="panel-heading" id="filterForm">

                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="input-group">
                                            <span
                                                class="input-group-btn">
                                                <button
                                                    class="btn btn-default"
                                                    type="submit"
                                                    name="search_submit">Filter</button>
                                            </span> <input type="text"
                                                class="form-control"
                                                id="filterQuery"
                                                name="filterQuery"
                                                value="<%=data.filterQuery%>">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <a href="#"
                                    class="btn btn-link center-block"
                                    onclick="toggleReference()"><span
                                    id="referenceText"> Show
                                        Reference</span><br> <span
                                    class="glyphicon glyphicon-chevron-down"
                                    id="detailButton"></span> </a>

                            </div>

                            <div id="filterReference">
                                <div class="form-group">

                                    <div class="col-md-12">
                                        <div class="alert alert-success">
                                            <p class="text-center">
                                                <span
                                                    class="glyphicon glyphicon-filter"></span>
                                                A query is formed by a
                                                list of filters. Each
                                                filter is in the format
                                                <strong>&nbsp;[filter
                                                    label]: [value1,
                                                    value2, value3....]</strong><br>
                                            </p>
                                        </div>

                                        <p class="text-center">
                                            <span
                                                class="glyphicon glyphicon-hand-right"></span>
                                            Combine filters with the <span
                                                class="label label-warning">
                                                AND</span> keyword or the <span
                                                class="label label-warning">|</span>
                                            separator.

                                        </p>
                                    </div>

                                </div>
                                <small>
                                    <div class="form-group">
                                        <div class="col-md-12">

                                            <div
                                                class="form-control-static">
                                                <strong>Sample
                                                    Queries:</strong> <br>
                                                <ul>
                                                    <li>E.g. role:
                                                        Instructor AND
                                                        request:
                                                        InstructorCourse,
                                                        InstructorEval
                                                        AND from:
                                                        15/03/13</li>
                                                    <li>E.g. from:
                                                        13/3/13 AND to:
                                                        17/3/13 AND
                                                        person:
                                                        teammates.test
                                                        AND response:
                                                        Pageload, System
                                                        Error Report,
                                                        Servlet Action
                                                        Failure</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">

                                            <div
                                                class="form-control-static">

                                                <strong>
                                                    Possible Labels:</strong>&nbsp;from,
                                                to, person, role,
                                                request, response<br>
                                                <ul>

                                                    <li>E.g. from:
                                                        13/03/13</li>


                                                    <li>E.g. to:
                                                        13/03/13</li>
                                                    <li>E.g.
                                                        person:
                                                        teammates.coord</li>
                                                    <li>E.g. role:
                                                        Instructor,
                                                        Student</li>
                                                    <li>E.g.
                                                        request:
                                                        InstructorEval,
                                                        StudentHome,
                                                        evaluationclosingreminders
                                                    </li>
                                                    <li>E.g.
                                                        response:
                                                        Pageload, System
                                                        Error Report,
                                                        Delete Course</li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <p
                                                class="form-control-static">
                                                <strong>
                                                    Possible Roles: </strong>
                                                Instructor, Student,
                                                Unknown

                                            </p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <p
                                                class="form-control-static">
                                                <strong>
                                                    Possible Servlets
                                                    Requests: </strong> <br>
                                                <br>
                                            <div
                                                class="table-responsive">
                                                <table
                                                    class="table table-condensed">
                                                    <%=data.getActionListAsHtml()%>
                                                </table>
                                            </div>
                                            </p>
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <p
                                                class="form-control-static">
                                                <strong>
                                                    Possible Responses:
                                                </strong> <br> <br> <div
                                                class="table-responsive">


                                                <table
                                                    class="table table-condensed">

                                                    <tr>
                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Remind
                                                                    Students
                                                                    About
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Send
                                                                    Evaluation
                                                                    Closing
                                                                    reminders</li>
                                                                <li
                                                                    class="list-group-item">Send
                                                                    Evaluation
                                                                    Opening
                                                                    reminders</li>


                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Publish
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Unpublish
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Send
                                                                    Registration</li>


                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Pageload</li>
                                                                <li
                                                                    class="list-group-item">System
                                                                    Error
                                                                    Report</li>
                                                                <li
                                                                    class="list-group-item">Servlet
                                                                    Action
                                                                    Failure</li>


                                                            </ul>
                                                        </td>

                                                    </tr>

                                                    <tr>
                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Add
                                                                    New
                                                                    Course</li>
                                                                <li
                                                                    class="list-group-item">Delete
                                                                    Course</li>
                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Course
                                                                    Info</li>
                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Enroll
                                                                    Students</li>
                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Student
                                                                    Details</li>
                                                                <li
                                                                    class="list-group-item">Delete
                                                                    Student</li>

                                                                <li
                                                                    class="list-group-item">Student
                                                                    Joining
                                                                    Course</li>
                                                            </ul>
                                                        </td>

                                                        <td>
                                                            <ul
                                                                class="list-group">
                                                                <li
                                                                    class="list-group-item">Create
                                                                    New
                                                                    Evaluation</li>
                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Evaluation
                                                                    Info</li>
                                                                <li
                                                                    class="list-group-item">Delete
                                                                    Evaluation</li>

                                                                <li
                                                                    class="list-group-item">Edit
                                                                    Submission</li>


                                                            </ul>
                                                        </td>
                                                
                                                </table>
                                            </div>

                                            </p>

                                        </div>

                                    </div>
                                </small>
                            </div>

                        </div>

                        <input type="hidden" name="offset"
                            value="<%=data.offset%>"> <input
                                                    type="hidden"
                                                    name="pageChange"
                                                    value="false"></form>


            
                                                </div>

                <%
                    if (data.queryMessage != null) {
                %>
                <div class="alert alert-danger" id="queryMessage">
                    <span class="glyphicon glyphicon-warning-sign"></span>
                    <%
                        out.println(" " + data.queryMessage);
                    %>
                </div>
                <%
                    }
                %>



               


            </div>


            <br> <br>


            <%
                List<ActivityLogEntry> appLogs = data.logs;
                if (appLogs != null) {
            %>
            <div class="panel panel-primary">
                <div class="panel-heading">
                    <strong>Activity Log <span>
                            Instructor </strong>
                </div>
                <div class="table-responsive">
                    <table class="table table-condensed"dataTable">

                        <thead>
                            <tr>
                                <th width="10%">Date</th>
                                <th>[Role][Action][Google
                                    ID][Name][Email]</th>
                            </tr>

                        </thead>
                        <tbody>
                            <%
                                if (appLogs.isEmpty()) {
                            %>
                            <tr>
                                <td colspan='2'><i>No
                                        application logs found</i></td>
                            </tr>
                            <%
                                } else {
                                        int index = 0;
                                        for (ActivityLogEntry log : appLogs) {
                            %>
                            <tr>

                                <td style="vertical-align: middle;"><%=log.getDateInfo()%></td>

                                <td>

                                    <form method="post"
                                        action="<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%>">


                                        <h4
                                            class="list-group-item-heading">
                                            <%=log.getIconRoleForShow()%>
                                            <%=log.getActionInfo()%>
                                            <small> <span
                                                id="personInfo_<%=index%>"><%=log.getPersonInfo()%></span>

                                                <button
                                                    id="actionButton_<%=index%>"
                                                    type="submit"
                                                    class="btn <%=log.getLogEntryActionsButtonClass()%> btn-xs">
                                                    <span
                                                        class="glyphicon glyphicon-zoom-in"></span>

                                                </button> <input type="hidden"
                                                name="filterQuery"
                                                value="person:<%=log.getId()%>">

                                            </small>

                                        </h4>

                                        <div>
                                            <%=log.getMessageInfo()%>

                                        </div>




                                    </form>
                                </td>

                            </tr>
                            <%
                                index++;
                                        }
                                    }
                            %>

                        </tbody>
                    </table>
                </div>

            </div>
            <%
                }
            %>

            <jsp:include
                page="<%=Const.ViewURIs.STATUS_MESSAGE_WITHOUT_FOCUS%>" />
            <br>

            <div>
                <a href="#frameBodyWrapper" class="btn  btn-primary"><span
                    class="glyphicon glyphicon-arrow-up"></span> Back To
                    Top</a>
            </div>
            <br> <br>
        </div>
    </div>

    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>