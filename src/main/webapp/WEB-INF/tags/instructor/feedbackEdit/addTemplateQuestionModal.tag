<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="instructorFeedbacks - feedback sessions 'template question' modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="feedbackSessionName" required="true"%>
<%@ attribute name="courseId" required="true"%>

<div class="modal fade" id="addTemplateQuestionModal" tabindex="-1" role="dialog" aria-labelledby="addTemplateQuestionModalTitle" aria-hidden="true">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">
          <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
        </button>
        <h4 class="modal-title" id="addTemplateQuestionModalTitle">Add Template Questions</h4>
      </div>
      <div class="modal-body">
        <form class="form" id="addTemplateQuestionModalForm" role="form" method="post"
              action="<%= Const.ActionURIs.INSTRUCTOR_FEEDBACK_TEMPLATE_QUESTION_ADD %>">
          <c:forEach items="${data.templateQuestions}" var="templateQn">
            <div class="panel panel-default" id="addTemplateQuestion-${templateQn.qnNumber}">
              <div class="panel-heading cursor-pointer">
                <div class="panel-title">
                  <label>
                    <input type="checkbox" name="templatequestionnum" value="${templateQn.qnNumber}">
                  </label> ${templateQn.qnDescription}
                </div>
              </div>
              <div class="panel-collapse collapse">
                <div class="panel-body">
                  <div class="panel panel-primary">
                    <div class="panel-heading">
                      <div class="row">
                        <div class="col-sm-7">
                          <span>
                            <strong>Question</strong>
                            <select class="nonDestructive text-primary" disabled="">
                              <option> ${templateQn.qnNumber} </option>
                            </select> &nbsp; ${templateQn.qnType}
                          </span>
                        </div>
                      </div>
                    </div>
                    <div class="panel-body">
                      <div class="col-sm-12 margin-bottom-15px background-color-light-blue">
                        <div class="form-group" style="padding: 15px;">
                          <h5 class="col-sm-2">
                            <label class="control-label">
                              Question
                            </label>
                          </h5>
                          <div class="col-sm-10">
                            <div data-toggle="tooltip" data-placement="top" data-original-title="Please enter the question for users to give feedback about.">
                              <textarea class="form-control textvalue nonDestructive" rows="2" name="questiontext" title="" placeholder="A concise version of the question" disabled="">${templateQn.qnText}</textarea>
                            </div>
                          </div>
                        </div>
                        <div class="form-group" style="padding: 0 15px;">
                          <h5 class="col-sm-2">
                            <label class="align-left">
                              [Optional]<br>Description
                            </label>
                          </h5>
                          <div class="col-sm-10">
                            <div class="well panel panel-default panel-body question-description mce-content-body content-editor empty" data-placeholder="More details about the question e.g. &quot;In answering the question, do consider communications made informally within the team, and formal communications with the instructors and tutors.&quot;" data-toggle="tooltip" data-placement="top" title="" data-original-title="Please enter the description of the question." spellcheck="false">
                              <p><br data-mce-bogus="1"></p>
                            </div>
                          </div>
                          <br>
                          ${templateQn.questionSpecificEditFormHtml}
                        </div>
                      </div>
                      <br>
                      <div class="col-sm-12 padding-15px margin-bottom-15px background-color-light-green">
                        <div class="col-sm-12 padding-0 margin-bottom-7px">
                          <b class="feedback-path-title">Feedback Path</b> (Who is giving feedback about whom?)
                        </div>
                        <div class="feedback-path-dropdown col-sm-12 btn-group">
                          <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">
                            Students in this course will give feedback on <span class="glyphicon glyphicon-arrow-right"></span> ${templateQn.qnFeedbackPath}
                          </button>
                        </div>
                      </div>
                      <br>
                      <div class="col-sm-12 margin-bottom-15px padding-15px background-color-light-green">
                        <div class="col-sm-12 padding-0 margin-bottom-7px">
                          <b class="visibility-title">Visibility</b> (Who can see the responses?)
                        </div>
                        <div class="visibility-options-dropdown btn-group col-sm-12 margin-bottom-10px">
                          <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false" disabled="">${templateQn.qnVisibilityOption}</button>
                        </div>
                        <div class="col-sm-12 visibility-message overflow-hidden">This is the visibility hint as seen by the feedback giver:
                          <ul class="text-muted background-color-warning">
                            <c:forEach items="${templateQn.qnVisibilityHints}" var="visibilityHint">
                              <li>${visibilityHint}</li>
                            </c:forEach>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </c:forEach>
          <input type="hidden" name="<%= Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="${feedbackSessionName}">
          <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${data.account.googleId}">
          <input type="hidden" name="<%= Const.ParamsNames.COURSE_ID %>" value="${courseId}">
          <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${data.sessionToken}">
        </form>
      </div>
      <div class="modal-footer margin-0">
        <button type="button" class="btn btn-primary" id="button_add_template_submit" disabled>Add</button>
        <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
      </div>
    </div>
  </div>
</div>
