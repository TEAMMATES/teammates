<%@ page import="teammates.common.Common"%>

<html>
	<head>
		<link rel=stylesheet href="webpageCompilation.css" type="text/css"></link>
		<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	</head>
	<body id="compilation">
		<div class="info">Coordinator Home Page</div>
		<div id="coordHomePage" class="wrapper"></div>
		
		<div class="info">Coordinator Add Course Page</div>
		<div id="coordAddCoursePage" class="wrapper"></div>
		
		<div class="info">Coordinator Enroll Students Page</div>
		<div id="coordEnrollPage" class="wrapper"></div>
		
		<div class="info">Coordinator Course Details Page</div>
		<div id="coordCourseDetailsPage" class="wrapper"></div>
		
		<div class="info">Coordinator Student Details Page</div>
		<div id="coordCourseStudentDetailsPage" class="wrapper"></div>
		
		<div class="info">Coordinator Student Edit Page</div>
		<div id="coordCourseStudentEditPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Page</div>
		<div id="coordCourseEvalPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Edit Page</div>
		<div id="coordCourseEvalEditPage" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Results Page (coordinatorEvaluationSummaryTable)</div>
		<div id="coordCourseEvalResultsPage1" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Results Page (coordinatorEvaluationDetailedReviewerTable)</div>
		<div id="coordCourseEvalResultsPage2" class="wrapper"></div>
		
		<div class="info">Coordinator Eval Results Page (coordinatorEvaluationDetailedRevieweeTable)</div>
		<div id="coordCourseEvalResultsPage3" class="wrapper"></div>
		
		<div class="info">Student Home Page</div>
		<div id="studentHomePage" class="wrapper"></div>
	</body>
	
	<script type="text/javascript">
		$(document).ready(function(){
			$('head').append('<link rel=stylesheet href="/stylesheets/main.css" type="text/css"></link>');
			$('head').append('<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css"></link>');
			
			$('#coordHomePage').load("<%= Common.PAGE_COORD_HOME %> #frameBodyWrapper");
			$('#coordAddCoursePage').load("<%= Common.PAGE_COORD_COURSE %> #frameBodyWrapper");
			$('#coordEnrollPage').load("<%= Common.PAGE_COORD_COURSE_ENROLL %>?courseid=CS1101 #frameBodyWrapper");
			$('#coordCourseDetailsPage').load("<%= Common.PAGE_COORD_COURSE_DETAILS %>?courseid=CS2104 #frameBodyWrapper");
			$('#coordCourseStudentDetailsPage').load("<%= Common.PAGE_COORD_COURSE_STUDENT_DETAILS %>?courseid=CS2104&studentemail=alice.b.tmms%40gmail.com #frameBodyWrapper");
			$('#coordCourseStudentEditPage').load("<%= Common.PAGE_COORD_COURSE_STUDENT_EDIT %>?courseid=CS1101&studentemail=benny.c.tmms%40gmail.com #frameBodyWrapper");
			$('#coordCourseEvalPage').load("<%= Common.PAGE_COORD_EVAL %> #frameBodyWrapper");
			$('#coordCourseEvalEditPage').load("<%= Common.PAGE_COORD_EVAL_EDIT %>?courseid=CS1101&evaluationname=Third+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage1').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage2').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			$('#coordCourseEvalResultsPage2').find('#coordinatorEvaluationSummaryTable').hide();
			$('#coordCourseEvalResultsPage2').find('#coordinatorEvaluationDetailedReviewerTable').show();
			$('#coordCourseEvalResultsPage3').load("<%= Common.PAGE_COORD_EVAL_RESULTS %>?courseid=CS2104&evaluationname=First+Eval #frameBodyWrapper");
			
			$('#studentHomePage').load("<%= Common.PAGE_STUDENT_HOME %> #frameBodyWrapper");
			
		});
	</script>
</html>