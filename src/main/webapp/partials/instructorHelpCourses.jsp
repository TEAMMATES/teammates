<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="courses">Courses</h2>
<div id="contentHolder">
  <h3>Enrolling Students in Courses</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="course-add-students">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-add-students-body">
        <h3 class="panel-title">How do I add students to a course?</h3>
      </div>
      <div id="course-add-students-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            There are two ways to enroll students in a course:
          </p>
          <ul>
            <li>
              From the <b>Home</b> page, click on the <button class="btn btn-primary btn-xs">Students</button> button of the corresponding course, and choose <b>Enroll</b>.
            </li>
            <li>
              From the <b>Courses</b> page, click on the <button class="btn btn-default btn-xs">Enroll</button> button of the corresponding course.
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-size">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-size-body">
        <h3 class="panel-title">Is there a size limit for a course?</h3>
      </div>
      <div id="course-size-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            No. However, if courses with an enrollment of more than 100 students need to be <a class="collapse-link" data-target="#course-enroll-sections-body" href="#course-enroll-sections">divided into sections</a>.<br>
            TEAMMATES uses section information to organize the results of your sessions into a downloadable report.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-teams">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-teams-body">
        <h3 class="panel-title">What should I do if my course doesn’t have teams?</h3>
      </div>
      <div id="course-teams-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            When you enroll students in a course, TEAMMATES requires you to specify a <b>Team</b> for each student.<br>
            If your class does not use groups or teams, simply provide a dummy value in the Team column.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-sections">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-sections-body">
        <h3 class="panel-title">What are sections meant for?</h3>
      </div>
      <div id="course-sections-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            Sections are used to organize students in courses with significantly large numbers of students.
            It is mandatory for courses with more than 100 students to organize students into sections.
            Courses with less than 100 students do not need to be split into sections.
          </p>
          <p>
            Section information is used to paginate the downloadable report of a session's results.
            When you download the results of a session as an Excel spreadsheet, each section will be given its own page in the file.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-enroll-sections">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-enroll-sections-body">
        <h3 class="panel-title">How do I enroll students into sections?</h3>
      </div>
      <div id="course-enroll-sections-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To specify a section for each student at the time of enrollment, include a <b>Section</b> column in the spreadsheet and ensure it is copied over to the student data text box, together with the rest of the data.
            To view more information, go to the <b>Courses</b> page, click on the <button class="btn btn-default btn-xs" type="button">Enroll</button> button for any course and scroll down to the <b>More Info</b> section.
          </p>
        </div>
      </div>
    </div>
  </div>
  <h3>Adding Instructors to Courses</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="course-add-instructor">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-add-instructor-body">
        <h3 class="panel-title">How do I add instructors to my course?</h3>
      </div>
      <div id="course-add-instructor-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            From your <b>Home</b> or <b>Courses</b> page, click the <button class="btn btn-default btn-xs" type="button">Edit</button> button of the course you would like to edit. You will be directed to the <b>Edit Course</b> page, which will look similar to the example below.<br>
            Here, you can add new instructors to the course, edit existing instructors' details, and delete instructors from the course, depending on your access privileges.
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <strong>Instructor 3:</strong>
                <div class="pull-right">

                  <a href="javascript:;" id="instrEditLink3" class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" title="Edit instructor details" disabled="">
                    <span class="glyphicon glyphicon-pencil"></span> Edit
                  </a>
                  <a href="javascript:;" id="instrDeleteLink3" class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" title="Delete the instructor from the course" disabled="">
                    <span class="glyphicon glyphicon-trash"></span> Delete
                  </a>
                </div>
              </div>

              <div class="panel-body">
                <form method="post" action="#" id="formEditInstructor3" name="formEditInstructors" class="form form-horizontal">
                  <input type="hidden" name="courseid" value="testCourse">

                  <input type="hidden" name="instructorid" value="sampleInstr">

                  <input type="hidden" name="user" value="sampleInstr">

                  <div id="instructorTable3">

                    <div class="form-group">
                      <label class="col-sm-3 control-label">Google ID:</label>
                      <div class="col-sm-9">
                        <input class="form-control immutable" type="text" id="instructorid3" value="sampleInstr" maxlength="45" tabindex="3" disabled="">
                      </div>
                    </div>

                    <div class="form-group">
                      <label class="col-sm-3 control-label">Name:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructorname" id="instructorname3" value="sampleInstr" data-toggle="tooltip" data-placement="top" maxlength="100" tabindex="4" disabled="" title="Enter the name of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Email:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructoremail" id="instructoremail3" value="sampleInstr@google.com" data-toggle="tooltip" data-placement="top" maxlength="45" tabindex="5" disabled="" title="Enter the Email of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">
                        <input type="checkbox" name="instructorisdisplayed" value="true" data-toggle="tooltip" data-placement="top" disabled="" title="If this is unselected, the instructor will be completely invisible to students. E.g. to give access to a colleague for ‘auditing’ your course"> Display to students as:
                      </label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructordisplayname" placeholder="E.g.Co-lecturer, Teaching Assistant" value="Instructor" data-toggle="tooltip" data-placement="top" disabled="" title="Specify the role of this instructor in this course as shown to the students">
                      </div>
                    </div>
                    <div id="accessControlInfoForInstr3">
                      <div class="form-group">
                        <label class="col-sm-3 control-label">Access Level:</label>
                        <div class="col-sm-9">
                          <p class="form-control-static">
                            <span>Co-owner</span>

                            <a href="javascript:;">
                              &nbsp;View Details
                            </a>

                          </p>
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="align-center">
                        <input id="btnSaveInstructor3" type="button" class="btn btn-primary" style="display:none;" value="Save changes" tabindex="6">
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
            <br>
            <br>
            <div class="align-center">
              <input id="btnShowNewInstructorForm" class="btn btn-primary" value="Add New Instructor" disabled="">
            </div>
          </div>
          <p>
            To add an instructor:
          </p>
          <ol>
            <li>
              Click the <button class="btn btn-primary btn-s" type="button">Add New Instructor</button> button at the bottom of the page. A form will appear for you to specify the necessary information about the new instructor.
            </li>
            <li>
              Fill in the name, email, role, and access level of the instructor you want to add. If you are not clear about certain input field, hover your cursor over the input field to view the tooltip for explanation of the field.<br>
            </li>
            <li>
              Click <button class="btn btn-primary btn-s" type="button">Add Instructor</button> to add the instructor.
            </li>
          </ol>
          <div class="bs-example">
            <div class="panel panel-primary" id="panelAddInstructor" style="">
              <div class="panel-heading">
                <strong>Instructor 2:</strong>
              </div>

              <div class="panel-body fill-plain">
                <form class="form form-horizontal">
                  <input type="hidden" name="courseid" value="testCourse2">
                  <input type="hidden" name="user" value="sampleInstr">

                  <div id="instructorAddTable">
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Name:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructorname" id="instructorname" data-toggle="tooltip" data-placement="top" maxlength="100" tabindex="8/" title="Enter the name of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Email:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructoremail" id="instructoremail" data-toggle="tooltip" data-placement="top" maxlength="45" tabindex="9/" title="Enter the Email of the instructor.">
                      </div>
                    </div>
                    <div id="accessControlEditDivForInstr2">
                      <div class="form-group">
                        <label class="col-sm-3 control-label">
                          <input type="checkbox" name="instructorisdisplayed" value="true" data-toggle="tooltip" data-placement="top" title="If this is unselected, the instructor will be completely invisible to students. E.g. to give access to a colleague for ‘auditing’ your course"> Display to students as:
                        </label>
                        <div class="col-sm-9">
                          <input class="form-control" type="text" name="instructordisplayname" placeholder="E.g.Co-lecturer, Teaching Assistant" data-toggle="tooltip" data-placement="top" title="Specify the role of this instructor in this course as shown to the students">
                        </div>
                      </div>
                      <div class="form-group">
                        <div class="col-sm-3">
                          <label class="control-label pull-right">Access-level</label>
                        </div>
                        <div class="col-sm-9">
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Co-owner" checked="">&nbsp;Co-owner: Can do everything
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Manager">&nbsp;Manager: Can do everything except for deleting the course
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Observer">&nbsp;Observer: Can only view information(students, submissions, comments etc.). &nbsp;Cannot edit/delete/submit anything.
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Tutor">&nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor2" value="Custom">&nbsp;Custom: No access by default. Any access needs to be granted explicitly.
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="align-center">
                        <input id="btnAddInstructor" type="button" class="btn btn-primary" value="Add Instructor" tabindex="10">
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-edit-instructor">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-edit-instructor-body">
        <h3 class="panel-title">How do I edit the information of an instructor in my course?</h3>
      </div>
      <div id="course-edit-instructor-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To edit the name, email and visible role of Instructor A from Course B, first go to the <b>Edit Course</b> page of Course B.<br>
            From your <b>Home</b> or <b>Courses</b> page, click the <button class="btn btn-default btn-xs" type="button">Edit</button> button of the course you would like to edit. You will be directed to the <b>Edit Course</b> page.<br>
          </p>
          <p>
            To edit Instructor A's information:
          </p>
          <ol>
            <li>
              Scroll to the panel showing Instructor A's information.<br>
            </li>
            <li>
              Click the <button class="btn btn-primary btn-xs" type="button"><span class="glyphicon glyphicon-pencil"></span> Edit</button> button in the top right hand corner of Instructor A's information panel. <br>
            </li>
            <li>
              Update the relevant fields with Instructor A's new information.
            </li>
            <li>
              Click <button class="btn btn-primary btn-s" type="button">Save changes</button> to save the changes and complete the edit process.
            </li>
          </ol>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-instructor-access">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-instructor-access-body">
        <h3 class="panel-title">How do I set an instructor's access level?</h3>
      </div>
      <div id="course-instructor-access-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            When <a class="collapse-link" data-target="#course-add-instructor-body" href="#course-add-instructor">adding an instructor</a> or <a class="collapse-link" data-target="#course-edit-instructor-body" href="#course-edit-instructor"> editing an instructor's information</a>, you can set the instructor's access level.
            There are 4 pre-defined privilege options for you to choose from:
          </p>
          <ul>
            <li>
              <b>Co-owner</b>: is able to perform any action on the course, including deleting the course. When you create a new course, your access level is Co-owner. This is the highest access level.
            </li>
            <li>
              <b>Manager</b>: has the same privileges as a Co-owner except that s/he is not allowed to delete the course.
            </li>
            <li>
              <b>Observer</b>: can view the course's information, but cannot edit or submit information
            </li>
            <li>
              <b>Tutor</b>: can view student details, give/view comments, and submit/view responses for sessions.
            </li>
          </ul>
          <p>
            All the access levels listed above have pre-defined privileges which you can view by clicking the 'View details' link next to the access level description.<br>
            To set custom privileges for an instructor, select the <b>Custom</b> access level, and specify which privileges the instructor should get.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-custom-instructor-access">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-custom-instructor-access-body">
        <h3 class="panel-title">How do I set custom privileges for an instructor?</h3>
      </div>
      <div id="course-custom-instructor-access-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            When you set the access level for an instructor to <b>Custom</b>, detailed privilege settings will appear.<br>
            Use the checkboxes to give instructors specific privileges.
          </p>
          <p>
            If a course has sections, you can customize an instructor's permissions for each section by clicking 'Give different permissions for a specific section'.<br>
            In the panel for section-level privilege settings, use the section checkboxes to apply your chosen settings to more than one section.<br>
            You can also change the degree of access the instructor has to specific sessions in that section by clicking 'Give different permissions for sessions in this section'.
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <strong>Instructor 1:</strong>
                <div class="pull-right">

                  <a href="javascript:;" id="instrEditLink1" class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" title="Edit instructor details" style="display: none;">
                    <span class="glyphicon glyphicon-pencil"></span> Edit
                  </a>
                  <a href="javascript:;" id="instrDeleteLink1" class="btn btn-primary btn-xs" data-toggle="tooltip" data-placement="top" title="Delete the instructor from the course">
                    <span class="glyphicon glyphicon-trash"></span> Delete
                  </a>
                </div>
              </div>

              <div class="panel-body">
                <form class="form form-horizontal">
                  <input type="hidden" name="courseid" value="testCourse2">

                  <input type="hidden" name="instructorid" value="sampleInstr">

                  <input type="hidden" name="user" value="sampleInstr">

                  <div id="instructorTable1">

                    <div class="form-group">
                      <label class="col-sm-3 control-label">Google ID:</label>
                      <div class="col-sm-9">
                        <input class="form-control immutable" type="text" id="instructorid1" value="sampleInstr" maxlength="45" tabindex="3" disabled="">
                      </div>
                    </div>

                    <div class="form-group">
                      <label class="col-sm-3 control-label">Name:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructorname" id="instructorname1" value="sampleInstr" data-toggle="tooltip" data-placement="top" maxlength="100" tabindex="4" title="Enter the name of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">Email:</label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructoremail" id="instructoremail1" value="sampleInstr@google.com" data-toggle="tooltip" data-placement="top" maxlength="45" tabindex="5" title="Enter the Email of the instructor.">
                      </div>
                    </div>
                    <div class="form-group">
                      <label class="col-sm-3 control-label">
                        <input type="checkbox" name="instructorisdisplayed" value="true" data-toggle="tooltip" data-placement="top" title="If this is unselected, the instructor will be completely invisible to students. E.g. to give access to a colleague for ‘auditing’ your course"> Display to students as:
                      </label>
                      <div class="col-sm-9">
                        <input class="form-control" type="text" name="instructordisplayname" placeholder="E.g.Co-lecturer, Teaching Assistant" value="Instructor" data-toggle="tooltip" data-placement="top" title="Specify the role of this instructor in this course as shown to the students">
                      </div>
                    </div>
                    <div id="accessControlInfoForInstr1" style="display: none;">
                      <div class="form-group">
                        <label class="col-sm-3 control-label">Access Level:</label>
                        <div class="col-sm-9">
                          <p class="form-control-static">
                            <span>Co-owner</span>

                            <a href="javascript:;">
                              &nbsp;View Details
                            </a>

                          </p>
                        </div>
                      </div>
                    </div>

                    <div id="accessControlEditDivForInstr1" style="">
                      <div class="form-group">
                        <div class="col-sm-3">
                          <label class="control-label pull-right">Access-level</label>
                        </div>
                        <div class="col-sm-9">
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor1" value="Co-owner"> &nbsp;Co-owner: Can do everything &nbsp;
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor1" value="Manager"> &nbsp;Manager: Can do everything except for deleting the course &nbsp;
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor1" value="Observer"> &nbsp;Observer: Can only view information(students, submissions, comments etc.). &nbsp;Cannot edit/delete/submit anything. &nbsp;
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor1" value="Tutor"> &nbsp;Tutor: Can view student details, give/view comments, submit/view responses for sessions &nbsp;
                          <a href="javascript:;">
                            View Details
                          </a>
                          <br>
                          <input type="radio" name="instructorrole" id="instructorroleforinstructor1" value="Custom" checked=""> &nbsp;Custom: No access by default. Any access needs to be granted explicitly.
                          <br>
                        </div>
                      </div>
                      <div id="tunePermissionsDivForInstructor1" style="">
                        <div class="form-group">
                          <div class="col-xs-12">
                            <div class="panel panel-info">
                              <div class="panel-heading">
                                <strong>In general, this instructor can</strong>
                              </div>
                              <div class="panel-body">
                                <div class="col-sm-3">
                                  <input type="checkbox" name="canmodifycourse" value="true"> Edit/Delete Course
                                </div>
                                <div class="col-sm-3">
                                  <input type="checkbox" name="canmodifyinstructor" value="true"> Add/Edit/Delete Instructors
                                </div>
                                <div class="col-sm-3">
                                  <input type="checkbox" name="canmodifysession" value="true"> Create/Edit/Delete Sessions
                                </div>
                                <div class="col-sm-3">
                                  <input type="checkbox" name="canmodifystudent" value="true"> Enroll/Edit/Delete Students
                                </div>
                                <br>
                                <br>
                                <div class="col-sm-6 border-right-gray">
                                  <input type="checkbox" name="canviewstudentinsection" value="true"> View Students' Details
                                  <br>
                                  <input type="checkbox" name="cangivecommentinsection" value="true"> Give Comments for Students
                                  <br>
                                  <input type="checkbox" name="canviewcommentinsection" value="true"> View Others' Comments on Students
                                  <br>
                                  <input type="checkbox" name="canmodifycommentinsection" value="true"> Edit/Delete Others' Comments on Students
                                  <br>
                                </div>
                                <div class="col-sm-5 col-sm-offset-1">
                                  <input type="checkbox" name="cansubmitsessioninsection" value="true"> Sessions: Submit Responses and Add Comments
                                  <br>
                                  <input type="checkbox" name="canviewsessioninsection" value="true"> Sessions: View Responses and Comments
                                  <br>
                                  <input type="checkbox" name="canmodifysessioncommentinsection" value="true"> Sessions: Edit/Delete Responses/Comments by Others
                                  <br>
                                </div>
                              </div>
                            </div>

                            <div id="tuneSectionPermissionsDiv0ForInstructor1" style="">
                              <div class="panel panel-info">
                                <div class="panel-heading">
                                  <div class="row">
                                    <div class="col-sm-2">
                                      <p>
                                        <strong>But in section(s)</strong>
                                      </p>
                                    </div>
                                    <div class="col-sm-9">
                                      <div class="col-sm-12">

                                        <div class="col-sm-4">
                                          <input type="checkbox" name="sectiongroup0section0" value="section 1" checked=""> section 1
                                        </div>
                                        <div class="col-sm-4">
                                          <input type="checkbox" name="sectiongroup0section1" value="section 2"> section 2
                                        </div>
                                      </div>

                                    </div>
                                    <div class="col-sm-1">
                                      <a href="javascript:;" class="pull-right">
                                        <span class="glyphicon glyphicon-trash"></span>
                                      </a>
                                    </div>
                                  </div>
                                  <br>
                                  <div class="row">
                                    <div class="col-sm-12">
                                      <p>
                                        <strong> the instructor can only,</strong>
                                      </p>
                                    </div>
                                  </div>

                                  <input type="hidden" name="issectiongroup0set" value="true">
                                </div>
                                <div class="panel-body">
                                  <br>
                                  <div class="col-sm-6 border-right-gray">
                                    <input type="checkbox" name="canviewstudentinsectionsectiongroup0" value="true"> View Students' Details
                                    <br>
                                    <input type="checkbox" name="cangivecommentinsectionsectiongroup0" value="true"> Give Comments for Students
                                    <br>
                                    <input type="checkbox" name="canviewcommentinsectionsectiongroup0" value="true"> View Others' Comments on Students
                                    <br>
                                    <input type="checkbox" name="canmodifycommentinsectionsectiongroup0" value="true"> Edit/Delete Others' Comments on Students
                                    <br>
                                    <br>
                                  </div>
                                  <div class="col-sm-5 col-sm-offset-1">
                                    <input type="checkbox" name="cansubmitsessioninsectionsectiongroup0" value="true"> Sessions: Submit Responses and Add Comments
                                    <br>
                                    <input type="checkbox" name="canviewsessioninsectionsectiongroup0" value="true"> Sessions: View Responses and Comments
                                    <br>
                                    <input type="checkbox" name="canmodifysessioncommentinsectionsectiongroup0" value="true"> Sessions: Edit/Delete Responses/Comments by Others
                                    <br>
                                    <br>
                                  </div>
                                  <a href="javascript:;" id="toggleSessionLevelInSection0ForInstructor1" class="small col-sm-5">Give different permissions for sessions in this section</a>
                                  <div id="tuneSessionPermissionsDiv0ForInstructor1" class="row" style="display: none;">

                                    <input type="hidden" name="issectiongroup0sessionsset" value="false">

                                    <table class="table table-striped">
                                      <thead>
                                      <tr>
                                        <td>SessionName</td>
                                        <td>Submit Responses and Add Comments</td>
                                        <td>View Responses and Comments</td>
                                        <td>Edit/Delete Responses/Comments by Others</td>
                                      </tr>
                                      </thead>
                                      <tbody>

                                      <tr>
                                        <td colspan="4" class="text-center text-bold">No sessions in this course for you to configure</td>
                                      </tr>
                                      </tbody>
                                    </table>
                                  </div>
                                </div>
                              </div>
                            </div>

                            <a href="javascript:;" class="small" id="addSectionLevelForInstructor1">Give different permissions for a specific section</a>

                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="form-group">
                      <div class="align-center">
                        <input id="btnSaveInstructor1" type="button" class="btn btn-primary" style="" value="Save changes" tabindex="6">
                      </div>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <h3>Managing Courses</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="course-view-students">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-view-students-body">
        <h3 class="panel-title">How do I view a list of students in a course?</h3>
      </div>
      <div id="course-view-students-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            There are two ways to access the list of students in a course.
          </p>
          <p>
            To view the list of students in Course A:
          </p>
          <ul>
            <li>
              From the <b>Home</b> page, find the panel corresponding to Course A. On the right hand side, click <button class="btn btn-primary btn-xs">Students <span class="caret dropdown-toggle"></span></button>
              and then select <b>View/Edit</b>.
            </li>
            <li>
              From the <b>Courses</b> page, click <button class="btn btn-default btn-xs">View</button> button of the row corresponding to Course A.
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-change-student-section">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-change-student-section-body">
        <h3 class="panel-title">How do I change a student's section?</h3>
      </div>
      <div id="course-change-student-section-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To edit the section Student A from Course B is enrolled in:
          </p>
          <ol>
            <li>
              Go to the <b>Students</b> page and click Course B's panel heading. You will see a list of students enrolled in Course B.
            </li>
            <li>
              Click the <button class="btn btn-default btn-xs">Edit</button> button in the last column of the row corresponding to Student A.
            </li>
            <li>
              A new page will open that allows you to <a class="collapse-link" data-target="#student-edit-details-body" href="#student-edit-details">edit the student's profile</a>, including a field to edit the student's section.<br>
            </li>
            <li>
              After editing the section name, click <button class="btn btn-primary btn-s">Save Changes</button> to confirm Student A's new section.
            </li>
          </ol>
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-disappear">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-disappear-body">
        <h3 class="panel-title">What should I do if a student says his/her courses have disappeared from the system?</h3>
      </div>
      <div id="course-disappear-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            The most likely reason for this is that the student has changed the primary email address associated with his/her Google ID. Please ask the student to email
            <a href="mailto:teammates@comp.nus.edu.sg">teammates@comp.nus.edu.sg</a> so that we help to rectify the problem.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-delete-students">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-delete-students-body">
        <h3 class="panel-title">How do I delete students from a course?</h3>
      </div>
      <div id="course-delete-students-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To remove Student A from Course B:
          </p>
          <ol>
            <li>
              <a class="collapse-link" data-target="#course-view-students-body" href="#course-view-students">View the student list</a> of Course B.
            </li>
            <li>
              In the row corresponding to Student A, click the <button class="btn btn-default btn-xs">Delete</button> button.
            </li>
            <li>
              Click <b>OK</b> to confirm that you would like to delete Student A from Course B.
            </li>
          </ol>
        </div>
      </div>
    </div>
  </div>
  <h3>Archiving Courses</h3>
  <div class="panel-group">
    <div class="panel panel-default" id="course-archive">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-archive-body">
        <h3 class="panel-title">How do I archive a course?</h3>
      </div>
      <div id="course-archive-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            When a course has ended, you can archive it so that it doesn't appear in your home page. Course, student and session details of an archived course are still stored on TEAMMATES. However, you cannot edit, create feedback sessions for or enroll students in an archived course.
          </p>
          <p>
            In your <b>Home</b> page, you will see panels for each course and a table of feedback sessions inside it, similar to the example below.<br>
            Click on the <button class="btn btn-primary btn-xs" type="button">Course <span class="caret"></span></button> button on the card heading of the course you want to archive.<br>
            Then select <button class="btn btn-default btn-xs" type="button">Archive</button> in the drop-down menu and click <b>OK</b> to confirm.
          </p>
          <p>
            You can also archive a course from the <b>Courses</b> page.<br>
            Under 'Active Courses', click on the <button class="btn btn-default btn-xs" type="button">Archive</button> button in the row corresponding to the course you want to archive.
          </p>
          <div class="bs-example">
            <div class="panel panel-primary">
              <div class="panel-heading">
                <div class="row">
                  <div class="col-sm-6">
                    <strong>
                      [AI532] : Artificial Intelligence
                    </strong>
                  </div>
                  <div class="mobile-margin-top-10px col-sm-6">
                    <span class="mobile-no-pull pull-right">
                      <div class="dropdown courses-table-dropdown">
                        <button class="btn btn-primary btn-xs" type="button">
                          Students
                          <span class="caret"></span>
                        </button>
                      </div>
                      <div class="dropdown courses-table-dropdown">
                        <button class="btn btn-primary btn-xs" type="button">
                          Instructors
                          <span class="caret"></span>
                        </button>
                      </div>
                      <div class="dropdown courses-table-dropdown">
                        <button class="btn btn-primary btn-xs" type="button">
                          Sessions
                          <span class="caret"></span>
                        </button>
                      </div>
                      <div class="dropdown courses-table-dropdown">
                        <button class="btn btn-primary btn-xs" type="button">
                          Course
                          <span class="caret"></span>
                        </button>
                      </div>
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-view-archived">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-view-archived-body">
        <h3 class="panel-title">How do I view courses I have archived?</h3>
      </div>
      <div id="course-view-archived-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            You can view all your archived courses by navigating to the <b>Courses</b> page.<br>
            Scroll to the <b>Archived Courses</b> heading, which looks similar to this:
          </p>
          <div class="bs-example">
            <h2 class="text-muted">Archived courses</h2>
            <table class="table table-bordered table-striped">
              <thead>
              <tr class="fill-default">
                <th onclick="toggleSort(this);" class="button-sort-none">
                  Course ID<span class="icon-sort unsorted"></span>
                </th>
                <th onclick="toggleSort(this);" class="button-sort-none">
                  Course Name<span class="icon-sort unsorted"></span>
                </th>
                <th class="align-center no-print">Action(s)</th>
              </tr>
              </thead>
              <tr>
                <td>AI532</td>
                <td>Artificial Intelligence</td>
                <td class="align-center no-print">
                  <button href="#" class="btn btn-default btn-xs" type="button">Unarchive</button>
                  <button href="#" class="btn btn-default btn-xs" type="button">Delete</button>
                </td>
              </tr>
            </table>
          </div>
          <p>
            The courses you have previously archived are listed here.
            In order to access information in an archived course, <a class="collapse-link" data-target="#course-unarchive-body" href="#course-unarchive">unarchive the course</a>.
          </p>
        </div>
      </div>
    </div>
    <div class="panel panel-default" id="course-unarchive">
      <div class="panel-heading cursor-pointer" data-toggle="collapse" data-target="#course-unarchive-body">
        <h3 class="panel-title">How do I unarchive an archived course?</h3>
      </div>
      <div id="course-unarchive-body" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To unarchive a course, first <a class="collapse-link" data-target="#course-view-archived-body" href="#course-view-archived">view the course</a> that you would like to unarchive in the <b>Courses</b> page.<br>
            Then, click on the <button href="#" class="btn btn-default btn-xs" type="button">Unarchive</button> button corresponding to the course you want to unarchive.
          </p>
          <div class="bs-example">
            <h2 class="text-muted">Archived courses</h2>
            <table class="table table-bordered table-striped">
              <thead>
              <tr class="fill-default">
                <th onclick="toggleSort(this);" class="button-sort-none">
                  Course ID<span class="icon-sort unsorted"></span>
                </th>
                <th onclick="toggleSort(this);" class="button-sort-none">
                  Course Name<span class="icon-sort unsorted"></span>
                </th>
                <th class="align-center no-print">Action(s)</th>
              </tr>
              </thead>
              <tr>
                <td>AI532</td>
                <td>Artificial Intelligence</td>
                <td class="align-center no-print">
                  <button href="#" class="btn btn-default btn-xs" type="button">Unarchive</button>
                  <button href="#" class="btn btn-default btn-xs" type="button">Delete</button>
                </td>
              </tr>
            </table>
          </div>
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
