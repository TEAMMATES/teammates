<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="editComments">Comments</h4>
<div id="contentHolder">
  <br>
  <ol style="list-style-type:none;">
    <li id="addResponseComments">
      <span class="text-bold">
          <b>1. Create comments for response</b>
      </span>
      <div>
        To create comments for response, go to the
        <b>‘Sessions’</b> page, click
        <b>'View Results'</b> for the relevant session.
        <br> Change the view type to
        <b>'Group by - Giver &gt; Recipient &gt; Question'</b> or
        <b>'Group by - Recipient &gt; Giver &gt; Question'</b>
        <br> Click the
        <b>'Add'</b>
        <span class="glyphicon glyphicon-comment glyphicon-primary"></span> button at the right-hand side inside a response, then you can create a comment for response in the following form.
        <br>
        <br>
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
    </li>
    <li id="editDeleteComments">
      <span class="text-bold">
        <b>2. Edit and delete comments</b>
      </span>
      <div>
        Go to the same page used for adding a comment, then hover the mouse upon a comment.
        <br> The
        <b>'Edit'</b>
        <span class="glyphicon glyphicon-pencil glyphicon-primary"></span> and '
        <b>Delete'</b>
        <span class="glyphicon glyphicon-trash glyphicon-primary"></span> buttons will be visible to you on the right-hand side.
        <br>
        <br>
      </div>
    </li>
    <li id="searchComments">
      <span class="text-bold">
          <b>3. Search for comments</b>
      </span>
      <div>
        Go to the
        <b>'Search'</b> page, tick the checkbox for comments.
        <br> Type the keywords in the input, then click the search button on the right-hand side or press the Enter key to start searching.
        <br>
        <br>
        <div class="bs-example">
          <div class="well well-plain">
            <div class="form-group">
              <div class="input-group">
                <input type="text" name="searchkey" value="" title="Search for comment" placeholder="Your search keyword" class="form-control" id="searchBox">
                <span class="input-group-btn">
                  <button class="btn btn-primary" type="submit" value="Search" id="buttonSearch">Search</button>
                </span>
              </div>
            </div>
            <div class="form-group">
              <ul class="list-inline">
                <li>
                  <span data-toggle="tooltip" class="glyphicon glyphicon-info-sign" title="Tick the checkboxes to limit your search to certain categories"></span>
                </li>
                <li>
                  <input id="comments-for-responses-check" type="checkbox" name="searchcommentforresponses" value="true" checked="">
                  <label for="comments-for-responses-check">Comments for responses</label>
                </li>
                <li>
                  <input id="students-check" type="checkbox" name="searchstudents" value="true">
                  <label for="students-check">Students</label>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </li>
  </ol>
  <br>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
</div>
