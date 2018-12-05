<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Student/Instructor feedback submission edit page" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.FrontEndLibrary" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="tsfse" %>
<%@ attribute name="isInstructor" required="true" %>
<%@ attribute name="moderatedPersonEmail" required="true" %>
<%@ attribute name="moderatedPersonName" required="true" %>

<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
  <script type="text/javascript" src="/js/feedbackSubmissionsEdit.js"></script>
</c:set>

<c:if test="${data.headerHidden}">
  <c:set var="altHeader">
    <nav class="navbar navbar-default navbar-fixed-top">
      <c:choose>
        <c:when test="${data.preview}">
          <h3 class="text-center">Previewing Session as ${isInstructor ? "Instructor" : "Student"}${" "}${moderatedPersonName} (${moderatedPersonEmail})</h3>
        </c:when>
        <c:when test="${data.moderation}">
          <div class="container">
            <div class="col-md-12">
              <h3 class="text-center">
                You are moderating responses for ${isInstructor ? "instructor" : "student"}${" "}${moderatedPersonName} (${moderatedPersonEmail})
                <small>
                  <a href="javascript:;" id="moderationHintButton"></a>
                </small>
              </h3>
              <ul id="moderationHint" class="hidden">
                <li>
                  The page below resembles the submission page as seen by the respondent ${moderatedPersonName} (${moderatedPersonEmail}).
                  You can use it to moderate responses submitted by the respondent or submit responses on behalf of the respondent.
                </li>
                <li>
                  Note that due to visibility settings, questions that are not supposed to show responses to instructors (i.e you) are not shown in the page below.
                </li>
              </ul>
            </div>
          </div>
        </c:when>
      </c:choose>
    </nav>
  </c:set>
</c:if>

<c:choose>
  <c:when test="${isInstructor}">
    <ti:instructorPage title="Submit Feedback" jsIncludes="${jsIncludes}" altNavBar="${altHeader}">
      <tsfse:feedbackSubmissionForm moderatedPersonEmail="${moderatedPersonEmail}"/>
    </ti:instructorPage>
  </c:when>
  <c:otherwise>
    <ts:studentPage title="Submit Feedback" jsIncludes="${jsIncludes}" altNavBar="${altHeader}">
      <c:if test="${not data.headerHidden}">
        <ts:studentMessageOfTheDay />
      </c:if>
      <c:if test="${empty data.account.googleId}">
        <div id="registerMessage" class="alert alert-info">
          ${data.registerMessage}
        </div>
      </c:if>
      <tsfse:feedbackSubmissionForm moderatedPersonEmail="${moderatedPersonEmail}"/>
    </ts:studentPage>
  </c:otherwise>
</c:choose>
