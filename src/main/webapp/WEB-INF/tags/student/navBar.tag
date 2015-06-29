<%@ tag description="Student Navigation Bar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag import="teammates.common.util.Const" %>
<c:set var="isUnregistered" value="${data.unregisteredStudent}" />
<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#contentLinks">
                 <span class="sr-only">Toggle navigation</span>
                 <span class="icon-bar"></span>
                 <span class="icon-bar"></span>
                 <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/index.html">TEAMMATES</a>
        </div>
        <div class="collapse navbar-collapse" id="contentLinks">
            <ul class="nav navbar-nav">
                <li<c:if test="${fn:contains(data.class,'StudentHome')}"> class="active"</c:if>>
                    <a class="navLinks" id="studentHomeNavLink" href="${data.studentHomeLink}"
                       <c:if test="${isUnregistered}">data-unreg="true"</c:if>>
                        Home
                    </a>
                </li>
                <li<c:if test="${fn:contains(data.class,'StudentProfilePage')}"> class="active"</c:if>>
                    <a class="navLinks" id="studentProfileNavLink" href="${data.studentProfileLink}"
                       <c:if test="${isUnregistered}">data-unreg="true"</c:if>>
                        Profile
                    </a>
                </li>
                <li<c:if test="${fn:contains(data.class,'StudentComments')}"> class="active"</c:if>>
                    <a class="navLinks" id="studentCommentsNavLink" href="${data.studentCommentsLink}"
                       <c:if test="${isUnregistered}">data-unreg="true"</c:if>>
                        Comments
                    </a>
                </li>
                <li<c:if test="${fn:contains(data.class,'StudentHelp')}"> class="active"</c:if>>
                    <a id="studentHelpLink" class='nav' href="/studentHelp.html" target="_blank">Help</a>
                </li>
            </ul>
            <c:if test="${not empty data.account && not empty data.account.googleId}">
                <ul class="nav navbar-nav pull-right">
                    <li>
                        <a class='nav logout' href="<%= Const.ViewURIs.LOGOUT %>">Logout
                            (<span class="text-info" data-toggle="tooltip" data-placement="bottom" 
                                    title="${data.account.googleId}">
                                    ${data.account.truncatedGoogleId}
                            </span>)
                        </a>
                    </li>
                </ul>
            </c:if>
        </div>
    </div>
</div>