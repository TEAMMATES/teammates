<h4>
    <a name="gs">Getting Started</a>
</h4>
<div id="contentHolder">
    <br>
    <ol type="1">
        <li>
            <span class="text-bold">Create a course
            </span>
            <div class="helpSectionContent">
                Go to the ‘Courses’ page and create a course.
                <br>Some of the elements in the user interface (e.g., text boxes) have hover over tips to tell you what the element does.
                <br>
            </div>
            <br>
            <div id="gettingStartedHtml" class="bs-example">
                <div class="panel panel-primary">
                    <div class="panel-body fill-plain">
                        <form class="form form-horizontal">
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Course ID:
                                </label>
                                <div class="col-sm-3">
                                    <input class="form-control" type="text" value="" data-toggle="tooltip" data-placement="top" maxlength="40" tabindex="1" placeholder="e.g. CS3215-2013Semester1" title="Enter the identifier of the course, e.g.CS3215-2013Semester1.">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-3 control-label">Course Name:
                                </label>
                                <div class="col-sm-9">
                                    <input class="form-control" type="text" value="" data-toggle="tooltip" data-placement="top" maxlength="64" tabindex="2" placeholder="e.g. Software Engineering" title="Enter the name of the course, e.g. Software Engineering.">
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-sm-offset-3 col-sm-9">
                                    <input type="button" class="btn btn-primary" value="Add Course" tabindex="3">
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <br>
            <span class="text-bold">Adding more instructors</span>
            <div class="helpSectionContent">
                More instructors (e.g. tutors) can be added to a course by going to the ‘edit’ link of the course.
                <br>Note that you can set the access control levels for these instructors.
                <br>
                <br>
            </div>
        </li>
        <li>
            <span class="text-bold">Enroll students
            </span>
            <div class="helpSectionContent">
                Enroll students by doing one of the following options:
                <ul>
                    <li>
                        Go to the ‘Home’ page, click on the ‘Students‘ button on the corresponding course, and choose ‘Enroll’
                    </li>
                    <li>
                        Go to the ‘Courses’ page and click the ‘Enroll‘ button of the corresponding course
                    </li>
                </ul>
                <br>
                <br>
            </div>
        </li>
        <li>
            <span class="text-bold">Create a session
            </span>
            <div class="helpSectionContent">
                Go to the ‘Sessions’ page and create a session (there are different session types to choose from).
                <br>
                <br>

                <div class="bs-example" id="sessionTypeSelectionHtml">
                    <div class="well well-plain">
                        <div class="row" data-toggle="tooltip" data-placement="top" title="Select a different type of session here.">
                            <h4 class="label-control col-md-2 text-md">Create new </h4>
                            <div class="col-md-5">
                                <select class="form-control" name="fstype" id="fstype">
                                    <option value="STANDARD" selected="">
                                        Session with your own questions
                                    </option>
                                    <option value="TEAMEVALUATION">
                                        Team peer evaluation session
                                    </option>
                                </select>
                            </div>
                            <h4 class="label-control col-md-1 text-md">Or: </h4>
                            <div class="col-md-3">
                                <a id="button_copy" class="btn btn-info" style="vertical-align:middle;">Copy from previous feedback sessions</a>
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                <div class="helpSectionContent">

                    <ul>
                        <li>Session with your own questions</li>
                        <div style="margin: 0 auto; padding: 0 50px;">
                            <ul>
                                <li>Creates an empty feedback session</li>
                                <li>Allows you to craft custom questions that fit your needs</li>
                            </ul>
                        </div>
                    </ul>
                    <ul>
                        <li>Team peer evaluation session</li>
                        <div style="margin: 0 auto; padding: 0 50px;">
                            <ul>
                                <li>Provides 5 standard questions for team peer evaluations</li>
                                <li> Allows you to modify/remove the given questions and add your own questions as required</li>
                            </ul>
                        </div>
                        <br>
                        <li>You can set custom feedback paths for each question:
                            <br>i.e. specify who is giving feedback to whom. e.g. the question 'What is the estimated contribution of team member?' can be set the following feedback path:
                            <br> Feedback giver: students in the course
                            <br> Feedback recipient: giver's team members
                            <br>


                        </li>
                        <br>
                        <li>You can set the visibility options for each question:
                            <br>Allows you to set who can see the answers, giver name and recipient name for each question.

                            <br>
                            <br>See
                            <a href="#sessionTypes">here</a> for more info about session types.</li>

                    </ul>
                    <br>




                </div>
            </div>
        </li>
        <li>
            <span class="text-bold">When sessions open</span>
            <div class="helpSectionContent">
                When it is time to open the session (based on the ‘opening time’ you specified), TEAMMATES automatically emails students instructions for accessing the session. A copy of that email will be sent to you as well.
                <br>
                <br> If you would like students to access TEAMMATES sooner (e.g. you would like them to fill in their profile page in advance), you can go to the 'View' link of the course and click the 'Remind Students to Join' button, which will send them 'access instructions' immediately.
                <br>
                <br>
                <div class="helpSectionContent"></div>
            </div>
        </li>
        <li>
            <span class="text-bold">While the session is open</span>
            <div class="helpSectionContent">
                You can view responses any time after the session is open, even when the session is still ongoing. Just go to the ‘Sessions’ page and click the corresponding ‘View results/responses’ link.
                <a href="#sessionTypes">Session Types
                </a> section has more information about the reports available for different session types.
                <br>
                <br>Students will be sent a reminder 24 hours before the closing time of a session. In addition, you can send further reminders to students any time while a session is open using the ‘remind’ link.
                <br>
                <br>
            </div>
        </li>
        <li>
            <span class="text-bold">After the session is closed</span>
            <div class="helpSectionContent">
                You can publish results (i.e. make it visible to students) using the ‘publish’ link in the ‘Sessions’ page.
                <br>
                <br>Results of sessions can be downloaded in spreadsheet format.
                <br>
                <br>
            </div>
        </li>
        <li>
            <span class="text-bold">Any time</span>
            <div class="helpSectionContent">
                You can use the ‘Students’ page any time to do these things:
                <div style="margin: 0 auto; padding: 0 50px;">
                    <ul>
                        <li>
                            <span class="text-bold">Email a group of students</span>: Filter out students in certain teams/courses and email them. Also handy for locating the email of any past student.</li>
                        <li>
                            <span class="text-bold">Comment on students</span>: Add a comments about any student. Handy for saving and retrieving comments about a student quickly. You can make these comments visible to others or keep them private.</li>
                        <li>
                            <span class="text-bold">View profile and all past records of a student
                            </span>: View profile of a student and see in one place all submissions given/received by a student. Handy for examining how a student progressed through a course. (Students &gt; All Records)
                        </li>
                        <li>
                            <span class="text-bold">View all comments</span>: View all comments from one page.
                        </li>
                        <li>
                            <span class="text-bold">Search</span>: Search for students, teams, sections.
                        </li>
                        <li>
                            <span class="text-bold">Archive old courses</span>: Archive old courses that you no longer need actively.
                        </li>
                    </ul>
                    <br>
                </div>
            </div>
        </li>
        <li>
            <span class="text-bold">When you need help</span>
            <div class="helpSectionContent">
                If you have a doubt or need our help, just
                <a href="mailto:teammates@comp.nus.edu.sg">email us
                </a>. We respond within 24 hours.
                <br>
                <br>
            </div>
        </li>
    </ol>
    <p align="right">
        <a href="#Top">Back to Top
        </a>
    </p>
    <div class="separate-content-holder">
        <hr>
    </div>
</div>