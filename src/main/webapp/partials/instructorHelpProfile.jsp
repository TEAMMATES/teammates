<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="profiles">Students</h2>
<div id="contentHolder">
  <br>
  <ol style="list-style-type: none;">
    <li id="viewStudentProfile">
      <span class="text-bold">
          <b>How do I view a student's student profile?</b>
      </span>
      <div>
        To view the profile of Student A from Course B:
        <ol>
          <li>
            Go to the <b>Students</b> page and click the panel heading for Course B. You will see a list of students enrolled in the course.
          </li>
          <li>
            Click the <button class="btn btn-default btn-xs">View</button> button in the last column of the row corresponding to Student A.
            A new page will open displaying the student's profile, similar to the sample profile below.
          </li>
        </ol>
        <div class="bs-example">
          <div class="row">
            <div class="col-md-2 col-xs-3 block-center">
              <img src="/images/profile_picture_default.png" class="profile-pic pull-right">
            </div>
            <div class="col-md-10 col-sm-9 col-xs-8">
              <table class="table table-striped">
                <thead>
                  <tr>
                    <th colspan="2">Profile</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td class="text-bold">Short Name (Gender)</td>
                    <td>Alice
                      (<i> female </i>)
                    </td>
                  </tr>
                  <tr>
                    <td class="text-bold">Personal Email</td>
                    <td>alice@email.com</td>
                  </tr>
                  <tr>
                    <td class="text-bold">Institution</td>
                    <td>National University of Singapore</td>
                  </tr>
                  <tr>
                    <td class="text-bold">Nationality</td>
                    <td>American</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div class="well well-plain">
            <button type="button" class="btn btn-default btn-xs icon-button pull-right"
                    id="button_add_comment" data-toggle="tooltip"
                    data-placement="top" title="Add comment">
              <span class="glyphicon glyphicon-comment glyphicon-primary"></span>
            </button>
            <div class="form form-horizontal" id="studentInfomationTable">
              <div class="form-group">
                <label class="col-sm-1 control-label">Student Name:</label>
                <div class="col-sm-11">
                  <p class="form-control-static">Alice Betsy</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-1 control-label">Section Name:</label>
                <div class="col-sm-11">
                  <p class="form-control-static">Section A</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-1 control-label">Team Name:</label>
                <div class="col-sm-11">
                  <p class="form-control-static">Team A</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-1 control-label">Official Email Address:</label>
                <div class="col-sm-11">
                  <p class="form-control-static">alice@email.com</p>
                </div>
              </div>
              <div class="form-group">
                <label class="col-sm-1 control-label">Comments:</label>
                <div class="col-sm-11">
                  <p class="form-control-static">Alice is a transfer student.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
        <p>
          The student's profile page displays the student's details and course-related information, such as:
        </p>
        <ul>
          <li>
            <b>Section name</b>: the name of the section you enrolled the student in. This only appears if sections are created for the course.
          </li>
          <li>
            <b>Team name</b>: the name of the team you enrolled the student in, or <i>NA</i> if the student does not belong to a team.
          </li>
          <li>
            <b>Official email address</b>: the email address that will be used to contact the student, taken from enrollment information
          </li>
          <li>
            <b>Comments</b>: additional student information you entered in the Comments column during enrollment
          </li>
        </ul>
        <p>
          Below this is the <b>More Info</b> section containing a personal description given by the student, if any.<br>
          You can press the <span class="text-muted glyphicon glyphicon-resize-full"></span> button in the top-right corner to display the information in a modal for better readability.
        </p>
        <div class="bs-example">
          <div class="modal fade" id="studentProfileMoreInfo" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
              <div class="modal-content">
                <div class="modal-header">
                  <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                  <h4 class="modal-title">Alice Betsy's Profile - More Info</h4>
                </div>
                <div class="modal-body">
                  <br>
                  <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
                  <p class="text-preserve-space height-fixed-md">
                    Hi I am Alice Betsy! I am from Colorado, America. I am a programming and gaming enthusiast. Aspiring to become a Software Architect in a well reputed organization.
                  </p>
                </div>
                <div class="modal-footer">
                  <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
                </div>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col-xs-12">
              <div class="panel panel-default">
                <div class="panel-body">
                  <span data-toggle="modal" data-target="#studentProfileMoreInfo"
                      class="text-muted pull-right glyphicon glyphicon-resize-full cursor-pointer"></span>
                  <h5>More Info </h5>
                  <%-- Note: When an element has class text-preserve-space, do not insert HTML spaces --%>
                  <p class="text-preserve-space height-fixed-md">Hi I am Alice Betsy! I am from Colorado, America. I am a programming and gaming enthusiast. Aspiring to become a Software Architect in a well reputed organization.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </li>
    <br>
    <li id="editStudentProfile">
      <span class="text-bold">
          <b>2. Editing student profiles</b>
      </span>
      <div>
        To edit the name, section, team, contact email, or instructor comments of Student A from Course B:
        <ol>
          <li>
            Go to the <b>Students</b> page and click the panel heading for Course B. You will see a list of students enrolled in Course B.
          </li>
          <li>
            Click the<button class="btn btn-default btn-xs">Edit</button> button in the last column of the row corresponding to Student A.<br>
          </li>
          <li>
            In the new page that opens, edit the relevant fields of Student A's profile. The page will look similar to the example below.
          </li>
          <li>
            Click <button class="btn btn-primary btn-s">Save Changes</button> to save your changes to Student A's profile
          </li>
        </ol>
        <div class="bs-example">
          <div class="panel panel-primary" id="studentEditProfile">
            <div class="panel-body fill-plain">
              <form class="form form-horizontal">
                <div class="form-group">
                  <label class="col-sm-1 control-label">Student Name:</label>
                  <div class="col-sm-11">
                    <input class="form-control" value="Alice Betsy">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-1 control-label">Section Name:</label>
                  <div class="col-sm-11">
                    <input class="form-control" value="Section A">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-1 control-label">Team Name:</label>
                  <div class="col-sm-11">
                    <input class="form-control" value="Team A">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-1 control-label">E-mail Address:
                  </label>
                  <div class="col-sm-11">
                    <input class="form-control" value="alice@email.com">
                  </div>
                </div>
                <div class="form-group">
                  <label class="col-sm-1 control-label">Comments:</label>
                  <div class="col-sm-11">
                    <textarea class="form-control" rows="6">Alice is a transfer student.</textarea>
                  </div>
                </div>
                <br>
                <div class="align-center">
                  <input type="button" class="btn btn-primary" id="button_submit" value="Save Changes">
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </li>
  </ol>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
