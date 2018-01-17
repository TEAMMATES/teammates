<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="sessionTypes">Sessions</h4>
<div id="contentHolder">
  <br>
  <ol style="list-style-type: none;">
    <li id="fbSetupSession">
      <span class="text-bold">
          <b>1. Setting up a feedback session</b>
        <br>
      </span>
      <div class="row">
        <div class="col-lg-6">
          To quickly create a session,
          <p></p>
          <ol>
            <li>
              Select a
              <b>Session type</b>
              <br>
              <span class="text-muted">
                <ul>
                  <li>
                    Session with your own questions
                  </li>
                  <li>
                    Team peer evaluation session (creates 5 standard questions)
                  </li>
                </ul>
              </span>
            </li>
            <li>
              Select a
              <b>Course ID</b> for which the session will be created
            </li>
            <li>
              Give it a nice
              <b>Session name</b>
            </li>
            <li>
              Select the
              <b>Submission opening/closing time</b>
              <span class="text-muted">
                <ul>
                  <li>
                    This is the time period where a student can submit responses.
                  </li>
                </ul>
              </span>
            </li>
            <li>
              Click
              <b>Create Feedback Session</b>!
            </li>
          </ol>
          <br>
          <br>
          <span class="text-muted">
            <a href="#fbSetupQuestions">Next step: Setting up questions</a>
          </span>
        </div>
        <div class="col-lg-6">
            <b>Advanced options:</b>
          <ul>
            <li>
              <b>Copy from previous feedback sessions</b>
              <span class="text-muted">
                <ul>
                  <li>
                    Copy from previously created feedback sessions to save time creating the same questions!
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Time zone</b>
              <span class="text-muted">
                <ul>
                  <li>
                    This is auto-detected from your computer and you should not need to change it.
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Instructions</b>
              <span class="text-muted">
                <ul>
                  <li>
                    Give more specific instructions to students
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Grace period</b>
              <span class="text-muted">
                <ul>
                  <li>
                    A small amount of time where students can still submit responses if the session closes while they are completing the session.
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Session visible from</b>
              <span class="text-muted">
                <ul>
                  <li>
                    When the students can see the questions (but cannot submit, until the session is
                    <i>open</i>)
                  </li>
                  <li>
                    Note the options for making the session private (for you to record your feedback about students, only you can see the session)
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Responses visible from</b>
              <span class="text-muted">
                <ul>
                  <li>
                    When the students can see results and responses from others.
                  </li>
                  <li>
                    Note the option for publishing the responses automatically at a given time.
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Send emails for</b>
              <span class="text-muted">
                <ul>
                  <li>
                    When emails are sent to students
                  </li>
                </ul>
              </span>
            </li>
            <li>
              <b>Private session</b>
              <span class="text-muted">
                <ul>
                  <li>
                    A private session is a session that is never visible to others.
                    This is for you to record your feedback about students, only you can see this session.
                    If you want to create a private session then set the "Session visible from" to Never.
                  </li>
                </ul>
              </span>
            </li>
          </ul>
        </div>
      </div>
      <br>
      <br>
      <p>
        This is the form used to set up sessions.
        <br>
        <br>
      </p>

      <div class="bs-example">
        <div id="createSessionHtmlCustomizable">

          <div class="well well-plain">
            <form class="form-group" name="form_feedbacksession">
              <div class="row" data-toggle="tooltip" data-placement="top" title="Select a different type of session here.">
                <h4 class="label-control col-md-2 text-md">Create new </h4>
                <div class="col-md-5">
                  <select class="form-control" name="fstype" id="fstype">
                    <option value="STANDARD" selected="">
                      Session with your own questions
                    </option>
                    <option value="TEAMEVALUATION">
                      Team peer evaluation session
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
                    <div class="col-md-6" data-toggle="tooltip" data-placement="top" title="You should not need to change this as your timezone is auto-detected. <br><br>However, note that daylight saving is not taken into account i.e. if you are in UTC -8:00 and there is daylight saving, you should choose UTC -7:00 and its corresponding timings.">
                      <div class="form-group">
                        <h5 class="col-sm-4">
                          <label for="timezone" class="control-label">Timezone</label>
                        </h5>
                        <div class="col-sm-8">
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
                            <option value="-4">(UTC -04:00) Santiago, La Paz, San Juan de Puerto Rico, Manaus, Halifax</option>
                            <option value="-3.5">(UTC -03:30) St. John's</option>
                            <option value="-3">(UTC -03:00) Buenos Aires, Montevideo, São Paulo</option>
                            <option value="-2">(UTC -02:00) Fernando de Noronha, South Georgia and the South Sandwich Islands</option>
                            <option value="-1">(UTC -01:00) Cape Verde, Greenland, Azores islands</option>
                            <option value="0">(UTC) Accra, Abidjan, Casablanca, Dakar, Dublin, Lisbon, London</option>
                            <option value="1">(UTC +01:00) Belgrade, Berlin, Brussels, Lagos, Madrid, Paris, Rome, Tunis, Vienna, Warsaw</option>
                            <option value="2">(UTC +02:00) Athens, Sofia, Cairo, Kiev, Istanbul, Beirut, Helsinki, Jerusalem, Johannesburg, Bucharest</option>
                            <option value="3">(UTC +03:00) Nairobi, Baghdad, Doha, Khartoum, Minsk, Riyadh</option>
                            <option value="3.5">(UTC +03:30) Tehran</option>
                            <option value="4">(UTC +04:00) Baku, Dubai, Moscow</option>
                            <option value="4.5">(UTC +04:30) Kabul</option>
                            <option value="5">(UTC +05:00) Karachi, Tashkent</option>
                            <option value="5.5">(UTC +05:30) Colombo, Delhi</option>
                            <option value="5.75">(UTC +05:45) Kathmandu</option>
                            <option value="6">(UTC +06:00) Almaty, Dhaka, Yekaterinburg</option>
                            <option value="6.5">(UTC +06:30) Yangon</option>
                            <option value="7">(UTC +07:00) Jakarta, Bangkok, Novosibirsk, Hanoi</option>
                            <option value="8">(UTC +08:00) Perth, Beijing, Manila, Singapore, Kuala Lumpur, Denpasar, Krasnoyarsk</option>
                            <option value="8.75">(UTC +08:45) Eucla</option>
                            <option value="9">(UTC +09:00) Seoul, Tokyo, Pyongyang, Ambon, Irkutsk</option>
                            <option value="9.5">(UTC +09:30) Adelaide</option>
                            <option value="10">(UTC +10:00) Canberra, Yakutsk, Port Moresby</option>
                            <option value="10.5">(UTC +10:30) Lord Howe Islands</option>
                            <option value="11">(UTC +11:00) Vladivostok, Noumea</option>
                            <option value="12">(UTC +12:00) Auckland, Suva</option>
                            <option value="12.75">(UTC +12:45) Chatham Islands</option>
                            <option value="13">(UTC +13:00) Phoenix Islands, Tokelau, Tonga</option>
                            <option value="14">(UTC +14:00) Line Islands</option>

                          </select>
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
                            Session visible from
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
                          <label class="label-control">Responses visible from</label>
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
                        <div class="col-md-4" data-toggle="tooltip" data-placement="top" title="Select this option if you intend to manually publish the session later on.">
                          <label for="resultsVisibleFromButton_later">Publish manually
                          </label>
                          <input type="radio" name="resultsVisibleFromButton" id="resultsVisibleFromButton_later" value="later">
                        </div>
                      </div>
                      <div class="row radio">
                        <div class="col-md-2" data-toggle="tooltip" data-placement="top" title="Select this option if you intend never to publish the responses.">
                          <label for="resultsVisibleFromButton_never">Never</label>
                          <input type="radio" name="resultsVisibleFromButton" id="resultsVisibleFromButton_never" value="never">
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
    </li>
    <br>
    <br>
    <br>
    <li id="fbSetupQuestions">
      <span class="text-bold">
          <b>2. Setting up questions</b>
      </span>
      <div>

        <p>
          After setting up a session, you will be brought to the session edit page to setup the questions. (You can also get here by clicking the edit button action for a particular session)
          <br> Depending on the session type that you selected, you might already see some questions.
          <br>
          <br> At the bottom of the page, you can add questions to the session:
        </p>

      </div>

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

      <div class="row">
        <div class="col-sm-6">
          <p>There are several question types to choose from.
            <br>See
            <a href="#fbQuestionTypes">here</a> for more information on each question type.
          </p>
          <ul>
            <li>
              <a href="#fbEssay">Essay question</a>
            </li>
            <li>
              <a href="#fbMcq">Multiple-choice (single answer) question</a>
            </li>
            <li>
              <a href="#fbMsq">Multiple-choice (multiple answers) question</a>
            </li>
            <li>
              <a href="#fbNumscale">Numerical-scale question</a>
            </li>
            <li>
              <a href="#fbConstSumOptions">Distribute points (among options) question</a>
            </li>
            <li>
              <a href="#fbConstSumRecipients">Distribute points (among recipients) question</a>
            </li>
            <li>
              <a href="#fbContrib">Team contribution question</a>
            </li>
            <li>
              <a href="#fbRubric">Rubric question</a>
            </li>
            <li>
              <a href="#fbRankOptions">Rank Options question</a>
            </li>
            <li>
              <a href="#fbRankRecipients">Rank Recipients question</a>
            </li>
          </ul>
          <br>
          <p>After selecting a question type, click
            <b>Add New Question</b> to add it to the session
          </p>
        </div>
        <div class="col-sm-6">
          <p>
            You can also
            <b>Copy Question</b> from existing feedback sessions and reuse the questions and their options to quickly set up the session.
            <br>
            <br>
            <br> Click
            <b>Done Editing</b> when you are finished with setting up all questions to return to the home page.
          </p>
        </div>
      </div>
      <br>
      <br>
      <p>
        Here, we set up a particular question after adding it to the session.
        <br> The following can be set:
      </p>
      <ul>
        <li>
          <b>Question text</b>
          <span class="text-muted"> - the question</span>
        </li>
        <li>
          <b>Feedback Path</b>
          <span class="text-muted"> - you can select a common feedback path from the dropdown menu, or choose "Other predefined combinations..." to select the Feedback Giver and Recipient separately.</span>
          <ul>
            <li>
              <b>Feedback Giver</b>
              <span class="text-muted"> - who gives the response (If you choose a ‘team’ as the giver, any member can submit the response on behalf of the team.)</span>
            </li>
            <li>
              <b>Feedback Recipient</b>
              <span class="text-muted"> - who the response is meant for/directed towards</span>
            </li>
          </ul>
        </li>
        <li>
          <b>Visibility options</b>
          <span class="text-muted"> - you can select a common visibility option from the dropdown menu, or choose "Custom visibility options..." to fully customize who can see the feedback response, the giver's identity, and the recipient's identity</span>
        </li>
      </ul>
      <p>
        <br> Given the example below, note how we can set the feedback path and visibility level for each question.
        <br> For the feedback path, "Other predefined combinations..." has been selected and further configured to be
        <b>for instructors</b> (i.e. you, tutors, co-lecturers) to give feedback
        <b>to the class in general</b>.
        <br> As for visibility options, only the instructors can see all the feedback responses as well as who gave feedback to whom.
        <br>
        <br>
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
                <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">
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

      <br>
      <span class="alert alert-success">
        <span class="glyphicon glyphicon-info-sign"></span> Remember to
        <b>Save Changes</b> to the question after editing!
      </span>
      <br>
      <br>When the session is open, an instructor can answer the above question. This is how it will look like:
      <br>
      <br>

      <div class="bs-example" id="intructorQuestionAnswer">

        <div class="form-horizontal">

          <div class="panel panel-primary">
            <div class="panel-heading">Question 1:
              <br>
              <span>Tutor's comments about the team presentation</span>
            </div>
            <div class="panel-body">
              <p class="text-muted">Only the following persons can see your responses: </p>
              <ul class="text-muted">

                <li class="unordered">The receiving teams can see your response, but not your name.</li>

                <li class="unordered">Instructors in this course can see your response, the name of the recipient, and your name.</li>

              </ul>

              <br>
              <div class="form-group margin-0">
                <div class="col-sm-4 form-inline mobile-align-left" style="text-align:right">
                  <label for="input">
                    <span data-toggle="tooltip" data-placement="top" title="" data-original-title="The party being evaluated or given feedback to" class="tool-tip-decorate">Evaluee </span> (Team):
                  </label>
                  <select class="participantSelect middlealign newResponse form-control" name="responserecipient-1-0" style="width:200px;max-width:275px">
                    <option value="" selected=""></option>
                    <option value="Team 1">Team 1</option>
                    <option value="Team 2">Team 2</option>
                    <option value="Team 3">Team 3</option>
                  </select>
                </div>
                <div class="col-sm-8">
                  <div id="rich-text-toolbar-response-text-container-1-0"></div>
                  <div class="panel panel-default panel-body mce-content-body content-editor empty" id="responsetext-1-0" name="responsetext-1-0" data-length-text-id="responseLength-1-0" data-recommended-text="0" contenteditable="true" style="position: relative;" spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="responsetext-1-0">
                  <div class="margin-top-7px text-color-gray font-weight-normal">
                    <div class="col-md-6 padding-0" style="display:none">
                      Recommended length for the answer: <span id="recommendedLength-1-0">0 </span>words
                    </div>
                    <div class="pull-right">
                      Response length: <span id="responseLength-1-0" style="color: rgb(128, 128, 128);">0</span> words
                    </div>
                  </div>
                </div>
              </div>

              <br>
              <div class="form-group margin-0">
                <div class="col-sm-4 form-inline mobile-align-left" style="text-align:right">
                  <label for="input">
                    <span data-toggle="tooltip" data-placement="top" title="" data-original-title="The party being evaluated or given feedback to" class="tool-tip-decorate">Evaluee </span>(Team):
                  </label>
                  <select class="participantSelect middlealign newResponse form-control" name="responserecipient-1-0" style="width:200px;max-width:275px">
                    <option value="" selected=""></option>
                    <option value="Team 1">Team 1</option>
                    <option value="Team 2">Team 2</option>
                    <option value="Team 3">Team 3</option>
                  </select>
                </div>
                <div class="col-sm-8">
                  <div id="rich-text-toolbar-response-text-container-1-0"></div>
                  <div class="panel panel-default panel-body mce-content-body content-editor empty" id="responsetext-1-0" name="responsetext-1-0" data-length-text-id="responseLength-1-0" data-recommended-text="0" contenteditable="true" style="position: relative;" spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="responsetext-1-0">
                  <div class="margin-top-7px text-color-gray font-weight-normal">
                    <div class="col-md-6 padding-0" style="display:none">
                      Recommended length for the answer: <span id="recommendedLength-1-0">0 </span>words
                    </div>
                    <div class="pull-right">
                      Response length: <span id="responseLength-1-0" style="color: rgb(128, 128, 128);">0</span> words
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

        </div>
      </div>
      <br>
      <br>
      <br>Here is another example question:
      <br>
      <br>

      <div class="bs-example">
        <form class="form-horizontal form_question" role="form">
          <div class="panel panel-primary questionTable" id="intrHelpQuestionTable">
            <div class="panel-heading">
              <div class="row">
                <div class="col-sm-12">
                  <span>
                    <strong>Question</strong>
                    <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-1">
                      <option value="1">1</option>
                      <option value="2">2</option>
                      <option value="3">3</option>
                      <option value="4">4</option>
                      <option value="5">5</option>
                      <option value="6">6</option>
                      <option value="7">7</option>
                      <option value="8">8</option>
                      <option value="9">9</option>
                      <option value="10">10</option>
                      <option value="11">11</option>
                      <option value="12">12</option>
                      <option value="13">13</option>
                      <option value="14">14</option>

                    </select>
                    &nbsp; Multiple-choice (single answer)
                  </span>
                  <span class="pull-right">
                    <a class="btn btn-primary btn-xs" id="questionedittext-1" data-toggle="tooltip" data-placement="top" title="Edit this question" style="display: none;">Edit</a>
                    <a class="btn btn-primary btn-xs" style="" onclick="return false" id="questionsavechangestext-1">Cancel</a>
                  </span>
                </div>
              </div>
            </div>
            <div class="panel-body">
              <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-blue">
                <div class="form-group" style="padding: 15px;">
                  <h5 class="col-sm-2">
                    <label class="control-label" for="questiontext--1">
                      Question
                    </label>
                  </h5>
                  <div class="col-sm-10">
                    <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext--1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?"></textarea>
                  </div>
                </div>
                <div class="form-group" style="padding: 15px;">
                  <h5 class="col-sm-2">
                    <label class="align-left" for="questiondescription--1">
                      [Optional]<br>Description
                    </label>
                  </h5>
                  <div class="col-sm-10">
                    <div id="rich-text-toolbar-q-descr-container"></div>
                    <div class="panel panel-default panel-body question-description mce-content-body content-editor empty" id="questiondescription--1" data-toggle="tooltip" data-placement="top" title="" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" tabindex="9" data-original-title="Please enter the description of the question." contenteditable="true" style="position: relative;" spellcheck="false"><p><br data-mce-bogus="1"></p></div>
                    <input type="hidden" name="questiondescription--1" disabled="" value="">
                    <input type="hidden" name="questiondescription" value="">
                  </div>
                  <div id="mcqForm" style="display: block;">
                    <div class="row">
                      <br>
                      <div class="col-sm-6">
                        <div id="mcqChoiceTable--1">
                          <div class="margin-bottom-7px" id="mcqOptionRow-0--1">
                            <div class="input-group width-100-pc">
                              <span class="input-group-addon">
                                <input type="radio" class="disabled_radio" disabled="">
                              </span>
                              <input class="form-control" type="text" name="mcqOption-0" id="mcqOption-0--1" value="">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="mcqRemoveOptionLink" onclick="removeMcqOption(0,-1)" style="" tabindex="-1">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>
                          <div class="margin-bottom-7px" id="mcqOptionRow-1--1">
                            <div class="input-group width-100-pc">
                              <span class="input-group-addon">
                                <input type="radio" class="disabled_radio" disabled="">
                              </span>
                              <input class="form-control" type="text" name="mcqOption-1" id="mcqOption-1--1" value="">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="mcqRemoveOptionLink" onclick="removeMcqOption(1,-1)" style="" tabindex="-1">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>

                          <div id="mcqAddOptionRow--1">
                            <div colspan="2">
                              <a class="btn btn-primary btn-xs addOptionLink" id="mcqAddOptionLink--1" onclick="addMcqOption(-1)" style="">
                                <span class="glyphicon glyphicon-plus">
                                </span> add more options
                              </a>

                              <div class="checkbox">
                                <label class="bold-label">
                                  <input type="checkbox" name="mcqOtherOptionFlag" id="mcqOtherOptionFlag--1" onchange="toggleMcqOtherOptionEnabled(this, -1)">
                                  Add 'Other' option (Allows respondents to type in their own answer)
                                </label>
                              </div>
                            </div>
                          </div>
                        </div>
                        <input type="hidden" name="noofchoicecreated" id="noofchoicecreated--1" value="2">
                      </div>
                      <div class="col-sm-6 col-lg-5">
                        <div class="border-gray narrow-slight visible-xs margin-bottom-7px margin-top-7px"></div>
                        <div class="checkbox padding-top-0">
                          <label class="bold-label">
                            <span class="inline-block">
                              <input type="checkbox" id="generateOptionsCheckbox--1" onchange="toggleMcqGeneratedOptions(this,-1)">
                              Or, generate options from the list of all
                            </span>
                          </label>
                          <select class="form-control width-auto inline" id="mcqGenerateForSelect--1" onchange="changeMcqGenerateFor(-1)" disabled="">
                            <option value="STUDENTS">students</option>
                            <option value="TEAMS">teams</option>
                            <option value="INSTRUCTORS">instructors</option>
                          </select>
                        </div>
                        <input type="hidden" id="generatedOptions--1" name="generatedOptions" value="NONE">
                      </div>
                      <br>
                    </div>
                  </div>
                </div>
              </div>
              <br>
              <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                <div class="col-sm-12 padding-0 margin-bottom-7px">
                  <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                </div>
                <div class="feedback-path-dropdown col-sm-12 btn-group">
                  <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    Please select a feedback path <span class="caret"></span>
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
                    Please select a visibility option <span class="caret"></span>
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
                <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display:none;">
                  <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                    <tbody>
                      <tr>
                        <th class="text-center">User/Group</th>
                        <th class="text-center">Can see answer</th>
                        <th class="text-center">Can see giver's name</th>
                        <th class="text-center">Can see recipient's name</th>
                      </tr>
                      <tr>
                        <td class="text-left">
                          <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what feedback recipient(s) can view">
                            Recipient(s)
                          </div>
                        </td>
                        <td>
                          <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" checked="">
                        </td>
                        <td>
                          <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" checked="">
                        </td>
                        <td>
                          <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="RECEIVER" disabled="" checked="">
                        </td>
                      </tr>
                      <tr>
                        <td class="text-left">
                          <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what team members of feedback giver can view">
                            Giver's Team Members
                          </div>
                        </td>
                        <td>
                          <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS">
                        </td>
                        <td>
                          <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS">
                        </td>
                        <td>
                          <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS">
                        </td>
                      </tr>
                      <tr>
                        <td class="text-left">
                          <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what team members of feedback recipients can view">
                            Recipient's Team Members
                          </div>
                        </td>
                        <td>
                          <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS">
                        </td>
                        <td>
                          <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS">
                        </td>
                        <td>
                          <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS">
                        </td>
                      </tr>
                      <tr>
                        <td class="text-left">
                          <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what other students can view">
                            Other students
                          </div>
                        </td>
                        <td>
                          <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="STUDENTS">
                        </td>
                        <td>
                          <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="STUDENTS">
                        </td>
                        <td>
                          <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="STUDENTS">
                        </td>
                      </tr>
                      <tr>
                        <td class="text-left">
                          <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what instructors can view">
                            Instructors
                          </div>
                        </td>
                        <td>
                          <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTORS" checked="">
                        </td>
                        <td>
                          <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTORS" checked="">
                        </td>
                        <td>
                          <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="INSTRUCTORS" checked="">
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">
                  This is the visibility hint as seen by the feedback giver:
                  <ul class="text-muted background-color-warning">

                    <li>You can see your own feedback in the results page later on.</li>

                    <li>Instructors in this course can see your response, the name of the recipient, and your name.</li>

                  </ul>
                </div>
              </div>
              <div>
                <span class="pull-right">
                  <input id="button_question_submit-1" type="submit" onclick="return false;" class="btn btn-primary" value="Save Question" tabindex="0" style="">
                </span>
              </div>
            </div>
          </div>
        </form>
      </div> This question is for students to give feedback to their own team members. The feedback receiver can see the feedback, but not who gave the feedback. Instructors can see who received what feedback, but not who gave the feedback.
      <br>
      <br>As you can see, these session are very flexible and can cater for a wide variety of feedback scenarios, all within a single session.
      <br>

    </li>
    <br>
    <br>
    <br>
    <li id="fbPreview">
      <span class="text-bold">
          <b>3. Previewing a session</b>
        </a>
      </span>

      <p>
        To see how the session looks like to anyone in the course, we can use the
        <b>Preview as</b> feature to take a look at how the current session would look like to any student or instructor when they are submitting responses.
        <br>
        <br> This allows easy and convenient confirmation that the questions and their settings are correct.
      </p>

      <div class="bs-example" id="preview">
        <div class="well well-plain" id="questionPreviewTable">
          <div class="row">
            <form class="form-horizontal">
              <label class="control-label col-sm-2 text-right">
                Preview Session:
              </label>
            </form>
            <div class="col-sm-5" data-toggle="tooltip" data-placement="top" title="View how this session would look like to a student who is submitting feedback.<br>Preview is unavailable if the course has yet to have any student enrolled.">
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
    </li>
    <br>
    <br>
    <li id="fbViewResults">
      <span class="text-bold">
          <b>4. Viewing results</b>
        </a>
      </span>

      <p>
        You can view results of sessions by clicking the
        <b>View Results</b> action of a session
        <br>You can see responses in several different formats using the following controls.
        <br>
        <br> There are
        <b>5</b> different views available, which indicate the order in which responses are grouped. Additionally, there are also options to
        <b>group the results by team</b> and
        <b>show/hide statistics</b> (available in some views), as well as options to view responses from a particular section.
        <br> We can also filter results on the page using the filter function.
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

      An example is given below:
      <br>
      <br>

      <div class="bs-example" id="responcesSortbyGiver">
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
      <br>
      <br>
      <br>If you publish results, students receive an email with a link to access responses. What they can see is governed by the visibility levels you set when setting up the session. Here is an example of what a student sees:
      <br>
      <br>
      <br>

      <div class="bs-example">

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
      <br>
      <br>
      <br>
    </li>
    <li id="fbQuestionTypes">
      <span class="text-bold">
          <b>5. Question Types</b>
        </a>
      </span> TEAMMATES currently provides the following question types. Click to see details for each question type.
      <ul>
        <li>
          <a href="#fbEssay">Essay question</a>
        </li>
        <li>
          <a href="#fbMcq">Multiple-choice (single answer) question</a>
        </li>
        <li>
          <a href="#fbMsq">Multiple-choice (multiple answers) question</a>
        </li>
        <li>
          <a href="#fbNumscale">Numerical-scale question</a>
        </li>
        <li>
          <a href="#fbConstSumOptions">Distribute points (among options) question</a>
        </li>
        <li>
          <a href="#fbConstSumRecipients">Distribute points (among recipients) question</a>
        </li>
        <li>
          <a href="#fbContrib">Team contribution question</a>
        </li>
        <li>
          <a href="#fbRubric">Rubric question</a>
        </li>
        <li>
          <a href="#fbRankOptions">Rank Options question</a>
        </li>
        <li>
          <a href="#fbRankRecipients">Rank Recipients question</a>
        </li>
      </ul>
      <br>
      <div class="row">
        <div class="col-sm-12">
            <b id="fbEssay">Essay Question</b>
          <br>
          Essay questions are open ended questions that allow students to give text feedback for a question.
          <br> To setup a question of this type, simply provide the question text, feedback path(giver/recipient) and visibility options.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form">
              <div class="panel panel-primary questionTable" id="essayQuestionTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-1" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Essay question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-2" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(2,5)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(2)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-2">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-2" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Comments about my contribution (shown to other teammates)</textarea>
                      </div>
                    </div>

                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-2">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-2"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-2" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false">
                          <p><br data-mce-bogus="1"></p>
                        </div>
                        <input type="hidden" name="questiondescription-2">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div>
                        <br>
                        <div class="row">
                          <div class="col-xs-12 question-recommended-length">
                            [Optional]
                            <span data-toggle="tooltip" data-placement="top" title="" data-original-title="The recommended length is shown to the respondent but not enforced" class="tool-tip-decorate">
                              Recommended length
                            </span>
                            for the response:
                            <input disabled="" type="number" class="form-control" name="recommendedlength" value="">
                            words
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
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                        Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver (Self feedback)
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
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to instructors only</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-1" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-sm-12">
            <b id="fbMcq">Multiple-choice (single answer) question</b>
          <br>
          Multiple-choice (single answer) questions allows you to specify several options, and lets students select one of them as the answer.
          <br> Other than specifying several options by yourself, TEAMMATES also supports
          <b>generating options</b> based on the list of students, teams and instructors in the course.
          <br> Example with specified options:
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post">
              <div class="panel panel-primary questionTable" id="specifiedOptionsTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-7" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Multiple-choice (single answer) question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-6" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(6,6)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-6">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(6)" id="questiondiscardchanges-6" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(6)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-6">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-6" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Did you understand today's lecture?</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-6">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-6"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-6" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-6">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-6">
                          <div id="mcqChoiceTable-6">
                            <div class="margin-bottom-7px" id="mcqOptionRow-0-6">
                              <div class="input-group width-100-pc">
                                <span class="input-group-addon">
                                  <input type="radio" class="disabled_radio" disabled="">
                                </span>
                                <input class="form-control" type="text" disabled="" name="mcqOption-0" id="mcqOption-0-6" value="Yes">
                                <span class="input-group-btn">
                                  <button class="btn btn-default removeOptionLink" type="button" id="mcqRemoveOptionLink" onclick="removeMcqOption(0,6)" style="display:none" tabindex="-1" disabled="">
                                    <span class="glyphicon glyphicon-remove">
                                    </span>
                                  </button>
                                </span>
                              </div>
                            </div>
                            <div class="margin-bottom-7px" id="mcqOptionRow-1-6">
                              <div class="input-group width-100-pc">
                                <span class="input-group-addon">
                                  <input type="radio" class="disabled_radio" disabled="">
                                </span>
                                <input class="form-control" type="text" disabled="" name="mcqOption-1" id="mcqOption-1-6" value="No">
                                <span class="input-group-btn">
                                  <button class="btn btn-default removeOptionLink" type="button" id="mcqRemoveOptionLink" onclick="removeMcqOption(1,6)" style="display:none" tabindex="-1" disabled="">
                                    <span class="glyphicon glyphicon-remove">
                                    </span>
                                  </button>
                                </span>
                              </div>
                            </div>

                            <div id="mcqAddOptionRow-6">
                              <div colspan="2">
                                <a class="btn btn-primary btn-xs addOptionLink" id="mcqAddOptionLink-6" onclick="addMcqOption(6)" style="display:none">
                                  <span class="glyphicon glyphicon-plus">
                                  </span> add more options
                                </a>

                                <div class="checkbox">
                                  <label class="bold-label">
                                    <input type="checkbox" name="mcqOtherOptionFlag" id="mcqOtherOptionFlag-6" onchange="toggleMcqOtherOptionEnabled(this, 6)" disabled="">
                                    Add 'Other' option (Allows respondents to type in their own answer)
                                  </label>
                                </div>
                              </div>
                            </div>
                          </div>

                          <input type="hidden" name="noofchoicecreated" id="noofchoicecreated-6" value="2" disabled="">
                        </div>
                        <div class="col-sm-6 col-lg-5 col-lg-offset-1 padding-right-25px">
                          <div class="border-gray narrow-slight visible-xs margin-bottom-7px margin-top-7px"></div>
                          <div class="checkbox padding-top-0">
                            <label class="bold-label">
                              <span class="inline-block">
                                <input type="checkbox" disabled="" id="generateOptionsCheckbox-6" onchange="toggleMcqGeneratedOptions(this,6)">
                                Or, generate options from the list of all
                              </span>
                            </label>
                            <select class="form-control width-auto inline" id="mcqGenerateForSelect-6" onchange="changeMcqGenerateFor(6)" disabled="">
                              <option value="STUDENTS">students</option>
                              <option value="TEAMS">teams</option>
                              <option value="INSTRUCTORS">instructors</option>
                            </select>
                          </div>
                          <input type="hidden" id="generatedOptions-6" name="generatedOptions" value="NONE" disabled="">
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver (Self feedback)</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display: none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-2" name="givertype">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-2" name="recipienttype">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option style="display: block;" value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option style="display: block;" value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text">students</span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to instructors only</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: table-row;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-7" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
          </div>

          Example with generated options:
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" >
              <div class="panel panel-primary questionTable" id="generatedOptionsTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-4" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Multiple-choice (single answer) question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-1" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(1,6)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-1">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(1)" id="questiondiscardchanges-1" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(1)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-1">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Which team do you think has the best feature?</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-1">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-1"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-1" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-1">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-6">
                          <div id="mcqChoiceTable-1">

                            <div id="mcqAddOptionRow-1">
                              <div colspan="2">
                                <a class="btn btn-primary btn-xs addOptionLink" id="mcqAddOptionLink-1" onclick="addMcqOption(1)" style="display:none">
                                  <span class="glyphicon glyphicon-plus">
                                  </span> add more options
                                </a>

                                <div class="checkbox" style="display: none;">
                                  <label class="bold-label">
                                    <input type="checkbox" name="mcqOtherOptionFlag" id="mcqOtherOptionFlag-1" onchange="toggleMcqOtherOptionEnabled(this, 1)" disabled="">
                                    Add 'Other' option (Allows respondents to type in their own answer)
                                  </label>
                                </div>
                              </div>
                            </div>
                          </div>

                          <input type="hidden" name="noofchoicecreated" id="noofchoicecreated-1" value="0" disabled="">
                        </div>
                        <div class="col-sm-6 col-lg-5 col-lg-offset-1 padding-right-25px">
                          <div class="border-gray narrow-slight visible-xs margin-bottom-7px margin-top-7px"></div>
                          <div class="checkbox padding-top-0">
                            <label class="bold-label">
                              <span class="inline-block">
                                <input type="checkbox" disabled="" id="generateOptionsCheckbox-1" checked="" onchange="toggleMcqGeneratedOptions(this,1)">
                                Or, generate options from the list of all
                              </span>
                            </label>
                            <select class="form-control width-auto inline" id="mcqGenerateForSelect-1" onchange="changeMcqGenerateFor(1)" disabled="">
                              <option value="STUDENTS">students</option>
                              <option selected="" value="TEAMS">teams</option>
                              <option value="INSTRUCTORS">instructors</option>
                            </select>
                          </div>
                          <input type="hidden" id="generatedOptions-1" name="generatedOptions" value="TEAMS" disabled="">
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Nobody specific (For general class feedback)</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-2" name="givertype">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-2" name="recipienttype">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to instructors only</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-4" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
          </div>

          Multiple-choice (single answer) questions also provide some statistics for the results collected, which includes the response count for each option, and the percentage for which each option was chosen. An example for the above question:
          <br>
          <br>
          <div class="bs-example">
            <div class="panel panel-info">
              <div class="panel-heading" data-target="#panelBodyCollapse-4" style="cursor: pointer;">
                <form style="display:none;" id="seeMore-4" class="seeMoreForm-4">
                </form>
                <div class="display-icon pull-right">
                  <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                </div>
                <strong>Question 4: </strong>
                <span>Which team do you think has the best feature?&nbsp;
                  <span><a href="javascript:;" id="questionAdditionalInfoButton-4-" class="color_gray" data-more="[more]" data-less="[less]">[more]</a>
                    <br>
                    <span id="questionAdditionalInfo-4-" style="display:none;">Multiple-choice (single answer) question options:
                      <br>The options for this question is automatically generated from the list of all teams in this course.
                    </span>
                  </span>
                </span>
              </div>
              <div class="panel-collapse collapse in" id="panelBodyCollapse-4">
                <div class="panel-body padding-0" id="questionBody-3">

                  <div class="resultStatistics">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-sm-4 text-color-gray">
                          <strong>
                            Response Summary
                          </strong>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-4">
                          <table class="table margin-0">
                            <thead>
                              <tr>
                                <td>
                                  Choice
                                </td>
                                <td>
                                  Response Count
                                </td>
                                <td>
                                  Percentage
                                </td>
                              </tr>
                            </thead>
                            <tbody>
                              <tr>
                                <td>
                                  Team 1
                                </td>
                                <td>
                                  1
                                </td>
                                <td>
                                  50%
                                </td>
                              </tr>
                              <tr>
                                <td>
                                  Team 2
                                </td>
                                <td>
                                  1
                                </td>
                                <td>
                                  50%
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="table-responsive">
                    <table class="table table-striped table-bordered dataTable margin-0">
                      <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                        <tr>
                          <th id="button_sortFromTeam" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                            Team
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortFromName" class="button-sort-none" onclick="toggleSort(this,1)" style="width: 15%;">
                            Giver
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortToTeam" class="button-sort-ascending" onclick="toggleSort(this,4)" style="width: 15%;">
                            Team
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortToName" class="button-sort-none" style="width: 15%;">
                            Recipient
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortFeedback" class="button-sort-none" onclick="toggleSort(this,5)">
                            Feedback
                            <span class="icon-sort unsorted"></span>
                          </th>
                        </tr>
                      </thead>
                      <tbody>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="middlealign">-</td>
                          <td class="middlealign">-</td>
                          <td class="multiline">Team 1</td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="middlealign">-</td>
                          <td class="middlealign">-</td>
                          <td class="multiline">Team 2</td>
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

      <div class="row">
        <div class="col-sm-12">
            <b id="fbMsq">Multiple-choice (multiple answers) question</b>
          <br>
          Multiple-choice (multiple answers) question are similar to the single answer version, except that students are able to select multiple options as their response.
          <br> The setup and result statistics is similar to the single answer version. See
          <a href="#fbMcq">above</a> for details.
        </div>
      </div>
      <br>
      <div class="row">
        <div class="col-sm-12">
            <b id="fbNumscale">Numerical scale question</b>
          <br>
          Numerical scale questions are questions that allow numerical responses from students.
          <br> To set up the question, provide the question text as well as the minimum, maximum values the student can input, as well as the increment, or precision of the number that is required.
          <br> If this sounds confusing, you can fiddle with the numbers and see what the acceptable responses are.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" >
              <div class="panel panel-primary questionTable" id="numericalQuestionTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-3" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Numerical-scale question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-6" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(6,6)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-6">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(6)" id="questiondiscardchanges-6" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(6)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-6">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-6" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Rate the latest assignment's difficulty. (1 = Very Easy, 5 = Very Hard).</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-6">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-6"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-6" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-6">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div>
                        <br>
                        <div>
                          <div>
                            <div class="row">
                              <div class="col-sm-4" data-toggle="tooltip" data-placement="top" title="" data-original-title="Minimum acceptable response value">Minimum value:
                                <input disabled="" type="number" class="form-control minScaleBox" id="minScaleBox-6" name="numscalemin" value="1" onchange="updateNumScalePossibleValues(6)">
                              </div>
                              <div class="col-sm-4" data-toggle="tooltip" data-placement="top" title="" data-original-title="Value to be increased/decreased each step">Increment:
                                <input disabled="" type="number" class="form-control stepBox" id="stepBox-6" name="numscalestep" value="1" min="0.001" step="0.001" onchange="updateNumScalePossibleValues(6)">
                              </div>
                              <div class="col-sm-4" data-toggle="tooltip" data-placement="top" title="" data-original-title="Maximum acceptable response value">Maximum value:
                                <input disabled="" type="number" class="form-control maxScaleBox" id="maxScaleBox-6" name="numscalemax" value="5" onchange="updateNumScalePossibleValues(6)">
                              </div>
                            </div>
                            <br>
                            <div class="row">
                              <div class="col-sm-12">
                                <span id="numScalePossibleValues-6">[Based on the above settings, acceptable responses are: 1, 2, 3, 4, 5]</span>
                              </div>
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
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Instructors in the course</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-2" name="givertype">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-2" name="recipienttype">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-6" style="display:none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what feedback recipient(s) can view">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="RECEIVER" disabled="" checked="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what team members of feedback giver can view">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS" disabled="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what team members of feedback recipients can view">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS" disabled="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what other students can view">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="STUDENTS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="STUDENTS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="STUDENTS" disabled="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what instructors can view">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTORS" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTORS" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="INSTRUCTORS" disabled="" checked="">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-6">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving instructor can see your response, but not your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-3" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
          </div>

          Statistics for numerical scale questions are also provided, including the average, minimum and maximum of the responses given.
          <br>
          <br>
          <div class="bs-example">
            <div class="panel panel-info">
              <div class="panel-heading" data-target="#panelBodyCollapse-3" style="cursor: pointer;">
                <form style="display:none;" id="seeMore-3" class="seeMoreForm-3">
                </form>
                <div class="display-icon pull-right">
                  <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                </div>
                <strong>Question 3: </strong>
                <span>Rate the latest assignment's difficulty. (1 = Very Easy, 5 = Very Hard).&nbsp;
                  <span><a href="javascript:;" id="questionAdditionalInfoButton-3-" class="color_gray" data-more="[more]" data-less="[less]">[more]</a>
                    <br>
                    <span id="questionAdditionalInfo-3-" style="display:none;">Numerical-scale question:
                      <br>Minimum value: 1. Increment: 1.0. Maximum value: 5.
                    </span>
                  </span>
                </span>
              </div>
              <div class="panel-collapse collapse in" id="panelBodyCollapse-3">
                <div class="panel-body padding-0" id="questionBody-2">

                  <div class="resultStatistics">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-sm-4 text-color-gray">
                          <strong>
                            Response Summary
                          </strong>
                        </div>
                      </div>
                      <div class="row">
                        <form class="form-horizontal col-sm-12" role="form">
                          <div class="form-group margin-0">
                            <label class="col-sm-2 control-label font-weight-normal">Average:</label>
                            <div class="col-sm-3">
                              <p class="form-control-static">4.5</p>
                            </div>
                          </div>
                          <div class="form-group margin-0">
                            <label class="col-sm-2 control-label font-weight-normal">Minimum:</label>
                            <div class="col-sm-3">
                              <p class="form-control-static">4</p>
                            </div>
                          </div>
                          <div class="form-group margin-0">
                            <label class="col-sm-2 control-label font-weight-normal">Maximum:</label>
                            <div class="col-sm-3">
                              <p class="form-control-static">5</p>
                            </div>
                          </div>
                        </form>
                      </div>
                    </div>
                  </div>
                  <div class="table-responsive">
                    <table class="table table-striped table-bordered dataTable margin-0">
                      <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                        <tr>
                          <th id="button_sortFromName" class="button-sort-none" onclick="toggleSort(this,1)" style="width: 15%;">
                            Team
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortFromTeam" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                            Giver
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortToName" class="button-sort-none" onclick="toggleSort(this,3)" style="width: 15%;">
                            Team
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortToTeam" class="button-sort-ascending" onclick="toggleSort(this,4)" style="width: 15%;">
                            Recipient
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortFeedback" class="button-sort-none">
                            Feedback
                            <span class="icon-sort unsorted"></span>
                          </th>
                        </tr>
                      </thead>
                      <tbody>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Instructor A</td>
                          <td class="multiline">4</td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Instructor A</td>
                          <td class="multiline">5</td>
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

      <div class="row">
        <div class="col-sm-12">
            <b id="fbConstSumOptions">Distribute points (among options) question</b>
          <br>
          Distribute points (among options) question allows you to specify some options, and allow the student to split some points among these options.
          <br> To setup the question, specify the options as well as the number of points to split among the options.
          <br>
          <br> You can also specify
          <b>Points to distribute X number of options</b>, which multiplies the points specified by the number of options.
          <br> For example, if there are 5 options and
          <b>Points to distribute X number of options</b> is set to 100, students will have to split 500 points in total to the 5 options.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" >
              <div class="panel panel-primary questionTable" id="amongOptionsTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-6" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Distribute points (among options) question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-6" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(6,6)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-6">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(6)" id="questiondiscardchanges-6" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(6)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-6">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-6" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">How important are the following factors to you? Give points accordingly.</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-6">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-6"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-6" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-6">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-6" id="constSumOptionTable-6">
                          <div class="margin-bottom-7px" id="constSumOptionRow-0-6">
                            <div class="input-group width-100-pc">
                              <input class="form-control" type="text" disabled="" name="constSumOption-0" id="constSumOption-0-6" value="Grades">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="constSumRemoveOptionLink" onclick="removeConstSumOption(0,6)" style="display:none" tabindex="-1" disabled="">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>
                          <div class="margin-bottom-7px" id="constSumOptionRow-1-6">
                            <div class="input-group width-100-pc">
                              <input class="form-control" type="text" disabled="" name="constSumOption-1" id="constSumOption-1-6" value="Fun">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="constSumRemoveOptionLink" onclick="removeConstSumOption(1,6)" style="display:none" tabindex="-1" disabled="">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>

                          <div id="constSumAddOptionRow-6">
                            <div colspan="2">
                              <a class="btn btn-primary btn-xs addOptionLink" id="constSumAddOptionLink-6" onclick="addConstSumOption(6)" style="display:none">
                                <span class="glyphicon glyphicon-plus">
                                </span> add more options
                              </a>
                            </div>
                          </div>

                          <input type="hidden" name="noofchoicecreated" id="noofchoicecreated-6" value="2" disabled="">
                          <input type="hidden" name="constSumToRecipients" id="constSumToRecipients-6" value="false" disabled="">
                        </div>
                        <div class="col-sm-6">
                          <div class="form-inline">
                            <div class="row">
                              <div class="col-md-12">
                                <label class="bold-label width-100-pc margin-top-7px margin-bottom-7px tablet-no-mobile-margin-top-0">
                                  <b>Total Points to distribute: </b>
                                </label>
                              </div>

                              <div class="col-xs-12 margin-bottom-7px padding-left-35px">
                                <div class="col-xs-1">
                                  <input type="radio" id="constSumPointsTotal-6" name="constSumPointsPerOption" value="false" checked="" disabled="">
                                </div>
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Respondents will have to distribute the total points specified here among the options, e.g. if you specify 100 points here and there are 3 options, respondents will have to distribute 100 points among 3 options.">
                                  <div class="col-xs-4 padding-0 col-sm-4">
                                    <input type="number" disabled="" class="form-control width-100-pc pointsBox" name="constSumPoints" id="constSumPoints-6" value="100" min="1" step="1" onchange="updateConstSumPointsValue(6)">
                                  </div>
                                  <div class="col-xs-6 padding-0">
                                    <label class="margin-top-7px padding-left-7px">in
                                      total
                                    </label>
                                  </div>
                                </div>
                              </div>
                              <div class="col-xs-12 margin-bottom-15px padding-left-35px" id="constSumOption_Option-6">
                                <div class="col-xs-1">
                                  <input type="radio" id="constSumPointsPerOption-6" name="constSumPointsPerOption" value="true" disabled="">
                                </div>
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="The number of points to distribute will vary based on the number of options, e.g. if you specify 100 points here and there are 3 options, the total number of points to distribute among 3 options will be 300 (i.e. 100 x 3).">
                                  <div class="col-xs-4 padding-0">
                                    <input type="number" disabled="" class="form-control width-100-pc pointsBox" name="constSumPointsForEachOption" id="constSumPointsForEachOption-6" value="100" min="1" step="1" onchange="updateConstSumPointsValue(6)">
                                  </div>
                                  <div class="col-xs-6 padding-0">
                                    <label class="margin-top-7px padding-left-7px">X</label>
                                    <label class="margin-top-7px"> (number of options) </label>
                                  </div>
                                </div>
                              </div>
                              <div class="col-xs-12 margin-bottom-15px padding-left-35px" id="constSumOption_Recipient-6" style="display:none">
                                <div class="col-xs-1">
                                  <input type="radio" id="constSumPointsPerRecipient-6" name="constSumPointsPerOption" value="true" disabled="">
                                </div>
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="The number of points to distribute will vary based on the number of recipients, e.g. if you specify 100 points here and there are 3 recipients, the total number of points to distribute among 3 recipients will be 300 (i.e. 100 x 3).">
                                  <div class="col-xs-4 padding-0">
                                    <input type="number" disabled="" class="form-control width-100-pc pointsBox" name="constSumPointsForEachRecipient" id="constSumPointsForEachRecipient-6" value="100" min="1" step="1" onchange="updateConstSumPointsValue(6)">
                                  </div>
                                  <div class="col-xs-6 padding-0">
                                    <label class="margin-top-7px padding-left-7px">X</label>
                                    <label class="margin-top-7px">(number of recipients)</label>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="row">
                            <div class="col-sm-12">
                              <div class="checkbox" id="constSum_tooltipText-6" data-toggle="tooltip" data-placement="top" data-container="body" title="" data-original-title="Ticking this prevents a giver from distributing the same number of points to multiple options">
                                <label class="bold-label">
                                  <input type="checkbox" name="constSumUnevenDistribution" disabled="" id="constSum_UnevenDistribution-6">
                                  <span id="constSum_labelText-6">Every option to receive a different number of points</span>
                                </label>
                              </div>
                            </div>
                          </div>
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver (Self feedback)</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-2" name="givertype">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-2" name="recipienttype">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to instructors only</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-9" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>

            When viewing results, statistics on the average points for each option are provided.
            <br>
            <br>
            <div class="bs-example">
              <div class="panel panel-info">
                <div class="panel-heading" data-target="#panelBodyCollapse-9" style="cursor: pointer;">
                  <div class="display-icon pull-right">
                    <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                  </div>
                  <strong>Question 9: </strong>
                  <span>How important are the following factors to you? Give points accordingly.&nbsp;
                    <span>
                      <a href="javascript:;" id="questionAdditionalInfoButton-9-" class="color_gray" data-more="[more]" data-less="[less]">[more]</a>
                      <br>
                      <span id="questionAdditionalInfo-9-" style="display:none;">Distribute points (among options) question options:
                        <ul style="list-style-type: disc;margin-left: 20px;">
                          <li>Grades</li>
                          <li>Fun</li>
                        </ul>Total points: 100
                      </span>
                    </span>
                  </span>
                </div>
                <div class="panel-collapse collapse in" id="panelBodyCollapse-9">
                  <div class="panel-body padding-0" id="questionBody-8">

                    <div class="resultStatistics">
                      <div class="panel-body">
                        <div class="row">
                          <div class="col-sm-4 text-color-gray">
                            <strong>
                              Response Summary
                            </strong>
                          </div>
                        </div>
                        <div class="row">
                          <div class="col-sm-4">
                            <table class="table margin-0">
                              <thead>
                                <tr>
                                  <td>
                                    Option
                                  </td>
                                  <td>
                                    Average Points
                                  </td>
                                </tr>
                              </thead>
                              <tbody>
                                <tr>
                                  <td>
                                    Grades
                                  </td>
                                  <td>
                                    32
                                  </td>
                                </tr>
                                <tr>
                                  <td>
                                    Fun
                                  </td>
                                  <td>
                                    67
                                  </td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="table-responsive">
                      <table class="table table-striped table-bordered dataTable margin-0">
                        <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                          <tr>
                            <th id="button_sortFromName" class="button-sort-none" onclick="toggleSort(this,1)" style="width: 15%;">
                              Team
                              <span class="icon-sort unsorted"></span>
                            </th>
                            <th id="button_sortFromTeam" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                              Giver
                              <span class="icon-sort unsorted"></span>
                            </th>
                            <th id="button_sortToName" class="button-sort-none" onclick="toggleSort(this,3)" style="width: 15%;">
                              Team
                              <span class="icon-sort unsorted"></span>
                            </th>
                            <th id="button_sortToTeam" class="button-sort-ascending" onclick="toggleSort(this,4)" style="width: 15%;">
                              Recipient
                              <span class="icon-sort unsorted"></span>
                            </th>
                            <th id="button_sortFeedback" class="button-sort-none">
                              Feedback
                              <span class="icon-sort unsorted"></span>
                            </th>
                          </tr>
                        </thead>
                        <tbody>

                          <tr>

                            <td class="middlealign">Team 1</td>
                            <td class="middlealign">Alice Betsy</td>
                            <td class="middlealign">Team 1</td>
                            <td class="middlealign">Alice Betsy</td>
                            <td class="multiline">
                              <ul>
                                <li>Grades: 20</li>
                                <li>Fun: 80</li>
                              </ul>
                            </td>
                          </tr>

                          <tr>

                            <td class="middlealign">Team 2</td>
                            <td class="middlealign">Charlie Davis</td>
                            <td class="middlealign">Team 2</td>
                            <td class="middlealign">Charlie Davis</td>
                            <td class="multiline">
                              <ul>
                                <li>Grades: 45</li>
                                <li>Fun: 55</li>
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

      <div class="row">
        <div class="col-sm-12">
            <b id="fbConstSumRecipients">Distribute points (among recipients) question</b>
          <br>
          Distribute points (among recipients) question is similar to Distribute points (among options) question. For this question type, students split points among the recipients of the question.
          <br> For example, if the question recipient is set to the giver's team members, and points to distribute in total is 100, students are required to split the 100 points among his team members.
          <br>
          <br> You can also specify
          <b>Points to distribute X number of recipients</b>, which multiplies the points specified by the number of recipients.
          <br> For example, if there are 5 team members and
          <b>Points to distribute X number of recipients</b> is set to 100, students will have to split 500 points in total to the 5 members.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" >
              <div class="panel panel-primary questionTable" id="amongRecipientTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-6" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Distribute points (among recipients) question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-6" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(6,6)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-6">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(6)" id="questiondiscardchanges-6" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(6)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-6">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-6" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Split points among the your team members and yourself, according to how much you think each member has contributed.</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-6">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-6"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-6" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-6">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-6" id="constSumOptionTable-6" style="display:none">

                          <div id="constSumAddOptionRow-6">
                            <div colspan="2">
                              <a class="btn btn-primary btn-xs addOptionLink" id="constSumAddOptionLink-6" onclick="addConstSumOption(6)" style="display:none">
                                <span class="glyphicon glyphicon-plus">
                                </span> add more options
                              </a>
                            </div>
                          </div>

                          <input type="hidden" name="noofchoicecreated" id="noofchoicecreated-6" value="0" disabled="">
                          <input type="hidden" name="constSumToRecipients" id="constSumToRecipients-6" value="true" disabled="">
                        </div>
                        <div class="col-sm-6">
                          <div class="form-inline">
                            <div class="row">
                              <div class="col-md-12">
                                <label class="bold-label width-100-pc margin-top-7px margin-bottom-7px tablet-no-mobile-margin-top-0">
                                  <b>Total Points to distribute: </b>
                                </label>
                              </div>

                              <div class="col-xs-12 margin-bottom-7px padding-left-35px">
                                <div class="col-xs-1">
                                  <input type="radio" id="constSumPointsTotal-6" name="constSumPointsPerOption" value="false" checked="" disabled="">
                                </div>
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Respondents will have to distribute the total points specified here among the recipients, e.g. if you specify 100 points here and there are 3 recipients, respondents will have to distribute 100 points among 3 recipients.">
                                  <div class="col-xs-4 padding-0 col-sm-4">
                                    <input type="number" disabled="" class="form-control width-100-pc pointsBox" name="constSumPoints" id="constSumPoints-6" value="100" min="1" step="1" onchange="updateConstSumPointsValue(6)">
                                  </div>
                                  <div class="col-xs-6 padding-0">
                                    <label class="margin-top-7px padding-left-7px">in
                                      total
                                    </label>
                                  </div>
                                </div>
                              </div>
                              <div class="col-xs-12 margin-bottom-15px padding-left-35px" id="constSumOption_Option-6" style="display:none">
                                <div class="col-xs-1">
                                  <input type="radio" id="constSumPointsPerOption-6" name="constSumPointsPerOption" value="true" disabled="">
                                </div>
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="The number of points to distribute will vary based on the number of options, e.g. if you specify 100 points here and there are 3 options, the total number of points to distribute among 3 options will be 300 (i.e. 100 x 3).">
                                  <div class="col-xs-4 padding-0">
                                    <input type="number" disabled="" class="form-control width-100-pc pointsBox" name="constSumPointsForEachOption" id="constSumPointsForEachOption-6" value="100" min="1" step="1" onchange="updateConstSumPointsValue(6)">
                                  </div>
                                  <div class="col-xs-6 padding-0">
                                    <label class="margin-top-7px padding-left-7px">X</label>
                                    <label class="margin-top-7px"> (number of options) </label>
                                  </div>
                                </div>
                              </div>
                              <div class="col-xs-12 margin-bottom-15px padding-left-35px" id="constSumOption_Recipient-6">
                                <div class="col-xs-1">
                                  <input type="radio" id="constSumPointsPerRecipient-6" name="constSumPointsPerOption" value="true" checked="" disabled="">
                                </div>
                                <div data-toggle="tooltip" data-placement="top" title="" data-original-title="The number of points to distribute will vary based on the number of recipients, e.g. if you specify 100 points here and there are 3 recipients, the total number of points to distribute among 3 recipients will be 300 (i.e. 100 x 3).">
                                  <div class="col-xs-4 padding-0">
                                    <input type="number" disabled="" class="form-control width-100-pc pointsBox" name="constSumPointsForEachRecipient" id="constSumPointsForEachRecipient-6" value="100" min="1" step="1" onchange="updateConstSumPointsValue(6)">
                                  </div>
                                  <div class="col-xs-6 padding-0">
                                    <label class="margin-top-7px padding-left-7px">X</label>
                                    <label class="margin-top-7px">(number of recipients)</label>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                          <div class="row">
                            <div class="col-sm-12">
                              <div class="checkbox" id="constSum_tooltipText-6" data-toggle="tooltip" data-placement="top" data-container="body" title="" data-original-title="Ticking this prevents a giver from distributing the same number of points to multiple recipients">
                                <label class="bold-label">
                                  <input type="checkbox" name="constSumUnevenDistribution" disabled="" id="constSum_UnevenDistribution-6">
                                  <span id="constSum_labelText-6">Every recipient to receive a different number of points</span>
                                </label>
                              </div>
                            </div>
                          </div>
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver's team members and Giver</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-2" name="givertype">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-2" name="recipienttype">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to recipient and instructors</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: table-row;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving student can see your response, and your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-10" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
            Other details are similar to the Distribute points (among recipients) question. See
            <a href="#fbConstSumOptions">here</a> for details.
            <br>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-sm-12">
            <b id="fbContrib">Team contribution question</b>
          <br>
          Team contribution questions are a specialized question type designed for team contribution evaluations.
          <br> They allow estimation of perceived contribution of a student and prevents a student from inflating his own score. To see more details about the calculation of results and other common questions, see the FAQ
          <a href="#Top">here</a>.
          <br>
          <br> An alternative to this specialized calculation scheme is to use the Distribute Points (among recipients) question, which provides a simple average calculation scheme.
          <br>
          <br> To setup the question, simply give some question text. The giver and recipient is fixed for this special question type.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" >
              <div class="panel panel-primary questionTable" id="teamContributionTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-1" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Team contribution question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-1" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(1,5)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-1">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(1)" id="questiondiscardchanges-1" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(1)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-1">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Please rate the estimated contribution of your team members and yourself.</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-1">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-1"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-1" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-1">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-6 row">
                          <div class="form-inline col-sm-12" id="contrib_tooltipText-1" data-toggle="tooltip" data-placement="top" data-container="body" title="" data-original-title="Ticking this allows a giver to select 'Not Sure' as his/her answer">
                            <input type="checkbox" name="isNotSureAllowedCheck" id="isNotSureAllowedCheck-1" checked="" disabled="">
                            <span style="margin-left: 5px; font-weight: bold;">Allow response giver to select 'Not Sure' as the answer</span>
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
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle disabled" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver's team members and Giver</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype--1" name="givertype">

                            <option disabled="" style="display: none;" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option disabled="" style="display: none;" value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option disabled="" style="display: none;" value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype--1" name="recipienttype">

                            <option disabled="" style="display: block;" value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option disabled="" style="display: block;" value="STUDENTS">
                              Other students in the course
                            </option>

                            <option disabled="" style="display: block;" value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option disabled="" style="display: block;" value="TEAMS">
                              Other teams in the course
                            </option>

                            <option disabled="" style="display: block;" value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option disabled="" style="display: block;" value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option disabled="" style="display: block;" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Shown anonymously to recipient and team members, visible to instructors</button>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions--1" style="display:none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" checked="" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage--1">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving student can see your response, but not your name.</li><li>Your team members can see your response, but not the name of the recipient, or your name.</li><li>The recipient's team members can see your response, but not the name of the recipient, or your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-11" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
          </div>
          <br> The results and statistics are presented as follows. See
          <a href="#faq-interpret-contribution-values-in-results">here</a> on how to use these results.
          <br>
          <br>
          <div class="bs-example">
            <div class="panel panel-info">
              <div class="panel-heading" data-target="#panelBodyCollapse-1" style="cursor: pointer;">
                <div class="display-icon pull-right">
                  <span class="glyphicon glyphicon-chevron-up pull-right"></span>
                </div>
                <strong>Question 1: </strong>
                <span>Please rate the estimated contribution of your team members and yourself.&nbsp;
                  <span>
                    <a href="javascript:;" id="questionAdditionalInfoButton-1-" class="color_gray" data-more="[more]" data-less="[less]">[more]</a>
                    <br>
                    <span id="questionAdditionalInfo-1-" style="display:none;">Team contribution question</span>
                  </span>
                </span>
              </div>
              <div class="panel-collapse collapse in" id="panelBodyCollapse-1">
                <div class="panel-body padding-0" id="questionBody-0">

                  <div class="resultStatistics">
                    <div class="panel-body">
                      <div class="row">
                        <div class="col-sm-4 text-color-gray">
                          <strong>
                            Response Summary
                          </strong>
                        </div>
                        <div class="col-sm-3 pull-right">
                          [
                          <a href="#faq-interpret-contribution-values-in-results" target="_blank" rel="noopener noreferrer" id="interpret_help_link">How do I interpret/use these values?</a>]
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-12">
                          <table class="table table-bordered table-responsive margin-0">
                            <thead>
                              <tr>
                                <td class="button-sort-ascending" id="button_sortteamname">Team
                                  <span class="icon-sort unsorted"></span>
                                </td>
                                <td class="button-sort-none" id="button_sortname">Student
                                  <span class="icon-sort unsorted"></span>
                                </td>
                                <td class="button-sort-none" id="button_sortclaimed" data-toggle="tooltip" data-placement="top" data-container="body" title="This is the student's own estimation of his/her contributions">
                                  <abbr title="Claimed Contribution">CC</abbr>
                                  <span class="icon-sort unsorted"></span>
                                </td>
                                <td class="button-sort-none" id="button_sortperceived" data-toggle="tooltip" data-placement="top" data-container="body" title="This is the average of what other team members think this student contributed">
                                  <abbr title="Percived Contribution">PC</abbr>
                                  <span class="icon-sort unsorted"></span>
                                </td>
                                <td class="button-sort-none" id="button_sortdiff" data-toggle="tooltip" data-placement="top" data-container="body" title="Perceived Contribution - Claimed Contribution">Diff
                                  <span class="icon-sort unsorted"></span>
                                </td>
                                <td class="align-center" data-toggle="tooltip" data-placement="top" data-container="body" title="The list of points that this student received from others">Ratings Received</td>
                              </tr>
                            </thead>
                            <tbody>
                              <tr>
                                <td>Team 1</td>
                                <td id="studentname">
                                  Emma Farrell
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span>0</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 1</td>
                                <td id="studentname">
                                  Danny Engrid
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span>0</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 1</td>
                                <td id="studentname">
                                  Alice Betsy
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span>0</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 1</td>
                                <td id="studentname">
                                  Benny Charles
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>
                                </td>
                                <td>
                                  <span>0</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>,
                                  <span class="color_neutral">E</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 2</td>
                                <td id="studentname">
                                  Gene Hudson
                                </td>
                                <td>
                                  <span class="color-positive">E +4%</span>
                                </td>
                                <td>
                                  <span class="color-positive">E +5%</span>
                                </td>
                                <td>
                                  <span class="color-positive">+1%</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color-positive">E +9%</span>,
                                  <span class="color-positive">E +7%</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 2</td>
                                <td id="studentname">
                                  Francis Gabriel
                                </td>
                                <td>
                                  <span class="color-positive">E +5%</span>
                                </td>
                                <td>
                                  <span class="color-positive">E +6%</span>
                                </td>
                                <td>
                                  <span class="color-positive">+1%</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color-positive">E +7%</span>,
                                  <span class="color-positive">E +10%</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 2</td>
                                <td id="studentname">
                                  Happy Guy
                                </td>
                                <td>
                                  <span class="color-positive">E +5%</span>
                                </td>
                                <td>
                                  <span class="color-positive">E +7%</span>
                                </td>
                                <td>
                                  <span class="color-positive">+2%</span>
                                </td>
                                <td>
                                  <span class="color_neutral">E</span>,
                                  <span class="color-positive">E +9%</span>,
                                  <span class="color-positive">E +12%</span>
                                </td>
                              </tr>
                              <tr>
                                <td>Team 2</td>
                                <td id="studentname">
                                  Charlie Davis
                                </td>
                                <td>
                                  <span class="color-negative">E -16%</span>
                                </td>
                                <td>
                                  <span class="color-negative">E -18%</span>
                                </td>
                                <td>
                                  <span class="color-negative">-2%</span>
                                </td>
                                <td>
                                  <span class="color-negative">E -19%</span>,
                                  <span class="color-negative">E -19%</span>,
                                  <span class="color-negative">E -17%</span>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="table-responsive">
                    <table class="table table-striped table-bordered dataTable margin-0">
                      <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                        <tr>
                          <th id="button_sortFromName" class="button-sort-none" onclick="toggleSort(this,1)" style="width: 15%;">
                            Team
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortFromTeam" class="button-sort-none" onclick="toggleSort(this,2)" style="width: 15%;">
                            Giver
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortToName" class="button-sort-none" onclick="toggleSort(this,3)" style="width: 15%;">
                            Team
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortToTeam" class="button-sort-ascending" onclick="toggleSort(this,4)" style="width: 15%;">
                            Recipient
                            <span class="icon-sort unsorted"></span>
                          </th>
                          <th id="button_sortFeedback" class="button-sort-none">
                            Feedback
                            <span class="icon-sort unsorted"></span>
                          </th>
                        </tr>
                      </thead>
                      <tbody>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color_neutral">Equal Share</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color_neutral">Equal Share</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="multiline">
                            <span class="color-negative">Equal Share -16%</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color-negative">Equal Share -18%</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +6%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +6%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +6%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color_neutral">Equal Share</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Alice Betsy</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Benny Charles</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Danny Engrid</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="middlealign">Team 1</td>
                          <td class="middlealign">Emma Farrell</td>
                          <td class="multiline">
                            <span class="color_neutral">Equal Share</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color_neutral">Equal Share</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="multiline">
                            <span class="color-negative">Equal Share -20%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +5%</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color-positive">Equal Share +6%</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +5%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +10%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="multiline">
                            <span class="color-negative">Equal Share -18%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +6%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +4%</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color-positive">Equal Share +5%</span>]
                            </span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +8%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Charlie Davis</td>
                          <td class="multiline">
                            <span class="color-negative">Equal Share -20%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Francis Gabriel</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +8%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Gene Hudson</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +7%</span>
                          </td>
                        </tr>

                        <tr>

                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="middlealign">Team 2</td>
                          <td class="middlealign">Happy Guy</td>
                          <td class="multiline">
                            <span class="color-positive">Equal Share +5%</span>
                            <span>&nbsp;&nbsp;[Perceived Contribution:
                              <span class="color-positive">Equal Share +7%</span>]
                            </span>
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

      <div class="row">
        <div class="col-sm-12">
            <b id="fbRubric">Rubric question</b>
          <br>
          Rubric questions allow instructors to create multiple sub-questions, with highly customizable choices and descriptions.
          <br>
          <br> To the student, the question looks similar to:
          <br>
          <br>
          <div class="bs-example">
            <div class="form-horizontal">
              <div class="panel panel-primary">
                <div class="panel-heading">Question 10:
                  <br>
                  <span>Please answer the following questions.</span>
                </div>
                <div class="panel-body">
                  <p class="text-muted">Only the following persons can see your responses: </p>
                  <ul class="text-muted">

                    <li class="unordered">Other students in the course can see your response, the name of the recipient, and your name.</li>

                    <li class="unordered">Instructors in this course can see your response, the name of the recipient, and your name.</li>

                  </ul>

                  <br>
                  <div class="form-group margin-0">
                    <div class="col-sm-2 form-inline" style="text-align:right">
                      <label for="input">To:</label>
                      <span> Charlie Davis</span>
                    </div>
                    <div class="col-sm-10">
                      <div class="row">
                        <div class="col-sm-12 table-responsive">
                          <table class="table table-striped table-bordered margin-0" id="rubricResponseTable-10-0">
                            <thead>
                              <tr>
                                <th class="col-md-1"></th>
                                <th class="rubricCol-10-0">
                                  <p>Strongly Agree</p>
                                </th>
                                <th class="rubricCol-10-1">
                                  <p>Agree</p>
                                </th>
                                <th class="rubricCol-10-2">
                                  <p>Disagree</p>
                                </th>
                                <th class="rubricCol-10-3">
                                  <p>Strongly Disagree</p>
                                </th>

                              </tr>
                            </thead>
                            <tbody>
                              <tr>
                                <td>
                                  <p>a) This student has contributed significantly to the project.</p>
                                </td>
                                <td class="col-md-1 cell-selected">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-0-0" name="rubricChoice-10-0-0" value="0-0" checked="">
                                  <span class="color_neutral overlay"> Routinely provides useful ideas when participating in the group and in classroom discussion. A definite leader who contributes a lot of effort.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-0-1" name="rubricChoice-10-0-0" value="0-1">
                                  <span class="color_neutral overlay"> Usually provides useful ideas when participating in the group and in classroom discussion. A strong group member who tries hard!</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-0-2" name="rubricChoice-10-0-0" value="0-2">
                                  <span class="color_neutral overlay"> Sometimes provides useful ideas when participating in the group and in classroom discussion. A satisfactory group member who does what is required.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-0-3" name="rubricChoice-10-0-0" value="0-3">
                                  <span class="color_neutral overlay"> Rarely provides useful ideas when participating in the group and in classroom discussion. May refuse to participate.</span>
                                </td>

                              </tr>
                              <tr>
                                <td>
                                  <p>b) This student delivers quality work.</p>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-1-0" name="rubricChoice-10-0-1" value="1-0">
                                  <span class="color_neutral overlay"> Provides work of the highest quality.</span>
                                </td>
                                <td class="col-md-1 cell-selected">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-1-1" name="rubricChoice-10-0-1" value="1-1" checked="">
                                  <span class="color_neutral overlay"> Provides high quality work.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-1-2" name="rubricChoice-10-0-1" value="1-2">
                                  <span class="color_neutral overlay"> Provides work that occasionally needs to be checked/redone by other group members to ensure quality.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-0-1-3" name="rubricChoice-10-0-1" value="1-3">
                                  <span class="color_neutral overlay"> Provides work that usually needs to be checked/redone by others to ensure quality.</span>
                                </td>

                              </tr>

                            </tbody>
                          </table>
                        </div>
                      </div>
                      <input type="hidden" id="rubricResponse-10-0" name="responsetext-10-0" value="">
                    </div>
                  </div>

                  <br>
                  <div class="form-group margin-0">
                    <div class="col-sm-2 form-inline" style="text-align:right">
                      <label for="input">To:</label>
                      <span> Francis Gabriel</span>
                    </div>
                    <div class="col-sm-10">
                      <div class="row">
                        <div class="col-sm-12 table-responsive">
                          <table class="table table-striped table-bordered margin-0" id="rubricResponseTable-10-1">
                            <thead>
                              <tr>
                                <th class="col-md-1"></th>
                                <th class="rubricCol-10-0">
                                  <p>Strongly Agree</p>
                                </th>
                                <th class="rubricCol-10-1">
                                  <p>Agree</p>
                                </th>
                                <th class="rubricCol-10-2">
                                  <p>Disagree</p>
                                </th>
                                <th class="rubricCol-10-3">
                                  <p>Strongly Disagree</p>
                                </th>

                              </tr>
                            </thead>
                            <tbody>
                              <tr>
                                <td>
                                  <p>a) This student has contributed significantly to the project.</p>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-0-0" name="rubricChoice-10-1-0" value="0-0">
                                  <span class="color_neutral overlay"> Routinely provides useful ideas when participating in the group and in classroom discussion. A definite leader who contributes a lot of effort.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-0-1" name="rubricChoice-10-1-0" value="0-1">
                                  <span class="color_neutral overlay"> Usually provides useful ideas when participating in the group and in classroom discussion. A strong group member who tries hard!</span>
                                </td>
                                <td class="col-md-1 cell-selected">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-0-2" name="rubricChoice-10-1-0" value="0-2" checked="">
                                  <span class="color_neutral overlay"> Sometimes provides useful ideas when participating in the group and in classroom discussion. A satisfactory group member who does what is required.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-0-3" name="rubricChoice-10-1-0" value="0-3">
                                  <span class="color_neutral overlay"> Rarely provides useful ideas when participating in the group and in classroom discussion. May refuse to participate.</span>
                                </td>

                              </tr>
                              <tr>
                                <td>
                                  <p>b) This student delivers quality work.</p>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-1-0" name="rubricChoice-10-1-1" value="1-0">
                                  <span class="color_neutral overlay"> Provides work of the highest quality.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-1-1" name="rubricChoice-10-1-1" value="1-1">
                                  <span class="color_neutral overlay"> Provides high quality work.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-1-2" name="rubricChoice-10-1-1" value="1-2">
                                  <span class="color_neutral overlay"> Provides work that occasionally needs to be checked/redone by other group members to ensure quality.</span>
                                </td>
                                <td class="col-md-1">
                                  <input class="overlay" type="radio" id="rubricChoice-10-1-1-3" name="rubricChoice-10-1-1" value="1-3">
                                  <span class="color_neutral overlay"> Provides work that usually needs to be checked/redone by others to ensure quality.</span>
                                </td>

                              </tr>

                            </tbody>
                          </table>
                        </div>
                      </div>
                      <input type="hidden" id="rubricResponse-10-1" name="responsetext-10-1" value="">
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <br> To setup the question, enter the main question text, and add choices and sub-questions by using the add column and add row buttons.
          <br> Rows and columns can be deleted by clicking on the respective 'x' buttons. Optional description text can be used to specify and describe the choices.
          <br> You may also assign weights to the choices for calculating statistics.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" >
              <div class="panel panel-primary questionTable" id="rubricQuestionTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-1" disabled="">
                          <option value="1">1</option>
                          <option value="2">2</option>
                          <option value="3">3</option>
                          <option value="4">4</option>
                          <option value="5">5</option>
                          <option value="6">6</option>
                          <option value="7">7</option>
                          <option value="8">8</option>
                          <option value="9">9</option>
                          <option value="10">10</option>
                          <option value="11">11</option>
                          <option value="12">12</option>

                        </select>
                        &nbsp; Rubric question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-1" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(1,1)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-1">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(1)" id="questiondiscardchanges-1" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(1)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-1">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">How well did the team member communicate?</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-1">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-1"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-1" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-1">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-12 table-responsive">
                          <table class="table table-bordered margin-0" id="rubricEditTable-1">
                            <thead>
                              <tr>
                                <th style="text-align:center; vertical-align:middle;">Choices <span class="glyphicon glyphicon-arrow-right"></span></th>
                                <th class="rubricCol-1-0">
                                  <div class="col-sm-12 input-group">
                                    <input type="text" class="form-control" value="Strongly Disagree" id="rubricChoice-1-0" name="rubricChoice-0" disabled="">
                                    <span class="input-group-addon btn btn-default rubricRemoveChoiceLink-1" id="rubricRemoveChoiceLink-1-0" onclick="removeRubricCol(0, 1)" onmouseover="highlightRubricCol(0, 1, true)" onmouseout="highlightRubricCol(0, 1, false)" style="display: none;">
                                      <span class="glyphicon glyphicon-remove"></span>
                                    </span>
                                  </div>
                                </th>
                                <th class="rubricCol-1-1">
                                  <div class="col-sm-12 input-group">
                                    <input type="text" class="form-control" value="Disagree" id="rubricChoice-1-1" name="rubricChoice-1" disabled="">
                                    <span class="input-group-addon btn btn-default rubricRemoveChoiceLink-1" id="rubricRemoveChoiceLink-1-1" onclick="removeRubricCol(1, 1)" onmouseover="highlightRubricCol(1, 1, true)" onmouseout="highlightRubricCol(1, 1, false)" style="display: none;">
                                      <span class="glyphicon glyphicon-remove"></span>
                                    </span>
                                  </div>
                                </th>
                                <th class="rubricCol-1-2">
                                  <div class="col-sm-12 input-group">
                                    <input type="text" class="form-control" value="Agree" id="rubricChoice-1-2" name="rubricChoice-2" disabled="">
                                    <span class="input-group-addon btn btn-default rubricRemoveChoiceLink-1" id="rubricRemoveChoiceLink-1-2" onclick="removeRubricCol(2, 1)" onmouseover="highlightRubricCol(2, 1, true)" onmouseout="highlightRubricCol(2, 1, false)" style="display: none;">
                                      <span class="glyphicon glyphicon-remove"></span>
                                    </span>
                                  </div>
                                </th>
                                <th class="rubricCol-1-3">
                                  <div class="col-sm-12 input-group">
                                    <input type="text" class="form-control" value="Strongly Agree" id="rubricChoice-1-3" name="rubricChoice-3" disabled="">
                                    <span class="input-group-addon btn btn-default rubricRemoveChoiceLink-1" id="rubricRemoveChoiceLink-1-3" onclick="removeRubricCol(3, 1)" onmouseover="highlightRubricCol(3, 1, true)" onmouseout="highlightRubricCol(3, 1, false)" style="display: none;">
                                      <span class="glyphicon glyphicon-remove"></span>
                                    </span>
                                  </div>
                                </th>

                              </tr>
                              <tr id="rubricWeights-1">
                                <th style="text-align:center; vertical-align:middle;"><input type="checkbox" class="nonDestructive" id="rubricAssignWeights-1" name="rubricAssignWeights" checked="" disabled=""><span data-toggle="tooltip" data-placement="top" data-original-title="Assign weights to the columns for calculating statistics." class="tool-tip-decorate"> Weights </span><span class="glyphicon glyphicon-arrow-right"></span></th>
                                <th class="rubricCol-1-0">
                                  <input type="number" class="form-control nonDestructive" value="4" id="rubricWeight-1-0" name="rubricWeight-0" step="0.01" disabled="">
                                </th>
                                <th class="rubricCol-1-1">
                                  <input type="number" class="form-control nonDestructive" value="3" id="rubricWeight-1-1" name="rubricWeight-1" step="0.01" disabled="">
                                </th>
                                <th class="rubricCol-1-2">
                                  <input type="number" class="form-control nonDestructive" value="2" id="rubricWeight-1-2" name="rubricWeight-2" step="0.01" disabled="">
                                </th>
                                <th class="rubricCol-1-3">
                                  <input type="number" class="form-control nonDestructive" value="1" id="rubricWeight-1-3" name="rubricWeight-3" step="0.01" disabled="">
                                </th>

                              </tr>
                            </thead>
                            <tbody>
                              <tr id="rubricRow-1-0">
                                <td>
                                  <div class="col-sm-12 input-group">
                                    <span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-1" id="rubricRemoveSubQuestionLink-1-0" onclick="removeRubricRow(0,1)" onmouseover="highlightRubricRow(0, 1, true)" onmouseout="highlightRubricRow(0, 1, false)" style="display: none;">
                                      <span class="glyphicon glyphicon-remove"></span>
                                    </span>
                                    <textarea class="form-control" rows="3" id="rubricSubQn-1-0" name="rubricSubQn-0" disabled="">This student participates well in online discussions.</textarea>
                                  </div>
                                </td>
                                <td class="align-center rubricCol-1-0">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-0" name="rubricDesc-0-0" disabled="">Rarely or never responds.</textarea>
                                </td>
                                <td class="align-center rubricCol-1-1">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-1" name="rubricDesc-0-1" disabled="">Occasionally responds, but never initiates discussions.</textarea>
                                </td>
                                <td class="align-center rubricCol-1-2">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-2" name="rubricDesc-0-2" disabled="">Takes part in discussions and sometimes initiates discussions.</textarea>
                                </td>
                                <td class="align-center rubricCol-1-3">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-3" name="rubricDesc-0-3" disabled="">Initiates discussions frequently, and engages the team.</textarea>
                                </td>

                              </tr>
                              <tr id="rubricRow-1-1">
                                <td>
                                  <div class="col-sm-12 input-group">
                                    <span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-1" id="rubricRemoveSubQuestionLink-1-1" onclick="removeRubricRow(1,1)" onmouseover="highlightRubricRow(1, 1, true)" onmouseout="highlightRubricRow(1, 1, false)" style="display: none;">
                                      <span class="glyphicon glyphicon-remove"></span>
                                    </span>
                                    <textarea class="form-control" rows="3" id="rubricSubQn-1-1" name="rubricSubQn-1" disabled="">This student completes assigned tasks on time.</textarea>
                                  </div>
                                </td>
                                <td class="align-center rubricCol-1-0">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-0" name="rubricDesc-1-0" disabled="">Rarely or never completes tasks.</textarea>
                                </td>
                                <td class="align-center rubricCol-1-1">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-1" name="rubricDesc-1-1" disabled="">Often misses deadlines.</textarea>
                                </td>
                                <td class="align-center rubricCol-1-2">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-2" name="rubricDesc-1-2" disabled="">Occasionally misses deadlines.</textarea>
                                </td>
                                <td class="align-center rubricCol-1-3">
                                  <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-3" name="rubricDesc-1-3" disabled="">Tasks are always completed before the deadline.</textarea>
                                </td>

                              </tr>

                            </tbody>
                          </table>
                        </div>
                        <input type="hidden" name="rubricNumRows" id="rubricNumRows-1" value="2" disabled="">
                        <input type="hidden" name="rubricNumCols" id="rubricNumCols-1" value="4" disabled="">
                      </div>
                      <div class="row">
                        <div class="col-sm-6 align-left">
                          <a class="btn btn-xs btn-primary" id="rubricAddSubQuestionLink-1" onclick="addRubricRow(1)" style="display: none;"><span class="glyphicon glyphicon-arrow-down"> </span> add row</a>
                        </div>
                        <div class="col-sm-6 align-right">
                          <a class="btn btn-xs btn-primary" id="rubricAddChoiceLink-1" onclick="addRubricCol(1)" style="display: none;">add column <span class="glyphicon glyphicon-arrow-right"></span></a>
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver's team members and Giver</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div data-original-title="Who will give feedback" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-2" name="givertype">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div data-original-title="Who the feedback is about" class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-2" name="recipienttype">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option style="display: block;" value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option style="display: block;" value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div style="display: none;" class="col-sm-12 row numberOfEntitiesElements">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                        Visible to recipient and instructors
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-1" style="display:none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what feedback recipient(s) can view">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="RECEIVER" disabled="" checked="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what team members of feedback giver can view">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="OWN_TEAM_MEMBERS" disabled="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what team members of feedback recipients can view">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="RECEIVER_TEAM_MEMBERS" disabled="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what other students can view">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="STUDENTS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="STUDENTS" disabled="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="STUDENTS" disabled="">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what instructors can view">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTORS" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTORS" disabled="" checked="">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="INSTRUCTORS" disabled="" checked="">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-1">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving student can see your response, and your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-12" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                    </span>
                  </div>
                </div>
              </div>
            </form>
          </div>
          <br> The statistics for rubric question show the frequency which a choice is selected for each sub-question.
          <br> If weights are assigned to the choices, they will be shown beside their corresponding choices.
          <br>
          <br>
          <div class="bs-example">
            <div class="resultStatistics">
              <div class="panel-body">
                <div class="row">
                  <div class="col-sm-4 text-color-gray">
                    <strong>
                      Response Summary
                    </strong>
                  </div>
                </div>
                <div class="row">
                  <div class="col-sm-12 table-responsive">
                    <table class="table table-striped table-bordered margin-0">
                      <thead>
                        <tr>
                          <th></th>
                          <th>
                            <p>Strongly Agree<span style="font-weight:normal;"> (Weight: 4)</span></p>
                          </th>
                          <th>
                            <p>Agree<span style="font-weight:normal;"> (Weight: 3)</span></p>
                          </th>
                          <th>
                            <p>Disagree<span style="font-weight:normal;"> (Weight: 2)</span></p>
                          </th>
                          <th>
                            <p>Strongly Disagree<span style="font-weight:normal;"> (Weight: 1)</span></p>
                          </th>
                          <th>
                            <p>Average</p>
                          </th>

                        </tr>
                      </thead>
                      <tbody>
                        <tr>
                          <td>
                            <p>a) This student has contributed significantly to the project.</p>
                          </td>
                          <td>
                            12% (1)
                          </td>
                          <td>
                            25% (2)
                          </td>
                          <td>
                            25% (2)
                          </td>
                          <td>
                            38% (3)
                          </td>
                          <td>
                            2.13
                          </td>

                        </tr>
                        <tr>
                          <td>
                            <p>b) This student delivers quality work.</p>
                          </td>
                          <td>
                            29% (2)
                          </td>
                          <td>
                            43% (3)
                          </td>
                          <td>
                            14% (1)
                          </td>
                          <td>
                            14% (1)
                          </td>
                          <td>
                            2.86
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

      <div class="row">
        <div class="col-sm-12">
            <b id="fbRankOptions">Rank Options question</b>
          <br>
          Rank options questions are question where the students rank options that are created by you.
          <br>

          <br>
          To setup the question, enter the main question text, and add the options for the students to rank. You can configure if students can give the same rank multiple times.
          <br>
          You can also configure the minimum and (or) maximum options to be ranked. When the minimum options to be ranked restriction is enabled, it will ensure that the respondent ranks at least the number of options mentioned in the restriction.
          <br>
          Similarly, when maximum options to be ranked restriction is enabled, it will ensure that the respondent ranks at most the number of options mentioned in the restriction.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post" action="/page/instructorFeedbackQuestionEdit" id="form_editquestion-3" name="form_editquestions">
              <div class="panel panel-primary questionTable" id="rankOptionsTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-3" disabled="">

                          <option value="1">
                            1
                          </option>

                        </select>
                        &nbsp;Rank (options) question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-2" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(2,2)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-2">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(2)" id="questiondiscardchanges-2" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(2)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-2">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-2" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Rank the following factors in order of importance to your group, where 1 is the most important. </textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-2">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-2"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-2" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-2">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <br>
                        <div class="col-sm-6" id="rankOptionTable-2">
                          <div class="margin-bottom-7px" id="rankOptionRow-0-2">
                            <div class="input-group width-100-pc">
                              <input class="form-control" type="text" disabled="" name="rankOption-0" id="rankOption-0-2" value="Clearly defined goals for the next milestone">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="rankRemoveOptionLink" onclick="removeRankOption(0,2)" style="display:none" tabindex="-1" disabled="">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>
                          <div class="margin-bottom-7px" id="rankOptionRow-1-2">
                            <div class="input-group width-100-pc">
                              <input class="form-control" type="text" disabled="" name="rankOption-1" id="rankOption-1-2" value="Commitment of all group members">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="rankRemoveOptionLink" onclick="removeRankOption(1,2)" style="display:none" tabindex="-1" disabled="">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>
                          <div class="margin-bottom-7px" id="rankOptionRow-2-2">
                            <div class="input-group width-100-pc">
                              <input class="form-control" type="text" disabled="" name="rankOption-2" id="rankOption-2-2" value="Good coordination between group members">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="rankRemoveOptionLink" onclick="removeRankOption(2,2)" style="display:none" tabindex="-1" disabled="">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>
                          <div class="margin-bottom-7px" id="rankOptionRow-3-2">
                            <div class="input-group width-100-pc">
                              <input class="form-control" type="text" disabled="" name="rankOption-3" id="rankOption-3-2" value="Better time management">
                              <span class="input-group-btn">
                                <button class="btn btn-default removeOptionLink" type="button" id="rankRemoveOptionLink" onclick="removeRankOption(3,2)" style="display:none" tabindex="-1" disabled="">
                                  <span class="glyphicon glyphicon-remove">
                                  </span>
                                </button>
                              </span>
                            </div>
                          </div>

                          <div id="rankAddOptionRow-2">
                            <div colspan="2">
                              <a class="btn btn-primary btn-xs addOptionLink" id="rankAddOptionLink-2" onclick="addRankOption(2)" style="display:none">
                                <span class="glyphicon glyphicon-plus">
                                </span> add more options
                              </a>
                            </div>
                          </div>

                          <input type="hidden" name="noofchoicecreated" id="noofchoicecreated-2" value="4" disabled="">
                        </div>
                        <div class="col-sm-6">
                          <div class="checkbox" data-toggle="tooltip" data-placement="top" data-container="body" title="" data-original-title="Ticking this will allow response givers to give the same rank to multiple options">
                            <label class="bold-label">
                              <input type="checkbox" name="rankAreDuplicatesAllowed" id="rankAreDuplicatesAllowed-2" checked="" disabled="">
                              Allow response giver to give the same rank to multiple options
                            </label>
                          </div>
                          <br>
                          <div class="row">
                            <div class="col-sm-9 col-xs-12">
                              <div class="checkbox" data-toggle="tooltip" data-placement="top" data-container="body" title="Ticking this will ensure respondent ranks at least the mentioned number of options.">
                                <label class="bold-label">
                                  <input type="checkbox" name="minOptionsToBeRankedEnabled" id="minOptionsToBeRankedEnabled-1" disabled>
                                  Minimum number of options a respondent must rank
                                </label>
                              </div>
                            </div>
                            <div class="col-sm-3 col-xs-12">
                              <div class="pull-right">
                                <input type="number" class="form-control" name="minOptionsToBeRanked" id="minOptionsToBeRanked-1" min="1" value="1" disabled/>
                              </div>
                            </div>
                          </div>
                          <br>
                          <div class="row">
                            <div class="col-sm-9 col-xs-12">
                              <div class="checkbox" data-toggle="tooltip" data-placement="top" data-container="body" title="Ticking this will ensure respondent ranks at most the mentioned number of options.">
                                <label class="bold-label">
                                  <input type="checkbox" name="maxOptionsToBeRankedEnabled" id="maxOptionsToBeRankedEnabled-1" disabled>
                                  Maximum number of options a respondent can rank
                                </label>
                              </div>
                            </div>
                            <div class="col-sm-3 col-xs-12">
                              <div class="pull-right">
                                <input type="number" class="form-control" name="maxOptionsToBeRanked" id="maxOptionsToBeRanked-1" min="1" value="1" disabled/>
                              </div>
                            </div>
                          </div>
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Feedback session creator (i.e., me) will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver (Self feedback)</button>
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
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to instructors only</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-3" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display:none" disabled="">
                    </span>
                  </div>
                </div>
              </div>
              <input type="hidden" name="fsname" value="rankk">
              <input type="hidden" name="courseid" value="instr.ema-demo">
              <input type="hidden" name="questionid" value="ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKzgCQw">
              <input type="hidden" name="questionnum" value="3">
              <input type="hidden" name="questiontype" value="RANK_OPTIONS">
              <input type="hidden" name="questionedittype" id="questionedittype-3" value="edit">
              <input type="hidden" name="showresponsesto" value="RECEIVER,INSTRUCTORS">
              <input type="hidden" name="showgiverto" value="RECEIVER,INSTRUCTORS">
              <input type="hidden" name="showrecipientto" value="RECEIVER,INSTRUCTORS">
              <input type="hidden" name="user" value="inst@email.com">
            </form>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-sm-12">
            <b id="fbRankRecipients">Rank Recipients question</b>
          <br>
          Rank recipients questions are questions where the students are to rank students, teams, or instructors.
          <br>
          <br> The options to rank are determined by the feedback path selected for the question. You can configure if students can give the same rank multiple times.
          <br>
          <br>
          <div class="bs-example">
            <form class="form-horizontal form_question" role="form" method="post">
              <div class="panel panel-primary questionTable" id="rankRecpientTable">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-4" disabled="">

                          <option value="1">
                            1
                          </option>

                        </select>
                        &nbsp;Rank (recipients) question
                      </span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs" id="questionedittext-4" data-toggle="tooltip" data-placement="top" title="" onclick="enableEdit(4,4)" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-4">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" onclick="discardChanges(4)" id="questiondiscardchanges-4" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs" onclick="deleteQuestion(4)" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-1">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">

                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Rank the teams in your class, based on how much work you think the teams have put in. </textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-1">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div id="rich-text-toolbar-q-descr-container-1"></div>
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-1" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-1">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <div class="col-sm-12">
                          <div class="checkbox" data-toggle="tooltip" data-placement="top" data-container="body" title="" data-original-title="Ticking this will allow response givers to give the same rank to multiple recipients">
                            <label class="bold-label">
                              <input type="checkbox" name="rankAreDuplicatesAllowed" id="rankAreDuplicatesAllowed-1" disabled="">
                              Allow response giver to give the same rank to multiple options
                            </label>
                          </div>
                        </div>
                        <br>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                    </div>
                    <div class="feedback-path-dropdown col-sm-12 btn-group">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Feedback session creator (i.e., me) will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Nobody specific (For general class feedback)</button>
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
                    <div class="feedback-path-others margin-top-7px" style="display:none;">
                      <div class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="" data-original-title="Who will give feedback">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who will give the feedback:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="givertype-1" name="givertype" disabled="" onchange="matchVisibilityOptionToFeedbackPath(this);getVisibilityMessage(this);">

                            <option selected="" value="SELF">
                              Feedback session creator (i.e., me)
                            </option>

                            <option value="STUDENTS">
                              Students in this course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in this course
                            </option>

                            <option value="TEAMS">
                              Teams in this course
                            </option>

                          </select>
                        </div>
                      </div>
                      <div class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="" data-original-title="Who the feedback is about">
                        <label class="col-sm-4 col-lg-5 control-label">
                          Who the feedback is about:
                        </label>
                        <div class="col-sm-8 col-lg-7">
                          <select class="form-control participantSelect" id="recipienttype-1" name="recipienttype" disabled="" onchange="matchVisibilityOptionToFeedbackPath(this);getVisibilityMessage(this);">

                            <option value="SELF">
                              Giver (Self feedback)
                            </option>

                            <option value="STUDENTS">
                              Other students in the course
                            </option>

                            <option value="INSTRUCTORS">
                              Instructors in the course
                            </option>

                            <option value="TEAMS">
                              Other teams in the course
                            </option>

                            <option value="OWN_TEAM">
                              Giver's team
                            </option>

                            <option style="display: block;" value="OWN_TEAM_MEMBERS">
                              Giver's team members
                            </option>

                            <option style="display: block;" value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                              Giver's team members and Giver
                            </option>

                            <option selected="" value="NONE">
                              Nobody specific (For general class feedback)
                            </option>

                          </select>
                        </div>
                      </div>
                      <div class="col-sm-12 row numberOfEntitiesElements" style="display: none;">
                        <label class="control-label col-sm-4 small">
                          The maximum number of <span class="number-of-entities-inner-text">teams</span> each respondent should give feedback to:
                        </label>
                        <div class="col-sm-8 form-control-static">
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" value="custom" type="radio">
                            <input class="nonDestructive numberOfEntitiesBox width-75-pc" name="numofrecipients" value="1" min="1" max="250" type="number">
                          </div>
                          <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                            <input class="nonDestructive" name="numofrecipientstype" checked="" value="max" type="radio">
                            <span class="">Unlimited</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <br>
                  <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                    <div class="col-sm-12 padding-0 margin-bottom-7px">
                      <b class="visibility-title">Visibility</b> (Who can see the responses?)
                    </div>
                    <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                      <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">Visible to recipient and instructors</button>
                      <ul class="dropdown-menu">
                        <li class="dropdown-header">Common visibility options</li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS">Shown anonymously to recipient and instructors</a>
                        </li>

                        <li>
                          <a class="visibility-options-dropdown-option" href="javascript:;" data-option-name="ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS">Shown anonymously to recipient, visible to instructors</a>
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
                    <div class="visibilityOptions col-sm-12 overflow-hidden" id="visibilityOptions-2" style="display: none;">
                      <table class="dataTable participantTable table table-striped text-center background-color-white margin-bottom-10px">
                        <tbody>
                          <tr>
                            <th class="text-center">User/Group</th>
                            <th class="text-center">Can see answer</th>
                            <th class="text-center">Can see giver's name</th>
                            <th class="text-center">Can see recipient's name</th>
                          </tr>
                          <tr style="display: table-row;">
                            <td class="text-left">
                              <div data-original-title="Control what feedback recipient(s) can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient(s)
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" value="RECEIVER" disabled="" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: table-row;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback giver can view" data-toggle="tooltip" data-placement="top" title="">
                                Giver's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="OWN_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr style="display: none;">
                            <td class="text-left">
                              <div data-original-title="Control what team members of feedback recipients can view" data-toggle="tooltip" data-placement="top" title="">
                                Recipient's Team Members
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="RECEIVER_TEAM_MEMBERS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what other students can view" data-toggle="tooltip" data-placement="top" title="">
                                Other students
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="STUDENTS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="STUDENTS" type="checkbox">
                            </td>
                          </tr>
                          <tr>
                            <td class="text-left">
                              <div data-original-title="Control what instructors can view" data-toggle="tooltip" data-placement="top" title="">
                                Instructors
                              </div>
                            </td>
                            <td>
                              <input class="visibilityCheckbox answerCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox giverCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                            <td>
                              <input class="visibilityCheckbox recipientCheckbox" value="INSTRUCTORS" checked="" type="checkbox">
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                    <div class="col-sm-12 visibilityMessage overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, and your name.</li></ul></div>
                  </div>
                  <div>
                    <span class="pull-right">
                      <input id="button_question_submit-4" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display:none" disabled="">
                    </span>
                  </div>
                </div>
              </div>
              <input type="hidden" name="fsname" value="rankk">
              <input type="hidden" name="courseid" value="instr.ema-demo">
              <input type="hidden" name="questionid" value="ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIKPCQw">
              <input type="hidden" name="questionnum" value="4">
              <input type="hidden" name="questiontype" value="RANK_RECIPIENTS">
              <input type="hidden" name="questionedittype" id="questionedittype-4" value="edit">
              <input type="hidden" name="showresponsesto" value="RECEIVER,INSTRUCTORS">
              <input type="hidden" name="showgiverto" value="RECEIVER,INSTRUCTORS">
              <input type="hidden" name="showrecipientto" value="RECEIVER,INSTRUCTORS">
              <input type="hidden" name="user" value="inst@email.com">
            </form>
          </div>

          <br> The statistics for both rank questions show the average rank an option/recipient received. Ties are handled during the computation of statistics. If duplicate ranks are allowed to be given, ties are resolved by assigning the best rank to the occurences of tied values. For example, for the data {1, 3, 3, 4}, the ranks will be converted to {1, 2, 2, 4}.
          <br>
          <br>
          <div class="bs-example">
            <div class="panel-body">
              <div class="row">
                <div class="col-sm-4 text-color-gray">
                  <strong>
                    Response Summary
                  </strong>
                </div>
              </div>
              <div class="row">
                <div class="col-sm-12">
                  <table class="table table-bordered table-responsive margin-0">
                    <thead>
                    <tr>
                      <td class="button-sort-ascending" id="button_sortteamname" onclick="toggleSort(this,1);" style="width: 35%;">Team
                        <span class="icon-sort unsorted"></span></td>
                      <td class="button-sort-none" onclick="toggleSort(this,2);">Recipient
                        <span class="icon-sort unsorted"></span></td>
                      <td class="button-sort-none" id="button_sortname" style="width:15%;">Ranks Received
                        <span class="icon-sort unsorted"></span></td>
                      <td class="button-sort-none" id="button_sortclaimed" style="width:15%;">Average Rank
                        <span class="icon-sort unsorted"></span></td>
                    </tr>
                    </thead>
                    <tbody>
                      <tr>
                        <td>
                          -
                        </td>
                        <td>
                          Team 1
                        </td>
                        <td>
                          1 , 1 , 2
                        </td>
                        <td>
                          1.33
                        </td>
                      </tr>
                      <tr>
                        <td>
                          -
                        </td>
                        <td>
                          Team 2
                        </td>
                        <td>
                          1 , 2
                        </td>
                        <td>
                          1.5
                        </td>
                      </tr>
                      <tr>
                        <td>
                          -
                        </td>
                        <td>
                          Team 3
                        </td>
                        <td>
                          1 , 2
                        </td>
                        <td>
                          1.5
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
    </li>
  </ol>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
