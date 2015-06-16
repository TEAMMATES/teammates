<%@ tag description="instructorStudentRecords - Student Profile" %>
<%@ attribute name="profile" type="teammates.ui.template.InstructorStudentRecordsStudentProfile" required="true" %>
<div class="row">
    <div class="col-xs-12">
        <div class="row" id="studentProfile">
            <div class="col-md-2 col-xs-3 block-center">
                <img src="${profile.pictureUrl}" class="profile-pic pull-right">
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
                            <td>${profile.shortName} (<i>${profile.gender}</i>)</td>
                        </tr>
                        <tr>
                            <td class="text-bold">Email</td>
                            <td>${profile.email}</td>
                        </tr>
                        <tr>
                            <td class="text-bold">Institution</td>
                            <td>${profile.institute}</td>
                        </tr>
                        <tr>
                            <td class="text-bold">Nationality</td>
                            <td>${profile.nationality}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <div class="panel panel-default">
                    <div class="panel-body">
                        <span data-toggle="modal" data-target="#studentProfileMoreInfo" class="text-muted pull-right glyphicon glyphicon-resize-full cursor-pointer"></span>
                        <h5>More Info</h5>
                        <!--Note: When an element has class text-preserve-space, do not insert and HTML spaces-->
                        <p class="text-preserve-space height-fixed-md">${profile.moreInfo}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>