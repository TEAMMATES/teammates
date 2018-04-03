<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<h2 class="text-color-primary" id="editCourse">Courses</h2>
<div id="contentHolder">
  <div class="panel-group">
    <div class="panel panel-primary" id="editCourseAddInstructor">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#editCourseAddInstructorBody">How do I add instructors to my course?</a>
        </h3>
      </div>
      <div id="editCourseAddInstructorBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            From the <b>Courses</b> tab, click the <b>Edit</b> button of the course you would like to edit. You will be directed to the <b>Edit Course</b> page.<br>
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
          <ol>
            <li>
              Click 'Add New Instructor' to add a new instructor. A form will appear for you to specify the necessary information about the new instructor.
            </li>
            <li>
              Fill in the name, email, role, and access level of the instructor you want to add. If you are not clear about certain input field, you can simply hover the input field and see the tooltip for explanation of the field.<br>
            </li>
            <li>
              Click 'Add Instructor' button to add the instructor.
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
    <div class="panel panel-primary" id="editCourseEditInstructor">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#editCourseEditInstructorBody">How do I edit the information of an instructor in my course?</a>
        </h3>
      </div>
      <div id="editCourseEditInstructorBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            To edit the name, email and visible role of Instructor A from Course B, first go to the <b>Edit Course</b> page of Course B.<br>
          </p>
          <ol>
            <li>
              Scroll to the card showing Instructor A's information.<br>
            </li>
            <li>
              Click the 'edit' button in the top right hand corner of Instructor A's information card. <br>
            </li>
            <li>
              Update the relevant fields with Instructor A's new information.
            </li>
            <li>
              Press "Save changes" button to save the changes and complete the edit process.
            </li>
          </ol>
        </div>
      </div>
    </div>
    <div class="panel panel-primary" id="editCourseSetAccessLevel">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#editCourseSetAccessLevelBody">How do I set an instructor's access level?</a>
        </h3>
      </div>
      <div id="editCourseSetAccessLevelBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            When adding an instructor or editing an instructor's information, you can set the instructor's access level. There are 4 pre-defined privilege options for you to choose from:
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
    <div class="panel panel-primary" id="editCourseSetCustom">
      <div class="panel-heading">
        <h3 class="panel-title">
          <a data-toggle="collapse" href="#editCourseSetCustomBody">How do I set custom privileges for an instructor?</a>
        </h3>
      </div>
      <div id="editCourseSetCustomBody" class="panel-collapse collapse">
        <div class="panel-body">
          <p>
            When you set the access level for an instructor to 'Custom', detailed privilege settings will appear.<br>
            Use the checkboxes to give instructors specific privileges.<br>
          </p>
          <p>
            If a course has sections, you can also customize an instructor's permissions for each section by clicking 'Give different permissions for a specific section'.<br>
            In the panel for section-level privilege settings, you can choose more than one section to apply the same set of settings.<br>
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
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
