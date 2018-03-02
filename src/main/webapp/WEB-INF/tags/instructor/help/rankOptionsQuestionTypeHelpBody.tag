<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - Rank Options Question Body" pageEncoding="UTF-8" %>

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
  <form class="form-horizontal form_question" role="form" method="post">
    <div class="panel panel-primary questionhelp-questionTable" id="rankOptionsTable">
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
    <input type="hidden" name="questionhelp-fsname" value="rankk">
    <input type="hidden" name="questionhelp-courseid" value="instr.ema-demo">
    <input type="hidden" name="questionhelp-questionid" value="ag50ZWFtbWF0ZXMtam9obnIdCxIQRmVlZGJhY2tRdWVzdGlvbhiAgICAgKzgCQw">
    <input type="hidden" name="questionhelp-questionnum" value="3">
    <input type="hidden" name="questionhelp-questiontype" value="RANK_OPTIONS">
    <input type="hidden" name="questionhelp-questionedittype" id="questionedittype-3" value="edit">
    <input type="hidden" name="questionhelp-showresponsesto" value="RECEIVER,INSTRUCTORS">
    <input type="hidden" name="questionhelp-showgiverto" value="RECEIVER,INSTRUCTORS">
    <input type="hidden" name="questionhelp-showrecipientto" value="RECEIVER,INSTRUCTORS">
    <input type="hidden" name="questionhelp-user" value="inst@email.com">
  </form>
</div>
