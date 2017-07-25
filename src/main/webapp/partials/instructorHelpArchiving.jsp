<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<h4>
    <a name="archiving">Archiving</a>
</h4>
<div id="contentHolder">
    <br>
    <ol style="list-style-type: none;">
        <li>
            <span class="text-bold">
                <a name="archivingCourse">
                    <h3>1. Archiving a course</h3>
                </a>
            </span>
            <div>
                You can archive a course so that it doesn't appear in your home page anymore. You cannot edit, create feedback sessions or enroll students in an archived course.
                <br><br>Go to your
                <b>'Home'</b> page. You will see a separate panel for each course and a table of feedback sessions inside it. It should look something similar to this:
                <br><br>
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
                Click on the <button class="btn btn-primary btn-xs" type="button">Course <span class="caret"></span></button> button on the panel heading of the course you want to archive.
                <br>Then select <b>'Archive'</b> in the drop-down menu and <b>'OK'</b> to confirm.

                <br><br>You can also archive a course from the courses page. Go to the <b>'Courses'</b> page.
                <br>Under 'Active Courses', click on the <b>'Archive'</b> button in the row corresponding to the course you want to archive.
            </div>
        </li>
        <li>
            <span class="text-bold">
                <a name="archivingViewArchivedCourses">
                    <h3>2. Viewing archived courses</h3>
                </a>
            </span>
            <div>
                You can view all your archived courses in the <b>'Courses'</b> page, under the <b>Archived Courses</b> heading. It will look similar to this:
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
        </li>
        <li>
            <span class="text-bold">
                <a name="archivingUnarchiveCourses">
                    <h3>3. Unarchiving a course</h3>
                </a>
            </span>
            <div>
                You can unarchive a course by clicking on the <button href="#" class="btn btn-default btn-xs" type="button">Unarchive</button> button in the list of archived courses in the
                <b>'Courses'</b> page.
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
