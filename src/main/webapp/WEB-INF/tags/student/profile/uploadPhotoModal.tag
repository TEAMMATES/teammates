<%@ tag description="studentProfile - Upload photo modal" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="modal" type="teammates.ui.template.StudentProfileUploadPhotoModal" required="true" %>
<c:set var="DEFAULT_PROFILE_PICTURE_PATH" value="<%= Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH %>" />
<div class="modal fade"
     id="studentPhotoUploader"
     role="dialog"
     aria-labelledby="myModalLabel"
     aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button"
                        class="close"
                        data-dismiss="modal"
                        aria-hidden="true">
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
                                <input id="studentPhoto"
                                       class="inline"
                                       type="file"
                                       name="<%= Const.ParamsNames.STUDENT_PROFILE_PHOTO %>">
                                <p class="help-block">
                                    Max Size: 5 MB
                                </p>
                                <button type="button"
                                        id="profileUploadPictureSubmit"
                                        class="btn btn-primary center-block"
                                        onclick="finaliseUploadPictureForm()">
                                    Upload Picture
                                </button>
                                <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${modal.googleId}">
                            </form>
                        </div>
                    </div>
                    <div class="col-xs-8 profile-pic-edit-col border-left-gray">
                        <c:choose>
                            <c:when test="${modal.pictureUrl == DEFAULT_PROFILE_PICTURE_PATH}">
                                <div class="alert alert-warning">
                                    Please upload a photo to start editing.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="profile-pic-edit">
                                    <img id="editableProfilePicture" src="${modal.pictureUrl}">
                                    <br><br>
                                    <label for="editableProfilePicture">
                                        Your Photo
                                    </label>
                                    <br>
                                </div>
                                <form id="profilePictureEditForm" method="post" action="<%= Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT %>">
                                    <input id="pictureHeight" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_HEIGHT %>" value="">
                                    <input id="pictureWidth" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_WIDTH %>" value="">
                                    <input id="cropBoxLeftX" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_LEFTX %>" value="">
                                    <input id="cropBoxTopY" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_TOPY %>" value="">
                                    <input id="cropBoxRightX" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_RIGHTX %>" value="">
                                    <input id="cropBoxBottomY" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_BOTTOMY %>" value="">
                                    <input id="blobKey" type="hidden" name="<%= Const.ParamsNames.BLOB_KEY %>" value="${modal.pictureKey}">
                                    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${modal.googleId}">
                                    <button type="button"
                                            id="profileEditPictureSubmit"
                                            class="btn btn-primary"
                                            onclick="finaliseEditPictureForm()">
                                        Save Edited Photo
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div><%-- /.row --%>
            </div><%-- /.modal-body --%>
            <div class="modal-footer">
            </div>
        </div><%-- /.modal-content --%>
    </div><%-- /.modal-dialog --%>
</div><%-- /.modal --%>