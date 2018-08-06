<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/instructorHelp.js"></script>
</c:set>
<t:helpPage jsIncludes="${jsIncludes}">
  <h1>Help for Instructors</h1>
  <a name="#top"></a>
  <div class="contentHolder">
    <p>
      Have questions about how to use TEAMMATES? This page answers some frequently asked questions.<br>
      If you are new to TEAMMATES, our <a href="/gettingStarted.jsp">Getting Started</a> guide will introduce you to the basic functions of TEAMMATES.<br>
      If you have any remaining questions, don't hesitate to <a href="mailto:teammates@comp.nus.edu.sg">email us</a>. We respond within 24 hours.
    </p>
    <div id="topics" style="display: block;">
      <p>
        Browse questions by topic:
      </p>
      <ul>
        <li>
          <a href="#students">Students</a>
        </li>
        <li>
          <a href="#courses">Courses</a>
        </li>
        <li>
          <a href="#sessions">Sessions</a>
        </li>
        <li>
          <a href="#questions">Questions</a>
        </li>
      </ul>
    </div>
    <div class="row padding-top-55px">
      <div class="col-sm-6 col-sm-offset-3">
        <div class="input-group input-group-lg">
            <input type="text" class="form-control" placeholder="How can we help?" id="searchQuery" autocomplete="off">
            <span class="input-group-btn">
              <button id="search" class="btn btn-default" type="submit">
                <span class="glyphicon glyphicon-search"></span>
              </button>
              <button id="clear" class="btn btn-default" type="submit"><span class="glyphicon glyphicon-remove"></span></button>
            </span>
        </div>
      </div>
    </div>
  </div>
  <div class="row margin-top-15px">
    <div class="col-sm-12">
      <h3 id="searchMetaData" class="text-color-primary"></h3>
      <hr>
    </div>
  </div>
  <div id="searchResults" class="panel-group margin-top-15px"></div>
  <div id="pagingDivider" class="separate-content-holder" style="display: none;">
    <hr>
  </div>
  <div class="row">
    <div class="col-sm-12 align-center">
      <div class="btn-group btn-group-sm" id="pagingControls"></div>
    </div>
  </div>
  <div id="allQuestions" style="display: block;">
    <jsp:include page="partials/instructorHelpStudents.jsp"/>
    <jsp:include page="partials/instructorHelpCourses.jsp"/>
    <jsp:include page="partials/instructorHelpSessions.jsp"/>
    <jsp:include page="partials/instructorHelpQuestions.jsp"/>
  </div>
</t:helpPage>
