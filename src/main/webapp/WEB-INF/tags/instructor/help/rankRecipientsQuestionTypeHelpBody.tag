<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - Rank Recipients Question Body" pageEncoding="UTF-8" %>

Rank recipients questions are questions where the students are to rank students, teams, or instructors.
<br>
<br> The options to rank are determined by the feedback path selected for the question. You can configure if students can give the same rank multiple times.
<br>
<br>
<div class="bs-example">
  <form class="form-horizontal form_question" role="form" method="post">
    <div class="panel panel-primary questionhelp-questionTable" id="rankRecpientTable">
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
                <select class="form-control participantSelect" id="givertype-1" name="questionhelp-givertype" disabled="" onchange="matchVisibilityOptionToFeedbackPath(this);getVisibilityMessage(this);">

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
                <select class="form-control participantSelect" id="recipienttype-1" name="questionhelp-recipienttype" disabled="" onchange="matchVisibilityOptionToFeedbackPath(this);getVisibilityMessage(this);">

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
    <input type="hidden" name="questionhelp-fsname" value="rankk">
    <input type="hidden" name="questionhelp-courseid" value="instr.ema-demo">
    <input type="hidden" name="questionhelp-questionid" value="ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgIKPCQw">
    <input type="hidden" name="questionhelp-questionnum" value="4">
    <input type="hidden" name="questionhelp-questiontype" value="RANK_RECIPIENTS">
    <input type="hidden" name="questionhelp-questionedittype" id="questionedittype-4" value="edit">
    <input type="hidden" name="questionhelp-showresponsesto" value="RECEIVER,INSTRUCTORS">
    <input type="hidden" name="questionhelp-showgiverto" value="RECEIVER,INSTRUCTORS">
    <input type="hidden" name="questionhelp-showrecipientto" value="RECEIVER,INSTRUCTORS">
    <input type="hidden" name="questionhelp-user" value="inst@email.com">
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
