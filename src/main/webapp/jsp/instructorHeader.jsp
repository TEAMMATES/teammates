<%--
    - @(#)
    - Description: This jsp file defines the black bar at the top of all 
    -                 instructor pages
 --%>
 
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.ui.controller.PageData" %>
<%
    PageData data = (PageData)request.getAttribute("data");
%>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#contentLinks">
                 <span class="sr-only">Toggle navigation</span>
                 <span class="icon-bar"></span>
                 <span class="icon-bar"></span>
                 <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/index.html">TEAMMATES</a>
        </div>
        <div class="collapse navbar-collapse" id="contentLinks">
            <ul class="nav navbar-nav">
                <li class="<%=data.getClass().toString().contains("Home")?"active":""%>">
                    <a class='nav home' data-link="instructorHome" href="<%=data.getInstructorHomeLink()%>">Home</a>
                </li>
                <li class="<%=data.getClass().toString().contains("InstructorCourse") && !data.getClass().toString().contains("CourseStudent")?"active":""%>">
                    <a class='nav courses' data-link="instructorCourse" href="<%=data.getInstructorCoursesLink()%>">Courses</a>
                </li>
                <li class="<%=(data.getClass().toString().contains("Eval") || data.getClass().toString().contains("Feedback"))?"active":""%>">
                    <a class='nav evaluations' data-link="instructorEval" href="<%=data.getInstructorFeedbacksLink()%>">Sessions</a>
                </li>
                <li class="<%=data.getClass().toString().contains("Student")?"active":""%>">
                    <a class='nav students' data-link="instructorStudent" href="<%=data.getInstructorStudentListLink()%>">Students</a>
                </li>
                <li class="<%=data.getClass().toString().contains("Comment")?"active":""%>">
                    <a class='nav comments' data-link="instructorComments" href="<%=data.getInstructorCommentsLink()%>">Comments</a>
                </li>
                <li class="<%=data.getClass().toString().contains("Search")?"active":""%>">
                    <a class='nav search' data-link="instructorSearch" href="<%=data.getInstructorSearchLink()%>">
                        Search
                    </a>
                </li>
                <li>
                    <a class='nav help' href="/instructorHelp.html" target="_blank">Help</a>
                </li>
            </ul>
            <ul class="nav navbar-nav pull-right">
                <li><a class='nav logout' href="<%=Const.ViewURIs.LOGOUT%>">Logout
                        
                        (<span class="text-info" data-toggle="tooltip" data-placement="bottom" 
                                title="<%=data.account.googleId%>">
                                <%=PageData.truncate(data.account.googleId,Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH)%>
                        </span>)
                    </a>
                </li>
            </ul>            
        </div>
    </div>
</div>