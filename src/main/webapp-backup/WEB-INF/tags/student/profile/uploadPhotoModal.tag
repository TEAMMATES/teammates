<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentProfile - Upload photo modal" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="modal" type="teammates.ui.template.StudentProfileUploadPhotoModal" required="true" %>
<%@ attribute name="sessionToken" required="true" %>
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
        </h4>
      </div>
      <div class="modal-body center-block align-center">
        <br>
        <div class="row">
          <div class="col-xs-4 profile-pic-edit-col">
            <div class="center-block align-center">
              <form id="profilePictureUploadForm" method="post">
                <span class="btn btn-primary profile-pic-file-selector">
                  Browse...
                  <input id="studentPhoto"
                      type="file"
                      name="<%= Const.ParamsNames.STUDENT_PROFILE_PHOTO %>">
                </span>
                <input type="text" class="filename-preview" value="No File Selected" disabled>
                <p class="help-block align-left">
                  Max Size: 5 MB
                </p>
                <button type="button"
                    id="profileUploadPictureSubmit"
                    class="btn btn-primary width-100-pc"
                    disabled>
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
                  <div class="alert alert-info">
                    Zoom, pan and rotate to crop your image to the desired area.
                  </div>
                  <button id="profilePicEditPanUp" type="button" class="btn btn-primary">
                    <span class="glyphicon glyphicon-arrow-up"></span>
                  </button>
                  <div class="profile-pic-edit-container row">
                    <div class="col-xs-2">
                      <button id="profilePicEditPanLeft" type="button" class="btn btn-primary">
                        <span class="glyphicon glyphicon-arrow-left"></span>
                      </button>
                    </div>
                    <div class="col-xs-8">
                      <img id="editableProfilePicture" src="${modal.pictureUrl}">
                    </div>
                    <div class="col-xs-2">
                      <button id="profilePicEditPanRight" type="button" class="btn btn-primary">
                        <span class="glyphicon glyphicon-arrow-right"></span>
                      </button>
                    </div>
                  </div>
                  <button id="profilePicEditPanDown" type="button" class="btn btn-primary">
                    <span class="glyphicon glyphicon-arrow-down"></span>
                  </button>
                  <div class="profile-pic-edit-toolbar">
                    <button id="profilePicEditRotateLeft" type="button" class="btn btn-primary">
                      <span class="glyphicon glyphicon-repeat glyphicon-flipped"></span>
                    </button>
                    <button id="profilePicEditZoomIn" type="button" class="btn btn-primary">
                      <span class="glyphicon glyphicon-zoom-in"></span>
                    </button>
                    <button id="profilePicEditZoomOut" type="button" class="btn btn-primary">
                      <span class="glyphicon glyphicon-zoom-out"></span>
                    </button>
                    <button id="profilePicEditRotateRight" type="button" class="btn btn-primary">
                      <span class="glyphicon glyphicon-repeat"></span>
                    </button>
                  </div>
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
                  <input id="rotate" type="hidden" name="<%= Const.ParamsNames.PROFILE_PICTURE_ROTATE %>" value="">
                  <input id="blobKey" type="hidden" name="<%= Const.ParamsNames.BLOB_KEY %>" value="${modal.pictureKey}">
                  <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${modal.googleId}">
                  <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${sessionToken}">
                  <button type="button"
                      id="profileEditPictureSubmit"
                      class="btn btn-primary">
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
