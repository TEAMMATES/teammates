<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="students">Students</h2>
<div id="contentHolder">
  <h3>Student Records</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="student-view-profile">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-view-profile-body">
        <h3 class="panel-title">How do I view a student's profile?</h3>
      </div>
      <div id="student-view-profile-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To view the profile of Student A from Course B:
          </p>
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
            You can press the <span class="text-muted glyphicon glyphicon-resize-full"></span> button in the top-right corner to display the information in a lightbox for better readability.
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
                    <p>
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
      </div>
    </div>
    <div class="panel panel-default" id="student-edit-details">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-edit-details-body">
        <h3 class="panel-title">How do I edit a student's details after enrolling the student?</h3>
      </div>
      <div id="student-edit-details-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To edit the name, section, team, contact email, or instructor comments of Student A from Course B:
          </p>
          <ol>
            <li>
              Go to the <b>Students</b> page and click the panel heading for Course B. You will see a list of students enrolled in Course B.
            </li>
            <li>
              Click the <button class="btn btn-default btn-xs">Edit</button> button in the last column of the row corresponding to Student A.<br>
            </li>
            <li>
              In the new page that opens, edit the relevant fields of Student A's details. The page will look similar to the example below.
            </li>
            <li>
              Click <button class="btn btn-primary btn-s">Save Changes</button> to save your changes to Student A's details.
            </li>
          </ol>
          <p>
            Note that moving a student to a different team (i.e. changing the student's Team ID) will change the student's team in all existing sessions in the course.
          </p>
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
      </div>
    </div>
    <div class="panel panel-default" id="student-view-responses">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-view-responses-body">
        <h3 class="panel-title">How do I view all the responses a student has given and received?</h3>
      </div>
      <div id="student-view-responses-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To view the responses that Student A from Course B has given and received:
          </p>
          <ol>
            <li>
              Go to the <b>Students</b> page and click the panel heading for Course B. You will see a list of students enrolled in the course.
            </li>
            <li>
              Click <button class="btn btn-xs btn-default">All Records</button> button corresponding to Student A to access all the responses Student A has given and received.
            </li>
          </ol>
        </div>
      </div>
    </div>
  </div>
  <h3>Finding Students</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="student-search">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-search-body">
        <h3 class="panel-title">How do I search for a student in my course?</h3>
      </div>
      <div id="student-search-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            You can search for students from the <b>Search</b> page. Click the <b>Search</b> tab in the navigation bar at the top of the page. You should see a search bar similar to the one below:
          </p>
          <div class="bs-example">
            <div class="well well-plain">
              <div class="form-group">
                <div class="input-group">
                  <input type="text" name="searchkey"
                         title="Search for comment"
                         placeholder="Your search keyword"
                         class="form-control">
                  <span class="input-group-btn">
                  <button class="btn btn-primary" type="submit"
                          value="Search">
                    Search
                  </button>
                </span>
                </div>
              </div>
              <div class="form-group">
                <ul class="list-inline">
                  <li>
                  <span data-toggle="tooltip" title="Tick the checkboxes to limit your search to certain categories"
                        class="glyphicon glyphicon-info-sign">
                  </span>
                  </li>
                  <li>
                    <input id="students-check" type="checkbox" checked>
                    <label for="students-check">
                      Students
                    </label>
                  </li>
                  <li>
                    <input id="search-feedback-sessions-data-check" type="checkbox">
                    <label for="search-feedback-sessions-data-check">
                      Questions, responses, comments on responses
                    </label>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          <p>
            To search for a student:
          </p>
          <ol>
            <li>
              Tick the option <b>Students</b> below the search box.
            </li>
            <li>
              Type your search terms into the search bar. You can search for a student record based on:
              <ul>
                <li>Section name</li>
                <li>Team name</li>
                <li>Student name</li>
                <li>Email</li>
              </ul>
            </li>
            <li>
              Click the <button class="btn btn-primary btn">Search</button> button.
            </li>
          </ol>
          <p>
            If you search for <code>alice</code>, the search results would show something like this (assuming such a student exists):
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <strong>Students</strong>
              </div>
              <div class="panel-body">
                <div class="panel panel-info">
                  <div class="panel-heading">
                    <strong>Course name appears here</strong>
                  </div>
                  <div class="panel-body padding-0">
                    <table class="table table-bordered table-striped table-responsive margin-0">
                      <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                      <tr>
                        <th>Photo</th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Section <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Team <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Student Name <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Status <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Email <span class="icon-sort unsorted"></span>
                        </th>
                        <th>Action(s)</th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr class="student_row">
                        <td>
                          <div class="profile-pic-icon-click align-center">
                            <a class="student-profile-pic-view-link btn-link">View Photo</a>
                            <img src="" alt="No Image Given" class="hidden">
                          </div>
                        </td>
                        <td>Section A</td>
                        <td>Team A</td>
                        <td><span class="highlight">Alice</span> Betsy</td>
                        <td class="align-center">Joined</td>
                        <td><span class="highlight">alice</span>@email.com</td>
                        <td class="no-print align-center">
                          <a class="btn btn-default btn-xs" title="View details of the student"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">View</a>
                          <a class="btn btn-default btn-xs" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">Edit</a>
                          <a class="course-student-delete-link btn btn-default btn-xs"
                             title="Delete the student and the corresponding submissions from the course" href="" data-toggle="tooltip" data-placement="top">Delete</a>
                          <a class="btn btn-default btn-xs" href="" title="View all data about this student" target="_blank" rel="noopener noreferrer"
                             data-toggle="tooltip" data-placement="top">All Records</a>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <p>
            You can search for multiple students based on the attributes mentioned above. To do so, include the terms you wish to search for in the search box separated by spaces.<br>
            For example, if you search for <code>alice Section A Team B jack@email.com</code>, the search would result in something similar to this (assuming the corresponding data exists):
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <strong>Students</strong>
              </div>
              <div class="panel-body">
                <div class="panel panel-info">
                  <div class="panel-heading">
                    <strong>Course name appears here</strong>
                  </div>
                  <div class="panel-body padding-0">
                    <table class="table table-bordered table-striped table-responsive margin-0">
                      <thead class="background-color-medium-gray text-color-gray font-weight-normal">
                      <tr>
                        <th>Photo</th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Section <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Team <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Student Name <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Status <span class="icon-sort unsorted"></span>
                        </th>
                        <th class="button-sort-none" onclick="toggleSort(this)">
                          Email <span class="icon-sort unsorted"></span>
                        </th>
                        <th>Action(s)</th>
                      </tr>
                      </thead>
                      <tbody>
                      <tr class="student_row">
                        <td>
                          <div class="profile-pic-icon-click align-center">
                            <a class="student-profile-pic-view-link btn-link">View Photo</a>
                            <img src="" alt="No Image Given" class="hidden">
                          </div>
                        </td>
                        <td><span class="highlight">Section A</span></td>
                        <td><span class="highlight">Team</span> A</td>
                        <td><span class="highlight">Alice</span> Betsy</td>
                        <td class="align-center">Joined</td>
                        <td><span class="highlight">alice</span>@email.com</td>
                        <td class="no-print align-center">
                          <a class="btn btn-default btn-xs" title="View details of the student"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">View</a>
                          <a class="btn btn-default btn-xs" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">Edit</a>
                          <a class="course-student-delete-link btn btn-default btn-xs"
                             title="Delete the student and the corresponding submissions from the course" href="" data-toggle="tooltip" data-placement="top">Delete</a>
                          <a class="btn btn-default btn-xs" href="" title="View all data about this student" target="_blank" rel="noopener noreferrer"
                             data-toggle="tooltip" data-placement="top">All Records</a>
                        </td>
                      </tr>
                      <tr class="student_row">
                        <td>
                          <div class="profile-pic-icon-click align-center">
                            <a class="student-profile-pic-view-link btn-link">View Photo</a>
                            <img src="" alt="No Image Given" class="hidden">
                          </div>
                        </td>
                        <td><span class="highlight">Section A</span></td>
                        <td><span class="highlight">Team</span> A</td>
                        <td>Jean Grey</td>
                        <td class="align-center">Joined</td>
                        <td>jean@email.com</td>
                        <td class="no-print align-center">
                          <a class="btn btn-default btn-xs" title="View details of the student"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">View</a>
                          <a class="btn btn-default btn-xs" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">Edit</a>
                          <a class="course-student-delete-link btn btn-default btn-xs"
                             title="Delete the student and the corresponding submissions from the course" href="" data-toggle="tooltip" data-placement="top">Delete</a>
                          <a class="btn btn-default btn-xs" href="" title="View all data about this student" target="_blank" rel="noopener noreferrer"
                             data-toggle="tooltip" data-placement="top">All Records</a>
                        </td>
                      </tr>
                      <tr class="student_row">
                        <td>
                          <div class="profile-pic-icon-click align-center">
                            <a class="student-profile-pic-view-link btn-link">View Photo</a>
                            <img src="" alt="No Image Given" class="hidden">
                          </div>
                        </td>
                        <td><span class="highlight">Section</span> B</td>
                        <td><span class="highlight">Team B</span></td>
                        <td>Oliver Gates</td>
                        <td class="align-center">Joined</td>
                        <td>oliver@email.com</td>
                        <td class="no-print align-center">
                          <a class="btn btn-default btn-xs" title="View details of the student"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">View</a>
                          <a class="btn btn-default btn-xs" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">Edit</a>
                          <a class="course-student-delete-link btn btn-default btn-xs"
                             title="Delete the student and the corresponding submissions from the course" href="" data-toggle="tooltip" data-placement="top">Delete</a>
                          <a class="btn btn-default btn-xs" href="" title="View all data about this student" target="_blank" rel="noopener noreferrer"
                             data-toggle="tooltip" data-placement="top">All Records</a>
                        </td>
                      </tr>
                      <tr class="student_row">
                        <td>
                          <div class="profile-pic-icon-click align-center">
                            <a class="student-profile-pic-view-link btn-link">View Photo</a>
                            <img src="" alt="No Image Given" class="hidden">
                          </div>
                        </td>
                        <td><span class="highlight">Section</span> B</td>
                        <td><span class="highlight">Team B</span></td>
                        <td>Thora Parker</td>
                        <td class="align-center">Joined</td>
                        <td>thora@email.com</td>
                        <td class="no-print align-center">
                          <a class="btn btn-default btn-xs" title="View details of the student"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">View</a>
                          <a class="btn btn-default btn-xs" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">Edit</a>
                          <a class="course-student-delete-link btn btn-default btn-xs"
                             title="Delete the student and the corresponding submissions from the course" href="" data-toggle="tooltip" data-placement="top">Delete</a>
                          <a class="btn btn-default btn-xs" href="" title="View all data about this student" target="_blank" rel="noopener noreferrer"
                             data-toggle="tooltip" data-placement="top">All Records</a>
                        </td>
                      </tr>
                      <tr class="student_row">
                        <td>
                          <div class="profile-pic-icon-click align-center">
                            <a class="student-profile-pic-view-link btn-link">View Photo</a>
                            <img src="" alt="No Image Given" class="hidden">
                          </div>
                        </td>
                        <td><span class="highlight">Section</span> C</td>
                        <td><span class="highlight">Team</span> C</td>
                        <td>Jack Wayne</td>
                        <td class="align-center">Joined</td>
                        <td><span class="highlight">jack@email.com</span></td>
                        <td class="no-print align-center">
                          <a class="btn btn-default btn-xs" title="View details of the student"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">View</a>
                          <a class="btn btn-default btn-xs" title="Use this to edit the details of this student. <br>To edit multiple students in one go, you can use the enroll page: <br>Simply enroll students using the updated data and existing data will be updated accordingly"
                             href="" target="_blank" rel="noopener noreferrer" data-toggle="tooltip" data-placement="top">Edit</a>
                          <a class="course-student-delete-link btn btn-default btn-xs"
                             title="Delete the student and the corresponding submissions from the course" href="" data-toggle="tooltip" data-placement="top">Delete</a>
                          <a class="btn btn-default btn-xs" href="" title="View all data about this student" target="_blank" rel="noopener noreferrer"
                             data-toggle="tooltip" data-placement="top">All Records</a>
                        </td>
                      </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="student-email">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-email-body">
        <h3 class="panel-title">How do I email a student or students in my course?</h3>
      </div>
      <div id="student-email-body" class="panel-collapse collapse">
        <div class="panel-body">
          <ol>
            <li>
              On the <b>Students</b> page, filter out the selected student/students.
            </li>
            <li>
              Click <button class="btn btn-xs btn-default">Copy Emails</button> to copy their email addresses to your clipboard
            </li>
            <li>
              Use your preferred email provider to email the students
            </li>
          </ol>
        </div>
      </div>
    </div>
  </div>
  <h3>Student Accounts</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="student-google-account">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-google-account-body">
        <h3 class="panel-title">Is it compulsory for students to use Google accounts?</h3>
      </div>
      <div id="student-google-account-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Students can submit feedback and view results without having to login to TEAMMATES, unless they choose to link their Google account (optional).
            TEAMMATES will send students a unique URL to access their feedback sessions and results.
            However, students who link their TEAMMATES account with their Google account will be able to access a dashboard of all their sessions and results through the TEAMMATES website.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="student-change-google-account">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#student-change-google-account-body">
        <h3 class="panel-title">How do I change the Google ID associated with a student?</h3>
      </div>
      <div id="student-change-google-account-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            At the moment, there is no way for students to update their own Google IDs.<br>
            Please ask the student to <a href="mailto:teammates@comp.nus.edu.sg">contact us</a> for assistance changing his/her Google ID.
          </p>
        </div>
      </div>
    </div>
  </div>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
