<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="teammates.ui.controller.StudentProfilePageData" %>
<%@ page import="teammates.common.datatransfer.StudentProfileAttributes" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.UploadOptions" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/student" prefix="ts" %>

<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/student.js"></script>
    <script type="text/javascript" src="/js/studentProfile.js"></script>
    <!--
    <script type="text/javascript" src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <link rel="stylesheet" href="/Jcrop/css/jquery.Jcrop.min.css">
    <script type="text/javascript" src="/Jcrop/js/jquery.Jcrop.min.js"></script>
    -->
</c:set>
<ts:studentPage pageTitle="TEAMMATES - Student Profile" bodyTitle="Student Profile" jsIncludes="${jsIncludes}">
    <br>
    <t:statusMessage />
    <br>
        <div class="modal fade" id="studentPhotoUploader" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
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
                                        <input id="studentPhoto" class="inline" type="file" name="<c:out value="Const.ParamsNames.STUDENT_PROFILE_PHOTO" />">
                                        <p class="help-block">
                                            Max Size: 5 MB
                                        </p>
                                        <button type="button" id="profileUploadPictureSubmit" class="btn btn-primary center-block" onclick="finaliseUploadPictureForm()">
                                            Upload Picture
                                        </button>
                                        <input type="hidden" name="<c:out value="Const.ParamsNames.USER_ID" />" value="<c:out value="${data.googleId}" />">
                                    </form>
                                </div>
                            </div>
                            <div class="col-xs-8 profile-pic-edit-col border-left-gray">
                            <c:choose>
                                <c:when test="${data.pictureUrl} == Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH">
                                    <div class="alert alert-warning">
                                        Please upload a photo to start editing.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="profile-pic-edit">
                                        <img id="editableProfilePicture" src="<c:out value="${data.pictureUrl}" />">
                                        <br><br>
                                        <label for="editableProfilePicture">
                                            Your Photo
                                        </label>
                                        <br>
                                    </div>
                                    <form id="profilePictureEditForm" method="post" action="<c:out value="Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT" />">
                                        <input id="pictureHeight" type="hidden" name="<c:out value="Const.ParamsNames.PROFILE_PICTURE_HEIGHT" />" value="">
                                        <input id="pictureWidth" type="hidden" name="<c:out value="Const.ParamsNames.PROFILE_PICTURE_WIDTH" />" value="">
                                        <input id="cropBoxLeftX" type="hidden" name="<c:out value="Const.ParamsNames.PROFILE_PICTURE_LEFTX" />" value="">
                                        <input id="cropBoxTopY" type="hidden" name="<c:out value="Const.ParamsNames.PROFILE_PICTURE_TOPY" />" value="">
                                        <input id="cropBoxRightX" type="hidden" name="<c:out value="Const.ParamsNames.PROFILE_PICTURE_RIGHTX" />" value="">
                                        <input id="cropBoxBottomY" type="hidden" name="<c:out value="Const.ParamsNames.PROFILE_PICTURE_BOTTOMY" />" value="">
                                        <input id="blobKey" type="hidden" name="<c:out value="Const.ParamsNames.BLOB_KEY" />" value="<c:out value="data.pictureKey" />">
                                        <input type="hidden" name="<c:out value="Const.ParamsNames.USER_ID" />" value="<c:out value="${data.googleId}" />">
                                        <button type="button"id="profileEditPictureSubmit" class="btn btn-primary" onclick="finaliseEditPictureForm()">
                                            Save Edited Photo
                                        </button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                            </div>
                        </div> <!-- /.row -->
                    </div> <!-- /.modal-body -->
                    <div class="modal-footer">
                    </div>
                </div> <!-- /.modal-content -->
            </div> <!-- /.modal-dialog -->
        </div> <!-- /.modal -->
        <div id="editProfileDiv" class="well well-plain well-narrow well-sm-wide">
            <h3 id="studentName">
                <strong><c:out value="${data.name}" /></strong>
            </h3>
            <br>
            <div class="form-group row">
                <div class="col-xs-6 col-sm-5 col-md-3 cursor-pointer" title="<c:out value="Const.Tooltips.STUDENT_PROFILE_PICTURE" />" data-toggle="tooltip" data-placement="top">
                    <img id="profilePic" src="<c:out value="${data.pictureUrl}" />" class="profile-pic" data-toggle="modal" data-target="#studentPhotoUploader" data-edit="<c:out value="data.editPicture" />">
                </div>
                <div class="">
                    <button id="uploadEditPhoto" class="btn btn-primary" type="button" data-toggle="modal" data-target="#studentPhotoUploader">
                        Upload/Edit Photo
                    </button>
                </div>
            </div>
            <form class="form center-block" role="form" method="post" action="<c:out value="Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE" />">
                <div class="form-group" title="<c:out value="Const.Tooltips.STUDENT_PROFILE_SHORTNAME" />" data-toggle="tooltip" data-placement="top">
                    <label for="studentNickname">
                        The name you prefer to be called by Instructors
                    </label>
                    <input id="studentShortname" name="<c:out value="Const.ParamsNames.STUDENT_SHORT_NAME" />" class="form-control" type="text" data-actual-value="<c:out value="${data.shortName}" />" value="<c:out value="${data.shortName}" />" placeholder="How the instructor should call you">
                </div>
                <div class="form-group" title="<c:out value="Const.Tooltips.STUDENT_PROFILE_EMAIL" />" data-toggle="tooltip" data-placement="top">
                    <label for="studentEmail">
                        Long term contact email <em class="font-weight-normal emphasis text-muted small">- only visible to your instructors</em>
                    </label>
                    <input id="studentEmail" name="<c:out value="Const.ParamsNames.STUDENT_PROFILE_EMAIL" />" class="form-control" type="email" data-actual-value="<c:out value="${data.email}" />" value="<c:out value="${data.email}" />" placeholder="Contact Email (for your instructors to contact you beyond graduation)">
                </div>
                <div class="form-group" title="<c:out value="Const.Tooltips.STUDENT_PROFILE_INSTITUTION" />" data-toggle="tooltip" data-placement="top">
                    <label for="studentInstitution">
                        Institution
                    </label>
                    <input id="studentInstitution" name="<c:out value="Const.ParamsNames.STUDENT_PROFILE_INSTITUTION" />" class="form-control" type="text" data-actual-value="<c:out value="${data.institute}" />" value="<c:out value="${data.institute}" />" placeholder="Your Institution">
                </div>
                <div class="form-group" title="<c:out value="Const.Tooltips.STUDENT_PROFILE_NATIONALITY" />" data-toggle="tooltip" data-placement="top">
                    <label for="studentNationality">
                        Nationality
                    </label>
                    <input id="studentNationality" name="<c:out value="Const.ParamsNames.STUDENT_NATIONALITY" />" class="form-control" type="text" data-actual-value="<c:out value="${data.nationality}" />" value="<c:out value="${data.nationality}" />" placeholder="Nationality">
                </div>
                <div class="form-group">
                    <label for="studentGender">
                        Gender
                    </label>
                    <div id="studentGender">
                        <label for="genderMale" class="radio-inline">
                            <input id="genderMale" name="<c:out value="Const.ParamsNames.STUDENT_GENDER" />" class="radio" type="radio" value="<c:out value="Const.GenderTypes.MALE" />" <c:out value="${data.gender == Const.GenderTypes.MALE} ? \"checked=\"checked\"\" : \"\"" />> Male
                        </label>
                        <label for="genderFemale" class="radio-inline">
                            <input id="genderFemale" name="<c:out value="Const.ParamsNames.STUDENT_GENDER" />" class="radio" type="radio" value="<c:out value="Const.GenderTypes.FEMALE" />" <c:out value="${data.gender == Const.GenderTypes.FEMALE} ? \"checked=\"checked\"\" : \"\"" />> Female
                        </label>
                        <label class="radio-inline" for="genderOther">
                            <input id="genderOther" name="<c:out value="Const.ParamsNames.STUDENT_GENDER" />" class="radio" type="radio" value="<c:out value="Const.GenderTypes.OTHER" />" <c:out value="${data.gender == Const.GenderTypes.OTHER} ? \"checked=\"checked\"\" : \"\"" />> Not Specified
                        </label>
                    </div>
                </div>
                <div class="form-group" title="<c:out value="Const.Tooltips.STUDENT_PROFILE_MOREINFO" />" data-toggle="tooltip" data-placement="top">
                    <label for="studentNationality">
                        More info about yourself
                    </label>
                    <!-- Do not add whitespace between the opening and closing tags-->
                    <textarea id="studentMoreInfo"  name="<c:out value="Const.ParamsNames.STUDENT_PROFILE_MOREINFO" />" rows="4" class="form-control" placeholder="<c:out value="Const.Tooltips.STUDENT_PROFILE_MOREINFO" />"><c:out value="${data.moreInfo}" /></textarea>
                </div>
                <br>
                <button type="submit" id="profileEditSubmit" class="btn btn-primary center-block">
                    Save Profile
                </button>
                <br>
                <p class="text-muted text-color-disclaimer">
                    <i>* This profile will be visible to all your Instructors and Coursemates</i>
                </p>
                <input type="hidden" name="<c:out value="Const.ParamsNames.USER_ID" />" value="<c:out value="${data.googleId}" />">
            </form>
        </div>
</ts:studentPage>