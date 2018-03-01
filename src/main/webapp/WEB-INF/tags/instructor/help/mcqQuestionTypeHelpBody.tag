<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - MCQ Question Body" pageEncoding="UTF-8" %>

Multiple-choice (single answer) questions allows you to specify several options, and lets students select one of them as the answer.
<br> Other than specifying several options by yourself, TEAMMATES also supports
<b>generating options</b> based on the list of students, teams and instructors in the course.
<br> Example with specified options:
<br>
<br>
<div class="bs-example">
  <form class="form-horizontal form_question" role="form" method="post">
    <div class="panel panel-primary questionhelp-questionTable" id="specifiedOptionsTable">
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
    <div class="panel panel-primary questionhelp-questionTable" id="generatedOptionsTable">
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
