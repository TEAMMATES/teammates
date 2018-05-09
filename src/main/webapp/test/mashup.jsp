<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ page import="teammates.common.util.StringHelper" %>
<%@ page import="teammates.logic.core.FeedbackQuestionsLogic" %>
<%@ page import="teammates.logic.api.Logic" %>
<%@ page import="teammates.common.datatransfer.attributes.StudentAttributes" %>

<!DOCTYPE html>
<html>
  <head>
    <meta content="width=device-width, initial-scale=1.0" name="viewport">

    <title>Webpage Compilation - TEAMMATES</title>

    <link rel="shortcut icon" href="/favicon.png">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" rel="stylesheet">
    <link type="text/css" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" rel="stylesheet">
    <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet">
    <style>
      .full-width {
        width: 100%;
      }

      .pageinfo {
        font-size: 14px;
        color: red;
      }

      .hr-bold {
        border: 2px red solid;
      }
    </style>
  </head>
  <body>
    <div class="container theme-showcase">
      <div class="row">
        <div id="dhtmltooltip"></div>
        <div class="alert alert-warning">
          <h4>Please remember to log in</h4>
        </div>
      </div>

      <div class="intro">
        <h1>Introduction</h1>
        <table class="table table-striped">
          <tr>
            <td>
              <p>
                This page lists all of the pages which make up the TEAMMATES app. It is used for manually
                inspecting the TEAMMATES UI for visual defects. To load this page, you will need to run the test
                <strong>MashupPageUiTest.java</strong>.
              </p>
            </td>
          </tr>
        </table>
      </div>
      <h1>Table Of Contents</h1>
      <table class="table table-striped">
        <tbody>
          <tr>
            <td>
              <h2>Instructor Pages</h2>
              <ul class="nav">
                <li>
                  <a href="#instructorHomePage">Instructor Home Page</a>
                </li>
                <li>
                  <a href="#instructorAddCoursePage">Instructor Add Course Page</a>
                </li>
                <li>
                  <a href="#instructorEditCoursePage">Instructor Edit Course Page</a>
                </li>
                <li>
                  <a href="#instructorEnrollPage">Instructor Enroll Students Page</a>
                </li>
                <li>
                  <a href="#instructorCourseDetailsPage">Instructor Course Details Page</a>
                </li>
                <li>
                  <a href="#instructorStudentListPage">Instructor Student List Page</a>
                </li>
                <li>
                  <a href="#instructorCourseStudentDetailsPage">Instructor Student Details Page</a>
                </li>
                <li>
                  <a href="#instructorCourseStudentEditPage">Instructor Student Edit Page</a>
                </li>
                <li>
                  <a href="#instructorStudentRecordsPage">Instructor Student Records Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackPage">Instructor Feedback Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackEditPage">Instructor Feedback Edit Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackPreviewAsStudentPage">Instructor Feedback Preview as Student Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackPreviewAsInstructorPage">Instructor Feedback Preview as Instructor Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackSubmitPage">Instructor Feedback Submit Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackQuestionSubmitPage">Instructor Feedback Question Submit Page</a>
                </li>
                <li>
                  <a href="#instructorFeedbackResultsPageByGiverRecipientQuestion">Instructor Feedback Results Page (By giver-recipient-question)</a>
                </li>
                <li>
                  <a href="#instructorFeedbackResultsPageByRecipientGiverQuestion">Instructor Feedback Results Page (By recipient-giver-question)</a>
                </li>
                <li>
                  <a href="#instructorFeedbackResultsPageByGiverQuestionRecipient">Instructor Feedback Results Page (By giver-question-recipient)</a>
                </li>
                <li>
                  <a href="#instructorFeedbackResultsPageByRecipientQuestionGiver">Instructor Feedback Results Page (By recipient-question-giver)</a>
                </li>
                <li>
                  <a href="#instructorFeedbackResultsPageByQuestion">Instructor Feedback Results Page (By question)</a>
                </li>
                <li>
                  <a href="#instructorSearchPage">Instructor Search Page</a>
                </li>
              </ul>
            </td>
            <td>
              <h2>Student Pages</h2>
              <ul class="nav">
                <li>
                  <a href="#studentHomePage">Student Home Page</a>
                </li>
                <li>
                  <a href="#studentProfilePage">Student Profile Page</a>
                </li>
                <li>
                  <a href="#studentCourseJoinConfirmationPage">Student Course Join Confirmation Page</a>
                </li>
                <li>
                  <a href="#studentCourseJoinConfirmationPageNew">Student Course Join Confirmation Page (New)</a>
                </li>
                <li>
                  <a href="#studentCourseDetailsPage">Student Course Details Page</a>
                </li>
                <li>
                  <a href="#studentFeedbackSubmitPage">Student Feedback Submit Page</a>
                </li>
                <li>
                  <a href="#studentFeedbackQuestionSubmitPage">Student Feedback Question Submit Page</a>
                </li>
                <li>
                  <a href="#studentFeedbackResultsPage">Student Feedback Results Page</a>
                </li>
              </ul>
            </td>
            <td>
              <h2>Admin Pages</h2>
              <ul class="nav">
                <li>
                  <a href="#adminHomePage">Admin Home Page</a>
                </li>
                <li>
                  <a href="#adminSearchPage">Admin Search Page</a>
                </li>
                <li>
                  <a href="#adminActivityLogPage">Admin Activity Log Page</a>
                </li>
              </ul>
            </td>
            <td>
              <h2>Static Pages</h2>
              <ul class="nav">
                <li>
                  <a href="#index">Home Page</a>
                </li>
                <li>
                  <a href="#features">Features Page</a>
                </li>
                <li>
                  <a href="#about">About Us Page</a>
                </li>
                <li>
                  <a href="#contact">Contact Page</a>
                </li>
                <li>
                  <a href="#terms">Terms Of Use Page</a>
                </li>
                <li>
                  <a href="#request">Request Account Page</a>
                </li>
                <li>
                  <a href="#usermap">Usermap Page</a>
                </li>
                <li>
                  <a href="#studentHelp">Student Help Page</a>
                </li>
                <li>
                  <a href="#instructorHelp">Instructor Help Page</a>
                </li>
                <li>
                  <a href="#gettingStarted">Getting Started Page</a>
                </li>

              </ul>
            </td>
            <td>
              <h2>Error Pages</h2>
              <ul class="nav">
                <li>
                  <a href="#deadlineExceededErrorPage">Deadline Exceeded Error Page</a>
                </li>
                <li>
                  <a href="#errorPage">Error Page</a>
                </li>
                <li>
                  <a href="#entityNotFoundPage">Entity Not Found Page</a>
                </li>
              </ul>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pageinfo">Instructor Home Page</div>
      <div id="instructorHomePage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Add Course Page</div>
      <div id="instructorAddCoursePage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Edit Course Page</div>
      <div id="instructorEditCoursePage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Enroll Students Page</div>
      <div id="instructorEnrollPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Course Details Page</div>
      <div id="instructorCourseDetailsPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Student List Page</div>
      <div id="instructorStudentListPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Student Details Page</div>
      <div id="instructorCourseStudentDetailsPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Student Edit Page</div>
      <div id="instructorCourseStudentEditPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Student Records Page</div>
      <div id="instructorStudentRecordsPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Page</div>
      <div id="instructorFeedbackPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Edit Page</div>
      <div id="instructorFeedbackEditPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Preview as Student Page</div>
      <div id="instructorFeedbackPreviewAsStudentPage"></div>
      <br><hr class="hr-bold"><br>
      <div class="pageinfo">Instructor Feedback Submit Page</div>
      <div id="instructorFeedbackSubmitPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Preview as Instructor Page</div>
      <div id="instructorFeedbackPreviewAsInstructorPage"></div>
      <br><hr class="hr-bold"><br>
      <div class="pageinfo">Instructor Feedback Question Submit Page</div>
      <div id="instructorFeedbackQuestionSubmitPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Results Page (By giver-recipient-question)</div>
      <div id="instructorFeedbackResultsPageByGiverRecipientQuestion"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Results Page (By recipient-giver-question)</div>
      <div id="instructorFeedbackResultsPageByRecipientGiverQuestion"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Results Page (By giver-question-recipient)</div>
      <div id="instructorFeedbackResultsPageByGiverQuestionRecipient"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Results Page (By recipient-question-giver)</div>
      <div id="instructorFeedbackResultsPageByRecipientQuestionGiver"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Feedback Results Page (By question)</div>
      <div id="instructorFeedbackResultsPageByQuestion"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Search Page</div>
      <div id="instructorSearchPage"></div>
      <br></br>
      <br></br>
      <br></br>
      <br></br>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Home Page</div>
      <div id="studentHomePage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Profile Page</div>
      <div id="studentProfilePage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Course Join Confirmation Page</div>
      <div id="studentCourseJoinConfirmationPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Course Join Confirmation Page (New)</div>
      <div id="studentCourseJoinConfirmationPageNew"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Course Details Page</div>
      <div id="studentCourseDetailsPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Feedback Submit Page</div>
      <div id="studentFeedbackSubmitPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Feedback Question Submit Page</div>
      <div id="studentFeedbackQuestionSubmitPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Feedback Results Page</div>
      <div id="studentFeedbackResultsPage"></div>
      <br></br>
      <br></br>
      <br></br>
      <br></br>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Admin Home Page</div>
      <div id="adminHomePage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Admin Search Page</div>
      <div id="adminSearchPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Admin Activity Log Page</div>
      <div id="adminActivityLogPage"></div>
      <br></br>
      <br></br>
      <br></br>
      <br></br>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Home Page</div>
      <div id="index">
        <iframe class="full-width" src="/index.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Features Page</div>
      <div id="features">
        <iframe class="full-width" src="/features.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">About Us Page</div>
      <div id="about">
        <iframe class="full-width" src="/about.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Contact Page</div>
      <div id="contact">
        <iframe class="full-width" src="/contact.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Terms Of Use Page</div>
      <div id="terms">
        <iframe class="full-width" src="/terms.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Request Account Page</div>
      <div id="request">
        <iframe class="full-width" src="/request.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Usermap Page</div>
      <div id="usermap">
        <iframe class="full-width" src="/usermap.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Student Help Page</div>
      <div id="studentHelp">
        <iframe class="full-width" src="/studentHelp.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Instructor Help Page</div>
      <div id="instructorHelp">
        <iframe class="full-width" src="/instructorHelp.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Getting Started Page</div>
      <div id="gettingStarted">
        <iframe class="full-width" src="/gettingStarted.jsp"></iframe>
      </div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Deadline Exceeded Error Page</div>
      <div id="deadlineExceededErrorPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Error Page</div>
      <div id="errorPage"></div>
      <br><hr class="hr-bold"><br>

      <div class="pageinfo">Entity Not Found Page</div>
      <div id="entityNotFoundPage"></div>
      <br><hr class="hr-bold"><br>
      <br></br>
      <br></br>
      <br></br>
      <br></br>
    </div>
  </body>

  <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
  <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
  <script>
    (function() {
      $(document).ready(function() {
        $('#instructorHomePage').load('<%=Const.ActionURIs.INSTRUCTOR_HOME_PAGE%>?user=teammates.test #mainContent');
        $('#instructorAddCoursePage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSES_PAGE%>?user=teammates.test #mainContent');
        $('#instructorEditCoursePage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE%>?user=teammates.test&courseid=CS1101 #mainContent',
          function(response, status, xml) {
            $('#instructorEditCoursePage').find('#panelAddInstructor').hide();
          });
        $('#instructorEnrollPage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE%>?user=teammates.test&courseid=CS1101 #mainContent');
        $('#instructorCourseDetailsPage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE%>?user=teammates.test&courseid=CS1101 #mainContent');
        $('#instructorStudentListPage').load('<%=Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE%>?user=teammates.test #mainContent');
        $('#instructorCourseStudentDetailsPage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE%>?user=teammates.test&courseid=CS2104&studentemail=teammates.test%40gmail.tmt #mainContent');
        $('#instructorCourseStudentEditPage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT%>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.tmt #mainContent');
        $('#instructorStudentRecordsPage').load('<%=Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE%>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.tmt #mainContent');
        $('#instructorFeedbackPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE%>?user=teammates.test #mainContent');
        $('#instructorFeedbackEditPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');
        $('#instructorFeedbackPreviewAsStudentPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&previewas=teammates.test@gmail.tmt #mainContent');
        $('#instructorFeedbackPreviewAsInstructorPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&previewas=teammates.test@gmail.tmt #mainContent');
        $('#instructorFeedbackSubmitPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');
        $('#instructorFeedbackResultsPageByGiverRecipientQuestion').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=giver-recipient-question #mainContent');
        $('#instructorFeedbackResultsPageByRecipientGiverQuestion').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=recipient-giver-question #mainContent');
        $('#instructorFeedbackResultsPageByGiverQuestionRecipient').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=giver-question-recipient #mainContent');
        $('#instructorFeedbackResultsPageByRecipientQuestionGiver').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=recipient-question-giver #mainContent');
        $('#instructorFeedbackResultsPageByQuestion').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=question #mainContent');
        $('#instructorSearchPage').load('<%=Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE%>?user=teammates.test&searchkey=teammates #mainContent');
        $('#studentHomePage').load('<%=Const.ActionURIs.STUDENT_HOME_PAGE%>?user=teammates.test #mainContent');
        $('#studentProfilePage').load('<%=Const.ActionURIs.STUDENT_PROFILE_PAGE%>?user=alice.b.tmms #mainContent');
        <%
          StudentAttributes student = new Logic().getStudentForEmail("CS4215", "teammates.test@gmail.tmt");
          if (student != null) {
            String url = StringHelper.encrypt(student.key);
        %>
            $('#studentCourseJoinConfirmationPage').load('<%=Const.ActionURIs.STUDENT_COURSE_JOIN%>?key=<%=student.key%> #mainContent');
            $('#studentCourseJoinConfirmationPageNew').load('<%=student.getRegistrationUrl()%> #mainContent');
        <%
          }
        %>
        $('#studentCourseDetailsPage').load('<%=Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE%>?user=teammates.test&courseid=CS2104 #mainContent');

        $('#studentFeedbackSubmitPage').load('<%=Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');

        $('#studentFeedbackResultsPage').load('<%=Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');
        $('#adminHomePage').load('<%=Const.ActionURIs.ADMIN_HOME_PAGE%> #mainContent');
        $('#adminSearchPage').load('<%=Const.ActionURIs.ADMIN_SEARCH_PAGE%>?limit=20&query=teammates&search=Search #mainContent');
        $('#adminActivityLogPage').load('<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%> #mainContent');
        loadErrorPage('<%=Const.ViewURIs.DEADLINE_EXCEEDED_ERROR_PAGE%>', '#deadlineExceededErrorPage');
        loadErrorPage('<%=Const.ViewURIs.ERROR_PAGE%>', '#errorPage');
        loadErrorPage('<%=Const.ViewURIs.ENTITY_NOT_FOUND_PAGE%>', '#entityNotFoundPage');
        $('#index').find('iframe').on('load', function() {
          calcHeight($('#index').find('iframe'));
        });
        $('#features').find('iframe').on('load', function() {
          calcHeight($('#features').find('iframe'));
        });
        $('#about').find('iframe').on('load', function() {
          calcHeight($('#about').find('iframe'));
        });
        $('#contact').find('iframe').on('load', function() {
          calcHeight($('#contact').find('iframe'));
        });
        $('#terms').find('iframe').on('load', function() {
          calcHeight($('#terms').find('iframe'));
        });
        $('#request').find('iframe').on('load', function() {
          calcHeight($('#request').find('iframe'));
        });
        $('#usermap').find('iframe').on('load', function() {
          calcHeight($('#usermap').find('iframe'));
        });
        $('#studentHelp').find('iframe').on('load', function() {
          calcHeight($('#studentHelp').find('iframe'));
        });
        $('#instructorHelp').find('iframe').on('load', function() {
          calcHeight($('#instructorHelp').find('iframe'));
        });
        $('#gettingStarted').find('iframe').on('load', function() {
            calcHeight($('#gettingStarted').find('iframe'));
        });
      });

      function calcHeight(iframe) {
        $(iframe).height($(iframe).contents().find('html').height());
      }

      function loadErrorPage(uri, id) {
        $.get(uri).fail(function(data) {
          $(id).html($('<div/>').html(data.responseText).find('#mainContent').html());
        });
      }
    })();
  </script>
</html>
