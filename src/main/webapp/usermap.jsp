<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/lib/d3.min.js"></script>
    <script type="text/javascript" src="/js/lib/topojson.min.js"></script>
    <script type="text/javascript" src="/js/lib/datamaps.none.min.js"></script>
    <script type="text/javascript" src="/js/countryCodes.js"></script>
    <script type="text/javascript" src="/js/userMap.js"></script>
</c:set>
<t:staticPage jsIncludes="${jsIncludes}">
    <div id="mainContainer">
        <div id="header">
            <div id="headerWrapper">
                <div id="imageHolder">
                    <a href="/index.html">
                        <img alt="TEAMMATES[Logo] - Online Peer Feedback/Evaluation System for Student Team Projects" 
                            src="images/teammateslogo.jpg" width="150px" height="47px">
                    </a>
                </div>
                <div id="menuHolder">
                    <div id="textHolder">
                        <ul id="navbar">
                            <li><a href="index.html">Home</a></li>
                            <li><a href="features.html">Features</a></li>
                            <li><a href="about.html">About Us</a></li>
                            <li><a href="contact.html">Contact</a></li>
                            <li><a href="terms.html">Terms of Use</a></li>
                        </ul>
                    </div>
                    <div id="loginHolder">
                        <form action="/login" style="float:left" name="studentLogin">
                            <input type="submit" name="student" class="button" id="btnStudentLogin" value="Student Login">
                        </form>
                        <form action="/login" name="instructorLogin" style="float:left">
                            <input type="submit" name="instructor" class="button" id="btnInstructorLogin" value=" Instructor Login">
                        </form>
                    </div>
                    <div style="clear: both;"></div>
                </div>
                <div style="clear: both;"></div>
            </div>
        </div>

        <div id="mainContent">
            <h1 id="caption">Who is using TEAMMATES?</h1>
            <div id="contentHolder">
                <div id="container" style="position: relative; width: 800px; height: 500px; border: 1px solid #DEDEDE"></div>
                <p id="lastUpdate" class="lastUpdate">Last updated: 30 March 2016</p>
                <h2 class="subcaption align-center">
                    <span id="totalUserCount" class="totalCount"></span> 
                    institutions from 
                    <span id="totalCountryCount" class="totalCount"></span> 
                    countries
                </h2>
            </div>
        </div>
    </div>
</t:staticPage>
