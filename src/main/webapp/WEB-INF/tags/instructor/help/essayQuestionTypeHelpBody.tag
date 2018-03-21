<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - Essay Question Body" pageEncoding="UTF-8" %>

<br>
<div class="bs-example">
  <form class="form-horizontal form_question" role="form">
    <div class="panel panel-primary questionhelp-questionTable" id="essayQuestionTable">
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