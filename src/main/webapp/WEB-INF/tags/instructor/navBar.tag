<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Instructor Navigation Bar" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
  <div class="container">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#contentLinks">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <t:teammatesLogo/>
    </div>
    <div class="collapse navbar-collapse" id="contentLinks">
      <ul class="nav navbar-nav">
        <li<c:if test="${fn:contains(data.getClass(), 'Home')}"> class="active"</c:if>>
          <a class='nav home' data-link="instructorHome" href="${data.instructorHomeLink}">Home</a>
        </li>
        <li<c:if test="${fn:contains(data.getClass(), 'InstructorCourse') && !fn:contains(data.getClass(), 'CourseStudent')}"> class="active"</c:if>>
          <a class='nav courses' data-link="instructorCourse" href="${data.instructorCoursesLink}">Courses</a>
        </li>
        <li<c:if test="${fn:contains(data.getClass(), 'Feedback')}"> class="active"</c:if>>
          <a class='nav evaluations' data-link="instructorEval" href="${data.instructorFeedbackSessionsLink}">Sessions</a>
        </li>
        <li<c:if test="${fn:contains(data.getClass(), 'Student')}"> class="active"</c:if>>
          <a class='nav students' data-link="instructorStudent" href="${data.instructorStudentListLink}">Students</a>
        </li>
        <li<c:if test="${fn:contains(data.getClass(), 'Search')}"> class="active"</c:if>>
          <a class='nav search' data-link="instructorSearch" href="${data.instructorSearchLink}">
            Search
          </a>
        </li>
        <li>
          <a class="nav help" href="/instructorHelp.jsp" target="_blank" rel="noopener noreferrer">Help</a>
        </li>
      </ul>
      <ul class="nav navbar-nav pull-right">
        <li>
          <a id="btnLogout" class="nav logout" href="<%= Const.ActionURIs.LOGOUT %>">Logout

            (<span class="text-info" data-toggle="tooltip" title="${data.account.googleId}" data-placement="bottom">
              ${data.account.truncatedGoogleId}
            </span>)
          </a>
        </li>
      </ul>
    </div>
  </div>
</div>
