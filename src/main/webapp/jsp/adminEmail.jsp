<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="teammates.ui.controller.AdminEmailPageData"%>
<%@ page import="teammates.ui.controller.AdminEmailComposePageData"%>
<%@ page import="teammates.ui.controller.AdminEmailSentPageData"%>
<%@ page import="teammates.ui.controller.AdminEmailDraftPageData"%>
<%@ page import="teammates.ui.controller.AdminEmailTrashPageData"%>

<%@ page import="teammates.common.util.Const"%>
<%@ page
    import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>

<%@ page import="teammates.common.util.Config"%>
<%@ page import="teammates.common.util.Const.AdminEmailPageState"%>
<%@ page import="teammates.common.datatransfer.AdminEmailAttributes"%>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory
            .getBlobstoreService();
%>

<%
    AdminEmailPageData data = (AdminEmailPageData) request.getAttribute("data");
%>


<html>
<head>
<link rel="shortcut icon" href="/favicon.png" />
<meta http-equiv="X-UA-Compatible" content="IE=8" />
<title>TEAMMATES - Administrator</title>
<link href="/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/stylesheets/teammatesCommon.css" rel="stylesheet">
<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
              <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
              <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
              <![endif]-->

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript"
    src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>

<script type="text/javascript" src="/js/adminEmail.js"></script>
<script src="/tinymce/js/tinymce/tinymce.min.js"></script>


