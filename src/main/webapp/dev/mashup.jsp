<%@ page import="teammates.common.util.Const"%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="shortcut icon" href="/favicon.png"></link>
		<meta http-equiv="X-UA-Compatible" content="IE=8"></link>
		<title>Teammates - Webpage Compilation</title>
		
		<link rel=stylesheet href="mashup.css" type="text/css"></link>
		<script language="JavaScript" src="/js/jquery-minified.js"></script>
		<script language="JavaScript" src="/js/tooltip.js"></script>
		<script src="/js/common.js" type="text/javascript"></script>
		
	</head>
	<body id="compilation">
		<div id="dhtmltooltip"></div>
		<h1>Table Of Contents</h1>
		<table id="tableofcontents">
		<tbody>
			<tr>
				<td width="33%">
					<h2>Instructor Pages</h2>
					<ul class="nav">
						<li><a href="#instructorHomePage">Instructor Home Page</a></li>
						<li><a href="#instructorAddCoursePage">Instructor Add Course Page</a></li>
						<li><a href="#instructorEditCoursePage">Instructor Edit Course Page</a></li>
						<li><a href="#instructorEnrollPage">Instructor Enroll Students Page</a></li>
						<li><a href="#instructorCourseDetailsPage">Instructor Course Details Page</a></li>
						<li><a href="#instructorCourseStudentDetailsPage">Instructor Student Details Page</a></li>
						<li><a href="#instructorCourseStudentEditPage">Instructor Student Edit Page</a></li>
						<li><a href="#instructorCourseEvalPage">Instructor Eval Page</a></li>
						<li><a href="#instructorCourseEvalEditPage">Instructor Eval Edit Page</a></li>
					    <li><a href="#instructorCourseEvalResultsPage1">Instructor Eval Results Page (instructorEvaluationSummaryTable)</a></li>
						<li><a href="#instructorCourseEvalResultsPage2">Instructor Eval Results Page (instructorEvaluationDetailedReviewerTable)</a></li>
						<li><a href="#instructorCourseEvalResultsPage3">Instructor Eval Results Page (instructorEvaluationDetailedRevieweeTable)</a></li>
						<li><a href="#instructorCourseEvalSubmissionViewPage">Instructor Eval Submission View Page</a></li>
						<li><a href="#instructorCourseEvalSubmissionEditPage">Instructor Eval Submission Edit Page</a></li>
						<li><a href="#instructorFeedbackPage">Instructor Feedback Page</a></li>
						<li><a href="#instructorFeedbackEditPage">Instructor Feedback Edit Page</a></li>
						<li><a href="#instructorFeedbackResultsPageByGiver">Instructor Feedback Results Page (By giver)</a></li>
						<li><a href="#instructorFeedbackResultsPageByRecipient">Instructor Feedback Results Page (By recipient)</a></li>
						<li><a href="#instructorFeedbackResultsPageByTable">Instructor Feedback Results Page (By table)</a></li>						
					</ul>
				</td>
				<td width="33%">
					<h2>Student Pages</h2>
					<ul class="nav">
						<li><a href="#studentHomePage">Student Home Page</a></li>
						<li><a href="#studentCourseDetailsPage">Student Course Details Page</a></li>
						<li><a href="#studentEvalEditPage">Student Eval Edit Page</a></li>
						<li><a href="#studentEvalResultsPage">Student Eval Results Page</a></li>
						<li><a href="#studentFeedbackSubmitPage">Student Feedback Submit Page</a></li>
						<li><a href="#studentFeedbackResultsPage">Student Feedback Results Page</a></li>
					</ul>
				<td>
				</td>
				<td>
					<h2>Admin Page</h2>
					<ul class="nav">
						<li><a href="#adminHomePage">Admin Home Page</a></li>
						<li><a href="#adminSearchPage">Admin Search Page</a></li>
						<li><a href="#adminActivityLogPage">Admin Activity Log Page</a></li>
					</ul>
				</td>
			</tr>
			</tbody>
		</table>
			
		<div class="pageinfo">Instructor Home Page</div>
		<div id="instructorHomePage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Add Course Page</div>
		<div id="instructorAddCoursePage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Edit Course Page</div>
		<div id="instructorEditCoursePage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Enroll Students Page</div>
		<div id="instructorEnrollPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Course Details Page</div>
		<div id="instructorCourseDetailsPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Student Details Page</div>
		<div id="instructorCourseStudentDetailsPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Student Edit Page</div>
		<div id="instructorCourseStudentEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Page</div>
		<div id="instructorCourseEvalPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Edit Page</div>
		<div id="instructorCourseEvalEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Results Page (instructorEvaluationSummaryTable)</div>
		<div id="instructorCourseEvalResultsPage1" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Results Page (instructorEvaluationDetailedReviewerTable)</div>
		<div id="instructorCourseEvalResultsPage2" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Results Page (instructorEvaluationDetailedRevieweeTable)</div>
		<div id="instructorCourseEvalResultsPage3" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Submission View Page</div>
		<div id="instructorCourseEvalSubmissionViewPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Eval Submission Edit Page</div>
		<div id="instructorCourseEvalSubmissionEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Feedback Page</div>
		<div id="instructorFeedbackPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Feedback Edit Page</div>
		<div id="instructorFeedbackEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Feedback Results Page (By giver)</div>
		<div id="instructorFeedbackResultsPageByGiver" class="wrapper"></div>		
		
		<div class="pageinfo">Instructor Feedback Results Page (By recipient)</div>
		<div id="instructorFeedbackResultsPageByRecipient" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Feedback Results Page (By table)</div>
		<div id="instructorFeedbackResultsPageByTable" class="wrapper"></div>
		<br></br>
		<br></br>
		<br></br>
		<br></br>
		<div class="pageinfo">Student Home Page</div>
		<div id="studentHomePage" class="wrapper"></div>
		
		<div class="pageinfo">Student Course Details Page</div>
		<div id="studentCourseDetailsPage" class="wrapper"></div>
		
		<div class="pageinfo">Student Eval Edit Page</div>
		<div id="studentEvalEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Student Eval Results Page</div>
		<div id="studentEvalResultsPage" class="wrapper"></div>
		
		<div class="pageinfo">Student Feedback Submit Page</div>
		<div id="studentFeedbackSubmitPage" class="wrapper"></div>

		<div class="pageinfo">Student Feedback Results Page</div>
		<div id="studentFeedbackResultsPage" class="wrapper"></div>
		<br></br>
		<br></br>
		<br></br>
		<br></br>
		<div class="pageinfo">Admin Home Page</div>
		<div id="adminHomePage" class="wrapper"></div>
		
		<div class="pageinfo">Admin Search Page</div>
		<div id="adminSearchPage" class="wrapper"></div>
		
		<div class="pageinfo">Admin Activity Log Page</div>
		<div id="adminActivityLogPage" class="wrapper"></div>
	</body>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$('head').append('<link rel=stylesheet href="/stylesheets/common.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourses.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseEnroll.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseStudentDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseStudentEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvals.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalResults.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalSubmissionView.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalSubmissionEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorFeedbacks.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentCourseDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentEvalEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentEvalResults.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentFeedback.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/adminHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/adminActivityLog.css" type="text/css">');

			$('#instructorHomePage').load("<%=Const.ActionURIs.INSTRUCTOR_HOME_PAGE%>?user=teammates.test #frameBodyWrapper");
			$('#instructorAddCoursePage').load("<%=Const.ActionURIs.INSTRUCTOR_COURSES_PAGE%>?user=teammates.test #frameBodyWrapper");
			$('#instructorEditCoursePage').load("<%=Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE%>?user=teammates.test&courseid=CS1101 #frameBodyWrapper");
			$('#instructorEnrollPage').load("<%=Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE%>?user=teammates.test&courseid=CS1101 #frameBodyWrapper");
			$('#instructorCourseDetailsPage').load("<%=Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE%>?user=teammates.test&courseid=CS1101 #frameBodyWrapper");
			$('#instructorCourseStudentDetailsPage').load("<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE%>?user=teammates.test&courseid=CS2104&studentemail=teammates.test%40gmail.com #frameBodyWrapper");
			$('#instructorCourseStudentEditPage').load("<%=Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT%>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");
			$('#instructorCourseEvalPage').load("<%=Const.ActionURIs.INSTRUCTOR_EVALS_PAGE%>?user=teammates.test #frameBodyWrapper");
			$('#instructorCourseEvalEditPage').load("<%=Const.ActionURIs.INSTRUCTOR_EVAL_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#instructorCourseEvalResultsPage1').load("<%=Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#instructorCourseEvalResultsPage2').load("<%=Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#instructorCourseEvalResultsPage2').find('#instructorEvaluationSummaryTable').hide();
				$('#instructorCourseEvalResultsPage2').find('#instructorEvaluationDetailedReviewerTable').show();
			});
			$('#instructorCourseEvalResultsPage3').load("<%=Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#instructorCourseEvalResultsPage3').find('#instructorEvaluationSummaryTable').hide();
				$('#instructorCourseEvalResultsPage3').find('#instructorEvaluationDetailedRevieweeTable').show();
			});
			$('#instructorCourseEvalSubmissionViewPage').load("<%=Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval&studentemail=teammates.test%40gmail.com #frameBodyWrapper");		
			$('#instructorCourseEvalSubmissionEditPage').load("<%=Const.ActionURIs.INSTRUCTOR_EVAL_SUBMISSION_EDIT%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval&studentemail=charlie.d.tmms%40gmail.com #frameBodyWrapper");
			$('#instructorFeedbackPage').load("<%=Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE%>?user=teammates.test #frameBodyWrapper");
			$('#instructorFeedbackEditPage').load("<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #frameBodyWrapper");
			$('#instructorFeedbackResultsPageByGiver').load("<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=giver #frameBodyWrapper");
			$('#instructorFeedbackResultsPageByRecipient').load("<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=recipient #frameBodyWrapper");
			$('#instructorFeedbackResultsPageByTable').load("<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session&frsorttype=table #frameBodyWrapper");
			
			$('#studentHomePage').load("<%=Const.ActionURIs.STUDENT_HOME_PAGE%>?user=teammates.test #frameBodyWrapper");
			$('#studentCourseDetailsPage').load("<%=Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE%>?user=teammates.test&courseid=CS2104 #frameBodyWrapper");
			$('#studentEvalEditPage').load("<%=Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#studentEvalResultsPage').load("<%=Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&evaluationname=Second+Eval #frameBodyWrapper");
			$('#studentFeedbackSubmitPage').load("<%=Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #frameBodyWrapper");
			$('#studentFeedbackResultsPage').load("<%=Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE%>?user=teammates.test&courseid=CS2104&fsname=First+feedback+session #frameBodyWrapper");
			
			$('#adminHomePage').load("<%=Const.ActionURIs.ADMIN_HOME_PAGE%> #frameBodyWrapper");
			$('#adminSearchPage').load("<%=Const.ActionURIs.ADMIN_SEARCH_PAGE%>?limit=20&query=teammates&search=Search #frameBodyWrapper");
			$('#adminActivityLogPage').load("<%=Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE%> #frameBodyWrapper");
			
			//Tooltip
			initializetooltip();
			document.onmousemove = positiontip;
		});
	</script>
</html>