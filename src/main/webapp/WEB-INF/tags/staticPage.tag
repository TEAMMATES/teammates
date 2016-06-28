<%@ tag description="Generic TEAMMATES Static Page" %>
<%@ attribute name="jsIncludes" %>
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
    <jsp:doBody />
    <div id="footer">
        <div id="footerWrapper">
            <div id="footerRightCol">
                Hosted on <a class="footer" href="http://code.google.com/appengine/" target="_blank">Google App Engine</a>
            </div>
        </div>
    </div>
</body>
</html>
