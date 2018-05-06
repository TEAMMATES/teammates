<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="sessions">Sessions</h2>
<div id="contentHolder">
  <h3>Setting Up Sessions</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="session-tips">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-tips-body">
        <h3 class="panel-title">Tips for conducting team peer evaluation sessions</h3>
      </div>
      <div id="session-tips-body" class="panel-collapse collapse">
        <div class="panel-body">
          <jsp:include page="instructorHelpTips.jsp"/>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-create">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-create-body">
        <h3 class="panel-title">How do I create and schedule a new feedback session?</h3>
      </div>
      <div id="session-create-body" class="panel-collapse collapse">
        <div class="panel-body">
          To create a new feedback session, click the <b>Sessions</b> tab at the top of the page. Then,
          fill out and submit the <b>Add New Feedback Session</b> form:
          <ol>
            <li>
              <b>Choose a session type</b>. You can choose between creating a session with your own questions,
              creating a copy of a session you previously made, or using one of our session templates.
              <br>
              <ul>
                <li>
                  Session with your own questions: you'll start with an empty template to which you can add your own
                  questions
                </li>
                <li>
                  Session using template: TEAMMATES will provide you with a template of a typical session that you can
                  add to and customize to suit your needs
                </li>
                <li>
                  Copy from previous feedback sessions: you can reuse questions and settings from a survey you created
                  in the past
                </li>
              </ul>
            </li>
            <li>
              <b>Select the course ID</b> of the course for which the session will be created.
            </li>
            <li>
              <b>Give your session a session name</b>. This name will be visible to session respondents.
            </li>
            <li>
              <b>Set the session's submission opening/closing time</b>. This is the time period during which students
              can submit responses. TEAMMATES will automatically open and close the session at times you specify.
            </li>
            <li>
              (Optional) Set advanced options to best suit your needs:
            </li>
            <ul>
              <li>
                Set a custom time zone
              </li>
              <li>
                Give students more specific instructions
              </li>
              <li>
                Set a grace period during which students can still submit responses if the session closes
              </li>
              <li>
                Choose when you want this session to be visible to students. After this time, students can see the questions,
                but they cannot submit their responses until the session is <i>open</i>
              </li>
              <li>
                Choose when you want to make this session's responses visible. At this time, TEAMMATES will automatically
                publish the results for students to view
              </li>
              <li>
                Choose whether TEAMMATES should send reminder or announcement emails to students about this session
              </li>
              <li>
                Make the session private. A private session is a session that is never visible to others.
                This is for you to record your feedback about students. If you want to create a private session,
                set "Make session visible" to <code>Never</code>
              </li>
            </ul>
            <li>
              <b>Click Create Feedback Session</b>!
            </li>
          </ol>
          <p>
            This is the form used to set up sessions.
          </p>
          <div class="bs-example">
            <div id="createSessionHtmlCustomizable">

              <div class="well well-plain">
                <form class="form-group" name="form_feedbacksession" onsubmit="return false">
                  <div class="row" data-toggle="tooltip" data-placement="top" title="Select a different type of session here.">
                    <h4 class="label-control col-md-2 text-md">Create new </h4>
                    <div class="col-md-5">
                      <select class="form-control" name="fstype" id="fstype">
                        <option value="STANDARD" selected="">
                          session with my own questions
                        </option>
                        <option value="TEAMEVALUATION">
                          session using template: team peer evaluation
                        </option>
                      </select>
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
                        <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="Please select the course for which the feedback session is to be created.">
                          <div class="form-group">
                            <h5 class="col-sm-4">
                              <label for="courseid" class="control-label">Course ID</label>
                            </h5>
                            <div class="col-sm-8">
                              <select class="form-control" name="courseid" id="courseid">
                                <option value="CS1101">CS1101</option>
                                <option value="CS2013">CS2103</option>
                                <option value="Other course">Other course</option>
                              </select>

                            </div>
                          </div>
                        </div>
                        <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="You should not need to change this as your timezone is auto-detected. Daylight saving time is supported.">
                          <div class="form-group">
                            <h5 class="col-sm-4">
                              <label class="control-label">
                                Time Zone
                              </label>
                            </h5>
                            <div class="col-sm-8">
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
                      <br>
                      <div class="row">
                        <div class="col-md-12" data-toggle="tooltip" data-placement="top" title="Enter the name of the feedback session e.g. Feedback Session 1.">
                          <div class="form-group">
                            <h5 class="col-sm-2">
                              <label for="fsname" class="control-label">Session name
                              </label>
                            </h5>
                            <div class="col-sm-10">
                              <input class="form-control" type="text" name="fsname" id="fsname" maxlength="38" value="" placeholder="e.g. Feedback for Project Presentation 1">
                            </div>
                          </div>
                        </div>
                      </div>
                      <br>
                      <div class="row" id="instructionsRow">
                        <div class="col-md-12" data-toggle="tooltip" data-placement="top" title="Enter instructions for this feedback session. e.g. Avoid comments which are too critical.<br> It will be displayed at the top of the page when users respond to the session.">
                          <div class="form-group">
                            <h5 class="col-sm-2">
                              <label for="instructions" class="control-label">Instructions</label>
                            </h5>
                            <div class="col-sm-10">
                              <textarea class="form-control" rows="4" cols="100%" name="instructions" id="instructions" placeholder="e.g. Please answer all the given questions.">Please answer all the given questions.</textarea>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="panel panel-primary" id="timeFramePanel">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-md-5" data-toggle="tooltip" data-placement="top" title="Please select the date and time for which users can start submitting responses for the feedback session.">
                          <div class="row">
                            <div class="col-md-6">
                              <label for="startdate" class="label-control">
                                Submission opening time
                              </label>
                            </div>
                          </div>
                          <div class="row">
                            <div class="col-md-6">
                              <input class="form-control col-sm-2 hasDatepicker" type="text" name="startdate" id="startdate" value="21/07/2014" placeholder="Date">
                            </div>
                            <div class="col-md-6">
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
                                <option value="11">1100H</option>
                                <option value="12">1200H</option>
                                <option value="13">1300H</option>
                                <option value="14">1400H</option>
                                <option value="15">1500H</option>
                                <option value="16">1600H</option>
                                <option value="17">1700H</option>
                                <option value="18">1800H</option>
                                <option value="19">1900H</option>
                                <option value="20">2000H</option>
                                <option value="21">2100H</option>
                                <option value="22">2200H</option>
                                <option value="23">2300H</option>
                                <option value="24" selected="">2359H</option>
                              </select>
                            </div>
                          </div>
                        </div>
                        <div class="col-md-5 border-left-gray" data-toggle="tooltip" data-placement="top" title="Please select the date and time after which the feedback session will no longer accept submissions from users.">
                          <div class="row">
                            <div class="col-md-6">
                              <label for="enddate" class="label-control">Submission closing time</label>
                            </div>
                          </div>
                          <div class="row">
                            <div class="col-md-6">
                              <input class="form-control col-sm-2 hasDatepicker" type="text" name="enddate" id="enddate" value="" placeholder="Date">
                            </div>
                            <div class="col-md-6">
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
                                <option value="11">1100H</option>
                                <option value="12">1200H</option>
                                <option value="13">1300H</option>
                                <option value="14">1400H</option>
                                <option value="15">1500H</option>
                                <option value="16">1600H</option>
                                <option value="17">1700H</option>
                                <option value="18">1800H</option>
                                <option value="19">1900H</option>
                                <option value="20">2000H</option>
                                <option value="21">2100H</option>
                                <option value="22">2200H</option>
                                <option value="23">2300H</option>
                                <option value="24" selected="">2359H</option>
                              </select>
                            </div>
                          </div>
                        </div>
                        <div class="col-md-2 border-left-gray" data-toggle="tooltip" data-placement="top" title="Please select the amount of time that the system will continue accepting <br>submissions after the specified deadline.">
                          <div class="row">
                            <div class="col-md-12">
                              <label for="graceperiod" class="control-label">
                                Grace period
                              </label>
                            </div>
                          </div>
                          <div class="row">
                            <div class="col-sm-12">
                              <select class="form-control" name="graceperiod" id="graceperiod">
                                <option value="0">0 mins</option>
                                <option value="5">5 mins</option>
                                <option value="10">10 mins</option>
                                <option value="15" selected="">15 mins</option>
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
                  <div class="panel panel-primary">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-md-6">
                          <div class="row">
                            <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="Please select when you want the questions for the feedback session to be visible to users who need to participate. Note that users cannot submit their responses until the submissions opening time set below.">
                              <label class="label-control">
                                Make session visible
                              </label>
                            </div>
                          </div>
                          <div class="row radio">
                            <div class="col-md-2" data-toggle="tooltip" data-placement="top" title="Select this option to enter in a custom date and time for which the feedback session will become visible.<br>Note that you can make a session visible before it is open for submissions so that users can preview the questions.">
                              <label for="sessionVisibleFromButton_custom">At
                              </label>
                              <input type="radio" name="sessionVisibleFromButton" id="sessionVisibleFromButton_custom" value="custom">
                            </div>
                            <div class="col-md-5">
                              <input class="form-control col-sm-2 hasDatepicker" type="text" name="visibledate" id="visibledate" value="" disabled="">
                            </div>
                            <div class="col-md-4">
                              <select class="form-control" name="visibletime" id="visibletime" disabled="">

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
                                <option value="11">1100H</option>
                                <option value="12">1200H</option>
                                <option value="13">1300H</option>
                                <option value="14">1400H</option>
                                <option value="15">1500H</option>
                                <option value="16">1600H</option>
                                <option value="17">1700H</option>
                                <option value="18">1800H</option>
                                <option value="19">1900H</option>
                                <option value="20">2000H</option>
                                <option value="21">2100H</option>
                                <option value="22">2200H</option>
                                <option value="23">2300H</option>
                                <option value="24" selected="">2359H</option>
                              </select>
                            </div>
                          </div>
                          <div class="row radio">
                            <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="Select this option to have the feedback session become visible when it is open for submissions (as selected above).">
                              <label for="sessionVisibleFromButton_atopen">Submission opening time </label>
                              <input type="radio" name="sessionVisibleFromButton" id="sessionVisibleFromButton_atopen" value="atopen">
                            </div>
                          </div>
                          <div class="row radio">
                            <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="Select this option if you want the feedback session to never be visible. Use this option if you want to use this as a private feedback session.">
                              <label for="sessionVisibleFromButton_never">Never</label>
                              <input type="radio" name="sessionVisibleFromButton" id="sessionVisibleFromButton_never" value="never">
                            </div>
                          </div>
                        </div>

                        <div class="col-md-6 border-left-gray" id="responsesVisibleFromColumn">
                          <div class="row">
                            <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="Please select when the responses for the feedback session will be visible to the designated recipients.<br>You can select the response visibility for each type of user and question later.">
                              <label class="label-control">Make responses visible</label>
                            </div>
                          </div>
                          <div class="row radio">
                            <div class="col-md-2" data-toggle="tooltip" data-placement="top" title="Select this option to use a custom time for when the responses of the feedback session<br>will be visible to the designated recipients.">
                              <label for="resultsVisibleFromButton_custom">At</label>

                              <input type="radio" name="resultsVisibleFromButton" id="resultsVisibleFromButton_custom" value="custom">
                            </div>
                            <div class="col-md-5">
                              <input class="form-control hasDatepicker" type="text" name="publishdate" id="publishdate" value="" disabled="">
                            </div>
                            <div class="col-md-4">
                              <select class="form-control" name="publishtime" id="publishtime" data-toggle="tooltip" data-placement="top" disabled="" title="Select this option to enter in a custom date and time for which</br>the responses for this feedback session will become visible.">
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
                                <option value="11">1100H</option>
                                <option value="12">1200H</option>
                                <option value="13">1300H</option>
                                <option value="14">1400H</option>
                                <option value="15">1500H</option>
                                <option value="16">1600H</option>
                                <option value="17">1700H</option>
                                <option value="18">1800H</option>
                                <option value="19">1900H</option>
                                <option value="20">2000H</option>
                                <option value="21">2100H</option>
                                <option value="22">2200H</option>
                                <option value="23">2300H</option>
                                <option value="24" selected="">2359H</option>
                              </select>
                            </div>
                          </div>
                          <div class="row radio">
                            <div class="col-md-3" data-toggle="tooltip" data-placement="top" title="Select this option to have the feedback responses be immediately visible<br>when the session becomes visible to users.">
                              <label for="resultsVisibleFromButton_atvisible">Immediately</label>
                              <input type="radio" name="resultsVisibleFromButton" id="resultsVisibleFromButton_atvisible" value="atvisible">
                            </div>
                          </div>
                          <div class="row radio">
                            <div class="col-md-5" data-toggle="tooltip" data-placement="top" title="Select this option if you intend to manually publish the session later on.">
                              <label for="resultsVisibleFromButton_later">Not now (publish manually)
                              </label>
                              <input type="radio" name="resultsVisibleFromButton" id="resultsVisibleFromButton_later" value="later">
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="panel panel-primary">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-md-12">
                          <label class="control-label">Send emails for</label>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-2" data-toggle="tooltip" data-placement="top" title="If the student has not joined the course yet, an email containing the link to join the course will automatically be sent on session opening time.">
                          <div class="checkbox">
                            <label for="sendreminderemail_join">Join reminder
                            </label>
                            <input type="checkbox" id="sendreminderemail_join" disabled="">
                          </div>
                        </div>
                        <div class="col-sm-3" data-toggle="tooltip" data-placement="top" title="Select this option to automatically send an email to students to notify them when the session is open for submission." disabled="">
                          <div class="checkbox">
                            <label>Session opening reminder
                            </label>
                            <input type="checkbox" name="sendreminderemail" id="sendreminderemail_open" value="FEEDBACK_OPENING">
                          </div>
                        </div>
                        <div class="col-sm-3" data-toggle="tooltip" data-placement="top" title="Select this option to automatically send an email to students to remind them to submit 24 hours before the end of the session.">
                          <div class="checkbox">
                            <label for="sendreminderemail_closing">Session closing reminder</label>
                            <input type="checkbox" name="sendreminderemail" id="sendreminderemail_closing" value="FEEDBACK_CLOSING">
                          </div>
                        </div>
                        <div class="col-sm-4" data-toggle="tooltip" data-placement="top" title="Select this option to automatically send an email to students to notify them when the session results is published.">
                          <div class="checkbox">
                            <label for="sendreminderemail_published">Results published announcement</label>
                            <input type="checkbox" name="sendreminderemail" id="sendreminderemail_published" value="FEEDBACK_PUBLISHED">
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="form-group">
                    <div class="col-md-offset-5 col-md-3">
                      <button class="btn btn-primary">Create Feedback Session</button>
                    </div>
                  </div>
                </form>
                <br>
                <br>
              </div>

            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-questions">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-questions-body">
        <h3 class="panel-title">How do I add questions to a session?</h3>
      </div>
      <div id="session-questions-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            After setting up a session, you can start adding questions.<br>
            You can also access this page by clicking the <button class="btn btn-xs btn-default">Edit</button> button of the desired session from the <b>Home</b> or <b>Sessions</b> page.
          </p>
          <p>
            To add a question:
          </p>
          <ol>
            <li>
              Scroll to the bottom of the page.
            </li>
            <li>
              Select between adding a question from our predefined <a href="#questions">question types</a> or
              copying a question from an existing feedback session.
            </li>
            <li>
              Save changes to the question when you have finished creating the question
            </li>
            <li>
              When you are finished adding questions, click <button class="btn btn-primary">Done Editing</button>.
            </li>
          </ol>
          <div class="bs-example" id="addQuestion">
            <div class="well well-plain" id="addNewQuestionTable">
              <div class="row">
                <div class="col-sm-12 row">
                  <div class="col-sm-offset-3 col-sm-9">
                    <button id="button_openframe" class="btn btn-primary margin-bottom-7px dropdown-toggle" type="button" data-toggle="dropdown">
                      Add New Question <span class="caret"></span>
                    </button>
                    <ul id="add-new-question-dropdown" class="dropdown-menu">
                      <li data-questiontype="TEXT"><a href="javascript:;">Essay question</a></li>
                      <li data-questiontype="MCQ"><a href="javascript:;"> Multiple-choice (single answer) question</a></li>
                      <li data-questiontype="MSQ"><a href="javascript:;">Multiple-choice (multiple answers) question</a></li>
                      <li data-questiontype="NUMSCALE"><a href="javascript:;">Numerical-scale question</a></li>
                      <li data-questiontype="CONSTSUM_OPTION"><a href="javascript:;">Distribute points (among options) question</a></li>
                      <li data-questiontype="CONSTSUM_RECIPIENT"><a href="javascript:;">Distribute points (among recipients) question</a></li>
                      <li data-questiontype="CONTRIB"><a href="javascript:;">Team contribution question</a></li>
                      <li data-questiontype="RUBRIC"><a href="javascript:;">Rubric question</a></li>
                      <li data-questiontype="RANK_OPTIONS"><a href="javascript:;">Rank (options) question</a></li>
                      <li data-questiontype="RANK_RECIPIENTS"><a href="javascript:;">Rank (recipients) question</a></li>
                    </ul>
                    <a target="_blank" rel="noopener noreferrer">
                      <i class="glyphicon glyphicon-info-sign"></i>
                    </a>
                    <a id="button_copy" class="btn btn-primary margin-bottom-7px" data-actionlink="/page/instructorFeedbackQuestionCopyPage?user=test%40example.com" data-fsname="hgc" data-courseid="teammates.instructor.uni-demo" data-target="#copyModal" data-toggle="modal">
                      Copy Question
                    </a>
                    <a id="button_done_editing" class="btn btn-primary margin-bottom-7px">
                      Done Editing
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <p>
            TEAMMATES gives you fine-grained control of each question. In addition to providing a range of different
            <a href="#questions">question types</a>,
            you can also customize your desired:
          </p>
          <ul>
            <li>
              <b>Feedback Path</b>: define who is giving feedback, and who the feedback is about.
              Select a common feedback path from the dropdown menu, or choose "Other predefined combinations..."
              to define the Feedback Giver and Recipient separately.
              If you choose a ‘team’ as the giver, any member can submit the response on behalf of the team.
            </li>
            <li>
              <b>Visibility options</b>: let students know who will be able to see their answers.
              Select a common visibility option from the dropdown menu, or choose "Custom visibility options..."
              to fully customize who can see the feedback response, the giver's identity, and the recipient's identity.
            </li>
          </ul>
          <p>
            In the example question below, students will give feedback on their own team members.
            The team member receiving feedback can see the feedback, but not who gave the feedback.
            Instructors can see who received what feedback, and who gave the feedback.
          </p>
          <div class="bs-example" id="settingQuestion">

            <form class="form-horizontal form_question" editstatus="hasResponses">
              <div class="panel panel-primary questionTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-12">
                  <span>
                    <strong>Question</strong>
                    <select class="questionNumber nonDestructive text-primary">
                      <option value="1">1</option>
                      <option value="2">2</option>
                      <option value="3">3</option>
                      <option value="4">4</option>
                      <option value="5">5</option>
                      <option value="6">6</option>
                      <option value="7">7</option>
                      <option value="8">8</option>

                    </select> &nbsp; Essay question
                  </span>
                      <span class="pull-right">
                    <a onclick="return false" class="btn btn-primary btn-xs">Cancel
                    </a>
                  </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext--1">Question
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext--1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?" style="z-index: auto; position: relative; line-height: 20px; font-size: 14px; transition: none; background: none 0% 0% / auto repeat scroll padding-box border-box rgb(255, 255, 255);"></textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription--1">[Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div class="panel panel-default panel-body question-description mce-content-body content-editor empty" id="questiondescription--1" data-toggle="tooltip" data-placement="top" title="" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" tabindex="9" data-original-title="Please enter the description of the question." contenteditable="true" style="position: relative;">
                        </div>
                      </div>
                      <div id="textForm" style="display: block;"><div>
                        <br>
                        <div class="row">
                          <div class="col-xs-12 question-recommended-length">[Optional]
                            <span data-toggle="tooltip" data-placement="top" title="" data-original-title="The recommended length is shown to the respondent but not enforced" class="tool-tip-decorate">Recommended length
                          </span> for the response:
                            <input type="number" class="form-control" name="recommendedlength" value=""> words
                          </div>
                        </div>
                      </div>
                      </div>
                    </div>

                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="col-sm-12 feedback-path-dropdown btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Students in this course will give feedback on
                        <span class="glyphicon glyphicon-arrow-right"></span> Giver's team members
                      </button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common feedback path combinations</li>
                        <li class="dropdown-submenu">
                          <a>Feedback session creator (i.e., me) will give feedback on...</a>
                          <ul class="dropdown-menu">
                            <li>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="NONE" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                Nobody specific (For general class feedback)
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="SELF" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                Giver (Self feedback)
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="INSTRUCTORS" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                Instructors in the course
                              </a>
                            </li>
                          </ul>
                        </li>
                        <li class="dropdown-submenu">
                          <a>Students in this course will give feedback on...</a>
                          <ul class="dropdown-menu">
                            <li>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="NONE" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                Nobody specific (For general class feedback)
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="SELF" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                Giver (Self feedback)
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="INSTRUCTORS" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                Instructors in the course
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="OWN_TEAM_MEMBERS" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver's team members">
                                Giver's team members
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="OWN_TEAM_MEMBERS_INCLUDING_SELF" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver's team members and Giver">
                                Giver's team members and Giver
                              </a>
                            </li>
                          </ul>
                        </li>
                        <li class="dropdown-submenu">
                          <a>Instructors in this course will give feedback on...</a>
                          <ul class="dropdown-menu">
                            <li>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="NONE" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                Nobody specific (For general class feedback)
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="SELF" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                Giver (Self feedback)
                              </a>
                              <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="INSTRUCTORS" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                Instructors in the course
                              </a>
                            </li>
                          </ul>
                        </li>
                        <li role="separator" class="divider"></li>
                        <li><a class="feedback-path-dropdown-option feedback-path-dropdown-option-other" href="javascript:;" data-path-description="Predefined combinations:">Other predefined combinations...</a></li>
                      </ul>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        Shown anonymously to recipient, visible to instructors
                      </button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient and team members, visible to instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="VISIBLE_TO_INSTRUCTORS_ONLY">Visible to instructors only</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="VISIBLE_TO_RECIPIENT_AND_INSTRUCTORS">Visible to recipient and instructors</a>
                        </li>

                        <li role="separator" class="divider"></li>
                        <li><a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="OTHER">Custom visibility options...</a></li>
                      </ul>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-2">
                      This is the visibility hint as seen by the feedback giver:
                      <ul class="text-muted background-color-warning">

                        <li>The receiving student can see your response, but not your name.</li>
                        <li>Instructors in this course can see your response, the name of the recipient, and your name.</li>
                      </ul>
                    </div>
                  </div>
                  <div>
                <span class="pull-right">
                  <input id="button_question_submit-1" type="submit" onclick="return false" class="btn btn-primary" value="Save Question" tabindex="0" style="">
                </span>
                  </div>
                </div>
              </div>

            </form>

          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-preview">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-preview-body">
        <h3 class="panel-title">How do I preview a session?</h3>
      </div>
      <div id="session-preview-body" class="panel-collapse collapse">
        <div class="panel-body">
          To see what the current session looks like to anyone in the course when they are submitting responses, use the <b>Preview</b> feature.<br>
          You can quickly and easily confirm that the questions and their settings are correct after editing questions.
          </p>
          <p>
            To access the preview panel of a specific session, click the <button class="btn btn-xs btn-default">Edit</button> button for that session in the <b>Home</b> or <b>Sessions</b> page. The preview
            panel is located at the bottom of the Edit Feedback Session page.
          </p>
          <div class="bs-example" id="preview">
            <div class="well well-plain" id="questionPreviewTable">
              <div class="row">
                <form class="form-horizontal">
                  <label class="control-label col-sm-2 text-right">
                    Preview Session:
                  </label>
                </form>
                <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="View how this session would look like to a student who is submitting feedback. Preview is unavailable if the course has yet to have any student enrolled.">
                  <form name="form_previewasstudent" class="form_preview">
                    <div class="col-sm-6">
                      <select class="form-control" name="previewas">
                        <option value="alice.b.tmms@gmail.com">[Team 1] Alice Betsy</option>
                        <option value="benny.c.tmms@gmail.com">[Team 1] Benny Charles</option>
                        <option value="danny.e.tmms@gmail.com">[Team 1] Danny Engrid</option>
                        <option value="emma.f.tmms@gmail.com">[Team 1] Emma Farrell</option>
                        <option value="charlie.d.tmms@gmail.com">[Team 2] Charlie Davis</option>
                        <option value="francis.g.tmms@gmail.com">[Team 2] Francis Gabriel</option>
                        <option value="gene.h.tmms@gmail.com">[Team 2] Gene Hudson</option>
                      </select>
                    </div>
                    <div class="col-sm-6">
                      <input id="button_preview_student" class="btn btn-primary" value="Preview as Student">
                    </div>
                  </form>
                </div>
                <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="View how this session would look like to an instructor who is submitting feedback.">
                  <form class="form_preview">
                    <div class="col-sm-6">
                      <select class="form-control" name="previewas">
                        <option value="inst@gmail.com">Instructor A</option>
                      </select>
                    </div>
                    <div class="col-sm-6">
                      <input id="button_preview_instructor" class="btn btn-primary" value="Preview as Instructor">
                    </div>
                  </form>
                </div>
              </div>
            </div>
            <div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <h3>Managing Session Responses</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="session-cannot-submit">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-cannot-submit-body">
        <h3 class="panel-title">What should I do if a student says he/she cannot submit an evaluation due to a technical glitch?</h3>
      </div>
      <div id="session-cannot-submit-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Instructors can submit responses on behalf of a student. To do so:
          </p>
          <ol>
            <li>
              Ask the student to view the submission page and send you his/her answers to the session questions.
            </li>
            <li>
              <a class="collapse-link" data-target="#session-view-results-body" href="#session-view-results">View the results</a> of the session.
            </li>
            <li>
              Scroll to the panel titled <b>Participants who have not responded to any question</b>. Click on the panel to expand it.
            </li>
            <li>
              Click the <button class="btn btn-xs btn-default">Submit Responses</button> button of the corresponding student. You will be directed to a page where you can submit responses on behalf of the student.
            </li>
            <li>
              Copy the student's responses to the corresponding questions, and click the <button class="btn btn-primary btn-s">Submit Feedback</button> button.
            </li>
          </ol>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-view-results">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-view-results-body">
        <h3 class="panel-title">How do I view the results of my session?</h3>
      </div>
      <div id="session-view-results-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            View responses to a session by clicking the <button class="btn btn-xs btn-default">Results</button> button of a session in the <b>Home</b> or <b>Sessions</b> page.<br>
            Click <button class="btn btn-primary btn-s">Edit View</button> to sort the results in an order that best suits you.
          </p>
          <p>5 different views are available, each denoting the order in which responses are grouped.
            Additionally, you can group the results by team, show or hide statistics, view missing responses and filter responses from a particular section.
          </p>
          <div class="bs-example" id="resultsTop">
            <div class="panel panel-info margin-0">
              <div class="panel-body">
                <div class="row">
                  <div class="col-sm-5" data-toggle="tooltip" title="View results in different formats">
                    <div class="form-group">
                      <label for="viewSelect" class="col-sm-2 control-label">
                        View:
                      </label>
                      <div class="col-sm-10">
                        <select id="viewSelect" class="form-control" name="frsorttype">
                          <option value="question" selected="">
                            Group by - Question
                          </option>
                          <option value="giver-recipient-question">
                            Group by - Giver &gt; Recipient &gt; Question
                          </option>
                          <option value="recipient-giver-question">
                            Group by - Recipient &gt; Giver &gt; Question
                          </option>
                          <option value="giver-question-recipient">
                            Group by - Giver &gt; Question &gt; Recipient
                          </option>
                          <option value="recipient-question-giver">
                            Group by - Recipient &gt; Question &gt; Giver
                          </option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="col-sm-2 pull-right">
                    <div class="col-sm-12" data-toggle="tooltip" title="Group results in the current view by team">
                      <div class="margin-0 checkbox padding-top-0 min-height-0">
                        <label class="text-strike">
                          <input type="checkbox" name="frgroupbyteam" id="frgroupbyteam"> Group by Teams
                        </label>
                      </div>
                    </div>
                    <div class="col-sm-12" data-toggle="tooltip" title="Show statistics">
                      <div class="margin-0 checkbox padding-top-0 min-height-0">
                        <label>
                          <input type="checkbox" id="show-stats-checkbox" name="frshowstats"> Show Statistics
                        </label>
                      </div>
                    </div>
                    <div class="col-sm-12" data-toggle="tooltip" title="Indicate missing responses">
                      <div class="margin-0 checkbox padding-top-0 min-height-0">
                        <label>
                          <input type="checkbox" id="show-stats-checkbox" name="frshowstats"> Indicate Missing Responses
                        </label>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="row">
                  <div class="col-sm-5" data-toggle="tooltip" title="View results in separated section">
                    <div class="form-group">
                      <label for="sectionSelect" class="col-sm-2 control-label">
                        Section:
                      </label>
                      <div class="col-sm-10">
                        <select id="sectionSelect" class="form-control" name="frgroupbysection">
                          <option value="All" selected="">
                            All
                          </option>
                          <option value="Tutorial Group 1">
                            Tutorial Group 1
                          </option>
                          <option value="Tutorial Group 2">
                            Tutorial Group 2
                          </option>
                          <option value="No specific section">
                            No specific section
                          </option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <p>
            In the example below, results are sorted by <b>Giver > Recipient > Question</b>. Additionally, missing responses
            have been recorded.
          </p>
          <div class="bs-example" id="responsesSortbyGiver">
            <div class="well well-plain">

              <div class="panel panel-primary">
                <div class="panel-heading">
                  From:
                  <strong>Alice Betsy (Team 2)</strong>
                  <a class="link-in-dark-bg" href="#responcesSortbyGiver">[alice.b.tmms@gmail.com]</a>
                  <div class="pull-right">
                    <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                      <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled="disabled">
                    </form>
                    &nbsp;
                    <div class="display-icon" style="display:inline;">
                      <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">

                  <div class="row ">
                    <div class="col-md-2">
                      <div class="col-md-12 tablet-margin-10px tablet-no-padding">
                        To:
                        <br>
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          <strong>-</strong>
                        </div>
                      </div>

                      <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
                        From:
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          Alice Betsy (Team 2)
                        </div>
                      </div>
                    </div>
                    <div class="col-md-10">

                      <div class="panel panel-info">
                        <div class="panel-heading">
                          Question 6: What do you like about our product?
                          <br>
                          <small>
                          <span>Multiple-choice (multiple answers) options:
                            <ul style="list-style-type: disc;">
                              <li>It's good
                              </li>
                              <li>It's perfect
                              </li>
                            </ul>
                          </span>
                          </small>
                        </div>
                        <div class="panel-body">
                          <div style="clear: both; overflow: hidden">
                            <div class="pull-left">
                              <ul class="selectedOptionsList">
                                <li>It's good
                                </li>
                              </ul>
                            </div>
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" data-toggle="tooltip" data-placement="top" title="Add comment">
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                          </div>

                        </div>
                      </div>

                    </div>
                  </div>

                  <div class="row border-top-gray">
                    <div class="col-md-2">
                      <div class="col-md-12 tablet-margin-10px tablet-no-padding">
                        To:
                        <br>
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          <strong>Alice Betsy (Team 2)</strong>
                        </div>
                      </div>

                      <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
                        From:
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          Alice Betsy (Team 2)
                        </div>
                      </div>
                    </div>
                    <div class="col-md-10">

                      <div class="panel panel-info">
                        <div class="panel-heading">Question 1: What is the best selling point of your product?
                        </div>
                        <div class="panel-body">
                          <div style="clear: both; overflow: hidden">
                            <div class="pull-left">My product is light.
                            </div>
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" data-toggle="tooltip" data-placement="top" title="Add comment">
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                          </div>

                        </div>
                      </div>

                    </div>
                  </div>

                  <div class="row border-top-gray">
                    <div class="col-md-2">
                      <div class="col-md-12 tablet-margin-10px tablet-no-padding">
                        To:
                        <br>
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          <strong>Benny Charles (Team 1)</strong>
                        </div>
                      </div>

                      <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
                        From:
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          Alice Betsy (Team 2)
                        </div>
                      </div>
                    </div>
                    <div class="col-md-10">

                      <div class="panel panel-info">
                        <div class="panel-heading">Question 2: Comment about 5 other students</div>
                        <div class="panel-body">
                          <div style="clear: both; overflow: hidden">
                            <div class="pull-left">Benny is a good student.
                            </div>
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" data-toggle="tooltip" data-placement="top" title="Add comment">
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                          </div>

                        </div>
                      </div>

                    </div>
                  </div>

                </div>
              </div>

              <div class="panel panel-primary" id="benny">
                <div class="panel-heading">
                  From:
                  <strong>Benny Charles (Team 1)</strong>
                  <a class="link-in-dark-bg" href="#benny">[benny.c.tmms@gmail.com]</a>
                  <div class="pull-right">
                    <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                      <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled="disabled">
                    </form>
                    &nbsp;
                    <div class="display-icon" style="display:inline;">
                      <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">

                  <div class="row ">
                    <div class="col-md-2">
                      <div class="col-md-12 tablet-margin-10px tablet-no-padding">
                        To:
                        <br>
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          <strong>Charlie Davis (Team 1)</strong>
                        </div>
                      </div>

                      <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
                        From:
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          Benny Charles (Team 1)
                        </div>
                      </div>
                    </div>
                    <div class="col-md-10">

                      <div class="panel panel-info">
                        <div class="panel-heading">Question 2: Comment about 5 other students</div>
                        <div class="panel-body">
                          <div style="clear: both; overflow: hidden">
                            <div class="pull-left">Charlie did alot of work.</div>
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" data-toggle="tooltip" data-placement="top" title="Add comment">
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                          </div>

                        </div>
                      </div>

                    </div>
                  </div>

                  <div class="row border-top-gray">
                    <div class="col-md-2">
                      <div class="col-md-12 tablet-margin-10px tablet-no-padding">
                        To:
                        <br>
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          <strong>Danny Engrid (Team 2)</strong>
                        </div>
                      </div>

                      <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
                        From:
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          Benny Charles (Team 1)
                        </div>
                      </div>
                    </div>
                    <div class="col-md-10">

                      <div class="panel panel-info">
                        <div class="panel-heading">Question 2: Comment about 5 other students</div>
                        <div class="panel-body">
                          <div style="clear: both; overflow: hidden">
                            <div class="pull-left">Danny starts with D.</div>
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" data-toggle="tooltip" data-placement="top" title="Add comment">
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                          </div>

                        </div>
                      </div>

                    </div>
                  </div>

                </div>
              </div>
              <!-- second -->
              <div class="panel panel-primary" id="charlie">
                <div class="panel-heading">
                  From:
                  <strong>Charlie Davis (Team 1)</strong>
                  <a class="link-in-dark-bg" href="#charlie">[charlie.d.tmms@gmail.com]</a>
                  <div class="pull-right">
                    <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                      <input type="submit" class="btn btn-primary btn-xs" value="Moderate Responses" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled="disabled">
                    </form>
                    &nbsp;
                    <div class="display-icon" style="display:inline;">
                      <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">

                  <div class="row ">
                    <div class="col-md-2">
                      <div class="col-md-12 tablet-margin-10px tablet-no-padding">
                        To:
                        <br>
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          <strong>Alice Betsy (Team 2)</strong>
                        </div>
                      </div>

                      <div class="col-md-12 tablet-margin-10px tablet-no-padding text-muted small"><br class="hidden-xs hidden-sm">
                        From:
                        <div class="tablet-bottom-align profile-pic-icon-hover inline-block">
                          Charlie Davis (Team 1)
                        </div>
                      </div>
                    </div>
                    <div class="col-md-10">

                      <div class="panel panel-info">
                        <div class="panel-heading">Question 2: Comment about 5 other students</div>
                        <div class="panel-body">
                          <div style="clear: both; overflow: hidden">
                            <div class="pull-left">Alice is a good coder.
                            </div>
                            <button type="button" class="btn btn-default btn-xs icon-button pull-right" data-toggle="tooltip" data-placement="top" title="Add comment">
                              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                            </button>
                          </div>

                        </div>
                      </div>

                    </div>
                  </div>

                </div>
              </div>
              <!-- third -->

              <div class="panel panel-warning">
                <div class="panel-heading" data-target="#panelBodyCollapse-12" style="cursor: pointer;">
                  <div class="display-icon pull-right">
                    <span class="glyphicon pull-right glyphicon-chevron-up"></span>
                  </div>
                  Participants who have not responded to any question
                </div>
                <div class="panel-collapse collapse in" id="panelBodyCollapse-12" style="height: auto;">
                  <div class="panel-body padding-0">
                    <table class="table table-striped table-bordered margin-0">
                      <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                      <tr>
                        <th id="button_sortFromTeam" class="button-sort-none" onclick="toggleSort(this)" style="width: 30%;">
                          Team
                        </th>
                        <th id="button_sortTo" class="button-sort-ascending" onclick="toggleSort(this)" style="width: 30%;">
                          Name
                        </th>
                        <th class="action-header">
                          Actions
                        </th>
                      </tr>
                      </thead>
                      <tbody>

                      <tr>
                        <td>Team 3</td>
                        <td>Danny Engrid</td>
                        <td class="action-button-item">
                          <form class="inline" method="post">
                            <input type="submit" class="btn btn-default btn-xs" value="Submit Responses" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled="disabled">
                          </form>
                        </td>
                      </tr>

                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

            </div>
          </div>
          <p>If you choose to publish the results of the session, students will receive an email with a link to access the
            session's results. What they can see is governed by the visibility levels you set when setting up the session.
            Here is an example of what a student might see:
          </p>
          <div class="bs-example" id="responsesStudentView">

            <div class="panel panel-default">
              <div class="panel-heading">
                <h4>Question 1: Tutor comments about the team presentation</h4>

                <div class="panel panel-primary">
                  <div class="panel-heading">
                    <b>To:</b> Team 1
                  </div>
                  <table class="table">
                    <tbody>

                    <tr class="resultSubheader">
                      <td>
                        <span class="bold">
                          <b>From:</b>
                        </span> Tutor James Hardy
                      </td>
                    </tr>
                    <tr>
                      <td class="multiline">The content was good but overran the time limit</td>
                    </tr>

                    </tbody>
                  </table>
                </div>

                <div class="panel panel-primary">
                  <div class="panel-heading">
                    <b>To:</b> Team 1
                  </div>
                  <table class="table">
                    <tbody>

                    <tr class="resultSubheader">
                      <td>
                        <span class="bold">
                          <b>From:</b>
                        </span> Dr Lee Davis
                      </td>
                    </tr>
                    <tr>
                      <td class="multiline">Good presentation.Please keep to the time limit
                      </td>
                    </tr>

                    </tbody>
                  </table>
                </div>

              </div>
            </div>

            <div class="panel panel-default">
              <div class="panel-heading">
                <h4>
                  Question 2: Was this team member punctual?
                  <br>
                  <small>
                    Multiple-choice (single answer) options:
                    <ul style="list-style-type: disc;">
                      <li>Yes</li>
                      <li>No</li>
                    </ul>

                  </small>

                </h4>

                <div class="panel panel-primary">
                  <div class="panel-heading">
                    <b>To:</b> You
                  </div>
                  <table class="table">
                    <tbody>

                    <tr class="resultSubheader">
                      <td>
                        <span class="bold">
                          <b>From:</b>
                        </span> anonymous
                      </td>
                    </tr>
                    <tr>
                      <td class="multiline">No</td>
                    </tr>

                    </tbody>
                  </table>
                </div>

                <div class="panel panel-primary">
                  <div class="panel-heading">
                    <b>To:</b> You
                  </div>
                  <table class="table">
                    <tbody>

                    <tr class="resultSubheader">
                      <td>
                        <span class="bold">
                          <b>From:</b>
                        </span> anonymous
                      </td>
                    </tr>
                    <tr>
                      <td class="multiline">No</td>
                    </tr>

                    </tbody>
                  </table>
                </div>
                <div class="panel panel-primary">
                  <div class="panel-heading">
                    <b>To:</b> You
                  </div>
                  <table class="table">
                    <tbody>

                    <tr class="resultSubheader">
                      <td>
                        <span class="bold">
                          <b>From:</b>
                        </span> anonymous
                      </td>
                    </tr>
                    <tr>
                      <td class="multiline">Yes</td>
                    </tr>

                    </tbody>
                  </table>
                </div>

              </div>
            </div>

          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-view-responses">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-view-responses-body">
        <h3 class="panel-title">How do I view all the responses a student has given and received?</h3>
      </div>
      <div id="session-view-responses-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To view the responses that Student A from Course B has given and received:
          </p>
          <ol>
            <li>
              Go to the <b>Students</b> page and click the panel heading for Course B. You will see a list of students enrolled in the course.
            </li>
            <li>
              Click <button class="btn btn-xs btn-default">All Records</button> button corresponding to Student A to access all the responses Student A has given and received.
            </li>
          </ol>
        </div>
      </div>
    </div>
  </div>
  <h3>Adding Comments to Responses</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="session-add-comments">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-add-comments-body">
        <h3 class="panel-title">How do I create a comment on a response?</h3>
      </div>
      <div id="session-add-comments-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            While <a class="collapse-link" data-target='#session-view-results-body' href="#session-view-results">viewing the results</a> of a session, you can add comments to respondents' answers.
          </p>
          <p>
            To create comments on a response in a session:
          </p>
          <ol>
            <li>
              View the results of a session
            </li>
            <li>
              Click <button class="btn btn-primary btn-s">Edit View</button> and change the view type to <b>Group by - Giver &gt; Recipient &gt; Question</b> or <b>Group by - Recipient &gt; Giver &gt; Question</b>
            </li>
            <li>
              Click the <span class="glyphicon glyphicon-comment glyphicon-primary"></span> icon on the right-hand side of the response you would like to comment on
            </li>
            <li>
              Fill in the form, which will appear similar to the example below
            </li>
          </ol>
          <div class="bs-example">
            <div class="row">
              <div class="col-md-2">
                <div class="col-md-12">
                  To:
                  <strong>Rose (Team 2)</strong>

                </div>
                <div class="col-md-12 text-muted small">
                  <br> From: Alice (Team 1)

                </div>
              </div>
              <div class="col-md-10">

                <div class="panel panel-info">
                  <div class="panel-heading">Question 3:
                    <span>Rate the latest assignment's difficulty. (1 = Very Easy, 5 = Very Hard).&nbsp;
                      <span>
                        <a href="javascript:;" id="questionAdditionalInfoButton-3-giver-0-recipient-8" class="color_gray" data-more="[more]" data-less="[less]">[more]</a>
                        <br>
                        <span id="questionAdditionalInfo-3-giver-0-recipient-8" style="display:none;">Numerical-scale question:
                          <br>Minimum value: 1. Increment: 1.0. Maximum value: 5.
                        </span>
                      </span>
                    </span>
                  </div>
                  <div class="panel-body">
                    <div style="clear:both; overflow: hidden">
                      <div class="pull-left">4</div>
                      <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" data-toggle="tooltip" data-placement="top" title="Add comment">
                        <span class="glyphicon glyphicon-comment glyphicon-primary">
                        </span>
                      </button>
                    </div>

                    <ul class="list-group" id="responseCommentTable-8-0-1" style="margin-top: 15px;">

                      <!-- frComment Add form -->
                      <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-8-0-1" style="">
                        <form class="responseCommentAddForm">
                          <div class="form-group">
                            <div class="form-group form-inline">
                              <div class="form-group text-muted">
                                <p>
                                  Giver: Alice (Team 1)
                                  <br>
                                  Recipient: Rose (Team 2)
                                </p>
                                You may change comment's visibility using the visibility options on the right hand side.
                              </div>
                              <a id="frComment-visibility-options-trigger-8-0-1" class="btn btn-sm btn-info pull-right">
                                <span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options
                              </a>
                            </div>
                            <div id="visibility-options-8-0-1" class="panel panel-default" style="display: none;">
                              <div class="panel-heading">Visibility Options</div>
                              <table class="table text-center" style="color:#000;">
                                <tbody>
                                <tr>
                                  <th class="text-center">User/Group</th>
                                  <th class="text-center">Can see this comment</th>
                                  <th class="text-center">Can see comment giver's name</th>
                                </tr>
                                <tr id="response-giver-8-0-1">
                                  <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="Control what response giver can view">
                                      Response Giver
                                    </div>
                                  </td>
                                  <td>
                                    <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="GIVER" checked="">
                                  </td>
                                  <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="GIVER" checked="">
                                  </td>
                                </tr>

                                <tr id="response-recipient-8-0-1">
                                  <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="Control what response recipient(s) can view">
                                      Response Recipient(s)
                                    </div>
                                  </td>
                                  <td>
                                    <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" checked="">
                                  </td>
                                  <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" checked="">
                                  </td>
                                </tr>

                                <tr id="response-instructors-8-0-1">
                                  <td class="text-left">
                                    <div data-toggle="tooltip" data-placement="top" title="Control what instructors can view">
                                      Instructors
                                    </div>
                                  </td>
                                  <td>
                                    <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTORS" checked="">
                                  </td>
                                  <td>
                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTORS" checked="">
                                  </td>
                                </tr>
                                </tbody>
                              </table>
                            </div>
                            <textarea class="form-control" rows="3" placeholder="Your comment about this response" name="responsecommenttext" id="responseCommentAddForm-8-0-1"></textarea>
                          </div>
                          <div class="col-sm-offset-5">
                            <a href="javascript:;" type="button" class="btn btn-primary" id="button_save_comment_for_add-8-0-1">Add</a>
                            <input type="button" class="btn btn-default" value="Cancel">
                          </div>
                        </form>
                      </li>
                    </ul>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-edit-delete-comments">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-edit-delete-comments-body">
        <h3 class="panel-title">How do I edit or delete a comment on a response?</h3>
      </div>
      <div id="session-edit-delete-comments-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To edit or delete a comment that you previously made on a response:
          </p>
          <ol>
            <li>
              Navigate to the page where you <a class="collapse-link" data-target="#session-add-comments-body" href="#session-add-comments">added the comment</a> that you want to edit or delete.
            </li>
            <li>
              Hover over the comment which you want to edit or delete.
            </li>
            <li>
              Click the <span class="glyphicon glyphicon-pencil glyphicon-primary"></span> icon to edit the comment, or
              or <span class="glyphicon glyphicon-trash glyphicon-primary"></span> icon to delete the comment. The icons are visible on the right-hand side of the comment field.
            </li>
            <li>
              If you are editing the comment, make your edits and click <button class="btn btn-primary btn-s">Save</button> to save changes.
            </li>
            <li>
              If you are deleting the comment, click <b>OK</b> to confirm that you want to delete the comment.
            </li>
          </ol>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="session-search">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#session-search-body">
        <h3 class="panel-title">How do I search for a feedback session question, response or comment on a response?</h3>
      </div>
      <div id="session-search-body" class="panel-collapse collapse">
        <div class="panel-body">
          You can search for questions, responses to a question and comments on responses in any of your courses. To do so:
          <ol>
            <li>
              Go to the <b>Search</b> page using the top navigation bar.
            </li>
            <li>
              Tick the option <b>Questions, responses, comments on responses</b> below the search bar.
            </li>
            <li>
              Type in the keywords of your search.
            </li>
            <li>
              Click <button class="btn btn-primary btn-xs">Search</button>.
            </li>
          </ol>
          <p>
            Assuming the relevant data exists, the results for a search with the keyword <code>good</code> would look something similar to this:
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <strong>Questions, responses, comments on responses</strong>
              </div>
              <div class="panel-body">
                <div class="row">
                  <div class="col-md-2">
                    <strong>
                      Session: Session 1 (Course 1)
                    </strong>
                  </div>
                  <div class="col-md-10">
                    <div class="panel panel-info">
                      <div class="panel-heading">
                        <b>Question 2</b>: What has been a highlight for you working on this project?
                      </div>
                      <table class="table">
                        <tbody>
                        <tr>
                          <td>
                            <b>From:</b> Alice Betsy (Team A)
                            <b>To:</b> Alice Betsy (Team A)
                          </td>
                        </tr>
                        <tr>
                          <td>
                            <strong>Response:</strong> A highlight for me has been putting Software Engineering skills to use.
                          </td>
                        </tr>
                        <tr class="active">
                          <td>Comment(s):</td>
                        </tr>
                        <tr>
                          <td>
                            <ul class="list-group comments">
                              <li class="list-group-item list-group-item-warning">
                                <div>
                                  <span class="text-muted">
                                    From: instructor@university.edu [Tue, 23 May 2017, 11:59 PM UTC]
                                  </span>
                                </div>
                                <div style="margin-left: 15px;">Alice, <span class="highlight">good</span> to know that you liked applying software engineering skills in the project.</div>
                              </li>
                            </ul>
                          </td>
                        </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
