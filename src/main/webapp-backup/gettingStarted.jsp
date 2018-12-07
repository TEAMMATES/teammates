<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:helpPage>
<h1 id="Top">Getting Started</h1>
<div id="contentHolder">
  <p>
    Welcome to TEAMMATES!
  </p>
  <p>
    To get started using TEAMMATES, follow the following steps, or watch our <a href="https://www.youtube.com/embed/mDtfmNmRwBM?autoplay=1&rel=0" target="_blank"><span class="glyphicon glyphicon-film" aria-hidden="true"></span> Video Tour</a>.<br>
    For more help, browse the answers to some <a href="instructorHelp.jsp">frequently asked questions</a>.
  </p>
  <ol>
    <li><a href="#course-setup">Set up a course</a></li>
    <li><a href="#session-setup">Create a session</a></li>
    <li><a href="#session-invites">Wait for your session to open</a></li>
    <li><a href="#session-results">View and publish session results</a></li>
    <li><a href="#other-actions">Learn about other actions you can perform</a></li>
    <li><a href="#contact-us">Contact us</a></li>
  </ol>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2 id="course-setup">1. Set up a course</h2>
  <div>
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
        <b><a href="/instructorHelp.jsp#course-add-students" target="_blank" rel="noopener noreferrer">Enroll students in the course</a></b><br>
        Go to the <b>Courses</b> page and click the <button class="btn btn-default btn-xs">Enroll</button> button of the corresponding course.<br>
        Students can be enrolled into teams (e.g. project groups) and sections (e.g. tutorial classes, lecture groups) to facilitate giving feedback in and among these smaller groups.<br>
        TEAMMATES will <b>not</b> automatically notify students that they have been enrolled. However, if you would like students to access TEAMMATES sooner (e.g. if you would like them to fill in their profile page in advance), click the <button class="btn btn-xs btn-default">View</button> button of the course in the <b>Courses</b> page. Then, click <button class="btn btn-s btn-primary"><span class="glyphicon glyphicon-envelope"></span> Remind Students to Join</button> button, which will send them instructions to access TEAMMATES immediately.
      </li>
      <li>
        <b><a href="/instructorHelp.jsp#course-add-instructor" target="_blank" rel="noopener noreferrer">Add instructors to the course</a></b><br>
          From the <b>Courses</b> page, click the <button class="btn btn-default btn-xs" type="button">Edit</button> button of the course you would like to add instructors to. You will be directed to the <b>Edit Course</b> page where you can add a new instructor to your course.
        You can specify the <a href="/instructorHelp.jsp#course-instructor-access" target="_blank" rel="noopener noreferrer">access level</a> of any instructor you add to a course. For more information about how to add an instructor to your course, click <a href="/instructorHelp.jsp#course-add-instructor" target="_blank" rel="noopener noreferrer">here</a>.
      </li>
    </ol>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2 id="session-setup">2. Create a session</h2>
  <div>
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
        <b><a href="/instructorHelp.jsp#session-questions" target="_blank" rel="noopener noreferrer">Add questions</a> to your session to suit your needs.</b><br>
        For each question, you can set the following:
      </li>
      <ul>
        <li>
          Question type: the style of question being asked. Choose from our 10 different <a href="/instructorHelp.jsp#questions" target="_blank" rel="noopener noreferrer">question types</a>.
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
        After you have finished setting up your session, <a href="/instructorHelp.jsp#session-preview" target="_blank" rel="noopener noreferrer">preview the session</a> as a student or another instructor.
      </li>
    </ol>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2 id="session-invites">3. Wait for your session to open</h2>
  <div>
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
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2 id="session-results">4. View and publish session results</h2>
  <div>
    <p>
      After a session has opened, you may:
    </p>
    <ul>
      <li>
        <a href="/instructorHelp.jsp#session-view-results" target="_blank" rel="noopener noreferrer">View responses</a>: see what respondents have answered, even if the session is still ongoing. Go to the <b>Sessions</b> page and click the corresponding <button class="btn btn-default btn-xs">Results</button> button.
      </li>
      <li>
        Moderate responses: edit inappropriate responses from respondents before publishing the responses.
      </li>
      <li>
        <a href="/instructorHelp.jsp#session-add-comments" target="_blank" rel="noopener noreferrer">Add comments to responses</a>: reply to respondents' answers, or add your own notes on a response. You can make your comment visible to other instructors, the response giver, and/or the response giver's team.
      </li>
      <li>
        Remind students to submit responses: TEAMMATES automatically sends reminders to students; however, you can also manually send reminder emails to students at any time while a session is open. Click the <button class="btn btn-xs btn-default">Remind</button> button of the session from the <b>Home</b> or <b>Sessions</b> page.
      </li>
      <li>
        <a href="/instructorHelp.jsp#session-cannot-submit" target="_blank" rel="noopener noreferrer">Submit responses for students</a>: if a student has missed the closing time of the session, or is unable to submit the evaluation due to technical problems, you can submit the student's responses on his/her behalf.
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
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2 id="other-actions">5. Learn about other actions you can perform</h2>
  <div>
    <p>
      Beyond allowing you to collect and disseminate feedback, TEAMMATES can be a useful repository of student and course information. You may use TEAMMATES to:
    </p>
    <ul>
      <li>
        <a href="/instructorHelp.jsp#student-view-profile" target="_blank" rel="noopener noreferrer">View a student's profile</a> and <a href="/instructorHelp.jsp#student-view-responses" target="_blank" rel="noopener noreferrer">all past records of a student</a>: view the profile that any enrolled student has written for him/herself, and see in one place all submissions given/received by a student. Handy for examining how a student progressed through a course.
      </li>
      <li>
        <a href="/instructorHelp.jsp#student-edit-details" target="_blank" rel="noopener noreferrer">Edit a student's data</a>: change a student's registered name, section or team name, or email address. You can also note down comments on students, for example to inform other instructors of information about a student that they should take note of.
      </li>
      <li>
        <a href="/instructorHelp.jsp#student-email" target="_blank" rel="noopener noreferrer">Email a group of students</a>: contact students regarding their feedback responses, or the course in general. Also handy for locating the email address of past students.
      </li>
      <li>
        Search: <a href="/instructorHelp.jsp#student-search" target="_blank" rel="noopener noreferrer">search for students, teams or sections</a>, or <a href="/instructorHelp.jsp#session-search" target="_blank" rel="noopener noreferrer">search for questions, responses or comments</a>.
      </li>
      <li>
        <a href="/instructorHelp.jsp#course-archive" target="_blank" rel="noopener noreferrer">Archive old courses</a>: archive old courses that you no longer need actively.
      </li>
    </ul>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
  <h2 id="contact-us">6. Contact us</h2>
  <div>
    <p>
      If you have doubts, comments, or questions about using TEAMMATES, just
      <a href="mailto:teammates@comp.nus.edu.sg">email us</a>.
      We respond within 24 hours.
    </p>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
</div>
</t:helpPage>
