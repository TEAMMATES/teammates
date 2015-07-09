<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.StringHelper" %>
<%@ page import="teammates.logic.core.FeedbackQuestionsLogic" %>
<%@ page import="teammates.logic.api.Logic" %>
<%@ page import="teammates.common.datatransfer.StudentAttributes" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="X-UA-Compatible" content="IE=8">
        <meta content="width=device-width, initial-scale=1.0" name="viewport">
        
        <title>Teammates - Webpage Compilation</title>
        
        <link rel="shortcut icon" href="/favicon.png">
        <link type="text/css" href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
        <link type="text/css" href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
        <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet">
        <link type="text/css" href="./mashup.css" rel="stylesheet">
        <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="/js/common.js"></script>
    </head>
    <body>
        <div class="container theme-showcase">
            <div class="row">
                <div id="dhtmltooltip">
                <div class="alert alert-warning"><h4>Please remember to log in</h4></div>
            </div>
            
            <div class="intro">
                <h1>Introduction</h1>
                <table class="table table-striped">
                    <tr>
                        <td>
                            <p>This page lists all of the pages which make up the TEAMMATES app. It is used for manually 
                               inspecting the TEAMMATES UI for visual defects. To load this page, you will need to run the test 
                               <strong>MashupPageUiTest.java</strong>.</p>                
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
                                <li><a href="#instructorHomePage">Instructor Home Page</a></li>
                                <li><a href="#instructorAddCoursePage">Instructor Add Course Page</a></li>
                                <li><a href="#instructorEditCoursePage">Instructor Edit Course Page</a></li>
                                <li><a href="#instructorEnrollPage">Instructor Enroll Students Page</a></li>
                                <li><a href="#instructorCourseDetailsPage">Instructor Course Details Page</a></li>
                                <li><a href="#instructorStudentListPage">Instructor Student List Page</a></li>
                                <li><a href="#instructorCourseStudentDetailsPage">Instructor Student Details Page</a></li>
                                <li><a href="#instructorCourseStudentEditPage">Instructor Student Edit Page</a></li>
                                <li><a href="#instructorStudentRecordsPage">Instructor Student Records Page</a></li>
                                <li><a href="#instructorCourseEvalPage">Instructor Eval Page</a></li>
                                <li><a href="#instructorCourseEvalEditPage">Instructor Eval Edit Page</a></li>
                                <li><a href="#instructorCourseEvalPreviewPage">Instructor Eval Preview Page</a></li>
                                <li><a href="#instructorCourseEvalResultsPage1">Instructor Eval Results Page (instructorEvaluationSummaryTable)</a></li>
                                <li><a href="#instructorCourseEvalResultsPage2">Instructor Eval Results Page (instructorEvaluationDetailedReviewerTable)</a></li>
                                <li><a href="#instructorCourseEvalResultsPage3">Instructor Eval Results Page (instructorEvaluationDetailedRevieweeTable)</a></li>
                                <li><a href="#instructorCourseEvalSubmissionViewPage">Instructor Eval Submission View Page</a></li>
                                <li><a href="#instructorCourseEvalSubmissionEditPage">Instructor Eval Submission Edit Page</a></li>
                                <li><a href="#instructorFeedbackPage">Instructor Feedback Page</a></li>
                                <li><a href="#instructorFeedbackEditPage">Instructor Feedback Edit Page</a></li>
                                <li><a href="#instructorFeedbackPreviewAsStudentPage">Instructor Feedback Preview as Student Page</a></li>
                                <li><a href="#instructorFeedbackPreviewAsInstructorPage">Instructor Feedback Preview as Instructor Page</a></li>
                                <li><a href="#instructorFeedbackSubmitPage">Instructor Feedback Submit Page</a></li>
                                <li><a href="#instructorFeedbackQuestionSubmitPage">Instructor Feedback Question Submit Page</a></li>
                                <li><a href="#instructorFeedbackResultsPageByGiverRecipientQuestion">Instructor Feedback Results Page (By giver-recipient-question)</a></li>
                                <li><a href="#instructorFeedbackResultsPageByRecipientGiverQuestion">Instructor Feedback Results Page (By recipient-giver-question)</a></li>
                                <li><a href="#instructorFeedbackResultsPageByGiverQuestionRecipient">Instructor Feedback Results Page (By giver-question-recipient)</a></li>
                                <li><a href="#instructorFeedbackResultsPageByRecipientQuestionGiver">Instructor Feedback Results Page (By recipient-question-giver)</a></li>
                                <li><a href="#instructorFeedbackResultsPageByQuestion">Instructor Feedback Results Page (By question)</a></li>
                                <li><a href="#instructorCommentsPage">Instructor Comments Page</a></li>
                                <li><a href="#instructorSearchPage">Instructor Search Page</a></li>
                            </ul>
                        </td>
                        <td>
                            <h2>Student Pages</h2>
                            <ul class="nav">
                                <li><a href="#studentHomePage">Student Home Page</a></li>
                                <li><a href="#studentProfilePage">Student Profile Page</a></li>
                                <li><a href="#studentCourseJoinConfirmationPage">Student Course Join Confirmation Page</a></li>
                                <li><a href="#studentCourseJoinConfirmationPageNew">Student Course Join Confirmation Page (New)</a></li>
                                <li><a href="#studentCourseDetailsPage">Student Course Details Page</a></li>
                                <li><a href="#studentEvalEditPage">Student Eval Edit Page</a></li>
                                <li><a href="#studentEvalResultsPage">Student Eval Results Page</a></li>
                                <li><a href="#studentFeedbackSubmitPage">Student Feedback Submit Page</a></li>
                                <li><a href="#studentFeedbackQuestionSubmitPage">Student Feedback Question Submit Page</a></li>
                                <li><a href="#studentFeedbackResultsPage">Student Feedback Results Page</a></li>
                                <li><a href="#studentCommentsPage">Student Comments Page</a></li>
                            </ul>
                        <td>
                        </td>
                        <td>
                            <h2>Admin Pages</h2>
                            <ul class="nav">
                                <li><a href="#adminHomePage">Admin Home Page</a></li>
                                <li><a href="#adminSearchPage">Admin Search Page</a></li>
                                <li><a href="#adminActivityLogPage">Admin Activity Log Page</a></li>
                            </ul>
                        </td>
                        <td>    
                            <h2>Static Pages</h2>
                            <ul class="nav">
                                <li><a href="#index">Home Page</a></li>
                                <li><a href="#features">Features Page</a></li>
                                <li><a href="#about">About Us Page</a></li>
                                <li><a href="#contact">Contact Page</a></li>
                                <li><a href="#terms">Terms Of Use Page</a></li>
                                <li><a href="#request">Request Account Page</a></li>
                                <li><a href="#studentHelp">Student Help Page</a></li>
                                <li><a href="#instructorHelp">Instructor Help Page</a></li>
                            </ul>
                        </td>
                        <td>    
                            <h2>Error Pages</h2>
                            <ul class="nav">
                                <li><a href="#deadlineExceededErrorPage">Deadline Exceeded Error Page</a></li>
                                <li><a href="#errorPage">Error Page</a></li>
                                <li><a href="#entityNotFoundPage">Entity Not Found Page</a></li>
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
                
                <div class="pageinfo">Instructor Eval Page</div>
                <div id="instructorCourseEvalPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Edit Page</div>
                <div id="instructorCourseEvalEditPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Preview Page</div>
                <div id="instructorCourseEvalPreviewPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Results Page (instructorEvaluationSummaryTable)</div>
                <div id="instructorCourseEvalResultsPage1"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Results Page (instructorEvaluationDetailedReviewerTable)</div>
                <div id="instructorCourseEvalResultsPage2"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Results Page (instructorEvaluationDetailedRevieweeTable)</div>
                <div id="instructorCourseEvalResultsPage3"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Submission View Page</div>
                <div id="instructorCourseEvalSubmissionViewPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Instructor Eval Submission Edit Page</div>
                <div id="instructorCourseEvalSubmissionEditPage"></div>
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
                
                <div class="pageinfo">Instructor Comments Page</div>
                <div id="instructorCommentsPage"></div>
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
                
                <div class="pageinfo">Student Eval Edit Page</div>
                <div id="studentEvalEditPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Student Eval Results Page</div>
                <div id="studentEvalResultsPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Student Feedback Submit Page</div>
                <div id="studentFeedbackSubmitPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Student Feedback Question Submit Page</div>
                <div id="studentFeedbackQuestionSubmitPage"></div>
                <br><hr class="hr-bold"><br>

                <div class="pageinfo">Student Feedback Results Page</div>
                <div id="studentFeedbackResultsPage"></div>
                <br><hr class="hr-bold"><br>
                
                <div class="pageinfo">Student Comments Page</div>
                <div id="studentCommentsPage"></div>
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
                    <iframe class="full-width" src="../index.html"></iframe>
                </div>
                <div class="pageinfo">Features Page</div>
                <div id="features">
                    <iframe class="full-width" src="../features.html"></iframe>
                </div>
                <div class="pageinfo">About Us Page</div>
                <div id="about">
                    <iframe class="full-width" src="../about.html"></iframe>
                </div>
                <div class="pageinfo">Contact Page</div>
                <div id="contact">
                    <iframe class="full-width" src="../contact.html"></iframe>
                </div>
                <div class="pageinfo">Terms Of Use Page</div>
                <div id="terms">
                    <iframe class="full-width" src="../terms.html"></iframe>
                </div>
                <div class="pageinfo">Request Account Page</div>
                <div id="request">
                    <iframe class="full-width" src="../request.html"></iframe>
                </div>
                <div class="pageinfo">Student Help Page</div>
                <div id="studentHelp">
                    <iframe class="full-width" src="../studentHelp.html"></iframe>
                </div>
                <div class="pageinfo">Instructor Help Page</div>
                <div id="instructorHelp">
                    <iframe class="full-width" src="../instructorHelp.html"></iframe>
                </div>
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
        </div>
    </body>
    
    <script type="text/javascript">
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
                $('#instructorCourseStudentDetailsPage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE%>?user=teammates.test&courseid=CS2104&studentemail=teammates.test%40gmail.com #mainContent');
                $('#instructorCourseStudentEditPage').load('<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT%>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.com #mainContent');
                $('#instructorStudentRecordsPage').load('<%=Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE%>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.com #mainContent');
                $('#instructorFeedbackPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE%>?user=teammates.test #mainContent');
                $('#instructorFeedbackEditPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');
                $('#instructorFeedbackPreviewAsStudentPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASSTUDENT%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&previewas=teammates.test@gmail.com #mainContent');
                $('#instructorFeedbackPreviewAsInstructorPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_PREVIEW_ASINSTRUCTOR%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&previewas=teammates.test@gmail.com #mainContent');
                $('#instructorFeedbackSubmitPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');
                <%
                    String instrQuestionId = null;
                    if (FeedbackQuestionsLogic.inst().getFeedbackQuestion("First feedback session", "CS2104", 3) != null) {
                        instrQuestionId = FeedbackQuestionsLogic.inst().getFeedbackQuestion("First feedback session", "CS2104", 3).getId();
                %>
                        $('#instructorFeedbackQuestionSubmitPage').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&questionid=<%=instrQuestionId%> #mainContent');
                <%
                    }
                %>
                $('#instructorFeedbackResultsPageByGiverRecipientQuestion').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=giver-recipient-question #mainContent');
                $('#instructorFeedbackResultsPageByRecipientGiverQuestion').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=recipient-giver-question #mainContent');
                $('#instructorFeedbackResultsPageByGiverQuestionRecipient').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=giver-question-recipient #mainContent');
                $('#instructorFeedbackResultsPageByRecipientQuestionGiver').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=recipient-question-giver #mainContent');
                $('#instructorFeedbackResultsPageByQuestion').load('<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=question #mainContent');
                $('#instructorCommentsPage').load('<%=Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE%>?user=teammates.test&courseid=CS2104 #mainContent');
                $('#instructorSearchPage').load('<%=Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE%>?user=teammates.test&searchkey=teammates #mainContent');
                $('#studentHomePage').load('<%=Const.ActionURIs.STUDENT_HOME_PAGE%>?user=teammates.test #mainContent');
                $('#studentProfilePage').load('<%=Const.ActionURIs.STUDENT_PROFILE_PAGE%>?user=alice.b.tmms #mainContent');
                <%
                    StudentAttributes student = new Logic().getStudentForEmail("CS4215", "teammates.test@gmail.com");
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
                <%
                    String studentQuestionId = null;
                    if (FeedbackQuestionsLogic.inst().getFeedbackQuestion("First feedback session", "CS2104", 1) != null) {
                        studentQuestionId = FeedbackQuestionsLogic.inst().getFeedbackQuestion("First feedback session", "CS2104", 1).getId();
                %>
                        $('#studentFeedbackQuestionSubmitPage').load('<%=Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&questionid=<%=studentQuestionId%> #mainContent');
                <%
                    }
                %>
                $('#studentFeedbackResultsPage').load('<%=Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #mainContent');
                $('#studentCommentsPage').load('<%=Const.ActionURIs.STUDENT_COMMENTS_PAGE%>?user=alice.b.tmms&courseid=CS2104 #mainContent');
                $('#adminHomePage').load('<%=Const.ActionURIs.ADMIN_HOME_PAGE%> #mainContent');
                $('#adminSearchPage').load('<%=Const.ActionURIs.ADMIN_SEARCH_PAGE%>?limit=20&query=teammates&search=Search #mainContent');
                $('#adminActivityLogPage').load('<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%> #mainContent');
                $('#deadlineExceededErrorPage').load('<%=Const.ViewURIs.DEADLINE_EXCEEDED_ERROR_PAGE%> #mainContent');
                $('#errorPage').load('<%=Const.ViewURIs.ERROR_PAGE%> #mainContent');
                $('#entityNotFoundPage').load('<%=Const.ViewURIs.ENTITY_NOT_FOUND_PAGE%> #mainContent');
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
                $('#studentHelp').find('iframe').on('load', function() {
                    calcHeight($('#studentHelp').find('iframe'));
                });
                $('#instructorHelp').find('iframe').on('load', function() {
                    calcHeight($('#instructorHelpt').find('iframe'));
                });
            });
            
            function calcHeight(iframe) {
                $(iframe).height($(iframe).contents().find('html').height());
            }
        })();
    </script>
</html>