</head>
<body>

    <div id="dhtmltooltip"></div>
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />


    <div class="container" id="mainContent">
                <div id="topOfPage"></div>
                <h1>Admin Email</h1>
                <br>

                <ul class="nav nav-tabs">
                    <li role="presentation" class="<%=data.getClass().toString().contains("AdminEmailCompose") ? "active" : ""%>">
                        <a href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE%>">Compose</a>
                    </li>
                    <li role="presentation" class="<%=data.getClass().toString().contains("AdminEmailSent") ? "active" : ""%>">
                        <a href="<%=Const.ActionURIs.ADMIN_EMAIL_SENT_PAGE%>">Sent</a>
                    </li>
                    <li role="presentation" class="<%=data.getClass().toString().contains("AdminEmailDraft") ? "active" : ""%>">
                        <a href="<%=Const.ActionURIs.ADMIN_EMAIL_DRAFT_PAGE%>">Draft</a>
                    </li>
                    <li role="presentation" class="<%=data.getClass().toString().contains("AdminEmailTrash") ? "active" : ""%>">
                        <a href="<%=Const.ActionURIs.ADMIN_EMAIL_TRASH_PAGE%>">Trash</a>
                    </li>
                </ul>
                
                <br>
                <br>
                
                
                <%
                  switch (data.getPageState()){
                  
                  default:
                  case COMPOSE:
                      
                      AdminEmailComposePageData aecPageData = (AdminEmailComposePageData) data;
                %>          
                
                      <div id="adminEmailCompose">
    
                        <form id="adminEmailMainForm" action="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_SEND%>" method="post">
                            
                            <% 
                              //provide email id if we are editing an email draft
                              if(aecPageData.emailToEdit !=null && aecPageData.emailToEdit.getSendDate() == null 
                                 && aecPageData.emailToEdit.getEmailId() != null){
                            %>
                                <input type="hidden" value="<%=aecPageData.emailToEdit.getEmailId()%>" name="<%=Const.ParamsNames.ADMIN_EMAIL_ID%>">
                            <% 
                              }
                            %>  
                            To :
                                <div class="row">
                                    <div class="col-md-11">
                                        <input id="addressReceiverEmails" type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEVIERS%>" 
                                               placeholder="example1@email.com,example2@email.com..."
                                               maxlength="500"
                                               value="<%=aecPageData.emailToEdit != null && aecPageData.emailToEdit.getAddressReceiver() != null 
                                            		     && aecPageData.emailToEdit.getAddressReceiver().size() > 0 ? aecPageData.emailToEdit.getAddressReceiver().get(0) : ""%>">
                                                         
                                        <%boolean hasGroupReceiver = aecPageData.emailToEdit != null 
                                        		                     && aecPageData.emailToEdit.getGroupReceiver() != null 
                                                                     && aecPageData.emailToEdit.getGroupReceiver().size() > 0;
                                        %>
                                               
                                        <input style="<%=hasGroupReceiver ? "" : "display:none;"%>" id="groupReceiverListFileKey" type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY%>" 
                                               value="<%=hasGroupReceiver ? aecPageData.emailToEdit.getGroupReceiver().get(0) : ""%>">
                                     </div>
                                     <div class="col-md-1 border-left-gray">
                                        <button type="button" class="btn btn-info" id="adminEmailGroupReceiverListUploadButton">
                                            <strong>
                                            Upload
                                            </strong>
                                        </button>
                                     </div>
                                </div>
                                
                            <br>
                            Subject : <input type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_SUBJECT%>" 
                                       value="<%=aecPageData.emailToEdit !=null? aecPageData.emailToEdit.getSubject() : ""%>">
                            <br>
                            <p>
                                <textarea cols="80" id="adminEmailBox"
                                    name="<%=Const.ParamsNames.ADMIN_EMAIL_CONTENT%>"
                                    rows="10"><%=aecPageData.emailToEdit !=null? aecPageData.emailToEdit.getContentForDisplay() : ""%></textarea>
                            </p>
                            <p>
                                <button type="button" id="composeSubmitButton" onclick="$('#adminEmailMainForm').submit();">Send</button>
                                <button type="button" id="composeSaveButton">save</button>
                            </p>
                        </form>
                        
                        <div id="adminEmailGroupReceiverListUploadBox" style="display:none;">
                                    <form id="adminEmailReceiverListForm" action="" method="POST" enctype="multipart/form-data">
                                        <span id="adminEmailGroupReceiverListInput"> <input
                                            type="file"
                                            name="<%=Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_TO_UPLOAD%>"
                                            id="adminEmailGroupReceiverList">
                                        </span>
                                    </form>
                        </div>
        
    
                        <div style="display: none;">
                            <form id="adminEmailFileForm" action=""
                                method="POST" enctype="multipart/form-data">
                                <span id="adminEmailFileInput"> <input
                                    type="file"
                                    name="<%=Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD%>"
                                    id="adminEmailFile">
                                </span>
                            </form>
    
                            <div id="documentBaseUrl"><%=Config.APP_URL %></div>
                        </div>
    
                    </div>
                <%
                    break;
                  
                
                
                
                  case SENT:
                      AdminEmailSentPageData sentPageData =  (AdminEmailSentPageData)data;
                %>
                    <div id="adminEmailSent">
                    
                        <div class="panel panel-success">
                            <div class="panel-heading">
                            <strong>
                                    <span id="sentEmailsCount">
                                        <%=sentPageData.adminSentEmailList.size() > 0 ? 
                                           "Emails Sent: " + sentPageData.adminSentEmailList.size() :
                                           "No Sent Email"
                                        %>
                                    </span>
                            </strong>
                            </div>
                                    <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>      
                                                <th>Action</th>          
                                                <th>Address Receiver</th>
                                                <th>Group Receiver</th>
                                                <th>Subject</th>
                                                <th onclick="toggleSort(this,5);"
                                                    class="button-sort-ascending">Date <span
                                                    class="icon-sort unsorted"
                                                    id="button_sort_date"></span></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                               for (AdminEmailAttributes ae : sentPageData.adminSentEmailList){  
                                            %>
                                                <tr id="<%=ae.getEmailId()%>">
                                                    <td>
                                                        <a target=blank href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE + 
                                                                  "?" + Const.ParamsNames.ADMIN_EMAIL_ID + 
                                                                  "=" + ae.getEmailId()%>">
                                                                  <span class="glyphicon glyphicon-edit">
                                                                  </span>
                                                        </a>
                                                        <a href="<%=Const.ActionURIs.ADMIN_EMAIL_MOVE_TO_TRASH + 
                                                                  "?" + Const.ParamsNames.ADMIN_EMAIL_ID + 
                                                                  "=" + ae.getEmailId()+ "&" + 
                                                                  Const.ParamsNames.ADMIN_EMAIL_TRASH_ACTION_REDIRECT + 
                                                                  "=sentpage"%>">
                                                                  <span class="glyphicon glyphicon-trash">
                                                                  </span>
                                                        </a>
                                                    </td>
                                                    <td><input value="<%=ae.getAddressReceiver().size() > 0 ? ae.getAddressReceiver().get(0) : "" %>" readonly="readonly" class="form-control"></td>
                                                    <td><input value="<%=ae.getGroupReceiver().size() > 0 ? ae.getGroupReceiver().get(0) : "" %>" readonly="readonly" class="form-control"></td>
                                                    <td><input value="<%=ae.getSubject()%>" readonly="readonly" class="form-control"></td>
                                                    <td><%=ae.getSendDateForDisplay()%></td>
                                                </tr>
                                            <%
                                               }
                                            %>
                                        </tbody>
                                    </table>
                                    </div>
                          </div>
                    </div>
                <%
                  break;
                         
                
                  case DRAFT:
                      AdminEmailDraftPageData draftPageData =  (AdminEmailDraftPageData)data;
                %>
                    <div id="adminEmailDraft">
                    
                        <div class="panel panel-info">
                            <div class="panel-heading">
                            <strong>
                                    <span id="draftEmailsCount">
                                        <%=draftPageData.draftEmailList.size() > 0 ? 
                                           "Email Drafts: " + draftPageData.draftEmailList.size() :
                                           "No Email Draft"
                                        %>
                                    </span>
                            </strong>
                            </div>
                            <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>      
                                        <th>Action</th>          
                                        <th>Address Receiver</th>
                                        <th>Group Receiver</th>
                                        <th>Subject</th>
                                        <th onclick="toggleSort(this,5);"
                                                    class="button-sort-ascending">Date <span
                                                    class="icon-sort unsorted"
                                                    id="button_sort_date"></span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                       for (AdminEmailAttributes ae : draftPageData.draftEmailList){  
                                    %>
                                        <tr id="<%=ae.getEmailId()%>">
                                            <td>
                                                <a target=blank href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE + 
                                                          "?" + Const.ParamsNames.ADMIN_EMAIL_ID + 
                                                          "=" + ae.getEmailId()%>">
                                                          <span class="glyphicon glyphicon-edit">
                                                          </span>
                                                </a>
                                                <a href="<%=Const.ActionURIs.ADMIN_EMAIL_MOVE_TO_TRASH + 
                                                          "?" + Const.ParamsNames.ADMIN_EMAIL_ID + 
                                                          "=" + ae.getEmailId() + "&" + 
                                                          Const.ParamsNames.ADMIN_EMAIL_TRASH_ACTION_REDIRECT + 
                                                          "=draftpage"%>">
                                                          <span class="glyphicon glyphicon-trash">
                                                          </span>
                                                </a>
                                            </td>
                                            <td><input value="<%=ae.getAddressReceiver().size() > 0 ? ae.getAddressReceiver().get(0) : "" %>" readonly="readonly" class="form-control"></td>
                                            <td><input value="<%=ae.getGroupReceiver().size() > 0 ? ae.getGroupReceiver().get(0) : "" %>" readonly="readonly" class="form-control"></td>
                                            <td><input value="<%=ae.getSubject()%>" readonly="readonly" class="form-control"></td>
                                            <td><%=ae.getCreateDateForDisplay()%></td>
                                        </tr>
                                    <%
                                       }
                                    %>
                                </tbody>
                            </table>
                            </div>
                         </div>
                            
                    </div>
                <%
                  break;
                
                
                
                  case TRASH:
                      AdminEmailTrashPageData trashPageData =  (AdminEmailTrashPageData)data;
                %>
                    <div id="adminEmailSent">
                    <div class="panel panel-danger">
                            <div class="panel-heading">
                            <strong>
                                    <span id="draftEmailsCount">
                                        <%=trashPageData.adminTrashEmailList.size() > 0 ? 
                                           "Trash Emails: " + trashPageData.adminTrashEmailList.size() :
                                           "No Trash Email"
                                        %>
                                    </span>
                            </strong>
                            <span class="pull-right">
                                <a class="btn btn-danger btn-xs" href="<%=trashPageData.getEmptyTrashBinActionUrl()%>">
                                    <strong>
                                        <span class="glyphicon glyphicon-floppy-remove">                                           
                                        </span>&nbsp;Empty Trash
                                    </strong>
                                 </a>
                            </span>
                            </div>
                            <div class="table-responsive">
                            <table class="table table-hover">
                                <thead>
                                    <tr>      
                                        <th>Action</th>          
                                        <th>Address Receiver</th>
                                        <th>Group Receiver</th>
                                        <th>Subject</th>
                                        <th onclick="toggleSort(this,5);"
                                                    class="button-sort-ascending">Date <span
                                                    class="icon-sort unsorted"
                                                    id="button_sort_date"></span></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                       for (AdminEmailAttributes ae : trashPageData.adminTrashEmailList){  
                                    %>
                                        <tr id="<%=ae.getEmailId()%>">
                                            <td>
                                                <a target=blank href="<%=Const.ActionURIs.ADMIN_EMAIL_COMPOSE_PAGE + 
                                                          "?" + Const.ParamsNames.ADMIN_EMAIL_ID + 
                                                          "=" + ae.getEmailId()%>">
                                                          <span class="glyphicon glyphicon-edit">
                                                          </span>
                                                </a>
                                                <a href="<%=Const.ActionURIs.ADMIN_EMAIL_MOVE_OUT_TRASH + 
                                                          "?" + Const.ParamsNames.ADMIN_EMAIL_ID + 
                                                          "=" + ae.getEmailId()%>">
                                                          <span class="glyphicon glyphicon-step-backward">
                                                          </span>
                                                </a>
                                            </td>
                                            <td><input value="<%=ae.getAddressReceiver().size() > 0 ? ae.getAddressReceiver().get(0) : "" %>" readonly="readonly" class="form-control"></td>
                                            <td><input value="<%=ae.getGroupReceiver().size() > 0 ? ae.getGroupReceiver().get(0) : "" %>" readonly="readonly" class="form-control"></td>
                                            <td><input value="<%=ae.getSubject()%>" readonly="readonly" class="form-control"></td>
                                            <td><%=ae.getSendDateForDisplay()%></td>
                                        </tr>
                                    <%
                                       }
                                    %>
                                </tbody>
                            </table>
                            </div>
                        </div>
                    </div>
                <%
                    break;
                  };
                %>
                
                
               
                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            </div>
    
     
    
    


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>