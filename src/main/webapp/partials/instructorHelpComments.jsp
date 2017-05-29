<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<h4>
    <a name="editComments">Comments</a>
</h4>
<div id="contentHolder">
    <br>
    <ol style="list-style-type:none;">
        <li>
            <span class="text-bold">
                <a name="addStudentComments">
                    <h3>1. Create comments for student</h3>
                </a>
            </span>
            <div class="row">
                <br> Go to the
                <b>‘Students’</b> page, click the panel heading for the relevant course. You will see a list of students similar to the following table.
                <br> Click
                <b>'Add Comment'</b> button in the last column, then you can comment on the student, his/her team, or his/her tutorial group.
                <br>
                <br>
                <div class="bs-example">
                    <table class="table table-responsive table-striped table-bordered margin-0">
                        <tbody>
                            <tr id="student-c0.0">
                                <td id="studentphoto-c0.0">
                                    <div class="profile-pic-icon-click align-center" data-link="">
                                        <a class="student-profile-pic-view-link btn-link">View Photo</a>
                                        <img src="" alt="No Image Given" class="hidden">
                                    </div>
                                </td>
                                <td id="studentsection-c0.0">Tutorial Group 1</td>
                                <td id="studentteam-c0.0.0">Team 1</td>
                                <td id="studentname-c0.0">Alice Betsy</td>
                                <td id="studentstatus-c0.0">Joined</td>
                                <td id="studentemail-c0.0">alice.b.tmms@gmail.com</td>
                                <td class="no-print align-center">
                                    <a class="btn btn-default btn-xs student-view-for-test" href="javascript:;" data-toggle="tooltip" data-placement="top" title="View the details of the student"> View</a>&nbsp;
                                    <a class="btn btn-default btn-xs student-edit-for-test" href="javascript:;" data-toggle="tooltip" data-placement="top" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"> Edit</a>&nbsp;
                                    <a class="btn btn-default btn-xs student-delete-for-test" href="javascript:;" onclick="return toggleDeleteStudentConfirmation('kk.kk.-demo','Alice Betsy')"> Delete</a>&nbsp;
                                    <a class="btn btn-default btn-xs student-records-for-test" href="javascript:;" data-toggle="tooltip" data-placement="top" title="View all student's evaluations and feedbacks"> All Records</a>&nbsp;
                                    <div class="btn-group" data-toggle="tooltip" data-placement="top" title="" data-original-title="Give a comment for this student, his/her team/section">
                                        <a class="btn btn-default btn-xs cursor-default" href="javascript:;" data-toggle="dropdown">
                                            Add Comment
                                        </a>
                                        <ul class="dropdown-menu align-left" role="menu" aria-labelledby="dLabel">
                                        <li role="presentation">
                                            <a target="_blank" rel="noopener noreferrer" role="menuitem" tabindex="-1" href="/page/instructorCourseStudentDetailsPage?courseid=sd.kjg-demo&amp;studentemail=alice.b.tmms%40gmail.tmt&amp;user=test%40example.com&amp;token=BCA680D091B6DFB0F61011773E472616&amp;addComment=student">
                                                Comment on student: Alice Betsy
                                            </a>
                                        </li>
                                        <li role="presentation">
                                            <a target="_blank" rel="noopener noreferrer" role="menuitem" tabindex="-1" href="/page/instructorCourseStudentDetailsPage?courseid=sd.kjg-demo&amp;studentemail=alice.b.tmms%40gmail.tmt&amp;user=test%40example.com&amp;token=BCA680D091B6DFB0F61011773E472616&amp;addComment=team">
                                                Comment on team: Team 1
                                            </a>
                                        </li>
                                        
                                            <li role="presentation">
                                                <a target="_blank" rel="noopener noreferrer" role="menuitem" tabindex="-1" href="/page/instructorCourseStudentDetailsPage?courseid=sd.kjg-demo&amp;studentemail=alice.b.tmms%40gmail.tmt&amp;user=test%40example.com&amp;token=BCA680D091B6DFB0F61011773E472616&amp;addComment=section">
                                                    Comment on section: Tutorial Group 1
                                                </a>
                                            </li>
                                        
                                        </ul>
                                        <a class="btn btn-default btn-xs dropdown-toggle" href="javascript:;" data-toggle="dropdown">
                                            <span class="caret"></span><span class="sr-only">Add comments</span>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <br> To create comments for the whole course, go to the
                <b>‘Courses’</b> page, click
                <b>'View'</b> for the relevant course.
                <br> Click the
                <b>'Add'</b>
                <span class="glyphicon glyphicon-comment glyphicon-primary"></span> button at the top right corner within the course details, then you can create a comment for the whole course in the following form.
                <br>
                <br>
                <div class="bs-example">
                    <div class="well well-plain" id="courseInformationHeader">
                        <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" data-toggle="tooltip" data-placement="top" title="Give a comment about all students in the course">
                            <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
                        </button>
                        <div class="form form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Course ID:</label>
                                <div class="col-sm-6" id="courseid">
                                    <p class="form-control-static">Demo-Course</p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Course name:</label>
                                <div class="col-sm-6" id="coursename">
                                    <p class="form-control-static">Sample Course 101</p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Sections:</label>
                                <div class="col-sm-6" id="total_sections">
                                    <p class="form-control-static">2</p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Teams:</label>
                                <div class="col-sm-6" id="total_teams">
                                    <p class="form-control-static">3</p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Total students:</label>
                                <div class="col-sm-6" id="total_students">
                                    <p class="form-control-static">9</p>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Instructors:</label>
                                <div class="col-sm-6" id="instructors">
                                    <div class="form-control-static">

                                        Co-owner: Demo (demo@demo.com)
                                        <br>
                                        <br>

                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="align-center">
                                    <input type="button" class="btn btn-primary" id="button_remind" data-toggle="tooltip" data-placement="top" value="Remind Students to Join" tabindex="1" title="Email an invitation to all students yet to join requesting them to join the course using their Google Accounts. Note: Students can use TEAMMATES without ‘joining’, but a joined student can access extra features e.g. set up a user profile">
                                    <form method="post" style="display:inline;">
                                        <input id="button_download" type="submit" class="btn btn-primary" name="fruploaddownloadbtn" value=" Download Student List ">
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div id="commentArea" class="well well-plain" style="">
                        <form method="post" name="form_commentadd">
                            <div class="form-group form-inline">
                                <label style="margin-right: 24px;">Recipient:
                                </label>
                                <select id="comment_recipient_select" class="form-control" disabled="">
                                    <option value="COURSE" selected="">The whole class</option>
                                </select>
                                <a id="visibility-options-trigger" class="btn btn-sm btn-info pull-right">
                                    <span class="glyphicon glyphicon-eye-close"></span>
                                    Show Visibility Options</a>
                            </div>
                            <p class="form-group text-muted">
                                The default visibility for your comment is private. You may change it using the ‘show visibility options’ button above.
                            </p>
                            <div id="visibility-options" class="panel panel-default" style="display: none;">
                                <div class="panel-heading">Visibility Options</div>
                                <table class="table text-center" style="background: #fff;">
                                    <tbody>
                                        <tr>
                                            <th class="text-center">User/Group</th>
                                            <th class="text-center">Can see this comment</th>
                                            <th class="text-center">Can see comment giver's name</th>
                                            <th class="text-center">Can see comment recipient's name</th>
                                        </tr>
                                        <tr id="recipient-course">
                                            <td class="text-left">
                                                <div data-toggle="tooltip" data-placement="top" title="Control what students in this course can view">
                                                    Students in this course</div>
                                            </td>
                                            <td>
                                                <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="COURSE">
                                            </td>
                                            <td>
                                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="COURSE">
                                            </td>
                                            <td>
                                                <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="COURSE" disabled="">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-left">
                                                <div data-toggle="tooltip" data-placement="top" title="Control what instructors can view">
                                                    Instructors</div>
                                            </td>
                                            <td>
                                                <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTOR">
                                            </td>
                                            <td>
                                                <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTOR">
                                            </td>
                                            <td>
                                                <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="INSTRUCTOR">
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <textarea class="form-control" rows="6" placeholder="Enter your comment here ..." style="margin-bottom: 15px;" name="commenttext" id="commentText"></textarea>
                            <div style="text-align: center;">
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </li>
        <li>
            <span class="text-bold">
                <a name="addResponseComments">
                    <h3>2. Create comments for response</h3>
                </a>
            </span>
            <div class="row">
                <br> To create comments for response, go to the
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
                                    <span >Rate the latest assignment's difficulty. (1 = Very Easy, 5 = Very Hard).&nbsp;
                                        <span >
                                            <a href="javascript:;" id="questionAdditionalInfoButton-3-giver-0-recipient-8" class="color_gray" onclick="toggleAdditionalQuestionInfo('3-giver-0-recipient-8')" data-more="[more]" data-less="[less]">[more]</a>
                                            <br>
                                            <span id="questionAdditionalInfo-3-giver-0-recipient-8" style="display:none;">Numerical-scale question:
                                                <br>Minimum value: 1. Increment: 1.0. Maximum value: 5.</span>
                                        </span>
                                    </span>
                                </div>
                                <div class="panel-body">
                                    <div style="clear:both; overflow: hidden">
                                        <div class="pull-left">4</div>
                                        <button type="button" class="btn btn-default btn-xs icon-button pull-right" id="button_add_comment" onclick="showResponseCommentAddForm(8,0,1)" data-toggle="tooltip" data-placement="top" title="Add comment">
                                            <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
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
                                                        <a id="frComment-visibility-options-trigger-8-0-1" class="btn btn-sm btn-info pull-right" onclick="toggleVisibilityEditForm(8,0,1)">
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
                                                                            Response Giver</div>
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
                                                                            Response Recipient(s)</div>
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
                                                                            Instructors</div>
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
                                                    <input type="button" class="btn btn-default" value="Cancel" onclick="hideResponseCommentAddForm(8,0,1)">
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
        <li>
            <span class="text-bold">
                <a name="viewComments">
                    <h3>3. View comments</h3>
                </a>
            </span>
            <div class="row">
                <br> You can view all your comments in the
                <b>'Comments'</b> page.
                <br>
            </div>
        </li>
        <li>
            <span class="text-bold">
                <a name="editDeleteComments">
                    <h3>4. Edit and delete comments</h3>
                </a>
            </span>
            <div class="row">
                <br> Go to the
                <b>'Comments'</b> page, hover the mouse upon a comment.
                <br> Then the
                <b>'Edit'</b>
                <span class="glyphicon glyphicon-pencil glyphicon-primary"></span> and '
                <b>Delete'</b>
                <span class="glyphicon glyphicon-trash glyphicon-primary"></span> buttons will be visible to you on the right-hand side.
                <br>
                <br>
                <div class="bs-example">
                    <div class="panel panel-info student-record-comments giver_display-by-you">
                        <div class="panel-heading">
                            From
                            <b>You</b>
                        </div>
                        <ul class="list-group comments">

                            <li id="4538783999459328" class="list-group-item list-group-item-warning status_display-public">
                                <form method="post" action="/page/instructorStudentCommentEdit" name="form_commentedit" class="form_comment" id="form_commentedit-1">
                                    <div id="commentBar-1" style="display: block;">

                                        <span class="text-muted">To
                                            <b>Alice</b> (Team 1,
                                            <a href="mailto:alice.b.tmms@gmail.com">alice.b.tmms@gmail.com</a>) on 01 Apr 2016, 23:59</span>

                                        <a type="button" id="commentdelete-1" class="btn btn-default btn-xs icon-button pull-right" onclick="return deleteComment('1');" data-toggle="tooltip" data-placement="top" title="Delete this comment">
                                            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                        </a>
                                        <a type="button" id="commentedit-1" class="btn btn-default btn-xs icon-button pull-right" onclick="return enableEdit('1');" data-toggle="tooltip" data-placement="top" title="Edit this comment">
                                            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                        </a>


                                        <span class="glyphicon glyphicon-eye-open" data-toggle="tooltip" style="margin-left: 5px;" data-placement="top" title="This comment is visible to instructors"></span>


                                    </div>
                                    <div id="plainCommentText1" style="margin-left: 15px; display: block;">Comment from Instructor to Student Alice</div>

                                    <div id="commentTextEdit1" style="display: none;">
                                        <div class="form-group form-inline">
                                            <div class="form-group text-muted">
                                                You may change comment's visibility using the visibility options on the right hand side.
                                            </div>
                                            <a id="visibility-options-trigger1" class="btn btn-sm btn-info pull-right">
                                                <span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options
                                            </a>
                                        </div>
                                        <div id="visibility-options1" class="panel panel-default" style="display: none;">
                                            <div class="panel-heading">Visibility Options</div>
                                            <table class="table text-center text-color-black">
                                                <tbody>
                                                    <tr>
                                                        <th class="text-center">User/Group</th>
                                                        <th class="text-center">Can see this comment</th>
                                                        <th class="text-center">Can see comment giver's name</th>
                                                        <th class="text-center">Can see comment recipient's name</th>
                                                    </tr>

                                                    <tr id="recipient-person1">
                                                        <td class="text-left">
                                                            <div data-toggle="tooltip" data-placement="top" title="Control what comment recipient(s) can view">
                                                                Recipient(s)</div>
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="PERSON">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="PERSON">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox recipientCheckbox" name="receiverFollowerCheckbox" type="checkbox" value="PERSON" disabled="">
                                                        </td>
                                                    </tr>


                                                    <tr id="recipient-team1">
                                                        <td class="text-left">
                                                            <div data-toggle="tooltip" data-placement="top" title="Control what team members of comment recipients can view">
                                                                Recipient's Team</div>
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="TEAM">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="TEAM" checked="">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="TEAM">
                                                        </td>
                                                    </tr>


                                                    <tr id="recipient-section1">
                                                        <td class="text-left">
                                                            <div data-toggle="tooltip" data-placement="top" title="Control what students in the same section can view">
                                                                Recipient's Section</div>
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="SECTION">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="SECTION" checked="">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="SECTION">
                                                        </td>
                                                    </tr>

                                                    <tr id="recipient-course1">
                                                        <td class="text-left">
                                                            <div data-toggle="tooltip" data-placement="top" title="Control what other students in this course can view">
                                                                Other students in this course</div>
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="COURSE">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="COURSE" checked="">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="COURSE">
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <td class="text-left">
                                                            <div data-toggle="tooltip" data-placement="top" title="Control what instructors can view">
                                                                Instructors</div>
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTOR" checked="">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTOR">
                                                        </td>
                                                        <td>
                                                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="INSTRUCTOR">
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                        <div class="form-group">
                                            <textarea class="form-control" rows="3" placeholder="Your comment about this student" name="commenttext" id="commentText1">Comment from Instructor to Student Alice in kk.kk.-demo</textarea>
                                        </div>
                                        <div class="col-sm-offset-5">
                                            <input id="commentsave-1" title="Save comment" onclick="return submitCommentForm('1');" type="submit" class="btn btn-primary" value="Save">
                                            <input type="button" class="btn btn-default" value="Cancel" onclick="return disableComment('1');">
                                        </div>
                                    </div>
                                </form>
                            </li>

                        </ul>
                    </div>
                </div>
            </div>
        </li>
        <li>
            <span class="text-bold">
                <a name="searchComments">
                    <h3>5. Search for comments</h3>
                </a>
            </span>
            <div class="row">
                <br> Go to the
                <b>'Search'</b> page, tick the checkboxes for comments.
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
                                    <input id="comments-for-student-check" type="checkbox" name="searchcommentforstudents" value="true" checked="">
                                    <label for="comments-for-student-check">Comments for students</label>
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
        <li>
            <span class="text-bold">
                <a name="notifyComments">
                    <h3>6. Notify students of comments</h3>
                </a>
            </span>
            <div class="row">
                <br> To notify students of their received comments, ensure those comments are visible to the recipient(s).
                <br> Click the
                <b>'Notify'</b>
                <a type="button" class="btn btn-sm btn-info" data-toggle="tooltip" href="javascript:;" title="Send email notification to 1 recipient(s) of comments pending notification">
                    <span class="badge" style="margin-right: 5px">1</span>
                    <span class="glyphicon glyphicon-comment"></span>
                    <span class="glyphicon glyphicon-arrow-right"></span>
                    <span class="glyphicon glyphicon-envelope"></span>
                </a> button on the top right corner to send the notification emails.
                <br>
                <br>
                <div class="bs-example">
                    <div class="well well-plain">
                        <div class="row">
                            <h4 class="col-sm-9 text-color-primary">
                                <strong> demo-course : Demo Course
                                </strong>
                            </h4>
                            <div class="btn-group pull-right" style="">
                                <a type="button" class="btn btn-sm btn-info" data-toggle="tooltip" style="margin-right: 17px;" href="javascript:;" title="Send email notification to 1 recipient(s) of comments pending notification">
                                    <span class="badge" style="margin-right: 5px">1</span>
                                    <span class="glyphicon glyphicon-comment"></span>
                                    <span class="glyphicon glyphicon-arrow-right"></span>
                                    <span class="glyphicon glyphicon-envelope"></span>
                                </a>
                            </div>
                        </div>
                        <div id="no-comment-panel" style="display:none;">
                            <br>
                            <div class="panel">
                                <div class="panel-body">
                                    You don't have any comment in this course.
                                </div>
                            </div>
                        </div>

                        <div id="panel_display-1">
                            <br>
                            <div class="panel panel-primary">
                                <div class="panel-heading">
                                    <strong>Comments for students</strong>
                                </div>
                                <div class="panel-body">


                                    <div class="panel panel-info student-record-comments giver_display-by-you">
                                        <div class="panel-heading">
                                            From
                                            <b>You</b>
                                        </div>
                                        <ul class="list-group comments">

                                            <li id="4691616115720192" class="list-group-item list-group-item-warning status_display-public">
                                                <form name="form_commentedit" class="form_comment" id="form_commentedit-1">
                                                    <div id="commentBar-1">

                                                        <span class="text-muted">To
                                                            <b>All students in this course</b> on 01 Jun 2012, 19:20</span>

                                                        <a type="button" id="commentdelete-1" class="btn btn-default btn-xs icon-button pull-right" onclick="return deleteComment('1');" data-toggle="tooltip" data-placement="top" title="Delete this comment" style="display: none;">
                                                            <span class="glyphicon glyphicon-trash glyphicon-primary"></span>
                                                        </a>
                                                        <a type="button" id="commentedit-1" class="btn btn-default btn-xs icon-button pull-right" onclick="return enableEdit('1');" data-toggle="tooltip" data-placement="top" title="Edit this comment" style="display: none;">
                                                            <span class="glyphicon glyphicon-pencil glyphicon-primary"></span>
                                                        </a>


                                                        <span class="glyphicon glyphicon-eye-open" data-toggle="tooltip" style="margin-left: 5px;" data-placement="top" title="This comment is visible to the whole class"></span>


                                                        <span class="glyphicon glyphicon-bell" data-toggle="tooltip" data-placement="top" title="This comment is pending notification. i.e., you have not sent a notification about this comment yet"></span>

                                                    </div>
                                                    <div id="plainCommentText1" style="margin-left: 15px;">Hi all!</div>

                                                    <div id="commentTextEdit1" style="display: none;">
                                                        <div class="form-group form-inline">
                                                            <div class="form-group text-muted">
                                                                You may change comment's visibility using the visibility options on the right hand side.
                                                            </div>
                                                            <a id="visibility-options-trigger1" class="btn btn-sm btn-info pull-right">
                                                                <span class="glyphicon glyphicon-eye-close"></span> Show Visibility Options
                                                            </a>
                                                        </div>
                                                        <div id="visibility-options1" class="panel panel-default" style="display: none;">
                                                            <div class="panel-heading">Visibility Options</div>
                                                            <table class="table text-center text-color-black">
                                                                <tbody>
                                                                    <tr>
                                                                        <th class="text-center">User/Group</th>
                                                                        <th class="text-center">Can see this comment</th>
                                                                        <th class="text-center">Can see comment giver's name</th>
                                                                        <th class="text-center">Can see comment recipient's name</th>
                                                                    </tr>



                                                                    <tr id="recipient-course1">
                                                                        <td class="text-left">
                                                                            <div data-toggle="tooltip" data-placement="top" title="Control what other students in this course can view">
                                                                                Students in this course</div>
                                                                        </td>
                                                                        <td>
                                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="COURSE" checked="">
                                                                        </td>
                                                                        <td>
                                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="COURSE" checked="">
                                                                        </td>
                                                                        <td>
                                                                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="COURSE" disabled="">
                                                                        </td>
                                                                    </tr>
                                                                    <tr>
                                                                        <td class="text-left">
                                                                            <div data-toggle="tooltip" data-placement="top" title="Control what instructors can view">
                                                                                Instructors</div>
                                                                        </td>
                                                                        <td>
                                                                            <input class="visibilityCheckbox answerCheckbox" type="checkbox" value="INSTRUCTOR">
                                                                        </td>
                                                                        <td>
                                                                            <input class="visibilityCheckbox giverCheckbox" type="checkbox" value="INSTRUCTOR">
                                                                        </td>
                                                                        <td>
                                                                            <input class="visibilityCheckbox recipientCheckbox" type="checkbox" value="INSTRUCTOR">
                                                                        </td>
                                                                    </tr>
                                                                </tbody>
                                                            </table>
                                                        </div>
                                                        <div class="form-group">
                                                            <textarea class="form-control" rows="3" placeholder="Your comment about this student" name="commenttext" id="commentText1">Hi all!</textarea>
                                                        </div>
                                                        <div class="col-sm-offset-5">
                                                            <input id="commentsave-1" title="Save comment" onclick="return false;" type="submit" class="btn btn-primary" value="Save">
                                                            <input type="button" class="btn btn-default" value="Cancel" onclick="return disableComment('1');">
                                                        </div>
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
        </li>
    </ol>
    <br>
    <p align="right">
        <a href="#Top">Back to Top</a>
    </p>
</div>