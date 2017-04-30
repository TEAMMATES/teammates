<%@ page import="teammates.common.util.FrontEndLibrary" %>
<!DOCTYPE html>
<html>
<head>
    <title>QUnit Testing Result</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_CSS %>" type="text/css" media="screen">
    <link rel="stylesheet" href="<%= FrontEndLibrary.BOOTSTRAP_THEME_CSS %>" type="text/css" media="screen">
    <link rel="stylesheet" href="<%= FrontEndLibrary.QUNIT_CSS %>" type="text/css" media="screen">
</head>
<body>
    <div id="qunit"></div>
    <div id="qunit-fixture"></div>
    <div id="blanket-main"></div>
    <hr><hr><hr>
    <h3>Elements required for Testing</h3>
    <span id= "submissionsNumber" class="submissionsNumber"></span>
    Any HTML elements required for the above tests are located here. <br><br>

    <input id="team_all" type="checkbox" checked="">
    <button id="test-bootbox-button"></button>
    <div id="test-bootbox-modal-stub"></div>
    <div id="visible">Visible</div>
    
    <!-- Library scripts -->
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTBOX %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.QUNIT %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BLANKET %>"></script>

    <!-- This contains helper functions for test -->
    <script type="text/javascript" src="/dev/CommonTestFunctions.js"></script>

    <!-- Own scripts; they should be equipped with data-cover attribute -->
    <script type="text/javascript" src="/js/const.js" data-cover></script>
    <script type="text/javascript" src="/js/common.js" data-cover></script>
    <script type="text/javascript" src="/js/richTextEditor.js" data-cover></script>
    <script type="text/javascript" src="/js/adminHome.js" data-cover></script>
    <script type="text/javascript" src="/js/index.js" data-cover></script>
    <script type="text/javascript" src="/js/instructor.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorCourses.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorCourseEnrollPage.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorCourseDetails.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbacks.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionConstSum.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionContrib.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionMcq.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionMsq.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionNumScale.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionRank.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/questionRubric.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/visibilityOptions.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorFeedbackEdit/feedbackPath.js" data-cover></script>
    <script type="text/javascript" src="/js/instructorStudentList.js" data-cover></script>
    <script type="text/javascript" src="/js/student.js" data-cover></script>

    <div id="test-scripts">
        <script type="text/javascript" src="/dev/CommonJsTest.js"></script>
        <script type="text/javascript" src="/dev/AdminHomeJsTest.js"></script>
        <script type="text/javascript" src="/dev/InstructorCourseDetailsJsTest.js"></script>
        <script type="text/javascript" src="/dev/InstructorEnrolmentJsTest.js"></script>
        <script type="text/javascript" src="/dev/InstructorFeedbacksJsTest.js"></script>
        <script type="text/javascript" src="/dev/InstructorJsTest.js"></script>
        <script type="text/javascript" src="/dev/StudentJsTest.js"></script>
        <script type="text/javascript" src="/dev/SubmissionCountJsTest.js"></script>
    </div>
</body>
</html>
