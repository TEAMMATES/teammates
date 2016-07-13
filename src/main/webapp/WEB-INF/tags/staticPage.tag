<%@ tag description="Generic TEAMMATES Static Page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="jsIncludes" %>
<%@ attribute name="currentPage" %>
<!DOCTYPE html>
<head>
    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type">
    <meta name="application-name" content="TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects">
    <meta name="keywords" content="Teammates, peer feedback, peer evaluation, student, team, project, free">
    <meta name="description" content="TEAMMATES is an Online Peer Feedback System for student team projects. It is completely free to use. Get your students to evaluate their performance in team projects, and view reports and summaries of their feedback and evaluations.">
    <link rel="stylesheet" href="stylesheets/teammates.css" type="text/css">
    <link rel="apple-touch-icon" href="apple-touch-icon.png">
    <title>TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects</title>
    <script type="text/javascript" src="/js/googleAnalytics.js"></script>
    ${jsIncludes}
</head>
<body>
    <div id="mainContainer">
        <div id="header">
            <div id="headerWrapper">
                <div id="imageHolder">
                    <a href="/index.jsp">
                        <img alt="TEAMMATES[Logo] - Online Peer Feedback/Evaluation System for Student Team Projects"
                             src="images/teammateslogo.jpg" width="150px" height="47px">
                    </a>
                </div>
                <div id="menuHolder">
                    <div id="textHolder">
                        <ul id="navbar">
                            <c:choose>
                                <c:when test="${currentPage == 'index'}">
                                    <li class="current"><strong>Home</strong></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="index.jsp">Home</a></li>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${currentPage == 'features'}">
                                    <li class="current"><strong>Features</strong></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="features.jsp">Features</a></li>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${currentPage == 'about'}">
                                    <li class="current"><strong>About Us</strong></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="about.jsp">About Us</a></li>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${currentPage == 'contact'}">
                                    <li class="current"><strong>Contact</strong></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="contact.jsp">Contact</a></li>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${currentPage == 'terms'}">
                                    <li class="current"><strong>Terms of Use</strong></li>
                                </c:when>
                                <c:otherwise>
                                    <li><a href="terms.jsp">Terms of Use</a></li>
                                </c:otherwise>
                            </c:choose>
                        </ul>
                    </div>
                    <div id="loginHolder">
                        <form action="/login" style="float: left;" name="studentLogin">
                            <input type="submit" name="student" class="button" id="btnStudentLogin" value="Student Login">
                        </form>
                        <form action="/login" style="float: left;" name="instructorLogin">
                            <input type="submit" name="instructor" class="button" id="btnInstructorLogin" value="Instructor Login">
                        </form>
                    </div>
                    <div style="clear: both;"></div>
                </div>
                <div style="clear: both;"></div>
            </div>
        </div>
        <div id="mainContent">
            <jsp:doBody />
        </div>
    </div>
    <div id="footer">
        <div id="footerWrapper">
            <div id="footerRightCol">
                Hosted on <a class="footer" href="http://code.google.com/appengine/" target="_blank">Google App Engine</a>
            </div>
        </div>
    </div>
</body>
</html>
