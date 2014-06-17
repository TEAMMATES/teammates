<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="teammates.ui.controller.PageData" %>
<%@ page import="teammates.common.util.Const" %>

<%
    PageData data = (PageData) request.getAttribute("data");
%>

<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" href="/favicon.png">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES - Student Profile</title>
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/bootstrap/css/bootstrap-theme.min.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">

    <script type="text/javascript" async="" src="https://ssl.google-analytics.com/ga.js"></script>
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    <script type="text/javascript" src="/js/jquery-minified.js"></script>
    <script type="text/javascript"
            src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.4/jquery-ui.min.js"></script>
    <script type="text/javascript" src="/js/common.js"></script>
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
        
        <div id="editProfileDiv" class="well well-plain well-narrow well-sm-wide">
            <h3 id="studentName"><strong><%=data.account.name %></strong></h3><br>
            <form class="form center-block" role="form" method="post" enctype="multipart/form-data"
                action="<%=Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE %>">
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_SHORTNAME %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentNickname">Shortname</label>
                    <input id="studentShortname" name="<%=Const.ParamsNames.STUDENT_SHORT_NAME %>" class="form-control" type="text" data-actual-value="<%=data.account.studentProfile.shortName == null ? "" : data.account.studentProfile.shortName %>" value="<%=data.account.studentProfile.shortName == null ? "" : data.account.studentProfile.shortName %>" placeholder="How the instructor should call you" />
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_EMAIL %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentEmail">Email<small><sup class="pull-right text-color-disclaimer"> - Only visible to your Instructors</sup></small></label>
                    <input id="studentEmail" name="<%=Const.ParamsNames.STUDENT_PROFILE_EMAIL %>" class="form-control" type="email"
                           data-actual-value="<%=data.account.studentProfile.email %>" value="<%=data.account.studentProfile.email == null ? "" : data.account.studentProfile.email %>" placeholder="Long-term contact email" />
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_INSTITUTION %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentInstitution">Institution</label>
                    <input id="studentInstitution" name="<%=Const.ParamsNames.STUDENT_PROFILE_INSTITUTION %>" class="form-control" type="text" data-actual-value="<%=data.account.studentProfile.institute == null ? "" : data.account.studentProfile.institute %>" value="<%=data.account.studentProfile.institute == null ? "" : data.account.studentProfile.institute %>" placeholder="Your Institution" />
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_COUNTRY%>" data-toggle="tooltip" data-placement="top">
                    <label for="studentCountry">Country</label>
                    <input id="studentCountry" name="<%=Const.ParamsNames.STUDENT_COUNTRY %>" class="form-control" type="text" data-actual-value="<%=data.account.studentProfile.country == null ? "" : data.account.studentProfile.country %>" value="<%=data.account.studentProfile.country == null ? "" : data.account.studentProfile.country %>" placeholder="Country" />
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
                            <%=data.account.studentProfile.gender.equals(Const.GenderTypes.OTHER) ? "checked=\"checked\"" : "" %> /> Undisclosed
                        </label>
                    </div>
    
                </div>
                <div class="form-group" title="<%=Const.Tooltips.STUDENT_PROFILE_MOREINFO %>" data-toggle="tooltip" data-placement="top">
                    <label for="studentCountry">More info about yourself</label>
                    <textarea id="studentMoreInfo"  name="<%=Const.ParamsNames.STUDENT_PROFILE_MOREINFO %>" 
                              rows="4" class="form-control"
                              placeholder="You may wish to specify miscellaneous information as well as links to external profiles."
                              ><%=data.account.studentProfile.moreInfo == null ? "" : data.account.studentProfile.moreInfo %></textarea>
                </div><br>
                <div class="form-group" title="Upload a close-up of your face " data-toggle="tooltip" data-placement="top">
                    <label for="studentPhoto">Your Photo</label>
                    <input id="studentPhoto" type="file" name="studentprofilephoto" />
                </div>
                <button type="submit" id="profileEditSubmit" class="btn btn-primary center-block">Save Profile</button>
                <br>
                <p class="text-muted text-color-disclaimer"> <i>* This profile will be visible to all your Instructors and Classmates by default</i></p>
            </form>
        </div>
    </div>
    <br><br><br>
    <jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
</body> 