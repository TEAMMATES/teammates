<%@ tag description="instructorHelpSessions and instructorFeedbackEdit - Team Contribution Question Body" pageEncoding="UTF-8" %>

Team contribution questions are a specialized question type designed for team contribution evaluations.
<br> They allow estimation of perceived contribution of a student and prevents a student from inflating his own score. To see more details about the calculation of results and other common questions, see the FAQ
<a href="/instructorHelp.jsp#Top" target="_blank" rel="noopener noreferrer">here</a>.
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
                <select class="form-control participantSelect" id="givertype--1" name="questionhelp-givertype">

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
                <select class="form-control participantSelect" id="recipienttype--1" name="questionhelp-recipienttype">

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
<a href="/instructorHelp.jsp#faq-interpret-contribution-values-in-results" target="_blank" rel="noopener noreferrer" id="interpret_help_link">here</a> on how to use these results.
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
                <a href="/instructorHelp.jsp#faq-interpret-contribution-values-in-results" target="_blank" rel="noopener noreferrer" id="interpret_help_link">How do I interpret/use these values?</a>]
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
