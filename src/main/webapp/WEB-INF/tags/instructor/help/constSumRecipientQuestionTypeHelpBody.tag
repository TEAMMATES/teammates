<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - Distribute Points Among Recipients Question Body" pageEncoding="UTF-8" %>

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
                <select class="form-control participantSelect" id="givertype-2" name="questionhelp-givertype">

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
                <select class="form-control participantSelect" id="recipienttype-2" name="questionhelp-recipienttype">

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
  Other details are similar to the Distribute points (among options) question. See
  <a href="#fbConstSumOptions" class="button_questionTypeHelpModal" data-modal-link = "questionTypeHelpModal_constSumOptions">here</a> for details.
  <br>
</div>
