<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - Numerical Scale Question Body" pageEncoding="UTF-8" %>

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
