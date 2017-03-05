<%@ page import="teammates.common.util.FrontEndLibrary" %>
<!DOCTYPE html>
<html>
<head>
    <title>QUnit Testing Result</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
    <table class="inputTable" id="searchTable">
        <tbody>
            <tr>
                <td><input id="searchbox"></td>
                <td><input class="button" id="button_search"
                    onclick="return applyFilters();" tabindex="2" type="submit"
                    value="Search" /></td>
            </tr>
        </tbody>
    </table>
    <br>
    <br>
    <div id="moreOptionsDiv" class="well well-plain">
        <form class="form-horizontal" role="form">
            <div class="row">
                <div class="col-sm-4">
                    <div class="text-color-primary">
                        <strong>Courses</strong>
                    </div>
                    <br>
                    <div class="checkbox">
                        <input type="checkbox" value="" id="course_all" checked=""> 
                        <label for="course_all"><strong>Select all</strong></label>
                    </div>
                    <br>
                    <div class="checkbox"><input id="course_check-0" type="checkbox" checked="">
                        <label for="course_check-0"> Name of Course 2 
                        
                        </label>
                    </div>
                    <div class="checkbox"><input id="course_check-1" type="checkbox" checked="">
                        <label for="course_check-1"> Name of Course 3 
                        
                        </label>
                    </div>    
                </div>

                <div class="col-sm-4">
                    <div class="text-color-primary">
                        <strong>Sections</strong>
                    </div>
                    <br>
                    <div class="checkbox">
                        <input type="checkbox" value="" id="section_all" checked=""> 
                        <label for="course_all"><strong>Select all</strong></label>
                    </div>
                    <br>
                    <div class="checkbox">
                        <input id="section_check-0-0" type="checkbox" checked="" class="section_check">
                        <label for="section_check-0-0"> [course2] : Name of Section A
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="section_check-0-1" type="checkbox" checked="" class="section_check">
                        <label for="section_check-0-1"> [course2] : Name of Section B
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="section_check-1-0" type="checkbox" checked="" class="section_check">
                        <label for="section_check-1-0"> [course3] : Name of Section C
                        </label>
                    </div>    
                </div>

                <div class="col-sm-4">
                    <div class="text-color-primary">
                        <strong>Teams</strong>
                    </div>
                    <br>
                    <div class="checkbox">
                        <input id="team_all" type="checkbox" checked="">
                        <label for="team_all"><strong>Select All</strong></label>
                    </div>
                    <br>
                    <div class="checkbox">
                        <input id="team_check-0-0-0" type="checkbox" checked="" class="team_check">
                        <label for="team_check-0-0-0">  [course2-sectionA] : Team 1 
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="team_check-0-1-0" type="checkbox" checked="" class="team_check">
                        <label for="team_check-0-1-0">  [course2-sectionB] : Team 2 
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="team_check-0-1-1" type="checkbox" checked="" class="team_check">
                        <label for="team_check-0-1-1">  [course2-sectionB] : Team 3 
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="team_check-0-10-0" type="checkbox" class="team_check">
                        <label for="team_check-0-10-0">  [course2-invalid section] : Team with invalid section 
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="team_check-1-0-0" type="checkbox" checked="" class="team_check">
                        <label for="team_check-1-0-0">  [course3-sectionC] : Team 1 
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="team_check-1-0-1" type="checkbox" checked="" class="team_check">
                        <label for="team_check-1-0-1">  [course3-sectionC] : Team 2 
                        </label>
                    </div>
                    <div class="checkbox">
                        <input id="team_check-1-0-2" type="checkbox" checked="" class="team_check">
                        <label for="team_check-1-0-2">  [course3-sectionC] : Team 3 
                        </label>
                    </div>
                        
                </div>
                <div class="col-sm-4">
                    <div class="text-color-primary">
                        <strong>Emails</strong>
                    </div>
                    <br>
                    <div class="checkbox">
                        <input id="show_email" type="checkbox" checked="">
                            <label for="show_email"><strong>Show Emails</strong></label>
                    </div>
                    <br>
                    <div id="emails">
                        <div id="student_email-c0.0">student1@email.com</div>
                        <div id="student_email-c0.1">student2@email.com</div>
                        <div id="student_email-c0.2">benny@email.com</div>
                        <div id="student_email-c1.0">benny@email.com</div>
                        <div id="student_email-c1.1">student3@email.com</div>
                        <div id="student_email-c1.2">student4@email.com</div>
                        <div id="student_email-c1.3">student5@email.com</div>
                        <div id="student_email-c1.4">student6@email.com</div>
                        <div id="student_email-c1.5">student7@email.com</div>
                        <div id="student_email-c1.6">student8@email.com</div>
                        <div id="student_email-c1.7">student9@email.com</div>
                        <div id="student_email-c1.8">student10@email.com</div>
                        <div id="student_email-c1.9">student11@email.com</div>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <div class="well well-plain" id="course-0">
        <div class="row">
            <div class="col-md-10 text-color-primary">
                <h4>
                    <strong>
                        [course2] : Name of Course 2
                    </strong>
                </h4>
            </div>
            <div class="table table-responsive table-striped table-bordered">
                <a class="btn btn-default btn-xs pull-right pull-down course-enroll-for-test"
                    href=""
                    title=""
                    data-toggle="tooltip"
                    data-placement="top">
                        <span class="glyphicon glyphicon-list"></span> Enroll
                </a>
            </div>
        </div>
        <table class="table table-responsive table-striped table-bordered">
            <thead>
                <tr>
                    <th> Section </th>
                    <th> Team</th>
                    <th > Student Name</th>
                    <th > Email</th>
                </tr>
            </thead>
            <tbody>
                <tr id="student-c0.0" style="display: table-row;">
                    <td id="studentsection-c0.0"> Section A </td>
                    <td id="studentteam-c0.0.0">Team 1</td>
                    <td id="studentname-c0.0" >Alice Betsy</td>
                    <td id="studentemail-c0.0">student1@email.com</td>
                </tr>
                <tr id="student-c0.1" style="display: table-row;">
                    <td id="studentsection-c0.1"> Section B </td>
                    <td id="studentteam-c0.1.0">Team 2</td>
                    <td id="studentname-c0.1" >Hugh Ivanov</td>
                    <td id="studentemail-c0.1">student2@email.com</td>
                </tr>
                <tr id="student-c0.2" style="display: table-row;">
                    <td id="studentsection-c0.1"> Section B </td>
                    <td id="studentteam-c0.1.1">Team 3</td>
                    <td id="studentname-c0.2" >Benny Charles</td>
                    <td id="studentemail-c0.2">benny@email.com</td>
                </tr>
            </tbody>
        </table>
    </div>

    <div class="well well-plain" id="course-1">
        <div class="row">
            <div class="col-md-10 text-color-primary">
                <h4>
                    <strong>
                        [course3] : Name of Course 3
                    </strong>
                </h4>
            </div>
            <div class="table table-responsive table-striped table-bordered">
                <a class="btn btn-default btn-xs pull-right pull-down course-enroll-for-test"
                    href=""
                    title=""
                    data-toggle="tooltip"
                    data-placement="top">
                        <span class="glyphicon glyphicon-list"></span> Enroll
                </a>
            </div>
        </div>
        <table class="table table-responsive table-striped table-bordered" >
            <thead>
                <tr>
                    <th> Section</th>
                    <th> Team</th>
                    <th > Student Name</th>
                    <th > Email</th>
                </tr>
            </thead>
            <tbody>
                <tr id="student-c1.0" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.0">Team 1</td>
                    <td id="studentname-c1.0" >Benny Charles</td>
                    <td id="studentemail-c1.0">benny@email.com</td>
                </tr>
                <tr id="student-c1.1" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.0">Team 1</td>
                    <td id="studentname-c1.0" >Carlos Santanna</td>
                    <td id="studentemail-c1.1">student3@email.com</td>
                </tr>
                <tr id="student-c1.2" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.0">Team 1</td>
                    <td id="studentname-c1.2" >Charlie D</td>
                    <td id="studentemail-c1.2">student4@email.com</td>
                </tr>
                <tr id="student-c1.3" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.1">Team 2</td>
                    <td id="studentname-c1.3" >Denny CharlÃ©s</td>
                    <td id="studentemail-c1.3">student5@email.com</td>
                </tr>
                <tr id="student-c1.4" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.1">Team 2</td>
                    <td id="studentname-c1.4" >Emma F</td>
                    <td id="studentemail-c1.4">student6@email.com</td>
                </tr>
                <tr id="student-c1.5" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.1">Team 2</td>
                    <td id="studentname-c1.5" >Frank Gatsby</td>
                    <td id="studentemail-c1.5">student7@email.com</td>
                </tr>
                <tr id="student-c1.6" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.2">Team 3</td>
                    <td id="studentname-c1.6" >Gabriel Hobb</td>
                    <td id="studentemail-c1.6">student8@email.com</td>
                </tr>
                <tr id="student-c1.7" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.2">Team 3</td>
                    <td id="studentname-c1.7" >Hans Iker</td>
                    <td id="studentemail-c1.7">student9@email.com</td>
                </tr>
                <tr id="student-c1.8" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.2">Team 3</td>
                    <td id="studentname-c1.8" >Ian Jacobsson</td>
                    <td id="studentemail-c1.8">student10@email.com</td>
                </tr>
                <tr id="student-c1.9" style="display: table-row;">
                    <td id="studentsection-c1.0"> Section C </td>
                    <td id="studentteam-c1.0.2">Team 3</td>
                    <td id="studentname-c1.9" >James K</td>
                    <td id="studentemail-c1.9">student11@email.com</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div id="visible">Visible</div>
    <table class="table table-bordered table-striped">
        <thead class="fill-primary">
            <tr id="resultsHeader-0">
                <th id="button_sortsection-0" class="button-sort-none" onclick="toggleSort(this)">
                    Section <span class="icon-sort unsorted"></span>
                </th>
                <th id="button_sortteam-0" class="button-sort-none" onclick="toggleSort(this)">
                    Team <span class="icon-sort unsorted"></span>
                </th>
                <th id="button_sortstudentname-0" class="button-sort-none" onclick="toggleSort(this)">
                    Student Name <span class="icon-sort unsorted"></span>
                </th>
                <th id="button_sortstudentstatus" class="button-sort-none" onclick="toggleSort(this)">
                    Status <span class="icon-sort unsorted"></span>
                </th>
                <th id="button_sortemail-0" class="button-sort-none" onclick="toggleSort(this)">
                    Email <span class="icon-sort unsorted"></span>
                </th>
                <th>Action(s)</th>
            </tr>
        </thead>
        <tbody>
            <tr class="student_row" id="student-c0.8">
                <td id="studentsection-c0.1">
                    Tutorial Group 2
                </td>
                <td id="studentteam-c0.1.2">
                    Team 3
                </td>
                <td id="studentname-c0.8">
                    Hugh Ivanov
                </td>
                <td class="align-center">
                    Yet to join
                </td>
                <td id="studentemail-c0.8">
                    hugh.i.tmms@gmail.tmt
                </td>
                <td class="no-print align-center">
                    <a class="course-student-remind-link btn btn-default btn-xs"
                    href="/page/instructorCourseRemind?courseid=teammates.instructor.uni-demo&studentemail=hugh.i.tmms%40gmail.tmt&user=teammates.instructor%40university.edu" data-toggle="tooltip"
                            data-placement="top">
                            Send Invite
                        </a>
                </td>
            </tr>
        </tbody>    
    </table>
    <script type="text/javascript" src="<%= FrontEndLibrary.QUNIT %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BLANKET %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.JQUERY %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.TINYMCE %>"></script>
    <script type="text/javascript" src="<%= FrontEndLibrary.BOOTBOX %>"></script>
    <script type="text/javascript" src="/js/richTextEditor.js" data-cover></script>
    <script type="text/javascript" src="/js/adminHome.js" data-cover></script>
    <script type="text/javascript" src="/js/common.js" data-cover></script>
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
    <script type="text/javascript" src="/js/instructor.js" data-cover></script>
    <script type="text/javascript" src="/js/student.js" data-cover></script>
    <script type="text/javascript" src="/js/index.js" data-cover></script>

    <script type="text/javascript" src="/dev/CommonTestFunctions.js"></script>
    <!-- Test scripts -->
    <script type="text/javascript" src="/dev/CommonJsTest.js"></script>
    <script type="text/javascript" src="/dev/AdminHomeJsTest.js"></script>
    <script type="text/javascript" src="/dev/InstructorCourseDetailsJsTest.js"></script>
    <script type="text/javascript" src="/dev/InstructorEnrolmentJsTest.js"></script>
    <script type="text/javascript" src="/dev/InstructorFeedbacksJsTest.js"></script>
    <script type="text/javascript" src="/dev/InstructorStudentListJsTest.js"></script>
    <script type="text/javascript" src="/dev/InstructorJsTest.js"></script>
    <script type="text/javascript" src="/dev/StudentJsTest.js"></script>
    <script type="text/javascript" src="/dev/SubmissionCountJsTest.js"></script>
</body>
</html>
