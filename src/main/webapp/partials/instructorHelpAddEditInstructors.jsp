<%@ page pageEncoding="UTF-8" %>
<h4 class="text-color-primary" id="editCourse">Add/Edit instructors</h4>
<div id="contentHolder">
  <br>
  <ul>
    <li id="editCourseAddInstructor">
      <b>How do I add instructors to my course?</b>
      <div class="helpSectionContent">
        After pressing 'edit' link of the course, you can go to course edit page, where you can add new instructors and edit existing instructors in the course. However, depending on your privilege setting, you may or may not perform certain actions.
        <br> For example, edit/delete instructors, add instructor links will be disabled for those who do not have the privilege.
        <br>
        <br>
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
        <br>
        <br> If the links are not disabled for you, you can press the 'Add New Instructor' link, a form will show up, containing all the necessary information that you need to provide.
        <br>
        <br>
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
        <br>
        <br> Fill in the form with the information of the instructor you want to add and press 'Add Instructor' button to add the instructor. If you are not clear about certain input field, you can simply hover the input field and see the tooltip for explanation of the field.
        <br>
        <br>
        <br>
      </div>
    </li>
    <br>
    <li id="editCourseEditInstructor">
      <b>How do I edit instructors in my course?</b>
      <div class="helpSectionContent">
        In course edit page, you can edit instructors in this course by pressing the 'edit' link for an instructor. After pressing the link, a form will appear for editing the instructor.
        <br> Fill in the form with the updated information of the instructor and press "Save changes" button to save the changes.
        <br>
        <br>
      </div>
    </li>
    <br>
    <li id="editCourseSetAccessLevel">
      <b>How do I set different access level for an instructor?</b>
      <div class="helpSectionContent">
        When adding/editing an instructor, you can set the access level for an instructor. There are 5 options for you to choose from: Co-owner, Manager, Observer, Tutor and Custom.
        <br>
        <b>Co-owner</b> has the highest access level and will be able to perform any action to the course. When you create a new course, your access level is Co-owner.
        <b>Manager</b> is like Co-owner except that s/he is not allowed to delete the course.
        <b>Observer</b> can only view information of this course.
        <b>Tutor</b> can view student details, give/view comments, submit/view responses for sessions. All the access levels listed above have pre-defined privileges and you can view the settings by clicking the 'View details' link next to the access level description. You can also use '
        <b>Custom</b>' to give customized privileges to an instructor.
        <br>
        <br>
        <br>
      </div>
    </li>
    <br>
    <li id="editCourseSetCustom">
      <b>How do I set custom privileges for an instructor?</b>
      <div class="helpSectionContent">
        When setting the access level for an instructor as 'Custom', detailed privilege settings will be available for you.
        <br>
        <br>
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
        <br>
        <br> If a course does have sections, the link 'Give different permissions for a specific section' will be shown for customization for different sections. In the panel for section-level privilege settings, you can choose more than one section to apply the same set of settings. You can also set settings for different sessions for student in that section by click the link 'Give different permissions for sessions in this section'.
        <br>
      </div>
    </li>
    <br>
  </ul>
  <p align="right">
    <a href="#Top">Back to Top</a>
  </p>
  <div class="separate-content-holder">
    <hr>
  </div>
</div>
