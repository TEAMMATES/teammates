<%@ page import="teammates.common.Common"%>
<!DOCTYPE html>
<html>
	<head>
		<link rel="shortcut icon" href="/favicon.png"></link>
		<meta http-equiv="X-UA-Compatible" content="IE=8"></link>
		<title>Teammates - Webpage Compilation</title>
		
		<link rel=stylesheet href="webpageCompilation.css" type="text/css"></link>
		<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
		<script language="JavaScript" src="/js/tooltip.js"></script>
		
	</head>
	<body id="compilation">
		<div id="dhtmltooltip"></div>
		<div>
			<ul id="nav">
				<li><a href="#instructorHomePage">Instructor Home Page</a></li>
				<li><a href="#instructorAddCoursePage">Instructor Add Course Page</a></li>
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
				<hr>
				<li><a href="#studentHomePage">Student Home Page</a></li>
				<li><a href="#studentCourseDetailsPage">Student Course Details Page</a></li>
				<li><a href="#studentEvalEditPage">Student Eval Edit Page</a></li>
				<li><a href="#studentEvalResultsPage">Student Eval Results Page</a></li>
				<hr>
				<li><a href="#adminPage">Admin Page</a></li>
				
			</ul>
		</div>
		<div class="pageinfo">Instructor Home Page</div>
		<div id="instructorHomePage" class="wrapper"></div>
		
		<div class="pageinfo">Instructor Add Course Page</div>
		<div id="instructorAddCoursePage" class="wrapper"></div>
		
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
		<br></br>
		<br></br>
		<br></br>
		<br></br>
		<div class="pageinfo">Admin Page</div>
		<div id="adminPage" class="wrapper"></div>
	</body>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$('head').append('<link rel=stylesheet href="/stylesheets/common.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourse.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseEnroll.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseStudentDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorCourseStudentEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEval.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalResults.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalSubmissionView.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/instructorEvalSubmissionEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentEvalEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentEvalResults.css" type="text/css">');
			
			$('#instructorHomePage').load("<%= Common.PAGE_INSTRUCTOR_HOME %>?user=teammates.test #frameBodyWrapper");
			$('#instructorAddCoursePage').load("<%= Common.PAGE_INSTRUCTOR_COURSE %>?user=teammates.test #frameBodyWrapper");
			$('#instructorEnrollPage').load("<%= Common.PAGE_INSTRUCTOR_COURSE_ENROLL %>?user=teammates.test&courseid=CS1101 #frameBodyWrapper");
			$('#instructorCourseDetailsPage').load("<%= Common.PAGE_INSTRUCTOR_COURSE_DETAILS %>?user=teammates.test&courseid=CS2104 #frameBodyWrapper");
			$('#instructorCourseStudentDetailsPage').load("<%= Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS %>?user=teammates.test&courseid=CS2104&studentemail=teammates.test%40gmail.com #frameBodyWrapper");
			$('#instructorCourseStudentEditPage').load("<%= Common.PAGE_INSTRUCTOR_COURSE_STUDENT_EDIT %>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");
			$('#instructorCourseEvalPage').load("<%= Common.PAGE_INSTRUCTOR_EVAL %>?user=teammates.test #frameBodyWrapper");
			$('#instructorCourseEvalEditPage').load("<%= Common.PAGE_INSTRUCTOR_EVAL_EDIT %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#instructorCourseEvalResultsPage1').load("<%= Common.PAGE_INSTRUCTOR_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#instructorCourseEvalResultsPage2').load("<%= Common.PAGE_INSTRUCTOR_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#instructorCourseEvalResultsPage2').find('#instructorEvaluationSummaryTable').hide();
				$('#instructorCourseEvalResultsPage2').find('#instructorEvaluationDetailedReviewerTable').show();
			});
			$('#instructorCourseEvalResultsPage3').load("<%= Common.PAGE_INSTRUCTOR_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#instructorCourseEvalResultsPage3').find('#instructorEvaluationSummaryTable').hide();
				$('#instructorCourseEvalResultsPage3').find('#instructorEvaluationDetailedRevieweeTable').show();
			});
			$('#instructorCourseEvalSubmissionViewPage').load("<%= Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_VIEW %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");		
			$('#instructorCourseEvalSubmissionEditPage').load("<%= Common.PAGE_INSTRUCTOR_EVAL_SUBMISSION_EDIT %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval&studentemail=charlie.d.tmms%40gmail.com #frameBodyWrapper");
			
			$('#studentHomePage').load("<%= Common.PAGE_STUDENT_HOME %>?user=teammates.test #frameBodyWrapper");
			$('#studentCourseDetailsPage').load("<%= Common.PAGE_STUDENT_COURSE_DETAILS %>?user=teammates.test&courseid=CS2104 #frameBodyWrapper");
			$('#studentEvalEditPage').load("<%= Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#studentEvalResultsPage').load("<%= Common.PAGE_STUDENT_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			
			$('#adminPage').load("<%=Common.PAGE_ADMIN_HOME %> #frameBodyWrapper");
			
			
			//Tooltip
			initializetooltip();
			document.onmousemove = positiontip;
		});
	</script>
</html>