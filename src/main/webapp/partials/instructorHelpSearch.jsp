<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="search">Search</a>
</h4>
<div id="contentHolder">
  <br>
  <ol style="list-style-type: none;">
    <li id="searchStudents">
      <span class="text-bold">
          <b>1. Searching for students</b>
      </span>
      <div>
        You can search for students by clicking on
        <b>'Search'</b> on the top nav bar. You should see a search bar similar to the one below:
        <br><br>
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
                  <input id="comments-for-student-check" type="checkbox">
                  <label for="comments-for-student-check">
                    Comments for students
                  </label>
                </li>
                <li>
                  <input id="comments-for-responses-check" type="checkbox">
                  <label for="comments-for-responses-check">
                    Comments for responses
                  </label>
                </li>
                <li>
                  <input id="students-check" type="checkbox" checked>
                  <label for="students-check">
                    Students
                  </label>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <br>Check the option <b>'Students'</b> below the search box. Now type the student's name into the search box and click on the <b>'Search'</b> button.
        <br>If you search for 'alice', the search results would show something like this (assuming such a student exists):
        <br><br>
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
        <br>You can search for a student record based on:
        <ul>
          <li>Section name</li>
          <li>Team name</li>
          <li>Student name</li>
          <li>Email</li>
        </ul>
        <br>You can search for multiple students based on the attributes mentioned above. To do so, include the values you wish to search for in the search box, separated by spaces.
        <br>For example, if you search for 'alice Section A Team B jack@email.com', the search would result in something similar to this (assuming the corresponding data exists):
        <br><br>
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
    </li>
    <br>
    <li id="searchCommentForResponses">
      <span class="text-bold">
          <b>2. Searching for comments on responses</b>
      </span>
      <div>
        You can search for comments on responses given by students. To do this check
        <b>'Comments for responses'</b> below the search box, type your keywords, then hit <b>'Search'</b>.
        <br>Suppose you search for 'good'. Assuming the relevant data exists, the search results would look something similar to this:
        <br><br>
        <div class="bs-example">
          <div class="panel panel-primary">
            <div class="panel-heading">
              <strong>Comments for responses</strong>
            </div>
            <div class="panel-body">
              <div class="row">
                <div class="col-md-2">
                  <strong>
                    Session: Session 1 (Course 1)
                  </strong>
                </div>
                <div class="col-md-10">
                  <div class="panel panel-info">
                    <div class="panel-heading">
                      <b>Question 2</b>: What has been a highlight for you working on this project?
                    </div>
                    <table class="table">
                      <tbody>
                        <tr>
                          <td>
                            <b>From:</b> Alice Betsy (Team A)
                            <b>To:</b> Alice Betsy (Team A)
                          </td>
                        </tr>
                        <tr>
                          <td>
                            <strong>Response:</strong> A highlight for me has been putting Software Engineering skills to use.
                          </td>
                        </tr>
                        <tr class="active">
                          <td>Comment(s):</td>
                        </tr>
                        <tr>
                          <td>
                            <ul class="list-group comments">
                              <li class="list-group-item list-group-item-warning">
                                <div>
                                  <span class="text-muted">
                                    From: instructor@university.edu [Tue, 23 May 2017, 11:59 PM UTC]
                                  </span>
                                </div>
                                <div style="margin-left: 15px;">Alice, <span class="highlight">good</span> to know that you liked applying software engineering skills in the project.</div>
                              </li>
                            </ul>
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
    </li>
  </ol>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
