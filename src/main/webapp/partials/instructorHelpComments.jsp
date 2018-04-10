<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="editComments">Comments</h4>
<div id="contentHolder">
  <div class="panel-group">
    <div class="panel panel-primary" id="addResponseComments">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#addResponseCommentsBody">How do I create a comment on a response?</a>
        </h3>
      </div>
      <div id="addResponseCommentsBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            While <a class="collapse-link" data-target='#fbViewResultsBody' href="#fbViewResults">viewing the results</a> of a session, you can add comments to respondents' answers.
          </p>
          <p>
            To create comments on a response in a session:
          </p>
          <ol>
            <li>
              <a class="collapse-link" data-target='#fbViewResultsBody' href="#fbViewResults">View the results of a session</a>.
            </li>
            <li>
              Click <button class="btn btn-primary btn-s">Edit View</button> and change the view type to <b>Group by - Giver &gt; Recipient &gt; Question</b> or <b>Group by - Recipient &gt; Giver &gt; Question</b>
            </li>
            <li>
              Click the <span class="glyphicon glyphicon-comment glyphicon-primary"></span> icon on the right-hand side of the response you would like to comment on
            </li>
            <li>
              Fill in the form, which will appear similar to the example below
            </li>
          </ol>
          <div class="bs-example">
            <div class="row">
              <div class="col-md-2">
                <div class="col-md-12">
                  To:
                  <strong>Rose (Team 2)</strong>

                </div>
                <div class="col-md-12 text-muted small">
                  <br> From: Alice (Team 1)

                </div>
              </div>
              <div class="col-md-10">

                <div class="panel panel-info">
                  <div class="panel-heading">Question 3:
                    <span>Rate the latest assignment's difficulty. (1 = Very Easy, 5 = Very Hard).&nbsp;
                      <span>
                        <a href="javascript:;" id="questionAdditionalInfoButton-3-giver-0-recipient-8" class="color_gray" data-more="[more]" data-less="[less]">[more]</a>
                        <br>
                        <span id="questionAdditionalInfo-3-giver-0-recipient-8" style="display:none;">Numerical-scale question:
                          <br>Minimum value: 1. Increment: 1.0. Maximum value: 5.
                        </span>
                      </span>
                    </span>
                  </div>
                  <div class="panel-body">
                    <div style="clear:both; overflow: hidden">
                      <div class="pull-left">4</div>
                      <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" data-toggle="tooltip" data-placement="top" title="Add comment">
                        <span class="glyphicon glyphicon-comment glyphicon-primary">
                        </span>
                      </button>
                    </div>

                    <ul class="list-group" id="responseCommentTable-8-0-1" style="margin-top: 15px;">

                      <!-- frComment Add form -->
                      <li class="list-group-item list-group-item-warning" id="showResponseCommentAddForm-8-0-1" style="">
                        <form class="responseCommentAddForm">
                          <div class="form-group">
                            <div class="form-group form-inline">
                              <div class="form-group text-muted">
                                <p>
                                  Giver: Alice (Team 1)
                                  <br>
                                  Recipient: Rose (Team 2)
                                </p>
                                You may change comment's visibility using the visibility options on the right hand side.
                              </div>
                              <a id="frComment-visibility-options-trigger-8-0-1" class="btn btn-sm btn-info pull-right">
                                <span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options
                              </a>
                            </div>
                            <div id="visibility-options-8-0-1" class="panel panel-default" style="display: none;">
                              <div class="panel-heading">Visibility Options</div>
                              <table class="table text-center" style="color:#000;">
                                <tbody>
                                  <tr>
                                    <th class="text-center">User/Group</th>
                                    <th class="text-center">Can see this comment</th>
                                    <th class="text-center">Can see comment giver's name</th>
                                  </tr>
                                  <tr id="response-giver-8-0-1">
                                    <td class="text-left">
                                      <div data-toggle="tooltip" data-placement="top" title="Control what response giver can view">
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

                                  <tr id="response-recipient-8-0-1">
                                    <td class="text-left">
                                      <div data-toggle="tooltip" data-placement="top" title="Control what response recipient(s) can view">
                                        Response Recipient(s)
                                      </div>
                                    </td>
                                    <td>
                                      <input class="visibilityCheckbox answerCheckbox centered" name="receiverLeaderCheckbox" type="checkbox" value="RECEIVER" checked="">
                                    </td>
                                    <td>
                                      <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="RECEIVER" checked="">
                                    </td>
                                  </tr>

                                  <tr id="response-instructors-8-0-1">
                                    <td class="text-left">
                                      <div data-toggle="tooltip" data-placement="top" title="Control what instructors can view">
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
                            <textarea class="form-control" rows="3" placeholder="Your comment about this response" name="responsecommenttext" id="responseCommentAddForm-8-0-1"></textarea>
                          </div>
                          <div class="col-sm-offset-5">
                            <a href="javascript:;" type="button" class="btn btn-primary" id="button_save_comment_for_add-8-0-1">Add</a>
                            <input type="button" class="btn btn-default" value="Cancel">
                          </div>
                        </form>
                      </li>
                    </ul>
                  </div>
                </div>

              </div>
            </div>
          </div>
      </div>
      </div>
    </div>
    <div class="panel panel-primary" id="editDeleteComments">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#editDeleteCommentsBody">How do I edit or delete a comment on a response?</a>
        </h3>
      </div>
      <div id="editDeleteCommentsBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To edit or delete a comment that you previously made on a response:
          </p>
          <ol>
            <li>
              Navigate to the page where you <a class="collapse-link" data-target="#addResponseCommentsBody" href="#addResponseComments">added the comment</a> that you want to edit or delete.
            </li>
            <li>
              Hover over the comment which you want to edit or delete.
            </li>
            <li>
              Click the <span class="glyphicon glyphicon-pencil glyphicon-primary"></span> icon to edit the comment, or
              or <span class="glyphicon glyphicon-trash glyphicon-primary"></span> icon to delete the comment. The icons are visible on the right-hand side of the comment field.
            </li>
            <li>
              If you are editing the comment, make your edits and click <button class="btn btn-primary btn-s">Save</button> to save changes.
            </li>
            <li>
              If you are deleting the comment, click <b>OK</b> to confirm that you want to delete the comment.
            </li>
          </ol>
        </div>
      </div>
    </div>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
</div>
