<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="questions">Questions</h2>
<div class="contentHolder">
  <div class="panel-group">
    <div class="panel panel-default" id="question-essay">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-essay-body">
        <h3 class="panel-title">Essay Questions</h3>
      </div>
      <div id="question-essay-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Essay questions are open-ended questions that allow respondents to give text feedback about a question.<br>
            To set up an essay question:
            <ol>
              <li>
                Specify the question text
              </li>
              <li>
                (Optional) Add a description for the question
              </li>
              <li>
                Specify the feedback path that should be used to generate the appropriate feedback recipients
              </li>
            </ol>
          </p>
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
                      <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                    <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
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
    </div>
    <div class="panel panel-default" id="question-mcq">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-mcq-body">
        <h3 class="panel-title">Multiple Choice (Single Answer) Questions</h3>
      </div>
      <div id="question-mcq-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Multiple-choice (single answer) questions allow respondents to choose one answer from your list of answer options.<br>
            Other than manually specifying options, TEAMMATES also supports <b>generating options</b> based on the list of students, teams and instructors in the course.
          </p>
          <p>
            To set up a multiple choice (single answer) question:
          </p>
          <ol>
            <li>
              Specify the question text
            </li>
            <li>
              (Optional) Add a description for the question
            </li>
            <li>
              Specify answer options by writing them manually, or generate options from your course's list of students, instructors or teams
            </li>
            <li>
              (Optional) Specify 'Other' option, and let the student enter their own answer
            </li>
            <li>
              (Optional) Assign weights to each option for calculating statistics
            </li>
            <li>
              Specify the feedback path that should be used to generate the appropriate feedback recipients
            </li>
          </ol>
          <div class="bs-example">
            <form class="form-horizontal form_question tally-checkboxes" role="form" method="post">
              <div class="panel panel-primary questionTable" id="questionTable-1">
                <div class="panel-heading">
                  <div class="row">
                    <div class="col-sm-7">
                      <span>
                        <strong>Question</strong>
                        <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-1" disabled="">
                          <option value="1">
                          1</option>
                        </select>
                      &nbsp;Multiple-choice (single answer) question</span>
                    </div>
                    <div class="col-sm-5 mobile-margin-top-10px">
                      <span class="mobile-no-pull pull-right">
                        <a class="btn btn-primary btn-xs btn-edit-qn" id="questionedittext-1" data-toggle="tooltip" data-placement="top" title="" data-qnnumber="1" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                          <span class="glyphicon glyphicon-pencil"></span> Edit
                        </a>
                        <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-1">
                          <span class="glyphicon glyphicon-ok"></span> Save
                        </a>
                        <a class="btn btn-primary btn-xs btn-discard-changes" style="display:none" data-qnnumber="1" id="questiondiscardchanges-1" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                          <span class="glyphicon glyphicon-ban-circle"></span> Discard
                        </a>
                        <a class="btn btn-primary btn-xs btn-delete-qn" data-qnnumber="1" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                          <span class=" glyphicon glyphicon-trash"></span> Delete
                        </a>
                      </span>
                    </div>
                  </div>
                </div>
                <div class="visibility-checkbox-delegate panel-body">
                  <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                    <div class="form-group" style="padding: 15px;">
                      <h5 class="col-sm-2">
                        <label class="control-label" for="questiontext-1">
                          Question
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">Did this team member work co-operatively with others?</textarea>
                      </div>
                    </div>
                    <div class="form-group" style="padding: 0 15px;">
                      <h5 class="col-sm-2">
                        <label class="align-left" for="questiondescription-1">
                          [Optional]<br>Description
                        </label>
                      </h5>
                      <div class="col-sm-10">
                        <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-1" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-1">
                        <input type="hidden" name="questiondescription" disabled="">
                      </div>
                      <div class="row">
                        <div class="col-sm-12 margin-bottom-7px">
                          <div class="col-sm-4 padding-0">
                            <input type="checkbox" class="nonDestructive" id="mcqHasAssignedWeights-1" name="mcqHasAssignedWeights" checked="" disabled="">
                            <span data-toggle="tooltip" data-placement="top" data-original-title="Assign weights to the choices for calculating statistics." class="tool-tip-decorate"> Choices are weighted </span>
                          </div>
                          <div class="col-sm-2 padding-left-45px align-center">
                            Weights <span class="glyphicon glyphicon-arrow-down"></span>
                          </div>
                        </div>
                        <br>
                        <div class="col-sm-6">
                          <div id="mcqChoiceTable-1">
                            <div class="row">
                              <div class="col-sm-9" id="mcqChoices-1">
                                <div class="input-group margin-bottom-7px" id="mcqOptionRow-0-1">
                                  <span class="input-group-addon">
                                    <input type="radio" class="disabled_radio" disabled="">
                                  </span>
                                  <input class="form-control" type="text" disabled="" name="mcqOption-0" id="mcqOption-0-1" value="Yes">
                                  <span class="input-group-btn">
                                    <button class="btn btn-default removeOptionLink" type="button" id="mcqRemoveOptionLink" onclick="removeMcqOption(0,1)" style="display:none" tabindex="-1" disabled="">
                                      <span class="glyphicon glyphicon-remove">
                                      </span>
                                    </button>
                                  </span>
                                </div>
                                <div class="input-group margin-bottom-7px" id="mcqOptionRow-1-1">
                                  <span class="input-group-addon">
                                    <input type="radio" class="disabled_radio" disabled="">
                                  </span>
                                  <input class="form-control" type="text" disabled="" name="mcqOption-1" id="mcqOption-1-1" value="No">
                                  <span class="input-group-btn">
                                    <button class="btn btn-default removeOptionLink" type="button" id="mcqRemoveOptionLink" onclick="removeMcqOption(1,1)" style="display:none" tabindex="-1" disabled="">
                                      <span class="glyphicon glyphicon-remove">
                                      </span>
                                    </button>
                                  </span>
                                </div>
                              </div>
                              <div class="col-sm-3" id="mcqWeights-1">
                                <div class="margin-bottom-7px">
                                  <input type="number" class="form-control nonDestructive" value="2" id="mcqWeight-0-1" name="mcqWeight-0" step="0.01" disabled="" required="">
                                </div>
                                <div class="margin-bottom-7px">
                                  <input type="number" class="form-control nonDestructive" value="1" id="mcqWeight-1-1" name="mcqWeight-1" step="0.01" disabled="" required="">
                                </div>
                              </div>
                            </div>
                            <div id="mcqAddOptionRow-1">
                              <div colspan="2">
                                <a class="btn btn-primary btn-xs addOptionLink" id="mcqAddOptionLink-1" onclick="addMcqOption(1)" style="display:none">
                                  <span class="glyphicon glyphicon-plus">
                                  </span> add more options
                                </a>
                                <div class="row">
                                  <div class="checkbox col-sm-9 padding-left-35px">
                                    <label class="bold-label">
                                      <input type="checkbox" name="mcqOtherOptionFlag" id="mcqOtherOptionFlag-1" checked="" onchange="toggleMcqOtherOptionEnabled(this, 1)" disabled="">
                                      Add 'Other' option (Allows respondents to type in their own answer)
                                    </label>
                                  </div>
                                  <div class="col-sm-3">
                                    <input type="number" class="form-control nonDestructive" value="1" id="mcqOtherWeight-1" name="mcqOtherWeight" step="0.01" disabled="" required="">
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                          <input type="hidden" name="noofchoicecreated" id="noofchoicecreated-1" value="2" disabled="">
                        </div>
                        <div class="col-sm-6 col-lg-5 col-lg-offset-1 padding-right-25px">
                          <div class="border-gray narrow-slight visible-xs margin-bottom-7px margin-top-7px"></div>
                          <div class="checkbox padding-top-0">
                            <label class="bold-label">
                              <span class="inline-block">
                                <input type="checkbox" disabled="" id="generateMcqOptionsCheckbox-1" onchange="toggleMcqGeneratedOptions(this,1)">
                                Or, generate options from the list of all
                              </span>
                            </label>
                            <select class="form-control width-auto inline" id="mcqGenerateForSelect-1" onchange="changeMcqGenerateFor(1)" disabled="">
                              <option value="STUDENTS">students</option>
                              <option value="STUDENTS_EXCLUDING_SELF">students (excluding self)</option>
                              <option value="TEAMS">teams</option>
                              <option value="TEAMS_EXCLUDING_SELF">teams (excluding self)</option>
                              <option value="INSTRUCTORS">instructors</option>
                            </select>
                          </div>
                          <input type="hidden" id="mcqGeneratedOptions-1" name="mcqGeneratedOptions" value="NONE" disabled="">
                        </div>
                        <br>
                      </div></div>
                    </div>
                    <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                      <div class="margin-bottom-7px">
                        <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                      </div>
                      <div class="feedback-path-dropdown btn-group">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                          Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver's team members</button>
                          <ul class="dropdown-menu">
                            <li class="dropdown-header">Common feedback path combinations</li>
                            <li class="dropdown-submenu">
                              <a>Feedback session creator (i.e., me) will give feedback on...</a>
                              <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="" data-original-title="Choose an option">
                                <li>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="NONE" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                  Nobody specific (For general class feedback)</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="SELF" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                  Giver (Self feedback)</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="INSTRUCTORS" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                  Instructors in the course</a>
                                </li>
                              </ul>
                            </li>
                            <li class="dropdown-submenu">
                              <a>Students in this course will give feedback on...</a>
                              <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="" data-original-title="Choose an option">
                                <li>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="NONE" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                  Nobody specific (For general class feedback)</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="SELF" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                  Giver (Self feedback)</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="INSTRUCTORS" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                  Instructors in the course</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="OWN_TEAM_MEMBERS" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver's team members">
                                  Giver's team members</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="OWN_TEAM_MEMBERS_INCLUDING_SELF" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver's team members and Giver">
                                  Giver's team members and Giver</a>
                                </li>
                              </ul>
                            </li>
                            <li class="dropdown-submenu">
                              <a>Instructors in this course will give feedback on...</a>
                              <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="" data-original-title="Choose an option">
                                <li>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="NONE" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                  Nobody specific (For general class feedback)</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="SELF" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                  Giver (Self feedback)</a>
                                  <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="INSTRUCTORS" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                  Instructors in the course</a>
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
                              <select class="form-control participantSelect" id="givertype-1" name="givertype" disabled="">
                                <option value="SELF">
                                Feedback session creator (i.e., me)</option>
                                <option selected="" value="STUDENTS">
                                Students in this course</option>
                                <option value="INSTRUCTORS">
                                Instructors in this course</option>
                                <option value="TEAMS">
                                Teams in this course</option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="" data-original-title="Who the feedback is about">
                            <label class="col-sm-4 col-lg-5 control-label">
                              Who the feedback is about:
                            </label>
                            <div class="col-sm-8 col-lg-7">
                              <select class="form-control participantSelect" id="recipienttype-1" name="recipienttype" disabled="">
                                <option value="SELF">
                                Giver (Self feedback)</option>
                                <option value="STUDENTS">
                                Other students in the course</option>
                                <option value="INSTRUCTORS">
                                Instructors in the course</option>
                                <option value="TEAMS">
                                Other teams in the course</option>
                                <option value="OWN_TEAM">
                                Giver's team</option>
                                <option selected="" value="OWN_TEAM_MEMBERS">
                                Giver's team members</option>
                                <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                                Giver's team members and Giver</option>
                                <option value="NONE">
                                Nobody specific (For general class feedback)</option>
                              </select>
                            </div>
                          </div>
                          <div class="col-sm-12 row numberOfEntitiesElements" style="display: none;">
                            <label class="control-label col-sm-4 small">
                              The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                            </label>
                            <div class="col-sm-8 form-control-static">
                              <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                                <input class="nonDestructive" type="radio" name="numofrecipientstype" value="custom" disabled="">
                                <input class="nonDestructive numberOfEntitiesBox width-75-pc" type="number" name="numofrecipients" value="1" min="1" max="250" disabled="">
                              </div>
                              <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                                <input class="nonDestructive" type="radio" name="numofrecipientstype" checked="" value="max" disabled="">
                                <span class="">Unlimited</span>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <br>
                      <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                        <div class="margin-bottom-7px">
                          <b class="visibility-title">Visibility</b> (Who can see the responses?)
                        </div>
                        <div class="visibility-options-dropdown btn-group margin-bottom-10px">
                          <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                          Visible to instructors only</button>
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
                        <div class="visibilityOptions overflow-hidden" id="visibilityOptions-1" style="display:none;">
                          <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
                            <tbody><tr>
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
                                <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" disabled="">
                              </td>
                              <td>
                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" disabled="">
                              </td>
                              <td>
                                <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="RECEIVER" disabled="">
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
                          </tbody></table>
                        </div>
                        <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                        <div class="visibility-message overflow-hidden" id="visibilityMessage-1">
                          This is the visibility hint as seen by the feedback giver:
                          <ul class="text-muted background-color-warning">
                            <li>Instructors in this course can see your response, the name of the recipient, and your name.</li>
                          </ul>
                        </div>
                      </div>
                      <div>
                        <span class="pull-right">
                          <input id="button_question_submit-1" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display:none" disabled="">
                        </span>
                      </div>
                    </div>
                  </div>
                  <input type="hidden" name="fsname" value="MCQ weights">
                  <input type="hidden" name="courseid" value="CS3424">
                  <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                  <input type="hidden" name="questionnum" value="1">
                  <input type="hidden" name="questiontype" value="MCQ">
                  <input type="hidden" name="questionedittype" id="questionedittype-1" value="edit">
                  <input type="hidden" name="showresponsesto" value="RECEIVER,INSTRUCTORS">
                  <input type="hidden" name="showgiverto" value="RECEIVER,INSTRUCTORS">
                  <input type="hidden" name="showrecipientto" value="RECEIVER,INSTRUCTORS">
                  <input type="hidden" name="user" value="test@example.com">
                  <input type="hidden" name="token" value="16BA89B57A759447D269E5AC30913AB5">
                </form>
              </div>
              <p>
                When you view the results of a multiple-choice (single answer) question, TEAMMATES calculates some statistics about the results collected,
                such as the number of responses for each option, and the percentage of response in which each option was chosen.
                If weights are assigned in the question, then additional statistics are calculated such as count and average point for each option.
                'Per Recipient Statistics' are calculated if weights are assigned, it contains the number of responses each recipient received for each option,
                and the total and average points for each recipient.
              </p>
              <p>
                <strong>Note: </strong> Weights can be assigned for questions with existing questions too. To do that, edit the question that needs weights,
                if there are existing responses for that question, the additional statistics will automatically get calculated for the question.
              </p>
              <div class="bs-example">
                <div class="panel panel-info">
                  <div class="panel-heading" data-target="#panelBodyCollapse-1" id="panelHeading-1" style="cursor: pointer;">
                    <form style="display:none;" id="seeMore-1" class="seeMoreForm-1">
                    </form>
                    <div class="display-icon pull-right"><span class="glyphicon pull-right glyphicon-chevron-up"></span></div>
                    <form method="post" class="inline">
                      <div id="DownloadQuestion-1" class="inline">
                        <input id="button_download-1" type="submit" class="btn-link text-bold padding-0 color-inherit" data-toggle="tooltip" title="" name="fruploaddownloadbtn" value="Question 1:" data-original-title="Download Question Results">
                      </div>
                    </form>
                    <div class="inline panel-heading-text">
                      <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                      <span class="text-preserve-space">Did this team member work co-operatively with others?&nbsp;<span style=" white-space: normal;">
                        <a href="javascript:;" id="questionAdditionalInfoButton-1-" class="color_gray" onclick="toggleAdditionalQuestionInfo('1-')" data-more="[more]" data-less="[less]">[more]</a>
                        <br>
                        <span id="questionAdditionalInfo-1-" style="display:none;">Multiple-choice (single answer) question options:
                          <ul style="list-style-type: disc;margin-left: 20px;"><li>Yes</li><li>No</li><li>Others</li></ul></span>
                        </span></span>
                      </div>
                    </div>
                    <div class="panel-collapse collapse in" id="panelBodyCollapse-1" style="height: auto;">
                      <div class="panel-body padding-0" id="questionBody-0">
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
                                <table class="table table-bordered table-striped margin-0">
                                  <thead>
                                    <tr>
                                      <td class="button-sort-none toggle-sort">
                                        Choice <span class="icon-sort unsorted"></span>
                                      </td>
                                      <td class="button-sort-none toggle-sort">
                                        Weight <span class="icon-sort unsorted"></span>
                                      </td>
                                      <td class="button-sort-none toggle-sort">
                                        Response Count <span class="icon-sort unsorted"></span>
                                      </td>
                                      <td class="button-sort-none toggle-sort">
                                        Percentage (%) <span class="icon-sort unsorted"></span>
                                      </td>
                                      <td class="button-sort-none toggle-sort">
                                        Weighted Percentage (%) <span class="icon-sort unsorted"></span>
                                      </td>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    <tr>
                                      <td>
                                        Yes
                                      </td>
                                      <td>
                                        2
                                      </td>
                                      <td>
                                        4
                                      </td>
                                      <td>
                                        50
                                      </td>
                                      <td>
                                        66.67
                                      </td>
                                    </tr>
                                    <tr>
                                      <td>
                                        No
                                      </td>
                                      <td>
                                        1
                                      </td>
                                      <td>
                                        2
                                      </td>
                                      <td>
                                        25
                                      </td>
                                      <td>
                                        16.67
                                      </td>
                                    </tr>
                                    <tr>
                                      <td>
                                        Other
                                      </td>
                                      <td>
                                        1
                                      </td>
                                      <td>
                                        2
                                      </td>
                                      <td>
                                        25
                                      </td>
                                      <td>
                                        16.67
                                      </td>
                                    </tr>
                                  </tbody>
                                </table>
                              </div>
                              <div class="col-sm-12 table-responsive">
                                <br>
                                <strong class="text-color-gray">
                                  Per Recipient Statistics
                                </strong>
                                <table class="table table-striped table-bordered margin-0">
                                  <thead>
                                    <tr>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          Team <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          Recipient Name <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          Yes [2] <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          No [1] <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          Other [1] <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          Total <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                      <th class="button-sort-none toggle-sort">
                                        <p>
                                          Average <span class="icon-sort unsorted"></span>
                                        </p>
                                      </th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    <tr>
                                      <td>Team 1</td><td>Jean Wong</td><td>1</td><td>0</td><td>1</td><td>3.00</td><td>1.50</td>
                                    </tr><tr>
                                      <td>Team 1</td><td>Ravi Kumar</td><td>2</td><td>0</td><td>0</td><td>4.00</td><td>2.00</td>
                                    </tr><tr>
                                      <td>Team 1</td><td>Tom Jacobs</td><td>0</td><td>1</td><td>1</td><td>2.00</td><td>1.00</td>
                                    </tr><tr>
                                      <td>Team 2</td><td>Chun Ling</td><td>0</td><td>1</td><td>0</td><td>1.00</td><td>1.00</td>
                                    </tr><tr>
                                      <td>Team 2</td><td>Desmond Wu</td><td>1</td><td>0</td><td>0</td><td>2.00</td><td>2.00</td>
                                    </tr>
                                  </tbody>
                                </table>
                              </div>
                            </div>
                          </div></div>
                          <div class="table-responsive">
                            <table class="table fixed-table-layout table-striped table-bordered data-table margin-0">
                              <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                                <tr>
                                  <th style="width: 10%; min-width: 67px;" id="button_sortFromTeam" class="button-sort-none toggle-sort">
                                    Team<span class="icon-sort unsorted"></span></th>
                                    <th style="width: 10%; min-width: 65px;" id="button_sortFromName" class="button-sort-none toggle-sort">
                                      Giver<span class="icon-sort unsorted"></span></th>
                                      <th style="width: 10%; min-width: 67px;" id="button_sortToTeam" class="button-sort-ascending toggle-sort">
                                        Team<span class="icon-sort unsorted"></span></th>
                                        <th style="width: 10%; min-width: 90px;" id="button_sortToName" class="button-sort-none toggle-sort">
                                          Recipient<span class="icon-sort unsorted"></span></th>
                                          <th style="width: 45%; min-width: 95px;" id="button_sortFeedback" class="button-sort-none toggle-sort">
                                            Feedback<span class="icon-sort unsorted"></span></th>
                                            <th style="width: 15%; min-width: 75px;" class="action-header">
                                            Actions</th>
                                          </tr>
                                        </thead>
                                        <tbody>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">Yes</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="jean@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-4-3-0" data-recipientindex="4" data-giverindex="3" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-4-3-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="4" data-giverindex="3" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-4-3-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-4-3-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Jean Wong (Team 1)<br>
                                                                  Recipient: Ravi Kumar (Team 1)</p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-4-3-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="4" data-giverindex="3" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                  Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-4-3-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-4-3-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-4-3-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-4-3-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-4-3-0">
                                                                Add
                                                              </a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="4">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%jean@example.com%ravi@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="4" data-giverindex="3" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">No</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="jean@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-5-3-0" data-recipientindex="5" data-giverindex="3" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-5-3-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="5" data-giverindex="3" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-5-3-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-5-3-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Jean Wong (Team 1)<br>
                                                                  Recipient: Tom Jacobs (Team 1)
                                                                </p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-5-3-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="5" data-giverindex="3" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-5-3-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-5-3-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-5-3-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-5-3-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-5-3-0">
                                                              Add</a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="5">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%jean@example.com%tom@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="5" data-giverindex="3" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">More or less</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="ravi@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-3-4-0" data-recipientindex="3" data-giverindex="4" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-3-4-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="4" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-3-4-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-3-4-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Ravi Kumar (Team 1)<br>
                                                                  Recipient: Jean Wong (Team 1)
                                                                </p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-3-4-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="3" data-giverindex="4" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-3-4-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-3-4-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-3-4-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-3-4-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-3-4-0">
                                                              Add</a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="3">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%ravi@example.com%jean@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="4" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">Not sure</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="ravi@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-5-4-0" data-recipientindex="5" data-giverindex="4" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-5-4-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="5" data-giverindex="4" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-5-4-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-5-4-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Ravi Kumar (Team 1)<br>
                                                                  Recipient: Tom Jacobs (Team 1)
                                                                </p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-5-4-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="5" data-giverindex="4" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-5-4-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-5-4-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-5-4-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-5-4-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-5-4-0">
                                                                Add
                                                              </a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="5">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%ravi@example.com%tom@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="5" data-giverindex="4" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">Yes</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="tom@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-3-5-0" data-recipientindex="3" data-giverindex="5" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-3-5-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="5" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-3-5-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-3-5-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Tom Jacobs (Team 1)<br>
                                                                  Recipient: Jean Wong (Team 1)
                                                                </p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-3-5-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="3" data-giverindex="5" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-3-5-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-3-5-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-3-5-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-3-5-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-3-5-0">
                                                              Add</a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="3">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%tom@example.com%jean@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="5" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 1</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">Yes</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="tom@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-4-5-0" data-recipientindex="4" data-giverindex="5" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-4-5-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="4" data-giverindex="5" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-4-5-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-4-5-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Tom Jacobs (Team 1)<br>
                                                                  Recipient: Ravi Kumar (Team 1)
                                                                </p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-4-5-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="4" data-giverindex="5" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-4-5-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-4-5-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-4-5-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-4-5-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-4-5-0">
                                                              Add</a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="4">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%tom@example.com%ravi@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="4" data-giverindex="5" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 2</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=0477F672339FC87D3F1558444B53051CC89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Chun Ling<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 2</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=BBA245E7E32F26797F5627A4883E044B61C071775F284A28CA96BE4DA2CAE194&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Desmond Wu<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">Yes</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="ling@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-2-1-0" data-recipientindex="2" data-giverindex="1" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-2-1-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="2" data-giverindex="1" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-2-1-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-2-1-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Chun Ling (Team 2)<br>
                                                                  Recipient: Desmond Wu (Team 2)
                                                                </p>
                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-2-1-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="2" data-giverindex="1" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-2-1-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-2-1-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-2-1-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-2-1-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-2-1-0">
                                                              Add</a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="2">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%ling@example.com%desmond@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="2" data-giverindex="1" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td class="word-wrap-break middlealign">Team 2</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=BBA245E7E32F26797F5627A4883E044B61C071775F284A28CA96BE4DA2CAE194&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Desmond Wu<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <td class="word-wrap-break middlealign">Team 2</td>
                                            <td class="word-wrap-break middlealign">
                                              <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=0477F672339FC87D3F1558444B53051CC89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                Chun Ling<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                              </div>
                                            </td>
                                            <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                            <td class="word-wrap-break text-preserve-space">No</td>
                                            <td>
                                              <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student">
                                                <input type="hidden" name="courseid" value="CS3424">
                                                <input type="hidden" name="fsname" value="MCQ weights">
                                                <input type="hidden" name="moderatedquestionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw">
                                                <input type="hidden" name="moderatedperson" value="desmond@example.com">
                                              </form>
                                              <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-1-2-0" data-recipientindex="1" data-giverindex="2" data-qnindex="0">
                                                Add Comment
                                              </button>
                                              <div class="modal fade" id="commentModal-1-2-0" role="dialog">
                                                <div class="modal-dialog modal-lg">
                                                  <div class="modal-content">
                                                    <div class="modal-header">
                                                      <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="1" data-giverindex="2" data-qnindex="0">
                                                        ×
                                                      </button>
                                                      <h4 class="modal-title">Add Comment:</h4>
                                                    </div>
                                                    <div class="modal-body">
                                                      <ul class="list-group" id="responseCommentTable-1-2-0" style="display:none">
                                                        <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-1-2-0" style="display: none;">
                                                          <form class="responseCommentAddForm">
                                                            <div class="form-group form-inline">
                                                              <div class="form-group text-muted">
                                                                <p>
                                                                  Giver: Desmond Wu (Team 2)<br>
                                                                Recipient: Chun Ling (Team 2)</p>
                                                                You may change comment's visibility using the visibility options on the right hand side.
                                                              </div>
                                                              <a id="frComment-visibility-options-trigger-1-2-0" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="1" data-giverindex="2" data-qnindex="0" data-frcindex="">
                                                                <span class="glyphicon glyphicon-eye-close"></span>
                                                                Show Visibility Options
                                                              </a>
                                                            </div>
                                                            <div id="visibility-options-1-2-0" class="panel panel-default" style="display: none;">
                                                              <div class="panel-heading">
                                                                Visibility Options
                                                              </div>
                                                              <table class="table text-center" style="color: #000;">
                                                                <tbody>
                                                                  <tr>
                                                                    <th class="text-center">User/Group</th>
                                                                    <th class="text-center">Can see this comment</th>
                                                                    <th class="text-center">Can see comment giver's name</th>
                                                                  </tr>
                                                                  <tr id="response-giver-1-2-0">
                                                                    <td class="text-left">
                                                                      <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                  <tr id="response-instructors-1-2-0">
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
                                                                  </tr>
                                                                </tbody>
                                                              </table>
                                                            </div>
                                                            <div class="form-group">
                                                              <div class="panel panel-default panel-body" id="responseCommentAddForm-1-2-0">
                                                              </div>
                                                              <input type="hidden" name="responsecommenttext">
                                                            </div>
                                                            <div class="col-sm-offset-5">
                                                              <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-1-2-0">
                                                              Add</a>
                                                            </div>
                                                            <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                            <input type="hidden" name="fsindex" value="1">
                                                            <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKTLCQw%desmond@example.com%ling@example.com">
                                                            <input type="hidden" name="courseid" value="CS3424">
                                                            <input type="hidden" name="fsname" value="MCQ weights">
                                                            <input type="hidden" name="user" value="test@example.com">
                                                            <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                            <input type="hidden" name="token" value="95DAF259F7FEFD63C0D3AD723C82437B">
                                                          </form>
                                                        </li>
                                                      </ul>
                                                    </div>
                                                    <div class="modal-footer">
                                                      <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="1" data-giverindex="2" data-qnindex="0">
                                                        Close
                                                      </button>
                                                    </div>
                                                  </div>
                                                </div>
                                              </div>
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
                      <div class="panel panel-default" id="question-msq">
                        <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-msq-body">
                          <h3 class="panel-title">Multiple Choice (Multiple Answers) Questions</h3>
                        </div>
                        <div id="question-msq-body" class="panel-collapse collapse">
                          <div class="panel-body">
                            <p>
                              Multiple-choice (multiple answers) question are similar to the single answer version, except that respondents are able to select multiple options as their response.
                              <br> The setup and result statistics is similar to the single answer version. See
                              <a class="collapse-link" data-target="#question-mcq-body" href="#question-mcq">above</a> for details.
                            </p>
                            <p>
                              <strong>Note:</strong> Multiple-choice (multiple answers) question allow respondents to select 'None of the above' option as an answer,
                              if 'Minimum number of options a respondent is allowed to select' option is not selected. The result statistics do not contain
                              'None of the above' responses, as statistics is not calculated for this option.
                            </p>
                          </div>
                        </div>
                      </div>
                      <div class="panel panel-default" id="question-numscale">
                        <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-numscale-body">
                          <h3 class="panel-title">Numerical Scale Questions</h3>
                        </div>
                        <div id="question-numscale-body" class="panel-collapse collapse">
                          <div class="panel-body">
                            <p>
                              Numerical scale questions allow numerical responses from respondents
                            </p>
                            <p>
                              To set up a numerical scale question:
                            </p>
                            <ol>
                              <li>
                                Specify the question text
                              </li>
                              <li>
                                (Optional) Add a description for the question
                              </li>
                              <li>
                                Specify the minimum and maximum valid input values — values outside of the range specified will not be allowed
                              </li>
                              <li>
                                Specify the precision at which input values should increment — TEAMMATES uses this value to enumerate all possible acceptable responses
                              </li>
                              <li>
                                Specify the feedback path that should be used to generate the appropriate feedback recipients
                              </li>
                            </ol>
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
                                        <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                                      <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-6">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving instructor can see your response, but not your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
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
                            <p>
                              Statistics for numerical scale questions are also provided for instructors.<br>
                              TEAMMATES calculates the mean, minimum and maximum values based on all responses given.
                            </p>
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
                                      <table class="table table-striped table-bordered data-table margin-0">
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
                      </div>
                      <div class="panel panel-default" id="question-constsum-options">
                        <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-constsum-options-body">
                          <h3 class="panel-title">Distribute Points (Among Options) Questions</h3>
                        </div>
                        <div id="question-constsum-options-body" class="panel-collapse collapse">
                          <div class="panel-body">
                            <p>
                              Distribute points (among options) questions allow respondents to split a fixed number of points among options that you specify.
                            </p>
                            <p>
                              To setup a distribute points (among options) question:
                            </p>
                            <ol>
                              <li>
                                Specify the question text
                              </li>
                              <li>
                                (Optional) Add a description for the question
                              </li>
                              <li>
                                List all the answer options from which students can choose
                              </li>
                              <li>
                                Choose the number of points students will get to split among the options — you can also choose to specify <b>points to distribute X number of options</b>, which gives students a total of <code>(specified points) x (number of options)</code> points
                              </li>
                              <li>
                                Specify the feedback path that should be used to generate the appropriate feedback recipients
                              </li>
                            </ol>
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
                                        <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                                      <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                                    </div>
                                    <div>
                                      <span class="pull-right">
                                        <input id="button_question_submit-9" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </form>
                            </div>
                            <p>
                              In the results view, TEAMMATES provides statistics on the average number of points each option received.
                            </p>
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
                                      <table class="table table-striped table-bordered data-table margin-0">
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
                      <div class="panel panel-default" id="question-constsum-recipients">
                        <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-constsum-recipients-body">
                          <h3 class="panel-title">Distribute Points (Among Recipients) Questions</h3>
                        </div>
                        <div id="question-constsum-recipients-body" class="panel-collapse collapse">
                          <div class="panel-body">
                            <p>
                              Distribute points (among recipients) questions allow respondents to split points among a list of recipients.<br>
                              For example, if the question recipient is set to the giver's team members, students are required to split points among their team members.
                            </p>
                            <p>
                              To set up a distribute points (among recipients) question:
                              <ol>
                                <li>
                                  Specify the question text
                                </li>
                                <li>
                                  (Optional) Add a description for the question
                                </li>
                                <li>
                                  Choose the number of points students will get to split among the options — you can also choose to specify <b>points to distribute X number of options</b>, which gives students a total of <code>(specified points) x (number of options)</code> points
                                </li>
                                <li>
                                  Specify the feedback path that should be used to generate the appropriate feedback recipients
                                </li>
                              </ol>
                            </p>
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
                                        <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                                      <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving student can see your response, and your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
                                    </div>
                                    <div>
                                      <span class="pull-right">
                                        <input id="button_question_submit-10" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display: none;" disabled="">
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </form>
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="panel panel-default" id="question-contrib">
                        <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-contrib-body">
                          <h3 class="panel-title">Team Contribution Questions</h3>
                        </div>
                        <div id="question-contrib-body" class="panel-collapse collapse">
                          <div class="panel-body">
                            <p>
                              Team contribution questions are a specialized question type designed to evaluate a student's level of contribution in a team.<br>
                              They estimate the perceived contribution of a student and prevent students from inflating their own scores.
                            </p>
                            <p>
                              If you do not wish to use TEAMMATES's specialized calculation scheme, you may choose to use a distribute points (among recipients) question type.
                              Distribute points (among recipients) questions calculate the mean of all scores given to the recipient.
                            </p>
                            <p>
                              To set up a team contribution question:
                              <ol>
                                <li>
                                  Specify the question text
                                </li>
                                <li>
                                  (Optional) Add a description for the question
                                </li>
                              </ol>
                              <p>
                                The feedback path for this question type is fixed: the feedback giver must be a student, and the student must give feedback about his/her team members and himself.
                              </p>
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
                                          <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                                        <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage--1">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>The receiving student can see your response, but not your name.</li><li>Your team members can see your response, but not the name of the recipient, or your name.</li><li>The recipient's team members can see your response, but not the name of the recipient, or your name.</li><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
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
                              <p>
                                Contribution questions in TEAMMATES are unique because they are targeted at measuring team contributions. <br>
                                Thus, TEAMMATES purposefully prevents students from influencing their own ‘perceived contribution’ value.
                              </p>
                              <p>
                                Perceived contribution is calculated based on what a student's other team members perceive as his/her contribution.
                                The student's own opinion about his own contribution is not considered for the calculation.
                              </p>
                              <p>
                                Students enter contribution estimates for self and team members, using the scale <code>Equal share + x%</code>. e.g. <code>Equal share -10%</code><br>
                                Based on those values, we try to deduce the student's answer to the following two questions:
                              </p>
                              <ol>
                                <li>
                                  In your opinion, what proportion of the project did you do?
                                </li>
                                <li>
                                  In your opinion, if your teammates were doing the project by themselves without you, how would they compare against each other in terms of contribution?
                                </li>
                              </ol>
                              <p>
                                In the calculation, we do not allow (1) to affect (2). We use (2) to calculate the average perceived contribution for each student. A more detailed version of this calculation can be found
                                <a href="/technicalInformation.jsp#calculatePointsContribution" target="_blank">here</a>.
                              </p>
                              <p>
                                The results and statistics are presented in the example below. Here is a summary of the terms used:
                              </p>
                              <ul>
                                <li>
                                  <b>E (Equal share)</b>: a relative measure of work done. e.g. For a 3-person team, an ‘Equal share’ means ‘a third of the total work done’.
                                </li>
                                <li>
                                  <b >CC (Claimed Contribution)</b>: This is what the student claimed he contributed.
                                </li>
                                <li>
                                  <b>Ratings Received </b>: These are the peer opinions as to how much the student contributed. These values have been adjusted to neutralize any attempts by students to boost their own standing by rating others low.
                                </li>
                                <li>
                                  <b>PC (Perceived Contribution)</b>: This is the average value of the ‘Ratings Received’. This can be considered as the
                                  <i>team’s perception of how much the student contributed</i>.
                                </li>
                                <li>
                                  <b>Diff</b>: The difference between the claimed contribution (CC) and the perceived contribution (PC). This value can be used to identify those who have over/under-estimated their own contribution.
                                </li>
                              </ul>
                              <p>
                                The ratings in a contribution question can be used to identify relative contribution levels of students in a team.
                                If you use these values for grading, also refer the ‘Interpret contribution numbers with care’ caveat in the
                                <a class="collapse-link" data-target="#session-tips-body" href="#session-tips">tips for conducting 'team peer evaluation' sessions</a> section.
                              </p>
                              <p>
                                The actual contribution values entered by the student may appear different from the values shown in the results because the system ‘normalizes’ those values so that there is no artificial inflation of contribution.
                                For example, if a student says everyone contributed ‘Equal share + 10%’, the system automatically normalizes it to ‘Equal share’ because in reality that is what the student means.
                                ‘Normalize’ here means scale up/down the values so that the <code>(sum of contributions) = ( n x Equal Share)</code> where <code>n</code> is the number of students being reviewed.
                              </p>
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
                                  <div class="panel-collapse collapse" id="panelBodyCollapse-1">
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
                                              <a href="#question-contrib" target="_blank" rel="noopener noreferrer" id="interpret_help_link">How do I interpret/use these values?</a>]
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
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span>0</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td>Team 1</td>
                                                    <td id="studentname">
                                                      Danny Engrid
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span>0</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td>Team 1</td>
                                                    <td id="studentname">
                                                      Alice Betsy
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span>0</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td>Team 1</td>
                                                    <td id="studentname">
                                                      Benny Charles
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>
                                                    </td>
                                                    <td>
                                                      <span>0</span>
                                                    </td>
                                                    <td>
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>,
                                                      <span class="color-neutral">E</span>
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
                                                      <span class="color-neutral">E</span>,
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
                                                      <span class="color-neutral">E</span>,
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
                                                      <span class="color-neutral">E</span>,
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
                                        <table class="table table-striped table-bordered data-table margin-0">
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
                                                <span class="color-neutral">Equal Share</span>
                                                <span>&nbsp;&nbsp;[Perceived Contribution:
                                                  <span class="color-neutral">Equal Share</span>]
                                                </span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Alice Betsy</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Benny Charles</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Alice Betsy</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Danny Engrid</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Alice Betsy</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Emma Farrell</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Benny Charles</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Alice Betsy</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Benny Charles</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Benny Charles</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
                                                <span>&nbsp;&nbsp;[Perceived Contribution:
                                                  <span class="color-neutral">Equal Share</span>]
                                                </span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Benny Charles</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Danny Engrid</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Benny Charles</td>
                                              <td class="middlealign">Team 1</td>
                                              <td class="middlealign">Emma Farrell</td>
                                              <td class="multiline">
                                                <span class="color-neutral">Equal Share</span>
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
                                                <span class="color-neutral">Equal Share</span>
                                              </td>
                                            </tr>
                                            <tr>
                                              <td colspan="5" class="middlealign">Additional answers omitted</td>
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
                        <div class="panel panel-default" id="question-rubric">
                          <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-rubric-body">
                            <h3 class="panel-title">Rubric Questions</h3>
                          </div>
                          <div id="question-rubric-body" class="panel-collapse collapse">
                            <div class="panel-body">
                              <p>
                                Rubric questions allow instructors to create multiple sub-questions with highly customizable choices and descriptions.
                              </p>
                              <p>
                                To respondents, a rubric question will appear as a table that looks similar to the example below. Respondents can choose one answer per row.
                              </p>
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
                                      <div class="col-sm-12 form-inline mobile-align-left">
                                        <label for="input" style="text-indent: 24px">
                                          <span data-toggle="tooltip" data-placement="top" title="" data-original-title="The party being evaluated or given feedback to" class="tool-tip-decorate">
                                            Evaluee/Recipient
                                          </span>
                                        </label>
                                      </div>
                                      <br><br>
                                      <div class="form-group margin-0">
                                        <div class="col-sm-3 form-inline mobile-align-left" style="text-align:right">
                                          <label>
                                            <span> Charlie Davis</span>
                                          </label>
                                          (Student) :
                                        </div>
                                        <div class="col-sm-9">
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
                                                      <span class="color-neutral overlay"> Routinely provides useful ideas when participating in the group and in classroom discussion. A definite leader who contributes a lot of effort.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-0-1" name="rubricChoice-10-0-0" value="0-1">
                                                      <span class="color-neutral overlay"> Usually provides useful ideas when participating in the group and in classroom discussion. A strong group member who tries hard!</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-0-2" name="rubricChoice-10-0-0" value="0-2">
                                                      <span class="color-neutral overlay"> Sometimes provides useful ideas when participating in the group and in classroom discussion. A satisfactory group member who does what is required.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-0-3" name="rubricChoice-10-0-0" value="0-3">
                                                      <span class="color-neutral overlay"> Rarely provides useful ideas when participating in the group and in classroom discussion. May refuse to participate.</span>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <p>b) This student delivers quality work.</p>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-1-0" name="rubricChoice-10-0-1" value="1-0">
                                                      <span class="color-neutral overlay"> Provides work of the highest quality.</span>
                                                    </td>
                                                    <td class="col-md-1 cell-selected">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-1-1" name="rubricChoice-10-0-1" value="1-1" checked="">
                                                      <span class="color-neutral overlay"> Provides high quality work.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-1-2" name="rubricChoice-10-0-1" value="1-2">
                                                      <span class="color-neutral overlay"> Provides work that occasionally needs to be checked/redone by other group members to ensure quality.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-0-1-3" name="rubricChoice-10-0-1" value="1-3">
                                                      <span class="color-neutral overlay"> Provides work that usually needs to be checked/redone by others to ensure quality.</span>
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
                                        <div class="col-sm-3 form-inline mobile-align-left" style="text-align:right">
                                          <label>
                                            <span> Francis Gabriel</span>
                                          </label>
                                          (Student) :
                                        </div>
                                        <div class="col-sm-9">
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
                                                      <span class="color-neutral overlay"> Routinely provides useful ideas when participating in the group and in classroom discussion. A definite leader who contributes a lot of effort.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-0-1" name="rubricChoice-10-1-0" value="0-1">
                                                      <span class="color-neutral overlay"> Usually provides useful ideas when participating in the group and in classroom discussion. A strong group member who tries hard!</span>
                                                    </td>
                                                    <td class="col-md-1 cell-selected">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-0-2" name="rubricChoice-10-1-0" value="0-2" checked="">
                                                      <span class="color-neutral overlay"> Sometimes provides useful ideas when participating in the group and in classroom discussion. A satisfactory group member who does what is required.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-0-3" name="rubricChoice-10-1-0" value="0-3">
                                                      <span class="color-neutral overlay"> Rarely provides useful ideas when participating in the group and in classroom discussion. May refuse to participate.</span>
                                                    </td>
                                                  </tr>
                                                  <tr>
                                                    <td>
                                                      <p>b) This student delivers quality work.</p>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-1-0" name="rubricChoice-10-1-1" value="1-0">
                                                      <span class="color-neutral overlay"> Provides work of the highest quality.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-1-1" name="rubricChoice-10-1-1" value="1-1">
                                                      <span class="color-neutral overlay"> Provides high quality work.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-1-2" name="rubricChoice-10-1-1" value="1-2">
                                                      <span class="color-neutral overlay"> Provides work that occasionally needs to be checked/redone by other group members to ensure quality.</span>
                                                    </td>
                                                    <td class="col-md-1">
                                                      <input class="overlay" type="radio" id="rubricChoice-10-1-1-3" name="rubricChoice-10-1-1" value="1-3">
                                                      <span class="color-neutral overlay"> Provides work that usually needs to be checked/redone by others to ensure quality.</span>
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
                              <p> To set up a rubric question:
                                <ol>
                                  <li>
                                    Specify the question text
                                  </li>
                                  <li>
                                    (Optional) Add a description for the question
                                  </li>
                                  <li>
                                    Add choices using the <code>Add Column</code> button, or delete choices using the <code>x</code> button at the bottom of each column
                                  </li>
                                  <li>
                                    Add subquestions using the <code>Add Row</code> button, or delete subquestions using the <code>x</code> button to the left of each subquestion
                                  </li>
                                  <li>
                                    (Optional) Add description text to describe each choice for each subquestion
                                  </li>
                                  <li>
                                    (Optional) Assign weights to each choice of each sub-question for calculating statistics
                                  </li>
                                  <li>
                                    Specify the feedback path that should be used to generate the appropriate feedback recipients
                                  </li>
                                </ol>
                              </p>
                              <div class="bs-example">
                                <form class="form-horizontal form_question tally-checkboxes" role="form" method="post">
                                  <div class="panel panel-primary questionTable" id="questionTable-1">
                                    <div class="panel-heading">
                                      <div class="row">
                                        <div class="col-sm-7">
                                          <span>
                                            <strong>Question
                                              <span id="questionnum-static-1">1:</span>
                                            </strong>
                                            <select class="questionNumber nonDestructive text-primary" name="questionnum" id="questionnum-1" disabled="" style="display: none;">
                                              <option value="1">
                                              1</option>
                                            </select>
                                          &nbsp;Rubric question</span>
                                        </div>
                                        <div class="col-sm-5 mobile-margin-top-10px">
                                          <span class="mobile-no-pull pull-right">
                                            <a class="btn btn-primary btn-xs btn-edit-qn" id="questionedittext-1" data-toggle="tooltip" data-placement="top" title="" data-qnnumber="1" data-original-title="Edit the existing question. Do remember to save the changes before moving on to editing another question.">
                                              <span class="glyphicon glyphicon-pencil"></span> Edit
                                            </a>
                                            <a class="btn btn-primary btn-xs" style="display:none" id="questionsavechangestext-1">
                                              <span class="glyphicon glyphicon-ok"></span> Save
                                            </a>
                                            <a class="btn btn-primary btn-xs btn-discard-changes" style="display:none" data-qnnumber="1" id="questiondiscardchanges-1" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discard any unsaved edits and revert back to original question.">
                                              <span class="glyphicon glyphicon-ban-circle"></span> Discard
                                            </a>
                                            <a class="btn btn-primary btn-xs btn-delete-qn" data-qnnumber="1" data-toggle="tooltip" data-placement="top" data-original-title="" title="">
                                              <span class=" glyphicon glyphicon-trash"></span> Delete
                                            </a>
                                            <a class="btn btn-primary btn-xs btn-duplicate-qn" data-qnnumber="1" data-toggle="tooltip" data-placement="top" title="" data-original-title="Make a copy of the existing question and add to the current feedback session.">
                                              <span class="glyphicon glyphicon-file"></span><span class="glyphicon glyphicon-file"></span> Duplicate
                                            </a>
                                          </span>
                                        </div>
                                      </div>
                                    </div>
                                    <div class="visibility-checkbox-delegate panel-body">
                                      <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                                        <div class="form-group" style="padding: 15px;">
                                          <h5 class="col-sm-2">
                                            <label class="control-label" for="questiontext-1">
                                              Question
                                            </label>
                                          </h5>
                                          <div class="col-sm-10">
                                            <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" id="questiontext-1" data-toggle="tooltip" data-placement="top" title="" placeholder="A concise version of the question e.g. &quot;How well did the team member communicate?&quot;" tabindex="9" disabled="" data-original-title="Please enter the question for users to give feedback about. e.g. What is the biggest weakness of the presented product?">How well did the team members communicate</textarea>
                                          </div>
                                        </div>
                                        <div class="form-group" style="padding: 0 15px;">
                                          <h5 class="col-sm-2">
                                            <label class="align-left" for="questiondescription-1">
                                              [Optional]<br>Description
                                            </label>
                                          </h5>
                                          <div class="col-sm-10">
                                            <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" id="questiondescription-1" data-toggle="tooltip" data-placement="top" title="" tabindex="9" data-original-title="Please enter the description of the question." spellcheck="false"><p><br data-mce-bogus="1"></p></div><input type="hidden" name="questiondescription-1">
                                            <input type="hidden" name="questiondescription" disabled="">
                                          </div>
                                          <div class="row">
                                            <div class="col-sm-4">
                                              <input type="checkbox" class="nonDestructive" id="rubricAssignWeights-1" name="rubricAssignWeights" checked="" disabled="">
                                              <span data-toggle="tooltip" data-placement="top" data-original-title="Assign weights to the columns for calculating statistics." class="tool-tip-decorate"> Choices are weighted </span>
                                            </div>
                                            <br>
                                            <div class="col-sm-12 table-responsive">
                                              <table class="table table-bordered margin-0" id="rubricEditTable-1">
                                                <thead>
                                                  <tr>
                                                    <th style="text-align:center; vertical-align:middle;">
                                                      Choices <span class="glyphicon glyphicon-arrow-right"></span>
                                                    </th>
                                                    <th class="rubricCol-1-0">
                                                      <input type="text" class="form-control" value="Strongly Disagree" id="rubricChoice-1-0" name="rubricChoice-0" disabled="">
                                                    </th>
                                                    <th class="rubricCol-1-1">
                                                      <input type="text" class="form-control" value="Disagree" id="rubricChoice-1-1" name="rubricChoice-1" disabled="">
                                                    </th>
                                                    <th class="rubricCol-1-2">
                                                      <input type="text" class="form-control" value="Agree" id="rubricChoice-1-2" name="rubricChoice-2" disabled="">
                                                    </th>
                                                    <th class="rubricCol-1-3">
                                                      <input type="text" class="form-control" value="Strongly Agree" id="rubricChoice-1-3" name="rubricChoice-3" disabled="">
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
                                                        <textarea class="form-control" rows="3" id="rubricSubQn-1-0" name="rubricSubQn-0" required="" disabled="">This student participates well in online discussions.</textarea>
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-0">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-0" name="rubricDesc-0-0" disabled="">Rarely or never responds.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="0" id="rubricWeight-1-0-0" name="rubricWeight-0-0" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-1">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-1" name="rubricDesc-0-1" disabled="">Occasionally responds, but never initiates discussions.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="1" id="rubricWeight-1-0-1" name="rubricWeight-0-1" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-2">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-2" name="rubricDesc-0-2" disabled="">Takes part in discussions and sometimes initiates discussions.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="2" id="rubricWeight-1-0-2" name="rubricWeight-0-2" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-3">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-0-3" name="rubricDesc-0-3" disabled="">Initiates discussions frequently, and engages the team.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="3" id="rubricWeight-1-0-3" name="rubricWeight-0-3" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                  </tr>
                                                  <tr id="rubricRow-1-1">
                                                    <td>
                                                      <div class="col-sm-12 input-group">
                                                        <span class="input-group-addon btn btn-default rubricRemoveSubQuestionLink-1" id="rubricRemoveSubQuestionLink-1-1" onclick="removeRubricRow(1,1)" onmouseover="highlightRubricRow(1, 1, true)" onmouseout="highlightRubricRow(1, 1, false)" style="display: none;">
                                                          <span class="glyphicon glyphicon-remove"></span>
                                                        </span>
                                                        <textarea class="form-control" rows="3" id="rubricSubQn-1-1" name="rubricSubQn-1" required="" disabled="">This student completes assigned tasks on time.</textarea>
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-0">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-0" name="rubricDesc-1-0" disabled="">Rarely or never completes tasks.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="0.5" id="rubricWeight-1-1-0" name="rubricWeight-1-0" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-1">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-1" name="rubricDesc-1-1" disabled="">Often misses deadlines.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="1" id="rubricWeight-1-1-1" name="rubricWeight-1-1" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-2">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-2" name="rubricDesc-1-2" disabled="">Occasionally misses deadlines.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="1.5" id="rubricWeight-1-1-2" name="rubricWeight-1-2" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-3">
                                                      <textarea class="form-control nonDestructive" rows="3" id="rubricDesc-1-1-3" name="rubricDesc-1-3" disabled="">Tasks are always completed before the deadline.</textarea>
                                                      <div class="rubricWeights-1">
                                                        <input type="number" class="form-control nonDestructive margin-top-10px" value="2" id="rubricWeight-1-1-3" name="rubricWeight-1-3" step="0.01" disabled="" required="">
                                                      </div>
                                                    </td>
                                                  </tr>
                                                  <tr id="rubric-options-row-1" class="rubricRemoveChoiceLink-1" style="display: none;">
                                                    <td></td>
                                                    <td class="align-center rubricCol-1-0" data-col="0">
                                                      <div class="btn-group">
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-left-1-0" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column left">
                                                          <span class="glyphicon glyphicon-arrow-left"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-1-0" onclick="removeRubricCol(0, 1)" onmouseover="highlightRubricCol(0, 1, true)" onmouseout="highlightRubricCol(0, 1, false)" disabled="">
                                                          <span class="glyphicon glyphicon-remove"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-right-1-0" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column right">
                                                          <span class="glyphicon glyphicon-arrow-right"></span>
                                                        </button>
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-1" data-col="1">
                                                      <div class="btn-group">
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-left-1-1" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column left">
                                                          <span class="glyphicon glyphicon-arrow-left"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-1-1" onclick="removeRubricCol(1, 1)" onmouseover="highlightRubricCol(1, 1, true)" onmouseout="highlightRubricCol(1, 1, false)" disabled="">
                                                          <span class="glyphicon glyphicon-remove"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-right-1-1" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column right">
                                                          <span class="glyphicon glyphicon-arrow-right"></span>
                                                        </button>
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-2" data-col="2">
                                                      <div class="btn-group">
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-left-1-2" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column left">
                                                          <span class="glyphicon glyphicon-arrow-left"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-1-2" onclick="removeRubricCol(2, 1)" onmouseover="highlightRubricCol(2, 1, true)" onmouseout="highlightRubricCol(2, 1, false)" disabled="">
                                                          <span class="glyphicon glyphicon-remove"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-right-1-2" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column right">
                                                          <span class="glyphicon glyphicon-arrow-right"></span>
                                                        </button>
                                                      </div>
                                                    </td>
                                                    <td class="align-center rubricCol-1-3" data-col="3">
                                                      <div class="btn-group">
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-left-1-3" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column left">
                                                          <span class="glyphicon glyphicon-arrow-left"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubricRemoveChoiceLink-1-3" onclick="removeRubricCol(3, 1)" onmouseover="highlightRubricCol(3, 1, true)" onmouseout="highlightRubricCol(3, 1, false)" disabled="">
                                                          <span class="glyphicon glyphicon-remove"></span>
                                                        </button>
                                                        <button type="button" class="btn btn-default" id="rubric-move-col-right-1-3" data-toggle="tooltip" data-placement="top" title="" disabled="" data-original-title="Move column right">
                                                          <span class="glyphicon glyphicon-arrow-right"></span>
                                                        </button>
                                                      </div>
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
                                              <a class="btn btn-xs btn-primary" id="rubricAddSubQuestionLink-1" onclick="addRubricRow(1)" style="display: none;">
                                                <span class="glyphicon glyphicon-arrow-down"></span> add row
                                              </a>
                                            </div>
                                            <div class="col-sm-6 align-right">
                                              <a class="btn btn-xs btn-primary" id="rubricAddChoiceLink-1" onclick="addRubricCol(1)" style="display: none;">
                                                add column <span class="glyphicon glyphicon-arrow-right"></span>
                                              </a>
                                            </div>
                                            <br>
                                          </div></div>
                                        </div>
                                        <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                                          <div class="margin-bottom-7px">
                                            <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                                          </div>
                                          <div class="feedback-path-dropdown btn-group">
                                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                                              Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> Giver's team members</button>
                                              <ul class="dropdown-menu">
                                                <li class="dropdown-header">Common feedback path combinations</li>
                                                <li class="dropdown-submenu">
                                                  <a>Feedback session creator (i.e., me) will give feedback on...</a>
                                                  <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="" data-original-title="Choose an option">
                                                    <li>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="NONE" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                                      Nobody specific (For general class feedback)</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="SELF" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                                      Giver (Self feedback)</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="SELF" data-recipient-type="INSTRUCTORS" data-path-description="Feedback session creator (i.e., me) will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                                      Instructors in the course</a>
                                                    </li>
                                                  </ul>
                                                </li>
                                                <li class="dropdown-submenu">
                                                  <a>Students in this course will give feedback on...</a>
                                                  <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="" data-original-title="Choose an option">
                                                    <li>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="NONE" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                                      Nobody specific (For general class feedback)</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="SELF" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                                      Giver (Self feedback)</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="INSTRUCTORS" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                                      Instructors in the course</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="OWN_TEAM_MEMBERS" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver's team members">
                                                      Giver's team members</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="STUDENTS" data-recipient-type="OWN_TEAM_MEMBERS_INCLUDING_SELF" data-path-description="Students in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver's team members and Giver">
                                                      Giver's team members and Giver</a>
                                                    </li>
                                                  </ul>
                                                </li>
                                                <li class="dropdown-submenu">
                                                  <a>Instructors in this course will give feedback on...</a>
                                                  <ul class="dropdown-menu" data-toggle="tooltip" data-trigger="manual" data-placement="top" data-container="body" title="" data-original-title="Choose an option">
                                                    <li>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="NONE" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Nobody specific (For general class feedback)">
                                                      Nobody specific (For general class feedback)</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="SELF" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Giver (Self feedback)">
                                                      Giver (Self feedback)</a>
                                                      <a class="feedback-path-dropdown-option" href="javascript:;" data-giver-type="INSTRUCTORS" data-recipient-type="INSTRUCTORS" data-path-description="Instructors in this course will give feedback on <span class='glyphicon glyphicon-arrow-right'></span> Instructors in the course">
                                                      Instructors in the course</a>
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
                                                  <select class="form-control participantSelect" id="givertype-1" name="givertype" disabled="">
                                                    <option value="SELF">
                                                    Feedback session creator (i.e., me)</option>
                                                    <option selected="" value="STUDENTS">
                                                    Students in this course</option>
                                                    <option value="INSTRUCTORS">
                                                    Instructors in this course</option>
                                                    <option value="TEAMS">
                                                    Teams in this course</option>
                                                  </select>
                                                </div>
                                              </div>
                                              <div class="col-sm-12 col-lg-6 padding-0 margin-bottom-7px" data-toggle="tooltip" data-placement="top" title="" data-original-title="Who the feedback is about">
                                                <label class="col-sm-4 col-lg-5 control-label">
                                                  Who the feedback is about:
                                                </label>
                                                <div class="col-sm-8 col-lg-7">
                                                  <select class="form-control participantSelect" id="recipienttype-1" name="recipienttype" disabled="">
                                                    <option value="SELF">
                                                    Giver (Self feedback)</option>
                                                    <option value="STUDENTS">
                                                    Other students in the course</option>
                                                    <option value="INSTRUCTORS">
                                                    Instructors in the course</option>
                                                    <option value="TEAMS">
                                                    Other teams in the course</option>
                                                    <option value="OWN_TEAM">
                                                    Giver's team</option>
                                                    <option selected="" value="OWN_TEAM_MEMBERS">
                                                    Giver's team members</option>
                                                    <option value="OWN_TEAM_MEMBERS_INCLUDING_SELF">
                                                    Giver's team members and Giver</option>
                                                    <option value="NONE">
                                                    Nobody specific (For general class feedback)</option>
                                                  </select>
                                                </div>
                                              </div>
                                              <div class="col-sm-12 row numberOfEntitiesElements" style="display: none;">
                                                <label class="control-label col-sm-4 small">
                                                  The maximum number of <span class="number-of-entities-inner-text"></span> each respondent should give feedback to:
                                                </label>
                                                <div class="col-sm-8 form-control-static">
                                                  <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                                                    <input class="nonDestructive" type="radio" name="numofrecipientstype" value="custom" disabled="">
                                                    <input class="nonDestructive numberOfEntitiesBox width-75-pc" type="number" name="numofrecipients" value="1" min="1" max="250" disabled="">
                                                  </div>
                                                  <div class="col-sm-4 col-md-3 col-lg-2 margin-bottom-7px">
                                                    <input class="nonDestructive" type="radio" name="numofrecipientstype" checked="" value="max" disabled="">
                                                    <span class="">Unlimited</span>
                                                  </div>
                                                </div>
                                              </div>
                                            </div>
                                          </div>
                                          <br>
                                          <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                                            <div class="margin-bottom-7px">
                                              <b class="visibility-title">Visibility</b> (Who can see the responses?)
                                            </div>
                                            <div class="visibility-options-dropdown btn-group margin-bottom-10px">
                                              <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                                              Visible to instructors only</button>
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
                                            <div class="visibilityOptions overflow-hidden" id="visibilityOptions-1" style="display:none;">
                                              <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
                                                <tbody><tr>
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
                                                    <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" disabled="">
                                                  </td>
                                                  <td>
                                                    <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" disabled="">
                                                  </td>
                                                  <td>
                                                    <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="RECEIVER" disabled="">
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
                                              </tbody></table>
                                            </div>
                                            <!-- Fix for collapsing margin problem. Reference: http://stackoverflow.com/questions/6204670 -->
                                            <div class="visibility-message overflow-hidden" id="visibilityMessage-1">
                                              This is the visibility hint as seen by the feedback giver:
                                              <ul class="text-muted background-color-warning">
                                                <li>Instructors in this course can see your response, the name of the recipient, and your name.</li>
                                              </ul>
                                            </div>
                                          </div>
                                          <div>
                                            <span class="pull-right">
                                              <input id="button_question_submit-1" type="submit" class="btn btn-primary" value="Save Changes" tabindex="0" style="display:none" disabled="">
                                            </span>
                                          </div>
                                        </div>
                                      </div>
                                    </form>
                              </div>
                              <p> Result statistics for rubric questions show how often a choice is selected for each sub-question.<br>
                                If weights are assigned to the choices, the weights will be used to calculate an average score.
                              </p>
                              <p>
                                <strong>Response Summary Table:</strong> Each choice cell of each sub-question in the Response summary table
                                has three parts <strong>Percentage (Response count) [Weight of the choice]</strong>. Each of the parts are explained below:
                                <ul>
                                  <li>
                                    Percentage: Shows the percentage of how often this choice is selected out of all responses for this sub-question.
                                  </li>
                                  <li>
                                    Response count: Shows the number of times this choice is selected
                                  </li>
                                  <li>
                                    Weight of the choice: Shows the weight attached with this choice of this sub-question.
                                  </li>
                                </ul>
                                If weights are assigned to the question, then based on the weight of each choice and the response count of that choice,
                                the overall average score of the sub-question is calculated.
                              </p>
                              <p>
                                <strong>Per Recipient Statistics:</strong> This table is only shown to the questions which have weights assigned.
                                Similar to the Response summary table, each choice of each sub-question in this table is divided into two parts,
                                which are <strong>Response Count [Weight of the choice]</strong>. Based on the weight assigned to the choice and
                                the response count of that choice, total and average points for the sub-question is calculated for each recipient.
                              </p>
                              <div class="bs-example">
                                <div class="resultStatistics">
                                  <div class="panel panel-info">
                                    <div class="panel-heading" data-target="#panelBodyCollapse-1" id="panelHeading-1" style="cursor: pointer;">
                                      <div class="display-icon pull-right"><span class="glyphicon pull-right glyphicon-chevron-up"></span></div>
                                      <form method="post" class="inline">
                                        <div id="DownloadQuestion-1" class="inline">
                                          <input id="button_download-1" type="submit" class="btn-link text-bold padding-0 color-inherit" data-toggle="tooltip" title="" name="fruploaddownloadbtn" value="Question 1:" data-original-title="Download Question Results">
                                        </div>
                                      </form>
                                      <div class="inline panel-heading-text">
                                        <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                        <span class="text-preserve-space">How well did the team members communicate&nbsp;<span style=" white-space: normal;">
                                          <a href="javascript:;" id="questionAdditionalInfoButton-1-" class="color_gray" onclick="toggleAdditionalQuestionInfo('1-')" data-more="[more]" data-less="[less]">[more]</a>
                                          <br>
                                          <span id="questionAdditionalInfo-1-" style="display:none;">Rubric question sub-questions:
                                            <p>a) This student participates well in online discussions.<br>b) This student completes assigned tasks on time.<br></p></span>
                                          </span></span>
                                        </div>
                                      </div>
                                      <div class="panel-collapse collapse in" id="panelBodyCollapse-1" style="height: auto;">
                                        <div class="panel-body padding-0" id="questionBody-0">
                                          <div class="resultStatistics">
                                            <div class="panel-body rubricStatistics">
                                              <div class="row">
                                                <div class="col-sm-8 text-color-gray">
                                                  <strong>
                                                    Response Summary
                                                  </strong>
                                                </div>
                                                <div class="col-sm-4 text-right hidden">
                                                  <input type="checkbox" class="excluding-self-response-checkbox" onclick="toggleExcludingSelfResultsForRubricStatistics(this)">
                                                  <span class="text-nowrap tool-tip-decorate" title="" data-placement="top" data-toggle="tooltip" data-original-title="Excludes giver's responses to himself/herself from Statistics">
                                                    Exclude self evaluation
                                                  </span>
                                                </div>
                                              </div>
                                              <div class="row">
                                                <div class="col-sm-12 table-responsive">
                                                  <table class="table table-striped table-bordered margin-0">
                                                    <thead>
                                                      <tr>
                                                        <th></th>
                                                        <th>
                                                          <p>Strongly Disagree</p>
                                                        </th>
                                                        <th>
                                                          <p>Disagree</p>
                                                        </th>
                                                        <th>
                                                          <p>Agree</p>
                                                        </th>
                                                        <th>
                                                          <p>Strongly Agree</p>
                                                        </th>
                                                        <th>
                                                          <p>Average</p>
                                                        </th>
                                                      </tr>
                                                    </thead>
                                                    <tbody class="table-body-including-self">
                                                      <tr>
                                                        <td>
                                                          <p>a) This student participates well in online discussions.</p>
                                                        </td>
                                                        <td>
                                                          50% (3) [0]
                                                        </td>
                                                        <td>
                                                          17% (1) [1]
                                                        </td>
                                                        <td>
                                                          17% (1) [2]
                                                        </td>
                                                        <td>
                                                          17% (1) [3]
                                                        </td>
                                                        <td>
                                                          1.00
                                                        </td>
                                                      </tr>
                                                      <tr>
                                                        <td>
                                                          <p>b) This student completes assigned tasks on time.</p>
                                                        </td>
                                                        <td>
                                                          0% (0) [0.5]
                                                        </td>
                                                        <td>
                                                          17% (1) [1]
                                                        </td>
                                                        <td>
                                                          67% (4) [1.5]
                                                        </td>
                                                        <td>
                                                          17% (1) [2]
                                                        </td>
                                                        <td>
                                                          1.50
                                                        </td>
                                                      </tr>
                                                    </tbody>
                                                    <tbody class="table-body-excluding-self hidden">
                                                    </tbody>
                                                  </table>
                                                </div>
                                                <div class="col-sm-12 table-responsive">
                                                  <br>
                                                  <strong class="text-color-gray">
                                                    Per Recipient Statistics
                                                  </strong>
                                                  <table class="table table-striped table-bordered margin-0">
                                                    <thead>
                                                      <tr>
                                                        <th>
                                                          <p>Team</p>
                                                        </th><th>
                                                          <p>Recipient Name</p>
                                                        </th><th>
                                                          <p>Sub Question</p>
                                                        </th><th>
                                                          <p>Strongly Disagree</p>
                                                        </th><th>
                                                          <p>Disagree</p>
                                                        </th><th>
                                                          <p>Agree</p>
                                                        </th><th>
                                                          <p>Strongly Agree</p>
                                                        </th><th>
                                                          <p>Total</p>
                                                        </th><th>
                                                          <p>Average</p>
                                                        </th>
                                                      </tr>
                                                    </thead>
                                                    <tbody>
                                                      <tr>
                                                        <td>Team 1</td><td>Jean Wong</td><td>a) This student participates well in online discussions.</td><td>1 [0.00]</td><td>1 [1.00]</td><td>0 [2.00]</td><td>0 [3.00]</td><td>1.00</td><td>0.50</td>
                                                      </tr><tr>
                                                        <td>Team 1</td><td>Jean Wong</td><td>b) This student completes assigned tasks on time.</td><td>0 [0.50]</td><td>0 [1.00]</td><td>2 [1.50]</td><td>0 [2.00]</td><td>3.00</td><td>1.50</td>
                                                      </tr><tr>
                                                        <td>Team 1</td><td>Ravi Kumar</td><td>a) This student participates well in online discussions.</td><td>1 [0.00]</td><td>0 [1.00]</td><td>0 [2.00]</td><td>1 [3.00]</td><td>3.00</td><td>1.50</td>
                                                      </tr><tr>
                                                        <td>Team 1</td><td>Ravi Kumar</td><td>b) This student completes assigned tasks on time.</td><td>0 [0.50]</td><td>0 [1.00]</td><td>1 [1.50]</td><td>1 [2.00]</td><td>3.50</td><td>1.75</td>
                                                      </tr><tr>
                                                        <td>Team 1</td><td>Tom Jacobs</td><td>a) This student participates well in online discussions.</td><td>1 [0.00]</td><td>0 [1.00]</td><td>1 [2.00]</td><td>0 [3.00]</td><td>2.00</td><td>1.00</td>
                                                      </tr><tr>
                                                        <td>Team 1</td><td>Tom Jacobs</td><td>b) This student completes assigned tasks on time.</td><td>0 [0.50]</td><td>1 [1.00]</td><td>1 [1.50]</td><td>0 [2.00]</td><td>2.50</td><td>1.25</td>
                                                      </tr>
                                                    </tbody>
                                                  </table>
                                                </div>
                                              </div>
                                            </div></div>
                                            <div class="table-responsive">
                                              <table class="table fixed-table-layout table-striped table-bordered data-table margin-0">
                                                <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                                                  <tr>
                                                    <th style="width: 10%; min-width: 67px;" id="button_sortFromTeam" class="button-sort-none">
                                                      Team<span class="icon-sort unsorted"></span></th>
                                                      <th style="width: 10%; min-width: 65px;" id="button_sortFromName" class="button-sort-none">
                                                        Giver<span class="icon-sort unsorted"></span></th>
                                                        <th style="width: 10%; min-width: 67px;" id="button_sortToTeam" class="button-sort-ascending">
                                                          Team<span class="icon-sort unsorted"></span></th>
                                                          <th style="width: 10%; min-width: 90px;" id="button_sortToName" class="button-sort-none">
                                                            Recipient<span class="icon-sort unsorted"></span></th>
                                                            <th style="width: 45%; min-width: 95px;" id="button_sortFeedback" class="button-sort-none">
                                                              Feedback<span class="icon-sort unsorted"></span></th>
                                                              <th style="width: 15%; min-width: 75px;" class="action-header">
                                                              Actions</th>
                                                            </tr>
                                                          </thead>
                                                          <tbody>
                                                            <tr>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space">a) Strongly Disagree <span class="color-neutral"><i>(Choice 1)</i></span><br>b) Agree <span class="color-neutral"><i>(Choice 3)</i></span><br></td>
                                                              <td>
                                                                <form class="inline" method="post">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                                <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-2-1-1" data-recipientindex="2" data-giverindex="1" data-qnindex="1" disabled>
                                                                  Add Comment
                                                                </button>
                                                                <div class="modal fade" id="commentModal-2-1-1" role="dialog">
                                                                  <div class="modal-dialog modal-lg">
                                                                    <div class="modal-content">
                                                                      <div class="modal-header">
                                                                        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="2" data-giverindex="1" data-qnindex="1">
                                                                          ×
                                                                        </button>
                                                                        <h4 class="modal-title">Add Comment:</h4>
                                                                      </div>
                                                                      <div class="modal-body">
                                                                        <ul class="list-group" id="responseCommentTable-2-1-1" style="display:none">
                                                                          <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-2-1-1" style="display: none;">
                                                                            <form class="responseCommentAddForm">
                                                                              <div class="form-group form-inline">
                                                                                <div class="form-group text-muted">
                                                                                  <p>
                                                                                    Giver: Jean Wong (Team 1)<br>
                                                                                  Recipient: Ravi Kumar (Team 1)</p>
                                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                                                </div>
                                                                                <a id="frComment-visibility-options-trigger-2-1-1" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="2" data-giverindex="1" data-qnindex="1" data-frcindex="">
                                                                                  <span class="glyphicon glyphicon-eye-close"></span>
                                                                                  Show Visibility Options
                                                                                </a>
                                                                              </div>
                                                                              <div id="visibility-options-2-1-1" class="panel panel-default" style="display: none;">
                                                                                <div class="panel-heading">
                                                                                  Visibility Options
                                                                                </div>
                                                                                <table class="table text-center" style="color: #000;">
                                                                                  <tbody>
                                                                                    <tr>
                                                                                      <th class="text-center">User/Group</th>
                                                                                      <th class="text-center">Can see this comment</th>
                                                                                      <th class="text-center">Can see comment giver's name</th>
                                                                                    </tr>
                                                                                    <tr id="response-giver-2-1-1">
                                                                                      <td class="text-left">
                                                                                        <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                                    <tr id="response-instructors-2-1-1">
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
                                                                                    </tr>
                                                                                  </tbody>
                                                                                </table>
                                                                              </div>
                                                                              <div class="form-group">
                                                                                <div class="panel panel-default panel-body" id="responseCommentAddForm-2-1-1">
                                                                                </div>
                                                                                <input type="hidden" name="responsecommenttext">
                                                                              </div>
                                                                              <div class="col-sm-offset-5">
                                                                                <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-2-1-1">
                                                                                Add</a>
                                                                              </div>
                                                                              <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                                              <input type="hidden" name="fsindex" value="2">
                                                                              <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww%jean@example.com%ravi@example.com">
                                                                              <input type="hidden" name="courseid" value="CS3424">
                                                                              <input type="hidden" name="fsname" value="New session">
                                                                              <input type="hidden" name="user" value="test@example.com">
                                                                              <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="token" value="D7BDEDE5AD98098C5524B5794CB57C53">
                                                                            </form>
                                                                          </li>
                                                                        </ul>
                                                                      </div>
                                                                      <div class="modal-footer">
                                                                        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="2" data-giverindex="1" data-qnindex="1">
                                                                          Close
                                                                        </button>
                                                                      </div>
                                                                    </div>
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space">a) Agree <span class="color-neutral"><i>(Choice 3)</i></span><br>b) Disagree <span class="color-neutral"><i>(Choice 2)</i></span><br></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                                <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-3-1-1" data-recipientindex="3" data-giverindex="1" data-qnindex="1" disabled>
                                                                  Add Comment
                                                                </button>
                                                                <div class="modal fade" id="commentModal-3-1-1" role="dialog">
                                                                  <div class="modal-dialog modal-lg">
                                                                    <div class="modal-content">
                                                                      <div class="modal-header">
                                                                        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="1" data-qnindex="1">
                                                                          ×
                                                                        </button>
                                                                        <h4 class="modal-title">Add Comment:</h4>
                                                                      </div>
                                                                      <div class="modal-body">
                                                                        <ul class="list-group" id="responseCommentTable-3-1-1" style="display:none">
                                                                          <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-3-1-1" style="display: none;">
                                                                            <form class="responseCommentAddForm">
                                                                              <div class="form-group form-inline">
                                                                                <div class="form-group text-muted">
                                                                                  <p>
                                                                                    Giver: Jean Wong (Team 1)<br>
                                                                                  Recipient: Tom Jacobs (Team 1)</p>
                                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                                                </div>
                                                                                <a id="frComment-visibility-options-trigger-3-1-1" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="3" data-giverindex="1" data-qnindex="1" data-frcindex="">
                                                                                  <span class="glyphicon glyphicon-eye-close"></span>
                                                                                  Show Visibility Options
                                                                                </a>
                                                                              </div>
                                                                              <div id="visibility-options-3-1-1" class="panel panel-default" style="display: none;">
                                                                                <div class="panel-heading">
                                                                                  Visibility Options
                                                                                </div>
                                                                                <table class="table text-center" style="color: #000;">
                                                                                  <tbody>
                                                                                    <tr>
                                                                                      <th class="text-center">User/Group</th>
                                                                                      <th class="text-center">Can see this comment</th>
                                                                                      <th class="text-center">Can see comment giver's name</th>
                                                                                    </tr>
                                                                                    <tr id="response-giver-3-1-1">
                                                                                      <td class="text-left">
                                                                                        <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                                    <tr id="response-instructors-3-1-1">
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
                                                                                    </tr>
                                                                                  </tbody>
                                                                                </table>
                                                                              </div>
                                                                              <div class="form-group">
                                                                                <div class="panel panel-default panel-body" id="responseCommentAddForm-3-1-1">
                                                                                </div>
                                                                                <input type="hidden" name="responsecommenttext">
                                                                              </div>
                                                                              <div class="col-sm-offset-5">
                                                                                <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-3-1-1">
                                                                                Add</a>
                                                                              </div>
                                                                              <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                                              <input type="hidden" name="fsindex" value="3">
                                                                              <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww%jean@example.com%tom@example.com">
                                                                              <input type="hidden" name="courseid" value="CS3424">
                                                                              <input type="hidden" name="fsname" value="New session">
                                                                              <input type="hidden" name="user" value="test@example.com">
                                                                              <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="token" value="D7BDEDE5AD98098C5524B5794CB57C53">
                                                                            </form>
                                                                          </li>
                                                                        </ul>
                                                                      </div>
                                                                      <div class="modal-footer">
                                                                        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="1" data-qnindex="1">
                                                                          Close
                                                                        </button>
                                                                      </div>
                                                                    </div>
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space">a) Strongly Disagree <span class="color-neutral"><i>(Choice 1)</i></span><br>b) Agree <span class="color-neutral"><i>(Choice 3)</i></span><br></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                                <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-1-2-1" data-recipientindex="1" data-giverindex="2" data-qnindex="1" disabled>
                                                                  Add Comment
                                                                </button>
                                                                <div class="modal fade" id="commentModal-1-2-1" role="dialog">
                                                                  <div class="modal-dialog modal-lg">
                                                                    <div class="modal-content">
                                                                      <div class="modal-header">
                                                                        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="1" data-giverindex="2" data-qnindex="1">
                                                                          ×
                                                                        </button>
                                                                        <h4 class="modal-title">Add Comment:</h4>
                                                                      </div>
                                                                      <div class="modal-body">
                                                                        <ul class="list-group" id="responseCommentTable-1-2-1" style="display:none">
                                                                          <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-1-2-1" style="display: none;">
                                                                            <form class="responseCommentAddForm">
                                                                              <div class="form-group form-inline">
                                                                                <div class="form-group text-muted">
                                                                                  <p>
                                                                                    Giver: Ravi Kumar (Team 1)<br>
                                                                                  Recipient: Jean Wong (Team 1)</p>
                                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                                                </div>
                                                                                <a id="frComment-visibility-options-trigger-1-2-1" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="1" data-giverindex="2" data-qnindex="1" data-frcindex="">
                                                                                  <span class="glyphicon glyphicon-eye-close"></span>
                                                                                  Show Visibility Options
                                                                                </a>
                                                                              </div>
                                                                              <div id="visibility-options-1-2-1" class="panel panel-default" style="display: none;">
                                                                                <div class="panel-heading">
                                                                                  Visibility Options
                                                                                </div>
                                                                                <table class="table text-center" style="color: #000;">
                                                                                  <tbody>
                                                                                    <tr>
                                                                                      <th class="text-center">User/Group</th>
                                                                                      <th class="text-center">Can see this comment</th>
                                                                                      <th class="text-center">Can see comment giver's name</th>
                                                                                    </tr>
                                                                                    <tr id="response-giver-1-2-1">
                                                                                      <td class="text-left">
                                                                                        <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                                    <tr id="response-instructors-1-2-1">
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
                                                                                    </tr>
                                                                                  </tbody>
                                                                                </table>
                                                                              </div>
                                                                              <div class="form-group">
                                                                                <div class="panel panel-default panel-body" id="responseCommentAddForm-1-2-1">
                                                                                </div>
                                                                                <input type="hidden" name="responsecommenttext">
                                                                              </div>
                                                                              <div class="col-sm-offset-5">
                                                                                <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-1-2-1">
                                                                                Add</a>
                                                                              </div>
                                                                              <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                                              <input type="hidden" name="fsindex" value="1">
                                                                              <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww%ravi@example.com%jean@example.com">
                                                                              <input type="hidden" name="courseid" value="CS3424">
                                                                              <input type="hidden" name="fsname" value="New session">
                                                                              <input type="hidden" name="user" value="test@example.com">
                                                                              <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="token" value="D7BDEDE5AD98098C5524B5794CB57C53">
                                                                            </form>
                                                                          </li>
                                                                        </ul>
                                                                      </div>
                                                                      <div class="modal-footer">
                                                                        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="1" data-giverindex="2" data-qnindex="1">
                                                                          Close
                                                                        </button>
                                                                      </div>
                                                                    </div>
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space">a) Strongly Disagree <span class="color-neutral"><i>(Choice 1)</i></span><br>b) Agree <span class="color-neutral"><i>(Choice 3)</i></span><br></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                                <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-3-2-1" data-recipientindex="3" data-giverindex="2" data-qnindex="1" disabled>
                                                                  Add Comment
                                                                </button>
                                                                <div class="modal fade" id="commentModal-3-2-1" role="dialog">
                                                                  <div class="modal-dialog modal-lg">
                                                                    <div class="modal-content">
                                                                      <div class="modal-header">
                                                                        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="2" data-qnindex="1">
                                                                          ×
                                                                        </button>
                                                                        <h4 class="modal-title">Add Comment:</h4>
                                                                      </div>
                                                                      <div class="modal-body">
                                                                        <ul class="list-group" id="responseCommentTable-3-2-1" style="display:none">
                                                                          <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-3-2-1" style="display: none;">
                                                                            <form class="responseCommentAddForm">
                                                                              <div class="form-group form-inline">
                                                                                <div class="form-group text-muted">
                                                                                  <p>
                                                                                    Giver: Ravi Kumar (Team 1)<br>
                                                                                  Recipient: Tom Jacobs (Team 1)</p>
                                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                                                </div>
                                                                                <a id="frComment-visibility-options-trigger-3-2-1" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="3" data-giverindex="2" data-qnindex="1" data-frcindex="">
                                                                                  <span class="glyphicon glyphicon-eye-close"></span>
                                                                                  Show Visibility Options
                                                                                </a>
                                                                              </div>
                                                                              <div id="visibility-options-3-2-1" class="panel panel-default" style="display: none;">
                                                                                <div class="panel-heading">
                                                                                  Visibility Options
                                                                                </div>
                                                                                <table class="table text-center" style="color: #000;">
                                                                                  <tbody>
                                                                                    <tr>
                                                                                      <th class="text-center">User/Group</th>
                                                                                      <th class="text-center">Can see this comment</th>
                                                                                      <th class="text-center">Can see comment giver's name</th>
                                                                                    </tr>
                                                                                    <tr id="response-giver-3-2-1">
                                                                                      <td class="text-left">
                                                                                        <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                                    <tr id="response-instructors-3-2-1">
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
                                                                                    </tr>
                                                                                  </tbody>
                                                                                </table>
                                                                              </div>
                                                                              <div class="form-group">
                                                                                <div class="panel panel-default panel-body" id="responseCommentAddForm-3-2-1">
                                                                                </div>
                                                                                <input type="hidden" name="responsecommenttext">
                                                                              </div>
                                                                              <div class="col-sm-offset-5">
                                                                                <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-3-2-1">
                                                                                Add</a>
                                                                              </div>
                                                                              <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                                              <input type="hidden" name="fsindex" value="3">
                                                                              <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww%ravi@example.com%tom@example.com">
                                                                              <input type="hidden" name="courseid" value="CS3424">
                                                                              <input type="hidden" name="fsname" value="New session">
                                                                              <input type="hidden" name="user" value="test@example.com">
                                                                              <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="token" value="D7BDEDE5AD98098C5524B5794CB57C53">
                                                                            </form>
                                                                          </li>
                                                                        </ul>
                                                                      </div>
                                                                      <div class="modal-footer">
                                                                        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="3" data-giverindex="2" data-qnindex="1">
                                                                          Close
                                                                        </button>
                                                                      </div>
                                                                    </div>
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=E91288C782CA96AA041C6B341301B986C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Jean Wong<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space">a) Disagree <span class="color-neutral"><i>(Choice 2)</i></span><br>b) Agree <span class="color-neutral"><i>(Choice 3)</i></span><br></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                                <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-1-3-1" data-recipientindex="1" data-giverindex="3" data-qnindex="1" disabled>
                                                                  Add Comment
                                                                </button>
                                                                <div class="modal fade" id="commentModal-1-3-1" role="dialog">
                                                                  <div class="modal-dialog modal-lg">
                                                                    <div class="modal-content">
                                                                      <div class="modal-header">
                                                                        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="1" data-giverindex="3" data-qnindex="1">
                                                                          ×
                                                                        </button>
                                                                        <h4 class="modal-title">Add Comment:</h4>
                                                                      </div>
                                                                      <div class="modal-body">
                                                                        <ul class="list-group" id="responseCommentTable-1-3-1" style="display:none">
                                                                          <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-1-3-1" style="display: none;">
                                                                            <form class="responseCommentAddForm">
                                                                              <div class="form-group form-inline">
                                                                                <div class="form-group text-muted">
                                                                                  <p>
                                                                                    Giver: Tom Jacobs (Team 1)<br>
                                                                                  Recipient: Jean Wong (Team 1)</p>
                                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                                                </div>
                                                                                <a id="frComment-visibility-options-trigger-1-3-1" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="1" data-giverindex="3" data-qnindex="1" data-frcindex="">
                                                                                  <span class="glyphicon glyphicon-eye-close"></span>
                                                                                  Show Visibility Options
                                                                                </a>
                                                                              </div>
                                                                              <div id="visibility-options-1-3-1" class="panel panel-default" style="display: none;">
                                                                                <div class="panel-heading">
                                                                                  Visibility Options
                                                                                </div>
                                                                                <table class="table text-center" style="color: #000;">
                                                                                  <tbody>
                                                                                    <tr>
                                                                                      <th class="text-center">User/Group</th>
                                                                                      <th class="text-center">Can see this comment</th>
                                                                                      <th class="text-center">Can see comment giver's name</th>
                                                                                    </tr>
                                                                                    <tr id="response-giver-1-3-1">
                                                                                      <td class="text-left">
                                                                                        <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                                    <tr id="response-instructors-1-3-1">
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
                                                                                    </tr>
                                                                                  </tbody>
                                                                                </table>
                                                                              </div>
                                                                              <div class="form-group">
                                                                                <div class="panel panel-default panel-body" id="responseCommentAddForm-1-3-1">
                                                                                </div>
                                                                                <input type="hidden" name="responsecommenttext">
                                                                              </div>
                                                                              <div class="col-sm-offset-5">
                                                                                <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-1-3-1">
                                                                                Add</a>
                                                                              </div>
                                                                              <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                                              <input type="hidden" name="fsindex" value="1">
                                                                              <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww%tom@example.com%jean@example.com">
                                                                              <input type="hidden" name="courseid" value="CS3424">
                                                                              <input type="hidden" name="fsname" value="New session">
                                                                              <input type="hidden" name="user" value="test@example.com">
                                                                              <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="token" value="D7BDEDE5AD98098C5524B5794CB57C53">
                                                                            </form>
                                                                          </li>
                                                                        </ul>
                                                                      </div>
                                                                      <div class="modal-footer">
                                                                        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="1" data-giverindex="3" data-qnindex="1">
                                                                          Close
                                                                        </button>
                                                                      </div>
                                                                    </div>
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=70620B464759414AFD63B1321B7CA89D&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Tom Jacobs<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign">Team 1</td>
                                                              <td class="word-wrap-break middlealign">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=FB0E5FFFEA7496F13D70CB3A58444B93C89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Ravi Kumar<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space">a) Strongly Agree <span class="color-neutral"><i>(Choice 4)</i></span><br>b) Strongly Agree <span class="color-neutral"><i>(Choice 4)</i></span><br></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                                <button type="button" class="btn btn-default btn-xs comment-button" style="margin-top:0.5em;" data-toggle="modal" data-target="#commentModal-2-3-1" data-recipientindex="2" data-giverindex="3" data-qnindex="1" disabled>
                                                                  Add Comment
                                                                </button>
                                                                <div class="modal fade" id="commentModal-2-3-1" role="dialog">
                                                                  <div class="modal-dialog modal-lg">
                                                                    <div class="modal-content">
                                                                      <div class="modal-header">
                                                                        <button type="button" class="close commentModalClose" data-dismiss="modal" data-recipientindex="2" data-giverindex="3" data-qnindex="1">
                                                                          ×
                                                                        </button>
                                                                        <h4 class="modal-title">Add Comment:</h4>
                                                                      </div>
                                                                      <div class="modal-body">
                                                                        <ul class="list-group" id="responseCommentTable-2-3-1" style="display:none">
                                                                          <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-2-3-1" style="display: none;">
                                                                            <form class="responseCommentAddForm">
                                                                              <div class="form-group form-inline">
                                                                                <div class="form-group text-muted">
                                                                                  <p>
                                                                                    Giver: Tom Jacobs (Team 1)<br>
                                                                                  Recipient: Ravi Kumar (Team 1)</p>
                                                                                  You may change comment's visibility using the visibility options on the right hand side.
                                                                                </div>
                                                                                <a id="frComment-visibility-options-trigger-2-3-1" class="btn btn-sm btn-info pull-right toggle-visib-add-form" data-recipientindex="2" data-giverindex="3" data-qnindex="1" data-frcindex="">
                                                                                  <span class="glyphicon glyphicon-eye-close"></span>
                                                                                  Show Visibility Options
                                                                                </a>
                                                                              </div>
                                                                              <div id="visibility-options-2-3-1" class="panel panel-default" style="display: none;">
                                                                                <div class="panel-heading">
                                                                                  Visibility Options
                                                                                </div>
                                                                                <table class="table text-center" style="color: #000;">
                                                                                  <tbody>
                                                                                    <tr>
                                                                                      <th class="text-center">User/Group</th>
                                                                                      <th class="text-center">Can see this comment</th>
                                                                                      <th class="text-center">Can see comment giver's name</th>
                                                                                    </tr>
                                                                                    <tr id="response-giver-2-3-1">
                                                                                      <td class="text-left">
                                                                                        <div data-toggle="tooltip" data-placement="top" title="" data-original-title="Control what response giver can view">
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
                                                                                    <tr id="response-instructors-2-3-1">
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
                                                                                    </tr>
                                                                                  </tbody>
                                                                                </table>
                                                                              </div>
                                                                              <div class="form-group">
                                                                                <div class="panel panel-default panel-body" id="responseCommentAddForm-2-3-1">
                                                                                </div>
                                                                                <input type="hidden" name="responsecommenttext">
                                                                              </div>
                                                                              <div class="col-sm-offset-5">
                                                                                <a href="/page/instructorFeedbackResponseCommentAdd" type="button" class="btn btn-primary" id="button_save_comment_for_add-2-3-1">
                                                                                Add</a>
                                                                              </div>
                                                                              <input type="hidden" name="questionid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww"><input type="hidden" name="isOnQuestionsPage" value="true">
                                                                              <input type="hidden" name="fsindex" value="2">
                                                                              <input type="hidden" name="responseid" value="ahF0ZWFtbWF0ZXMtc3VrYW50YXIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgJykCww%tom@example.com%ravi@example.com">
                                                                              <input type="hidden" name="courseid" value="CS3424">
                                                                              <input type="hidden" name="fsname" value="New session">
                                                                              <input type="hidden" name="user" value="test@example.com">
                                                                              <input type="hidden" name="showresponsecommentsto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="showresponsegiverto" value="GIVER, INSTRUCTORS">
                                                                              <input type="hidden" name="token" value="D7BDEDE5AD98098C5524B5794CB57C53">
                                                                            </form>
                                                                          </li>
                                                                        </ul>
                                                                      </div>
                                                                      <div class="modal-footer">
                                                                        <button type="button" class="btn btn-default commentModalClose" data-dismiss="modal" data-recipientindex="2" data-giverindex="3" data-qnindex="1">
                                                                          Close
                                                                        </button>
                                                                      </div>
                                                                    </div>
                                                                  </div>
                                                                </div>
                                                              </td>
                                                            </tr>
                                                            <tr class="pending_response_row">
                                                              <td class="word-wrap-break middlealign color-neutral">Team 2</td>
                                                              <td class="word-wrap-break middlealign color-neutral">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=0477F672339FC87D3F1558444B53051CC89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Chun Ling<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign color-neutral">Team 2</td>
                                                              <td class="word-wrap-break middlealign color-neutral">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=BBA245E7E32F26797F5627A4883E044B61C071775F284A28CA96BE4DA2CAE194&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Desmond Wu<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space color-neutral"><i>No Response</i></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
                                                                </form>
                                                              </td>
                                                            </tr>
                                                            <tr class="pending_response_row">
                                                              <td class="word-wrap-break middlealign color-neutral">Team 2</td>
                                                              <td class="word-wrap-break middlealign color-neutral">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=BBA245E7E32F26797F5627A4883E044B61C071775F284A28CA96BE4DA2CAE194&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Desmond Wu<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <td class="word-wrap-break middlealign color-neutral">Team 2</td>
                                                              <td class="word-wrap-break middlealign color-neutral">
                                                                <div class="profile-pic-icon-hover" data-link="/page/studentProfilePic?studentemail=0477F672339FC87D3F1558444B53051CC89A7046D807814E294A39DF1D149867&amp;courseid=7246BD595FC12ECC2C445BD853407325&amp;user=test%40example.com" data-original-title="" title="">
                                                                  Chun Ling<img src="" alt="No Image Given" class="hidden profile-pic-icon-hidden">
                                                                </div>
                                                              </td>
                                                              <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                                                              <td class="word-wrap-break text-preserve-space color-neutral"><i>No Response</i></td>
                                                              <td>
                                                                <form class="inline" method="post" action="/page/instructorEditStudentFeedbackPage?user=test%40example.com" target="_blank">
                                                                  <input type="submit" class="btn btn-default btn-xs" value="Moderate Response" data-toggle="tooltip" title="" data-original-title="Edit the responses given by this student" disabled>
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
                                            </div>
                                          </div>
                                        </div>
                                      </div>
                        <div class="panel panel-default" id="question-rank-options">
                          <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-rank-options-body">
                            <h4 class="panel-title">Rank (Options) Questions</h4>
                          </div>
                          <div id="question-rank-options-body" class="panel-collapse collapse">
                            <div class="panel-body">
                              <p>
                                Rank options questions allow respondents to rank the options that you create.
                              </p>
                              <p>
                                To set up a rank options question:
                                <ol>
                                  <li>
                                    Specify the question text
                                  </li>
                                  <li>
                                    (Optional) Add a description for the question
                                  </li>
                                  <li>
                                    List the options for respondents to rank
                                  </li>
                                  <li>
                                    (Optional) Allow respondents to give the same rank to multiple options
                                  </li>
                                  <li>
                                    (Optional) Set the minimum and/or maximum number of options a respondent should rank — setting these values ensures that respondents will rank your desired number of options
                                  </li>
                                  <li>
                                    Specify the feedback path that should be used to generate the appropriate feedback recipients
                                  </li>
                                </ol>
                              </p>
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
                                          <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                                        <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, the name of the recipient, and your name.</li></ul></div>
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
                        </div>
                        <div class="panel panel-default" id="question-rank-recipients">
                          <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#question-rank-recipients-body">
                            <h4 class="panel-title">Rank (Recipients) Questions</h4>
                          </div>
                          <div id="question-rank-recipients-body" class="panel-collapse collapse">
                            <div class="panel-body">
                              <p>
                                Rank recipients questions allow respondents to rank themselves, students, teams, or instructors.
                              </p>
                              <p>
                                <p>
                                  To set up a rank recipients question:
                                  <ol>
                                    <li>
                                      Specify the question text
                                    </li>
                                    <li>
                                      (Optional) Add a description for the question
                                    </li>
                                    <li>
                                      Specify the feedback path that should be used to generate the options respondents get to rank
                                    </li>
                                    <li>
                                      (Optional) Allow respondents to give the same rank to multiple options
                                    </li>
                                  </ol>
                                </p>
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
                                            <table class="data-table participantTable table table-striped text-center background-color-white margin-bottom-10px">
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
                                          <div class="col-sm-12 visibility-message overflow-hidden" id="visibilityMessage-2">This is the visibility hint as seen by the feedback giver:<ul class="text-muted background-color-warning"><li>Instructors in this course can see your response, and your name.</li></ul></div>
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
                                <p>
                                  When viewing the results of a rank recipients question, you will be able to see the following results for each feedback recipient:
                                </p>
                                <ul>
                                  <li>
                                    <b>Ranks received</b>: a list of the ranks which a recipient received from respondents
                                  </li>
                                  <li>
                                    <b>Overall rank</b>: the recipient's rank relative to other recipients, as computed by TEAMMATES
                                  </li>
                                </ul>
                                <p>
                                  Technical details about how ranks are calculated are available <a href="/technicalInformation.jsp#calculateRanks" target="_blank">here</a>.
                                </p>
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
                                                    <td class="button-sort-none" id="button_sortclaimed" style="width:15%;">Overall Rank
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
                                                        1
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
                                                        2
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
                                                        2
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
                              <p align="right">
                                <a href="#Top">Back to Top</a>
                              </p>
                              <div class="separate-content-holder">
                                <hr>
                              </div>
