<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="gs">Getting Started</h2>
<div id="contentHolder">
  <div class="panel-group">
    <div class="panel panel-default" id="start-create-course">
      <div class="panel-heading">
        <h4 class="panel-title">
          <a data-toggle="collapse" href="#start-create-course-body">1. Set up a course</a>
        </h4>
      </div>
      <div id="start-create-course-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            A course is how TEAMMATES organises feedback sessions. Each course contains instructors, students and sessions specific to the course.
          </p>
          <ol>
            <li>
              <b>Create a course</b><br>
              From the <b>Home</b> page, click <button class="btn btn-primary btn-s">Add New Course</button>.<br>
              Fill out the following form. Hover your mouse over text to reveal tooltips which tell you what the element does.
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
            </li>
            <li>
              <b><a class="collapse-link" data-target="#course-add-students-body" href="#course-add-students">Enroll students in the course</a></b><br>
              Go to the <b>Courses</b> page and click the <button class="btn btn-default btn-xs">Enroll</button> button of the corresponding course.<br>
              Students can be enrolled into teams (e.g. project groups) and sections (e.g. tutorial classes, lecture groups) to facilitate giving feedback in and among these smaller groups.
            </li>
            <li>
              <b><a class="collapse-link" data-target="#course-add-instructor-body" href="#course-add-instructor">Add instructors to the course</a></b><br>
                From the <b>Courses</b> page, click the <button class="btn btn-default btn-xs" type="button">Edit</button> button of the course you would like to add instructors to. You will be directed to the <b>Edit Course</b> page where you can add a new instructor to your course.
              You can specify the <a class="collapse-link" data-target="#course-instructor-access-body" href="#course-instructor-access">access level</a> of any instructor you add to a course. For more information about how to add an instructor to your course, click <a class="collapse-link" data-target="#course-add-instructor-body" href="#course-add-instructor">here</a>.
            </li>
          </ol>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="start-create-session">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#start-create-session-body">2. Create a session</a>
        </h3>
      </div>
      <div id="start-create-session-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            A feedback session is a course-specific feedback questionnaire. Design the questions you want answered in your feedback session, then wait for the session to open! Each feedback session will be open to responses between the opening and closing times you specify.
          </p>
          <ol>
            <li>
              <b>Create a session</b><br>
              Go to the <b>Sessions</b> page and create a session. Choose between:
            </li>
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
            <li>
              <b><a class="collapse-link" data-target="#session-questions-body" href="#session-questions">Add questions</a> to your session to suit your needs.</b><br>
              For each question, you can set the following:
            </li>
            <ul>
              <li>
                Question type: the style of question being asked. Choose from our 10 different <a href="#questions">question types</a>.
              </li>
              <li>
                Question feedback path: the feedback giver and feedback recipient
              </li>
              <li>
                Response visibility options: who can see the answers, giver name and recipient of a response.
              </li>
            </ul>
            <li>
              <b>Preview your session</b><br>
              After you have finished setting up your session, <a class="collapse-link" data-target="#session-preview-body" href="#session-preview">preview the session</a> as a student or another instructor.
            </li>
          </ol>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="start-session-invitations">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#start-session-invitations-body">3. Wait for your session to open</a>
        </h3>
      </div>
      <div id="start-session-invitations-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            After you have set up your session, you're all set! <b>You do not have to inform students when a session opens.</b>
          </p>
          <p>
            TEAMMATES automatically sends emails to students and instructors according to the preferences you specify when you create a session. The default settings are:
          </p>
          <ul>
            <li>
              When a session opens, TEAMMATES will automatically email students instructions to access the session. A copy of that email will be sent to you.
            </li>
            <li>
              24 hours before the closing time of a session, students will be sent a reminder to complete their responses.
            </li>
            <li>
              When the results of a session are published, students will be sent instructions to access the results.
            </li>
          </ul>
          <p>
            You can manually send reminders to students at any time while a session is open. Click the <button class="btn btn-xs btn-default">Remind</button> button of the session from the <b>Home</b> or <b>Sessions</b> page.
          </p>
          <p>
            In addition, if you would like students to access TEAMMATES sooner (e.g. if you would like them to fill in their profile page in advance), click the <button class="btn btn-xs btn-default">View</button> button of the course and click <button class="btn btn-s btn-primary"><span class="glyphicon glyphicon-envelope"></span> Remind Students to Join</button> button, which will send them instructions to access TEAMMATES immediately.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="start-view">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#start-view-body">4. View and publish session results</a>
        </h3>
      </div>
      <div id="start-view-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            After a session has opened, you may:
          </p>
          <ul>
            <li>
              <a class="collapse-link" data-target="#session-view-results-body" href="#session-view-results">View responses</a>: see what respondents have answered, even if the session is still ongoing. Go to the <b>Sessions</b> page and click the corresponding <button class="btn btn-default btn-xs">Results</button> button.
            </li>
            <li>
              Moderate responses: edit inappropriate responses from respondents before publishing the responses.
            </li>
            <li>
              <a class="collapse-link" data-target="#session-add-comments-body" href="#session-add-comments">Add comments to responses</a>: reply to respondents' answers, or add your own notes on a response. You can make your comment visible to other instructors, the response giver, and/or the response giver's team.
            </li>
            <li>
              Remind students to submit responses: TEAMMATES automatically sends reminders to students; however, you can also manually send reminder emails to students at any time while a session is open. Click the <button class="btn btn-xs btn-default">Remind</button> button of the session from the <b>Home</b> or <b>Sessions</b> page.
            </li>
            <li>
              <a class="collapse-link" data-target="#session-cannot-submit-body" href="#session-cannot-submit">Submit responses for students</a>: if a student has missed the closing time of the session, or is unable to submit the evaluation due to technical problems, you can submit the student's responses on his/her behalf.
            </li>
          </ul>
          <p>
            After a session closes, you may:
          </p>
          <ul>
            <li>
              Publish results: make a session's results visible to students. Click the <button class="btn btn-xs btn-default">Results</button><button class="btn btn-xs btn-default"><span class="caret"></span></button> dropdown on the <b>Sessions</b> page, then select <b>Publish Results</b>. Students will not be able to view the session's results until you publish them.
            </li>
            <li>
              Download results of a session in spreadsheet format: first view the results of a session, then click <button class="btn btn-xs btn-primary">Download Results</button> to download the results of a session as a CSV file.
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="start-actions">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#start-actions-body">5. Learn about other actions you can perform</a>
        </h3>
      </div>
      <div id="start-actions-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Beyond allowing you to collect and disseminate feedback, TEAMMATES can be a useful repository of student and course information. You may use TEAMMATES to:
          </p>
          <ul>
            <li>
              <a class="collapse-link" data-target="#student-view-profile-body" href="#student-view-profile">View a student's profile</a> and <a class="collapse-link" data-target="#student-view-responses-body" href="#student-view-responses">all past records of a student</a>: view the profile that any enrolled student has written for him/herself, and see in one place all submissions given/received by a student. Handy for examining how a student progressed through a course.
            </li>
            <li>
              <a class="collapse-link" data-target="#student-edit-profile-body" href="#student-edit-profile">Edit a student's data</a>: change a student's registered name, section or team name, or email address. You can also note down comments on students, for example to inform other instructors of information about a student that they should take note of.
            </li>
            <li>
              <a class="collapse-link" data-target="#student-email-body" href="#student-email">Email a group of students</a>: contact students regarding their feedback responses, or the course in general. Also handy for locating the email address of past students.
            </li>
            <li>
              Search: <a class="collapse-link" data-target="#student-search-body" href="#student-search">search for students, teams or sections</a>, or <a class="collapse-link" data-target="#session-search-body" href="#session-search">search for questions, responses or comments</a>.
            </li>
            <li>
              <a class="collapse-link" data-target="#course-archive-body" href="#course-archive">Archive old courses</a>: archive old courses that you no longer need actively.
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="start-help">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#start-help-body">6. Contact us</a>
        </h3>
      </div>
      <div id="start-help-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            If you have doubts, comments, or questions about using TEAMMATES, just
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
