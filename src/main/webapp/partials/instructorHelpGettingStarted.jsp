<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="gs">Getting Started</h2>
<div id="contentHolder">
  <div class="panel-group">
    <div class="panel panel-default" id="startCreateCourse">
      <div class="panel-heading">
        <h4 class="panel-title">
          <a data-toggle="collapse" href="#startCreateCourseBody">How do I create a course?</a>
        </h4>
      </div>
      <div id="startCreateCourseBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            From the <b>Home</b> page, click <button class="btn btn-primary btn-s">Add New Course</button>.<br>
            From any other page, navigate to the <b>Courses</b> page using the top navigation bar.<br>
            Fill out the following form. Hover your mouse over text to reveal tooltips which tell you what the element does.
          </p>
          <div class="bs-example">
          <div class="panel panel-primary">
            <div class="panel-body fill-plain">
              <form class="form form-horizontal">
                <div class="form-group">
                  <label class="col-sm-3 control-label">
                    Course ID:
                  </label>
                  <div class="col-sm-3">
                    <input class="form-control" type="text" value="" data-toggle="tooltip" data-placement="top" maxlength="40" tabindex="1" placeholder="e.g. CS3215-2013Semester1" title="Enter the identifier of the course, e.g.CS3215-2013Semester1.">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-3 control-label">
                    Course Name:
                  </label>
                  <div class="col-sm-9">
                    <input class="form-control" type="text" value="" data-toggle="tooltip" data-placement="top" maxlength="64" tabindex="2" placeholder="e.g. Software Engineering" title="Enter the name of the course, e.g. Software Engineering.">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-3 control-label">Time Zone:</label>
                  <div class="col-sm-9">
                    <div class="input-group">
                      <select class="form-control" title="" data-toggle="tooltip" data-placement="top" data-original-title="The time zone for the course. This is auto-detected based on your device settings.">
                        <option>UTC</option>
                        <option>Other options omitted...</option>
                      </select>
                      <span class="input-group-btn">
                      <input type="button" class="btn btn-primary" value="Auto-Detect">
                    </span>
                    </div>
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-sm-offset-3 col-sm-9">
                    <input type="button" class="btn btn-primary" value="Add Course" tabindex="3">
                  </div>
                </div>
              </form>
            </div>
          </div>
        </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="startEnrollStudents">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#startEnrollStudentsBody">How do I enroll students in a course?</a>
        </h3>
      </div>
      <div id="startEnrollStudentsBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Enroll students by doing one of the following:
          </p>
          <ul>
            <li>
              Go to the <b>Home</b> page, click on the <button class="btn btn-primary btn-xs">Students <span class="caret dropdown-toggle"></span></button> button on the corresponding course, and choose <b>View / Enroll</b>
            </li>
            <li>
              Go to the <b>Courses</b> page and click the <button class="btn btn-default btn-xs">Enroll</button> button of the corresponding course.
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="startAddInstructors">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#startAddInstructorsBody">How do I add instructors to a course?</a>
        </h3>
      </div>
      <div id="startAddInstructorsBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            From your <b>Home</b> or <b>Courses</b> page, click the <button class="btn btn-default btn-xs" type="button">Edit</button> button of the course you would like to edit. You will be directed to the <b>Edit Course</b> page, which will look similar to the example below.<br>
            Here, you can add new instructors to the course, edit existing instructors' details, and delete instructors from the course, depending on your access privileges.
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <strong>Instructor 3:</strong>
                <div class="pull-right">

                  <a href="javascript:;" id="instrEditLink3" class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" title="Edit instructor details" disabled="">
                    <span class="glyphicon glyphicon-pencil"></span> Edit
                  </a>
                  <a href="javascript:;" id="instrDeleteLink3" class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" title="Delete the instructor from the course" disabled="">
                    <span class="glyphicon glyphicon-trash"></span> Delete
                  </a>
                </div>
              </div>

              <div class="panel-body">
                <form method="post" action="#" id="formEditInstructor3" name="formEditInstructors" class="form form-horizontal">
                  <input type="hidden" name="courseid" value="testCourse">

                  <input type="hidden" name="instructorid" value="sampleInstr">

                  <input type="hidden" name="user" value="sampleInstr">

                  <div id="instructorTable3">

                    <div class="form-group">
                      <label class="col-sm-3 control-label">Google ID:</label>
                      <div class="col-sm-9">
                        <input class="form-control immutable" type="text" id="instructorid3" value="sampleInstr" maxlength="45" tabindex="3" disabled="">
                      </div>
                    </div>

                    <div class="form-group">
                      <label class="col-sm-3 control-label">Name:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructorname" id="instructorname3" value="sampleInstr" data-toggle="tooltip" data-placement="top" maxlength="100" tabindex="4" disabled="" title="Enter the name of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Email:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructoremail" id="instructoremail3" value="sampleInstr@google.com" data-toggle="tooltip" data-placement="top" maxlength="45" tabindex="5" disabled="" title="Enter the Email of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">
                        <input type="checkbox" name="instructorisdisplayed" value="true" data-toggle="tooltip" data-placement="top" disabled="" title="If this is unselected, the instructor will be completely invisible to students. E.g. to give access to a colleague for ‘auditing’ your course"> Display to students as:
                      </label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructordisplayname" placeholder="E.g.Co-lecturer, Teaching Assistant" value="Instructor" data-toggle="tooltip" data-placement="top" disabled="" title="Specify the role of this instructor in this course as shown to the students">
                      </div>
                    </div>
                    <div id="accessControlInfoForInstr3">
                      <div class="form-group">
                        <label class="col-sm-3 control-label">Access Level:</label>
                        <div class="col-sm-9">
                          <p class="form-control-static">
                            <span>Co-owner</span>

                            <a href="javascript:;">
                              &nbsp;View Details
                            </a>

                          </p>
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="align-center">
                        <input id="btnSaveInstructor3" type="button" class="btn btn-primary" style="display:none;" value="Save changes" tabindex="6">
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            <br>
            <br>
            <div class="align-center">
              <input id="btnShowNewInstructorForm" class="btn btn-primary" value="Add New Instructor" disabled="">
            </div>
          </div>
          <p>
            To add an instructor:
          </p>
          <ol>
            <li>
              Click the <button class="btn btn-primary btn-s" type="button">Add New Instructor</button> button at the bottom of the page. A form will appear for you to specify the necessary information about the new instructor.
            </li>
            <li>
              Fill in the name, email, role, and access level of the instructor you want to add. If you are not clear about certain input field, hover your cursor over the input field to view the tooltip for explanation of the field.<br>
            </li>
            <li>
              Click <button class="btn btn-primary btn-s" type="button">Add Instructor</button> to add the instructor.
            </li>
          </ol>
          <div class="bs-example">
            <div class="panel panel-primary" id="panelAddInstructor" style="">
              <div class="panel-heading">
                <strong>Instructor 2:</strong>
              </div>

              <div class="panel-body fill-plain">
                <form class="form form-horizontal">
                  <input type="hidden" name="courseid" value="testCourse2">
                  <input type="hidden" name="user" value="sampleInstr">

                  <div id="instructorAddTable">
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Name:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructorname" id="instructorname" data-toggle="tooltip" data-placement="top" maxlength="100" tabindex="8/" title="Enter the name of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Email:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructoremail" id="instructoremail" data-toggle="tooltip" data-placement="top" maxlength="45" tabindex="9/" title="Enter the Email of the instructor.">
                      </div>
                    </div>
                    <div id="accessControlEditDivForInstr2">
                      <div class="form-group">
                        <label class="col-sm-3 control-label">
                          <input type="checkbox" name="instructorisdisplayed" value="true" data-toggle="tooltip" data-placement="top" title="If this is unselected, the instructor will be completely invisible to students. E.g. to give access to a colleague for ‘auditing’ your course"> Display to students as:
                        </label>
                        <div class="col-sm-9">
                          <input class="form-control" type="text" name="instructordisplayname" placeholder="E.g.Co-lecturer, Teaching Assistant" data-toggle="tooltip" data-placement="top" title="Specify the role of this instructor in this course as shown to the students">
                        </div>
                      </div>
                      <div class="form-group">
                        <div class="col-sm-3">
                          <label class="control-label pull-right">Access-level</label>
                        </div>
                        <div class="col-sm-9">
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Co-owner" checked="">&nbsp;Co-owner: Can do everything
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Manager">&nbsp;Manager: Can do everything except for deleting the course
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Observer">&nbsp;Observer: Can only view information(students, submissions, comments etc.). &nbsp;Cannot edit/delete/submit anything.
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Tutor">&nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Custom">&nbsp;Custom: No access by default. Any access needs to be granted explicitly.
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="align-center">
                        <input id="btnAddInstructor" type="button" class="btn btn-primary" value="Add Instructor" tabindex="10">
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="startCreateSession">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#startCreateSessionBody">How do I create a session?</a>
        </h3>
      </div>
      <div id="startCreateSessionBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Go to the <b>Sessions</b> page and create a session. Choose between the following session types:
          </p>
          <ul>
            <li>Session with my own questions</li>
              <ul>
                <li>Creates an empty feedback session</li>
                <li>Allows you to craft custom questions that fit your needs</li>
              </ul>
            <li>Session using template: team peer evaluation</li>
            <ul>
              <li>Provides 5 standard questions for team peer evaluations</li>
              <li> Allows you to modify/remove the given questions and add your own questions as required</li>
            </ul>
          </ul>
          <div class="bs-example">
            <div class="well well-plain">
              <div class="row" data-toggle="tooltip" data-placement="top" title="Select a different type of session here.">
                <h4 class="label-control col-md-2 text-md">Create new </h4>
                <div class="col-md-5">
                  <div class="col-xs-10 tablet-no-padding" title="" data-toggle="tooltip" data-placement="top" data-original-title="Select a session type here.">
                    <select class="form-control" name="fstype" id="fstype">
                      <option value="STANDARD" selected="">
                        session with my own questions
                      </option>
                      <option value="TEAMEVALUATION" selected>
                        session using template: team peer evaluation
                      </option>
                    </select>
                  </div>
                  <div class="col-xs-1">
                    <h5>
                      <a href="/instructorHelp.jsp#fbSetupSession" target="_blank" rel="noopener noreferrer">
                        <span class="glyphicon glyphicon-info-sign"></span>
                      </a>
                    </h5>
                  </div>
                </div>
                <h4 class="label-control col-md-1 text-md">Or: </h4>
                <div class="col-md-3">
                  <a id="button_copy" class="btn btn-info" style="vertical-align:middle;">Copy from previous feedback sessions</a>
                </div>
              </div>
              <br>
              <div class="panel panel-primary">
                <div class="panel-body">
                  <div class="row">
                    <div class="col-sm-12 col-md-6" title="" data-toggle="tooltip" data-placement="top" data-original-title="Please select the course for which the feedback session is to be created.">
                      <div class="form-group">
                        <h5 class="col-sm-2 col-md-4">
                          <label class="control-label" for="courseid">
                            Course ID
                          </label>
                        </h5>
                        <div class="col-sm-10 col-md-8">
                          <select class="form-control" name="courseid" id="courseid">
                            <option value="teammates.instructor.uni-demo">teammates.instructor.uni-demo</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="col-sm-12 col-md-6 tablet-no-mobile-margin-top-20px" title="" data-toggle="tooltip" data-placement="top" data-original-title="You should not need to change this as your timezone is auto-detected. Daylight saving time is supported.">
                      <div class="form-group">
                        <h5 class="col-sm-2 col-md-4">
                          <label class="control-label">
                            Time Zone
                          </label>
                        </h5>
                        <div class="col-sm-10 col-md-8">
                          <div class="input-group">
                            <select class="form-control">
                              <option>UTC</option>
                              <option>Other options omitted...</option>
                            </select>
                            <span class="input-group-btn">
                              <input type="button" class="btn btn-primary" value="Auto-Detect">
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br class="hidden-xs">
                  <div class="row">
                    <div class="col-sm-12" title="" data-toggle="tooltip" data-placement="top" data-original-title="Enter the name of the feedback session e.g. Feedback Session 1.">
                      <div class="form-group">
                        <h5 class="col-sm-2">
                          <label class="control-label" for="fsname">
                            Session name
                          </label>
                        </h5>
                        <div class="col-sm-10">
                          <input class="form-control" type="text" name="fsname" id="fsname" maxlength="38" placeholder="e.g. Feedback for Project Presentation 1" value="">
                        </div>
                      </div>
                    </div>
                  </div>
                  <br class="hidden-xs">
                  <div class="row" id="instructionsRow">
                    <div class="col-sm-12" title="" data-toggle="tooltip" data-placement="top" data-original-title="Enter instructions for this feedback session. e.g. Avoid comments which are too critical.<br> It will be displayed at the top of the page when users respond to the session.">
                      <div class="form-group">
                        <h5 class="col-sm-2 margin-top-0">
                          <label class="control-label" for="instructions">
                            Instructions
                          </label>
                        </h5>
                        <div class="col-sm-10">
                          <div id="richtext-toolbar-container"></div>
                          <div id="instructions" class="panel panel-default panel-body mce-content-body content-editor" contenteditable="true" style="position: relative;" spellcheck="false">
                            <p>Please answer all the given questions.</p>
                          </div>
                          <input type="hidden" name="instructions">
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="panel panel-primary" id="timeFramePanel">
                <div class="panel-body">
                  <div class="row">
                    <div class="col-md-5" title="" data-toggle="tooltip" data-placement="top" data-original-title="Please select the date and time for  which users can start submitting responses for the feedback session.">
                      <div class="row">
                        <div class="col-xs-12">
                          <label class="label-control" for="startdate">
                            Submission opening time
                          </label>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-xs-6">
                          <input class="form-control col-sm-2 hasDatepicker" type="text" name="startdate" id="startdate" value="22/05/2017" placeholder="Date">
                        </div>
                        <div class="col-xs-6">
                          <select class="form-control" name="starttime" id="starttime">
                            <option value="1">0100H</option>
                            <option value="2">0200H</option>
                            <option value="3">0300H</option>
                            <option value="4">0400H</option>
                            <option value="5">0500H</option>
                            <option value="6">0600H</option>
                            <option value="7">0700H</option>
                            <option value="8">0800H</option>
                            <option value="9">0900H</option>
                            <option value="10">1000H</option>
                            <option value="other">Other options omitted...</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="col-md-5 border-left-gray" title="" data-toggle="tooltip" data-placement="top" data-original-title="Please select the date and time after which the feedback session will no longer accept submissions from users.">
                      <div class="row">
                        <div class="col-xs-12">
                          <label class="label-control" for="enddate">
                            Submission closing time
                          </label>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-xs-6">
                          <input class="form-control col-sm-2 hasDatepicker" type="text" name="enddate" id="enddate" value="" placeholder="Date">
                        </div>
                        <div class="col-xs-6">
                          <select class="form-control" name="endtime" id="endtime">
                            <option value="1">0100H</option>
                            <option value="2">0200H</option>
                            <option value="3">0300H</option>
                            <option value="4">0400H</option>
                            <option value="5">0500H</option>
                            <option value="6">0600H</option>
                            <option value="7">0700H</option>
                            <option value="8">0800H</option>
                            <option value="9">0900H</option>
                            <option value="10">1000H</option>
                            <option value="other">Other options omitted...</option>
                          </select>
                        </div>
                      </div>
                    </div>
                    <div class="col-md-2 border-left-gray" title="" data-toggle="tooltip" data-placement="top" data-original-title="Please select the amount of time that the system will continue accepting <br>submissions after the specified deadline.">
                      <div class="row">
                        <div class="col-xs-12">
                          <label class="control-label" for="graceperiod">
                            Grace period
                          </label>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-xs-12">
                          <select class="form-control" name="graceperiod" id="graceperiod">
                            <option value="0">0 mins</option>
                            <option value="5">5 mins</option>
                            <option value="10">10 mins</option>
                            <option selected="" value="15">15 mins</option>
                            <option value="20">20 mins</option>
                            <option value="25">25 mins</option>
                            <option value="30">30 mins</option>
                          </select>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div id="uncommonSettingsSection">
                <div id="uncommonSettingsSessionResponsesVisible" class="margin-bottom-15px text-muted">
                  <span id="uncommonSettingsSessionResponsesVisibleInfoText">Session is visible at submission opening time, responses are only visible when you publish the results.</span>
                  <a class="edit-uncommon-settings-button" id="editUncommonSettingsSessionResponsesVisibleButton" data-edit="[Edit]" data-done="[Done]">[Change]</a>
                </div>
                <div id="uncommonSettingsSendEmails" class="margin-bottom-15px text-muted">
                  <span id="uncommonSettingsSendEmailsInfoText">Emails are sent when session opens (within 15 mins), 24 hrs before session closes and when results are published.</span>
                  <a class="edit-uncommon-settings-button" id="editUncommonSettingsSendEmailsButton" data-edit="[Edit]" data-done="[Done]">[Change]</a>
                </div>
              </div>
              <div class="form-group">
                <div class="row">
                  <div class="col-md-offset-5 col-md-3">
                    <button id="button_submit" type="submit" class="btn btn-primary">
                      Create Feedback Session
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <p>
            Feedback paths specify who is giving feedback to whom. You can set custom feedback paths for each question by setting the <b>feedback giver</b> and <b>feedback recipient</b> of each question.
          </p>
          <p>
            You can also change the visibility options for each question, which defines who can see the answers, giver name and recipient of a response.<br>
            Click <a class="collapse-link" data-target="#fbSetupSessionBody" href="#fbSetupSession">here</a> for more detailed information about how to create a session.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="startSessionInvitations">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#startSessionInvitationsBody">Do I have to remind students to respond to a session?</a>
        </h3>
      </div>
      <div id="startSessionInvitationsBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            No. TEAMMATES sends automatic reminder emails according to your course preferences.
          </p>
          <p>
            When a session is about to open (at the 'opening time' you specified), TEAMMATES automatically emails students instructions to access the session. A copy of that email will be sent to you as well.
          </p>
          <p>
            If you would like students to access TEAMMATES sooner (e.g. if you would like them to fill in their profile page in advance), you can go to the 'View' link of the course and click the 'Remind Students to Join' button, which will send them 'access instructions' immediately.
          <p>
            Students will be sent a reminder 24 hours before the closing time of a session. In addition, you can send further reminders to students any time while a session is open using the ‘remind’ link.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="startInstructorActions">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#startInstructorActionsBody">What actions can I perform?</a>
        </h3>
      </div>
      <div id="startInstructorActionsBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            At any time, you may:
          </p>
          <ul>
            <li>
              Email a group of students: filter out students in certain teams/courses and email them. Also handy for locating the email of any past student.</li>
            <li>
              <a class="collapse-link" data-target="#editStudentProfileBody" href="#editStudentProfile">Comment on a student's profile</a>: add a comments about any student. Handy for saving and retrieving comments about a student quickly. You can make these comments visible to others or keep them private.</li>
            <li>
              <a class="collapse-link" data-target="#fbSetupSessionBody" href="#fbSetupSession">View profile and all past records of a student</a>: view profile of a student and see in one place all submissions given/received by a student. Handy for examining how a student progressed through a course. (Students &gt; All Records)
            </li>
            <li>
              Search: <a class="collapse-link" data-target="#searchsearchStudentsBody" href="#searchStudents">search for students, teams or sections</a>, or <a class="collapse-link" data-target="#searchFeedbackSessionDataBody" href="#searchFeedbackSessionData">search for questions, responses or comments</a>.
            </li>
            <li>
              <a class="collapse-link" data-target="#archivingCourseBody" href="#archivingCourse">Archive old courses</a>: archive old courses that you no longer need actively.
            </li>
          </ul>
          <p>
            After a session has opened, you may:
          </p>
          <ul>
            <li>
              <a class="collapse-link" data-target="#fbViewResultsBody" href="#fbViewResults">View responses</a>: see what respondents have answered, even if the session is still ongoing. Go to the <b>Sessions</b> page and click the corresponding <button class="btn btn-default btn-xs">Results</button> button.
            </li>
          </ul>
          <p>
            After a session closes, you may:
          </p>
          <ul>
            <li>
              Publish results: make a session's results visible to students using the ‘publish’ link in the ‘Sessions’ page
            </li>
            <li>
              Download results of a session in spreadsheet format
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="startNeedHelp">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#startNeedHelpBody">Who can I contact when I need help?</a>
        </h3>
      </div>
      <div id="startNeedHelpBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            If you have a doubt or need our help, just
            <a href="mailto:teammates@comp.nus.edu.sg">email us</a>.
            We respond within 24 hours.
          </p>
        </div>
      </div>
    </div>
  </div>
  <p align="right">
    <a href="#Top">Back to Top
    </a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
