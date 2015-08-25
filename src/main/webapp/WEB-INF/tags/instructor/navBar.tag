<%@ tag description="Instructor Navigation Bar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
                <li<c:if test="${fn:contains(data.class,'Home')}"> class="active"</c:if>>
                    <a class='nav home' data-link="instructorHome" href="${data.instructorHomeLink}">Home</a>
                </li>
                <li<c:if test="${fn:contains(data.class,'InstructorCourse') && !fn:contains(data.class, 'CourseStudent')}"> class="active"</c:if>>
                    <a class='nav courses' data-link="instructorCourse" href="${data.instructorCoursesLink}">Courses</a>
                </li>
                <li<c:if test="${fn:contains(data.class,'Feedback')}"> class="active"</c:if>>
                    <a class='nav evaluations' data-link="instructorEval" href="${data.instructorFeedbacksLink}">Sessions</a>
                </li>
                <li<c:if test="${fn:contains(data.class,'Student')}"> class="active"</c:if>>
                    <a class='nav students' data-link="instructorStudent" href="${data.instructorStudentListLink}">Students</a>
                </li>
                <li<c:if test="${fn:contains(data.class,'Comment')}"> class="active"</c:if>>
                    <a class='nav comments' data-link="instructorComments" href="${data.instructorCommentsLink}">Comments</a>
                </li>
                <li<c:if test="${fn:contains(data.class,'Search')}"> class="active"</c:if>>
                    <a class='nav search' data-link="instructorSearch" href="${data.instructorSearchLink}">
                        Search
                    </a>
                </li>
                <li>
                    <a class='nav help' href="/instructorHelp.html" target="_blank">Help</a>
                </li>
            </ul>
            <ul class="nav navbar-nav pull-right">
                <li><a class='nav logout' href="/logout.jsp">Logout
                        
                        (<span class="text-info" data-toggle="tooltip" data-placement="bottom" 
                                title="${data.account.googleId}">
                                ${data.account.truncatedGoogleId}
                        </span>)
                    </a>
                </li>
            </ul>            
        </div>
    </div>
</div>