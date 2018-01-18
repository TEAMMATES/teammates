<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="gs">Getting Started</h4>
<div id="contentHolder">
  <br>
  <ol type="1">
    <li>
      <span class="text-bold">Create a course
      </span>
      <div class="helpSectionContent">
        Go to the ‘Courses’ page and create a course.
        <br>Some of the elements in the user interface (e.g., text boxes) have hover over tips to tell you what the element does.
        <br>
      </div>
      <br>
      <div id="gettingStartedHtml" class="bs-example">
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
                  <select class="form-control" name="coursetimezone" id="coursetimezone" tabindex="3" placeholder="Select a time zone...">
                    <option value="">Select a time zone...</option>
                    <option value="Africa/Abidjan">Africa/Abidjan (UTC)</option>
                    <option value="Africa/Accra">Africa/Accra (UTC)</option>
                    <option value="Africa/Addis_Ababa">Africa/Addis_Ababa (UTC +03:00)</option>
                    <option value="Africa/Algiers">Africa/Algiers (UTC +01:00)</option>
                    <option value="Africa/Asmara">Africa/Asmara (UTC +03:00)</option>
                    <option value="Africa/Asmera">Africa/Asmera (UTC +03:00)</option>
                    <option value="Africa/Bamako">Africa/Bamako (UTC)</option>
                    <option value="Africa/Bangui">Africa/Bangui (UTC +01:00)</option>
                    <option value="Africa/Banjul">Africa/Banjul (UTC)</option>
                    <option value="Africa/Bissau">Africa/Bissau (UTC)</option>
                    <option value="Other">Other options omitted...</option>
                  </select>
                  <div class="alert alert-info time-zone-info-box">
                    <span class="glyphicon glyphicon-info-sign"></span>
                    Time zone is auto-detected based on your device settings.
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
      <br>
      <span class="text-bold">Adding more instructors</span>
      <div class="helpSectionContent">
        More instructors (e.g. tutors) can be added to a course by going to the ‘edit’ link of the course.
        <br>Note that you can set the access control levels for these instructors.
        <br>
        <br>
      </div>
    </li>
    <li>
      <span class="text-bold">
        Enroll students
      </span>
      <div class="helpSectionContent">
        Enroll students by doing one of the following options:
        <ul>
          <li>
            Go to the ‘Home’ page, click on the ‘Students‘ button on the corresponding course, and choose ‘Enroll’
          </li>
          <li>
            Go to the ‘Courses’ page and click the ‘Enroll‘ button of the corresponding course
          </li>
        </ul>
        <br>
        <br>
      </div>
    </li>
    <li>
      <span class="text-bold">Create a session
      </span>
      <div class="helpSectionContent">
        Go to the ‘Sessions’ page and create a session (there are different session types to choose from).
        <br>
        <br>

        <div class="bs-example" id="sessionTypeSelectionHtml">
          <div class="well well-plain">
            <div class="row" data-toggle="tooltip" data-placement="top" title="Select a different type of session here.">
              <h4 class="label-control col-md-2 text-md">Create new </h4>
              <div class="col-md-5">
                <div class="col-xs-10 tablet-no-padding" title="" data-toggle="tooltip" data-placement="top" data-original-title="Select a session type here.">
                  <select class="form-control" name="fstype" id="fstype">
                    <option value="STANDARD" selected="">
                      Session with your own questions
                    </option>
                    <option value="TEAMEVALUATION" selected>
                      Team peer evaluation session
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
                  <div class="col-sm-12 col-md-6 tablet-no-mobile-margin-top-20px" title="" data-toggle="tooltip" data-placement="top" data-original-title="You should not need to change this as your timezone is auto-detected. <br><br>However, note that daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is daylight saving, you should choose UTC -7:00 and its corresponding timings.">
                    <div class="form-group">
                      <h5 class="col-sm-2 col-md-4">
                        <label class="control-label" for="timezone">
                          Timezone
                        </label>
                      </h5>
                      <div class="col-sm-10 col-md-8">
                        <select class="form-control" name="timezone" id="timezone">
                          <option value="-12">(UTC -12:00) Baker Island, Howland Island</option>
                          <option value="-11">(UTC -11:00) American Samoa, Niue</option>
                          <option value="-10">(UTC -10:00) Hawaii, Cook Islands</option>
                          <option value="-9.5">(UTC -09:30) Marquesas Islands</option>
                          <option value="-9">(UTC -09:00) Gambier Islands, Alaska</option>
                          <option value="-8">(UTC -08:00) Los Angeles, Vancouver, Tijuana</option>
                          <option value="-7">(UTC -07:00) Phoenix, Calgary, Ciudad Juárez</option>
                          <option value="-6">(UTC -06:00) Chicago, Guatemala City, Mexico City, San José, San Salvador, Tegucigalpa, Winnipeg</option>
                          <option value="-5">(UTC -05:00) New York, Lima, Toronto, Bogotá, Havana, Kingston</option>
                          <option value="-4.5">(UTC -04:30) Caracas</option>
                          <option value="Other">Other options omitted...</option>
                        </select>
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
                <a class="editUncommonSettingsButton" id="editUncommonSettingsSessionResponsesVisibleButton" data-edit="[Edit]" data-done="[Done]">[Change]</a>
              </div>
              <div id="uncommonSettingsSendEmails" class="margin-bottom-15px text-muted">
                <span id="uncommonSettingsSendEmailsInfoText">Emails are sent when session opens (within 15 mins), 24 hrs before session closes and when results are published.</span>
                <a class="editUncommonSettingsButton" id="editUncommonSettingsSendEmailsButton" data-edit="[Edit]" data-done="[Done]">[Change]</a>
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
        <br>
        <div class="helpSectionContent">

          <ul>
            <li>Session with your own questions</li>
            <div style="margin: 0 auto; padding: 0 50px;">
              <ul>
                <li>Creates an empty feedback session</li>
                <li>Allows you to craft custom questions that fit your needs</li>
              </ul>
            </div>
          </ul>
          <ul>
            <li>Team peer evaluation session</li>
            <div style="margin: 0 auto; padding: 0 50px;">
              <ul>
                <li>Provides 5 standard questions for team peer evaluations</li>
                <li> Allows you to modify/remove the given questions and add your own questions as required</li>
              </ul>
            </div>
            <br>
            <li>You can set custom feedback paths for each question:
              <br>i.e. specify who is giving feedback to whom. e.g. the question 'What is the estimated contribution of team member?' can be set the following feedback path:
              <br> Feedback giver: students in the course
              <br> Feedback recipient: giver's team members
              <br>

            </li>
            <br>
            <li>You can set the visibility options for each question:
              <br>Allows you to set who can see the answers, giver name and recipient name for each question.

              <br>
              <br>See
              <a href="#sessionTypes">here</a> for more info about session types.
            </li>

          </ul>
          <br>

        </div>
      </div>
    </li>
    <li>
      <span class="text-bold">When sessions open</span>
      <div class="helpSectionContent">
        When it is time to open the session (based on the ‘opening time’ you specified), TEAMMATES automatically emails students instructions for accessing the session. A copy of that email will be sent to you as well.
        <br>
        <br> If you would like students to access TEAMMATES sooner (e.g. you would like them to fill in their profile page in advance), you can go to the 'View' link of the course and click the 'Remind Students to Join' button, which will send them 'access instructions' immediately.
        <br>
        <br>
        <div class="helpSectionContent"></div>
      </div>
    </li>
    <li>
      <span class="text-bold">While the session is open</span>
      <div class="helpSectionContent">
        You can view responses any time after the session is open, even when the session is still ongoing. Just go to the ‘Sessions’ page and click the corresponding ‘View results/responses’ link.
        <a href="#sessionTypes">Session Types
        </a> section has more information about the reports available for different session types.
        <br>
        <br>Students will be sent a reminder 24 hours before the closing time of a session. In addition, you can send further reminders to students any time while a session is open using the ‘remind’ link.
        <br>
        <br>
      </div>
    </li>
    <li>
      <span class="text-bold">After the session is closed</span>
      <div class="helpSectionContent">
        You can publish results (i.e. make it visible to students) using the ‘publish’ link in the ‘Sessions’ page.
        <br>
        <br>Results of sessions can be downloaded in spreadsheet format.
        <br>
        <br>
      </div>
    </li>
    <li>
      <span class="text-bold">Any time</span>
      <div class="helpSectionContent">
        You can use the ‘Students’ page any time to do these things:
        <div style="margin: 0 auto; padding: 0 50px;">
          <ul>
            <li>
              <span class="text-bold">Email a group of students</span>: Filter out students in certain teams/courses and email them. Also handy for locating the email of any past student.</li>
            <li>
              <span class="text-bold">Comment on students</span>: Add a comments about any student. Handy for saving and retrieving comments about a student quickly. You can make these comments visible to others or keep them private.</li>
            <li>
              <span class="text-bold">View profile and all past records of a student
              </span>: View profile of a student and see in one place all submissions given/received by a student. Handy for examining how a student progressed through a course. (Students &gt; All Records)
            </li>
            <li>
              <span class="text-bold">View all comments</span>: View all comments from one page.
            </li>
            <li>
              <span class="text-bold">Search</span>: Search for students, teams, sections.
            </li>
            <li>
              <span class="text-bold">Archive old courses</span>: Archive old courses that you no longer need actively.
            </li>
          </ul>
          <br>
        </div>
      </div>
    </li>
    <li>
      <span class="text-bold">When you need help</span>
      <div class="helpSectionContent">
        If you have a doubt or need our help, just
        <a href="mailto:teammates@comp.nus.edu.sg">email us</a>.
        We respond within 24 hours.
        <br>
        <br>
      </div>
    </li>
  </ol>
  <p align="right">
    <a href="#Top">Back to Top
    </a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
