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
				<li><a href="#coordHomePage">Coordinator Home Page</a></li>
				<li><a href="#coordAddCoursePage">Coordinator Add Course Page</a></li>
				<li><a href="#coordEnrollPage">Coordinator Enroll Students Page</a></li>
				<li><a href="#coordCourseDetailsPage">Coordinator Course Details Page</a></li>
				<li><a href="#coordCourseStudentDetailsPage">Coordinator Student Details Page</a></li>
				<li><a href="#coordCourseStudentEditPage">Coordinator Student Edit Page</a></li>
				<li><a href="#coordCourseEvalPage">Coordinator Eval Page</a></li>
				<li><a href="#coordCourseEvalEditPage">Coordinator Eval Edit Page</a></li>
			    <li><a href="#coordCourseEvalResultsPage1">Coordinator Eval Results Page (coordinatorEvaluationSummaryTable)</a></li>
				<li><a href="#coordCourseEvalResultsPage2">Coordinator Eval Results Page (coordinatorEvaluationDetailedReviewerTable)</a></li>
				<li><a href="#coordCourseEvalResultsPage3">Coordinator Eval Results Page (coordinatorEvaluationDetailedRevieweeTable)</a></li>
				<li><a href="#coordCourseEvalSubmissionViewPage">Coordinator Eval Submission View Page</a></li>
				<li><a href="#coordCourseEvalSubmissionEditPage">Coordinator Eval Submission Edit Page</a></li>
				<hr>
				<li><a href="#studentHomePage">Student Home Page</a></li>
				<li><a href="#studentCourseDetailsPage">Student Course Details Page</a></li>
				<li><a href="#studentEvalEditPage">Student Eval Edit Page</a></li>
				<li><a href="#studentEvalResultsPage">Student Eval Results Page</a></li>
				<hr>
				<li><a href="#adminPage">Admin Page</a></li>
				
			</ul>
		</div>
		<div class="pageinfo">Coordinator Home Page</div>
		<div id="coordHomePage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Add Course Page</div>
		<div id="coordAddCoursePage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Enroll Students Page</div>
		<div id="coordEnrollPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Course Details Page</div>
		<div id="coordCourseDetailsPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Student Details Page</div>
		<div id="coordCourseStudentDetailsPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Student Edit Page</div>
		<div id="coordCourseStudentEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Page</div>
		<div id="coordCourseEvalPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Edit Page</div>
		<div id="coordCourseEvalEditPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Results Page (coordinatorEvaluationSummaryTable)</div>
		<div id="coordCourseEvalResultsPage1" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Results Page (coordinatorEvaluationDetailedReviewerTable)</div>
		<div id="coordCourseEvalResultsPage2" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Results Page (coordinatorEvaluationDetailedRevieweeTable)</div>
		<div id="coordCourseEvalResultsPage3" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Submission View Page</div>
		<div id="coordCourseEvalSubmissionViewPage" class="wrapper"></div>
		
		<div class="pageinfo">Coordinator Eval Submission Edit Page</div>
		<div id="coordCourseEvalSubmissionEditPage" class="wrapper"></div>
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
			$('head').append('<link rel=stylesheet href="/stylesheets/coordHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordCourse.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordCourseEnroll.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordCourseDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordCourseStudentDetails.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordCourseStudentEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordEval.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordEvalEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordEvalResults.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordEvalSubmissionView.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/coordEvalSubmissionEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentHome.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentEvalEdit.css" type="text/css">');
			$('head').append('<link rel=stylesheet href="/stylesheets/studentEvalResults.css" type="text/css">');
			
			$('#coordHomePage').load("<%= Common.PAGE_COORD_HOME %>?user=teammates.test #frameBodyWrapper");
			$('#coordAddCoursePage').load("<%= Common.PAGE_COORD_COURSE %>?user=teammates.test #frameBodyWrapper");
			$('#coordEnrollPage').load("<%= Common.PAGE_COORD_COURSE_ENROLL %>?user=teammates.test&courseid=CS1101 #frameBodyWrapper");
			$('#coordCourseDetailsPage').load("<%= Common.PAGE_COORD_COURSE_DETAILS %>?user=teammates.test&courseid=CS2104 #frameBodyWrapper");
			$('#coordCourseStudentDetailsPage').load("<%= Common.PAGE_COORD_COURSE_STUDENT_DETAILS %>?user=teammates.test&courseid=CS2104&studentemail=teammates.test%40gmail.com #frameBodyWrapper");
			$('#coordCourseStudentEditPage').load("<%= Common.PAGE_COORD_COURSE_STUDENT_EDIT %>?user=teammates.test&courseid=CS2104&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");
			$('#coordCourseEvalPage').load("<%= Common.PAGE_COORD_EVAL %>?user=teammates.test #frameBodyWrapper");
			$('#coordCourseEvalEditPage').load("<%= Common.PAGE_COORD_EVAL_EDIT %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage1').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage2').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#coordCourseEvalResultsPage2').find('#coordinatorEvaluationSummaryTable').hide();
				$('#coordCourseEvalResultsPage2').find('#coordinatorEvaluationDetailedReviewerTable').show();
			});
			$('#coordCourseEvalResultsPage3').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper", function(response, status, xml){
				$('#coordCourseEvalResultsPage3').find('#coordinatorEvaluationSummaryTable').hide();
				$('#coordCourseEvalResultsPage3').find('#coordinatorEvaluationDetailedRevieweeTable').show();
			});
			$('#coordCourseEvalSubmissionViewPage').load("<%= Common.PAGE_COORD_EVAL_SUBMISSION_VIEW %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");		
			$('#coordCourseEvalSubmissionEditPage').load("<%= Common.PAGE_COORD_EVAL_SUBMISSION_EDIT %>?user=teammates.test&courseid=CS2104&evaluationname=First+Eval&studentemail=charlie.d.tmms%40gmail.com #frameBodyWrapper");
			
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