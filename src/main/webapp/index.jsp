<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/checkBrowserVersion.js"></script>
    <script type="text/javascript" src="/js/index.js"></script>
</c:set>
<t:staticPage jsIncludes="${jsIncludes}" currentPage="index">
    <div id="browserMessage" style="display: none;"></div>
    <br>
    <h1 class="caption" style="text-align: center;">
        Student peer evaluations/feedback, shareable instructor comments, and more...
    </h1>
    <div id="contentHolder">                  
        <img alt="Overview of TEAMMATES - anonymous peer feedback and confidential peer evaluations" src="images/overview.png" width="750px" align="right">
        <div class="align-center">
            <br>
            <table width="100%">
                <tr>
                    <td width="140px"></td>
                    <td style="vertical-align: middle; text-align: left;">
                        <h2 class="subcaption">
                            <span id= "submissionsNumber" class="submissionsNumber">5,000,000+</span> feedback entries submitted so far ...
                        </h2>
                    </td>
                    <td style="text-align: right;">
                        <a href="https://youtube.googleapis.com/v/mDtfmNmRwBM&hd=1&autoplay=1&rel=0" target="_blank"><img src="images/videoTour.png" height="40px"></a>
                        &nbsp;&nbsp;
                        <a href="request.jsp"><img src="images/requestButton.png" height="40px"></a>
                    </td>
                </tr>
            </table>
        </div>
        <img src="images/raised-edge.png" width="900px" height="30px">
        <br>
        
        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Praised by Users" src="images/overview_praise.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Praised by Users:</h2>
                <p id="testimonialContainer">
                    Thank you for building such a wonderful tool. 
                    <br>-Faculty user, Canada
                </p>
            </div>
            <div style="clear: both;"></div>
        </div>
        
        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Award winning, mature, field-tested and improving all the time" src="images/award_winning.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Award Winning, Mature, Field-tested:</h2>
                <p>
                    TEAMMATES has been in operation since 2010. It has benefited from the work of over <a href="about.jsp">190 developers</a>.<br>
                    TEAMMATES won the Grand Prize at the OSS Awards World Challenge 2014 and was selected as a mentoring organization for Google Summer of Code Program (2014, 2015, 2016) and Facebook Open Academy Program (2016).
                </p>
            </div>
            <div style="clear: both;"></div>
        </div>

        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Designed for simplicity, flexibility, power" src="images/overview_peerfeedback.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Designed for Simplicity, Flexibility, and Power:</h2>
                <p>
                    TEAMMATES was designed by a team of teachers and students, for teachers and students. 
                    It aims to provide a powerful peer feedback and peer evaluations mechanism with a very high degree of flexibility.
                    <a href="features.jsp">More about our features...</a>
                </p>
            </div>
            <div style="clear: both;"></div>
        </div>
        
        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Powered by Google Infrastructure" src="images/overview_google.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Powered by Google Infrastructure:</h2>
                <p>
                    TEAMMATES runs on the <a href="https://cloud.google.com/products/" target="blank">Google App Engine</a>,
                    using cutting edge cloud technologies and benefiting from the same systems and infrastructure that power Google's applications.
                </p>
            </div>
            <div style="clear: both;"></div>
        </div>

        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Growing global community" src="images/overview_countries.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Growing Global Community:</h2>
                <p>
                    TEAMMATES community is growing fast, spanning over 1000 universities from many countries across the globe:
                    Singapore, Canada, USA, UK, Turkey, Australia, Malaysia, Belgium, Taiwan, Macau, Sri Lanka, India, China, Vietnam, and more ...
                </p>
                <p><a href="usermap.jsp">See who is using TEAMMATES.</a></p>
            </div>
            <div style="clear: both;"></div>
        </div>

        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Not for Profit" src="images/overview_funding.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Not for Profit:</h2>
                <p>
                    TEAMMATES does not have commercial ambitions. It is funded mainly by education grants from the National University of Singapore.
                    TEAMMATES also received funding support under the <a href="https://developers.google.com/open-source/gsoc/" target="_blank"> Google Summer of Code</a> program in 2014 and 2015.
                </p>
            </div>
            <div style="clear: both;"></div>
        </div>

        <div class="overviewByte">
            <div class="overviewBytePicture">
                <img alt="TEAMMATES - Well received by Academic community" src="images/overview_conference.png" width="175px">
            </div>
            <div class="overviewByteData">
                <h2 class="overviewByteHeading">Well Received by the Academic Community:</h2>
                <p>
                    TEAMMATES has been presented, and well-received, in education-related conferences such as the 
                    <a href="http://conferences.computer.org/cseet/2011/CSEET_2011/Index.html" target="blank">International Conference on Software Engineering Education and Training (CSEET)</a>,
                    the <a href="http://www.cdtl.nus.edu.sg/Tlhe/tlhe2011/default.htm" target="blank">International Conference on Teaching and Learning in Higher Education (TLHE)</a>,
                    the <a href="https://iated.org/edulearn13/" target="blank">EDULEARN13 Conference (Spain)</a>,
                    and the <a href="http://cdtl.nus.edu.sg/tel2013/" target="blank">Tel2013 Symposium (Singapore)</a>.
                </p>
            </div>
            <div style="clear: both;"></div>
        </div>
    </div>
</t:staticPage>
