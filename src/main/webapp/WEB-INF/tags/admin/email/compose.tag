<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="adminEmail.jsp - Compose email" pageEncoding="UTF-8" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="emailToEdit" required="true" type="teammates.common.datatransfer.attributes.AdminEmailAttributes" %>

<div id="adminEmailCompose">
  <form id="adminEmailMainForm" action="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_SEND%>" method="post">

    <%-- Provide email id if we are editing an email draft --%>
    <c:if test="${(not empty emailToEdit) and (not empty emailToEdit.sendDate) and (not empty emailToEdit.emailId)}">
      <input type="hidden" value="${emailToEdit.emailId}" name="<%=Const.ParamsNames.ADMIN_EMAIL_ID%>">
    </c:if>
    <input type="hidden" name="<%=Const.ParamsNames.SESSION_TOKEN%>" value="${data.sessionToken}">

    To :
    <div class="row">
      <div class="col-md-11">
        <input id="addressReceiverEmails" type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS%>"
            placeholder="example1@email.com,example2@email.com..."
            maxlength="500"
            value="${(not empty emailToEdit) and (not empty emailToEdit.addressReceiver) ? emailToEdit.firstAddressReceiver : ''}">

        <input style="${(not empty emailToEdit) and (not empty emailToEdit.groupReceiver) ? '' : 'display:none;'}"
            id="groupReceiverListFileKey" type="text" class="form-control"
            name="<%=Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY%>"
            value="${(not empty emailToEdit) and (not empty emailToEdit.groupReceiver) ? emailToEdit.firstGroupReceiver : ''}">
      </div>
      <div class="col-md-1 border-left-gray">
        <button type="button" class="btn btn-info" id="adminEmailGroupReceiverListUploadButton">
          <strong>Upload</strong>
        </button>
      </div>
    </div>
    <br>
    Subject :
    <input type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_SUBJECT%>"
        value="${not empty emailToEdit ? emailToEdit.subject : ''}">
    <br>
    <p>
      <textarea cols="80" id="adminEmailBox" name="<%=Const.ParamsNames.ADMIN_EMAIL_CONTENT%>" rows="10">
        ${not empty emailToEdit ? emailToEdit.contentValue : ''}
      </textarea>
    </p>
    <p>
      <button type="button" id="composeSubmitButton">Send</button>
      <button type="button" id="composeSaveButton">save</button>
    </p>
  </form>

  <div id="adminEmailGroupReceiverListUploadBox" style="display:none;">
    <form id="adminEmailReceiverListForm" action="" method="POST" enctype="multipart/form-data">
      <span id="adminEmailGroupReceiverListInput">
      <input
          type="file"
          name="<%=Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_TO_UPLOAD%>"
          id="adminEmailGroupReceiverList">
      </span>
    </form>
  </div>

  <div style="display: none;">
    <form id="adminEmailFileForm" action=""
        method="POST" enctype="multipart/form-data">
      <span id="adminEmailFileInput">
      <input
          type="file"
          name="<%=Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD%>"
          id="adminEmailFile">
      </span>
    </form>

    <div id="documentBaseUrl"><%=Config.APP_URL%></div>
  </div>

</div>
