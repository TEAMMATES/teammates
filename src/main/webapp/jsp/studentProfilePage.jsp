<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.ui.controller.StudentProfilePageData" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.UploadOptions" %>

<%
    StudentProfilePageData data = (StudentProfilePageData) request.getAttribute("data");
    
    String pictureUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + 
            "?"+Const.ParamsNames.BLOB_KEY+"=" + data.account.studentProfile.pictureKey + 
            "&"+Const.ParamsNames.USER_ID+"="+data.account.googleId;
    if (data.account.studentProfile.pictureKey == "") {
    	pictureUrl = Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES - Student Profile</title>
    <link rel="stylesheet" href="/Jcrop/css/jquery.Jcrop.min.css">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">

    <script type="text/javascript" async="" src="https://ssl.google-analytics.com/ga.js"></script>
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript"
            src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
    <script src="/Jcrop/js/jquery.Jcrop.min.js"></script>
    <script src="/bootstrap/js/bootstrap.min.js"></script>

    <script type="text/javascript" src="/js/student.js"></script>
    <script type="text/javascript" src="/js/studentProfile.js"></script>
    <!-- [if lt IE 9]>
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif] -->
    
</head>
<body>
    <jsp:include page="<%=Const.ViewURIs.STUDENT_HEADER %>" />
    <div id="frameBodyWrapper" class="container theme-showcase">
        <div id="topOfPage"></div>
        <h2>Student Profile</h2>
        <br>
        
        <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
        <br>
        
        <div class="modal fade" id="studentPhotoUploader" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        <h4 class="modal-title">
                            Upload/Edit Photo 
                            <small>(Use the selection in the photo to specify a crop)</small>
                        </h4>
                    </div>
                    <div class="modal-body center-block align-center">
                        <br>
                        <div class="row">
                            <div class="col-xs-4 profile-pic-edit-col">
                                <div class="center-block align-center">
                                    <form id="profilePictureUploadForm" method="post"> 
                                        <input id="studentPhoto" class="inline" type="file" name="<%=Const.ParamsNames.STUDENT_PROFILE_PHOTO%>" />
                                        <p class="help-block">Max Size: 5 MB</p>
                                        <button type="button" id="profileUploadPictureSubmit" class="btn btn-primary center-block" onclick="finaliseUploadPictureForm()">
                                            Upload Picture
                                        </button>
                                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                                    </form>
                                </div>
                            </div>
                            <div class="col-xs-8 profile-pic-edit-col border-left-gray">
                                <% if (!pictureUrl.equals(Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH)) { %>
                                    <div class="profile-pic-edit">
                                        <img id="editableProfilePicture" src="<%=pictureUrl %>" /><br><br>
                                        <label for="editableProfilePicture">Your Photo</label><br>
                                    </div>
                                    <form id="profilePictureEditForm" method="post" action="<%=Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT %>">
                                        <input id="pictureHeight" type="hidden" name="<%=Const.ParamsNames.PROFILE_PICTURE_HEIGHT %>" value="">
                                        <input id="pictureWidth" type="hidden" name="<%=Const.ParamsNames.PROFILE_PICTURE_WIDTH %>" value="">
                                        <input id="cropBoxLeftX" type="hidden" name="<%=Const.ParamsNames.PROFILE_PICTURE_LEFTX %>" value="">
                                        <input id="cropBoxTopY" type="hidden" name="<%=Const.ParamsNames.PROFILE_PICTURE_TOPY %>" value="">
                                        <input id="cropBoxRightX" type="hidden" name="<%=Const.ParamsNames.PROFILE_PICTURE_RIGHTX %>" value="">
                                        <input id="cropBoxBottomY" type="hidden" name="<%=Const.ParamsNames.PROFILE_PICTURE_BOTTOMY %>" value="">
                                        <input id="blobKey" type="hidden" name="<%=Const.ParamsNames.BLOB_KEY %>" value="<%=data.account.studentProfile.pictureKey %>">
                                        <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
                                        <button type="button"id="profileEditPictureSubmit" class="btn btn-primary" onclick="finaliseEditPictureForm()">Save Edited Photo</button>
                                    </form>
                                <% } else { %>
                                    <div class="alert alert-warning">
                                        Please upload a photo to start editing.
                                    </div>
                                <% } %>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
        
        <div id="editProfileDiv" class="well well-plain well-narrow well-sm-wide">
            <h3 id="studentName"><strong><%=data.account.name %></strong></h3><br>
            <div class="form-group row">
                <div class="col-xs-6 col-sm-5 col-md-3 cursor-pointer" 
                     title="<%=Const.Tooltips.STUDENT_PROFILE_PICTURE %>" data-toggle="tooltip" data-placement="top">
                    <img id="profilePic" src="<%=pictureUrl %>" class="profile-pic" data-toggle="modal" data-target="#studentPhotoUploader" data-edit="<%=data.editPicture %>" />
                </div>
                <div class="">
                    <button id="uploadEditPhoto" class="btn btn-primary" type="button" data-toggle="modal" data-target="#studentPhotoUploader">Upload/Edit Photo</button>
                </div>
            </div>
            <form class="form center-block" role="form" method="post"
                  action="<%=Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE %>">
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_SHORTNAME %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentNickname">The name you prefer to be called by Instructors</label>
                    <input id="studentShortname" name="<%=Const.ParamsNames.STUDENT_SHORT_NAME %>" class="form-control" type="text" data-actual-value="<%=data.account.studentProfile.shortName == null ? "" : data.account.studentProfile.shortName %>" value="<%=data.account.studentProfile.shortName == null ? "" : data.account.studentProfile.shortName %>" placeholder="How the instructor should call you" />
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_EMAIL %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentEmail">Email <em class="font-weight-normal emphasis text-muted small">- only visible to your instructors</em></label>
                    <input id="studentEmail" name="<%=Const.ParamsNames.STUDENT_PROFILE_EMAIL %>" class="form-control" type="email"
                           data-actual-value="<%=data.account.studentProfile.email %>" value="<%=data.account.studentProfile.email == null ? "" : data.account.studentProfile.email %>" placeholder="Contact Email (for your instructors to contact you beyond graduation)" />
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_INSTITUTION %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentInstitution">Institution</label>
                    <input id="studentInstitution" name="<%=Const.ParamsNames.STUDENT_PROFILE_INSTITUTION %>" class="form-control" type="text" data-actual-value="<%=data.account.studentProfile.institute == null ? "" : data.account.studentProfile.institute %>" value="<%=data.account.studentProfile.institute == null ? "" : data.account.studentProfile.institute %>" placeholder="Your Institution" />
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_NATIONALITY%>" data-toggle="tooltip" data-placement="top">
                    <label for="studentNationality">Nationality</label>
                    <input id="studentNationality" name="<%=Const.ParamsNames.STUDENT_NATIONALITY%>" class="form-control" type="text" data-actual-value="<%=data.account.studentProfile.nationality == null ? "" : data.account.studentProfile.nationality %>" value="<%=data.account.studentProfile.nationality == null ? "" : data.account.studentProfile.nationality %>" placeholder="Nationality" />
                </div>
                <div class="form-group">
                    <label for="studentGender">Gender</label>
                    <div id="studentGender">
                        <label for="genderMale" class="radio-inline">
                            <input id="genderMale" name="<%=Const.ParamsNames.STUDENT_GENDER %>" class="radio" type="radio" 
                            value="<%=Const.GenderTypes.MALE %>"
                            <%=data.account.studentProfile.gender.equals(Const.GenderTypes.MALE) ? "checked=\"checked\"" : "" %> /> Male
                        </label>
                        <label for="genderFemale" class="radio-inline">
                            <input id="genderFemale" name="<%=Const.ParamsNames.STUDENT_GENDER %>" class="radio" type="radio"
                            value="<%=Const.GenderTypes.FEMALE %>"
                            <%=data.account.studentProfile.gender.equals(Const.GenderTypes.FEMALE) ? "checked=\"checked\"" : "" %> /> Female
                        </label>
                        <label class="radio-inline" for="genderOther">
                            <input id="genderOther" name="<%=Const.ParamsNames.STUDENT_GENDER %>" class="radio" type="radio"
                            value="<%=Const.GenderTypes.OTHER %>"
                            <%=data.account.studentProfile.gender.equals(Const.GenderTypes.OTHER) ? "checked=\"checked\"" : "" %> /> Not Specified
                        </label>
                    </div>
    
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_MOREINFO %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentNationality">More info about yourself</label>
                    <textarea id="studentMoreInfo"  name="<%=Const.ParamsNames.STUDENT_PROFILE_MOREINFO %>" 
                              rows="4" class="form-control"
                              placeholder="<%=Const.Tooltips.STUDENT_PROFILE_MOREINFO %>"
                              ><%=data.account.studentProfile.moreInfo == null ? "" : data.account.studentProfile.moreInfo %></textarea>
                </div><br>
                <button type="submit" id="profileEditSubmit" class="btn btn-primary center-block">Save Profile</button>
                <br>
                <p class="text-muted text-color-disclaimer"> <i>* This profile will be visible to all your Instructors and Coursemates</i></p>
                <input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId%>">
            </form>
        </div>
    </div>
    <br><br><br>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body> 