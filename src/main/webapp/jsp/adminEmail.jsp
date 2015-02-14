<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="teammates.ui.controller.AdminEmailPageData"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page
    import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>

<%@ page import="teammates.common.util.Config"%>

<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();
%>

<%
	AdminEmailPageData data = (AdminEmailPageData) request
			.getAttribute("data");
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
<script type="text/javascript"
    src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script type="text/javascript" src="/bootstrap/js/bootstrap.min.js"></script>

<script type="text/javascript" src="/js/adminEmail.js"></script>
<script src="/tinymce/js/tinymce/tinymce.min.js"></script>


</head>
<body>

    
   
    <div id="dhtmltooltip"></div>
    <jsp:include page="<%=Const.ViewURIs.ADMIN_HEADER%>" />


    <div class="container theme-showcase" role="main">

        <div id="frameBody">
            <div id="frameBodyWrapper">
                <div id="topOfPage"></div>
                <div id="headerOperation" class="page-header">
                    <h1>Admin Email</h1>
                </div>


                <form action="/admin/adminEmailPage" method="post">
                
                    To : <input type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_RECEVIER%>" >
                    <br>
                    Subject : <input type="text" class="form-control" name="<%=Const.ParamsNames.ADMIN_EMAIL_SUBJECT%>" >
                    <br>
                    <p>
                        <textarea cols="80" id="adminEmailBox"
                            name="<%=Const.ParamsNames.ADMIN_EMAIL_CONTENT%>"
                            rows="10"></textarea>
                    </p>
                    <p>
                        <input type="submit" value="Submit" />
                    </p>
                </form>
                
                
                <jsp:include page="<%=Const.ViewURIs.STATUS_MESSAGE%>" />
            </div>
        </div>
    </div>
    
     
    <div style="display: none;">
        <form id="adminEmailFileForm"
            action="<%=data.getNewUploadUrl()%>"
            method="POST" enctype="multipart/form-data">
            <span id="adminEmailFileInput">
            <input type="file" name="<%=Const.ParamsNames.ADMIN_EMAIL_IMAGE_TO_UPLOAD%>" id="adminEmailFile">
            </span>
        </form>
        
        <div id="documentBaseUrl"><%=Config.APP_URL %></div>
    </div>
    


    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body>
</html>