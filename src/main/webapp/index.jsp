<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/static" prefix="ts" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="teammates.common.util.Const" %>
<c:set var="jsIncludes">
    <script type="text/javascript" src="/js/checkBrowserVersion.js"></script>
    <script type="text/javascript" src="/js/index.js"></script>
</c:set>
<c:set var="aboutPage" value="<%= Const.ViewURIs.ABOUT %>" />
<c:set var="featuresPage" value="<%= Const.ViewURIs.FEATURES %>" />
<c:set var="usermap" value="<%= Const.ViewURIs.USERMAP %>" />
<t:staticPage jsIncludes="${jsIncludes}" currentPage="index">
    <div id="browserMessage" style="display: none;"></div>
    <br>
    <h1 id="caption" style="text-align: center;">
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
                            <span id= "submissionsNumber" class="submissionsNumber">3,000,000+</span> feedback entries submitted so far ...
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
        
        <c:set var="praisedByUsers">
            <p id="testimonialContainer">
                Thank you for building such a wonderful tool.
                <br>-Faculty user, Canada
            </p>
        </c:set>
        <ts:overviewByte caption="Praised by Users" imgSuffix="praise" desc="${praisedByUsers}" />
        
        <c:set var="awardWinning">
            <p>
                TEAMMATES has been in operation since 2010. It has benefited from the work of over <a href="${aboutPage}">130 developers</a>.<br>
                TEAMMATES won the Grand Prize at the OSS Awards World Challenge 2014 and was selected for the Google Summer of Code programs in 2014 and 2015.
            </p>
        </c:set>
        <ts:overviewByte caption="Award-winning, Mature, Field-tested, and Improving All the Time" imgSuffix="awardwinning" desc="${awardWinning}" />

        <c:set var="simplicity">
            <p>
                TEAMMATES was designed by a team of teachers and students, for teachers and students.
                It aims to provide a powerful peer feedback and peer evaluations mechanism with a very high degree of flexibility.
                <a href="${featuresPage}">More about our features...</a>
            </p>
        </c:set>
        <ts:overviewByte caption="Designed for Simplicity, Flexibility, and Power" imgSuffix="peerfeedback" desc="${simplicity}" />
        
        <c:set var="googleInfrastructure">
            <p>
                TEAMMATES runs on the <a href="https://cloud.google.com/products/" target="blank">Google App Engine</a>,
                using cutting edge cloud technologies and benefiting from the same systems and infrastructure that power Google's applications.
            </p>
        </c:set>
        <ts:overviewByte caption="Powered by Google Infrastructure" imgSuffix="google" desc="${googleInfrastructure}" />

        <c:set var="globalCommunity">
            <p>
                TEAMMATES community is growing fast, spanning over 700 universities from many countries across the globe:
                Singapore, Canada, USA, UK, Turkey, Australia, Malaysia, Belgium, Taiwan, Macau, Sri Lanka, India, China, Vietnam, and more ...
            </p>
            <p><a href="${usermap}">See who is using TEAMMATES.</a></p>
        </c:set>
        <ts:overviewByte caption="Growing Global Community" imgSuffix="countries" desc="${globalCommunity}" />

        <c:set var="notForProfit">
            <p>
                TEAMMATES does not have commercial ambitions. It is funded mainly by education grants from the National University of Singapore.
                TEAMMATES also received funding support under the <a href="https://www.google-melange.com/"> Google Summer of Code</a> program in 2014 and 2015.
            </p>
        </c:set>
        <ts:overviewByte caption="Not for Profit" imgSuffix="funding" desc="${notForProfit}" />

        <c:set var="academicCommunity">
            <p>
                TEAMMATES has been presented, and well-received, in education-related conferences such as the
                <a href="http://conferences.computer.org/cseet/2011/CSEET_2011/Index.jsp" target="blank">International Conference on Software Engineering Education and Training (CSEET)</a>,
                the <a href="http://www.cdtl.nus.edu.sg/tlhe/" target="blank">International Conference on Teaching and Learning in Higher Education (TLHE)</a>,
                the <a href="http://iated.org/edulearn13/" target="blank">EDULEARN13 Conference (Spain)</a>,
                and the <a href="http://cdtl.nus.edu.sg/tel2013/" target="blank">Tel2013 Symposium (Singapore)</a>.
            </p>
        </c:set>
        <ts:overviewByte caption="Well-received by the Academic Community" imgSuffix="conference" desc="${academicCommunity}" />
    </div>
</t:staticPage>