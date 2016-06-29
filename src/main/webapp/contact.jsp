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
                            <li><a href="features.html">Features</a></li>
                            <li><a href="about.html">About Us</a></li>
                            <li class="current"><strong>Contact</strong></li>
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
                Contact Us
            </h1>
            <img src="images/contact.png" width="125px" style="padding-left:100px">
            <div id="contentHolder">
                <p><span class="bold">Email: </span> You can contact us at the following email address - <a href="mailto:teammates@comp.nus.edu.sg">teammates@comp.nus.edu.sg</a></p>
                <br>
                <p><span class="bold">Blog: </span>Visit the <a href="http://teammatesonline.blogspot.sg/">TEAMMATES Blog</a> to see our latest updates and information.</p>
                <br>
                <p><span class="bold">Bug reports and feature requests: </span>Any
                    Bug reports or Feature requests can be submitted to
                   above email address.</p>
                 <br>
                 <p><span class="bold">Interested in joining us?: </span>Visit our <a href="https://github.com/TEAMMATES/repo">Developer Website</a>.</p>
            </div>
        </div>
    </div>
</t:staticPage>
