<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:staticPage>
    <div id="mainContainer">
        <div id="header">
            <div id="headerWrapper">
                <div id="imageHolder">
                        <a href="/index.html">
                    <img alt="TEAMMATES[Logo] - Online Peer Feedback/Evaluation System for Student Team Projects" src="images/teammateslogo.jpg" width="150px" height="47px">
                    </a>
                </div>
                <div id="menuHolder">
                    <div id="textHolder">
                        <ul id="navbar">
                            <li><a href="index.html">Home</a></li>
                            <li id="current"><a href="features.html">Features</a></li>
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
            <h1 id="caption">
                Request for an Account
            </h1>
            
            <div id="contentHolder">
               <iframe src="https://spreadsheets.google.com/embeddedform?formkey=dDNsQmU4QXVYTVRhMjA2dEJWYW82Umc6MQ" width="760px" height="880px" frameborder="0" marginheight="0" marginwidth="0">Loading...</iframe>
            </div>  
        </div>
    </div>
</t:staticPage>
