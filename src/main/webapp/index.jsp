<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/index.js"></script>
</c:set>
<t:staticPage jsIncludes="${jsIncludes}" currentPage="index">
  <div id="browserMessage" style="display: none;"></div>
  <br>
  <main>
    <h1 class="h2 color-orange text-center">
      Student peer evaluations/feedback, shareable instructor comments, and more...
    </h1>
    <div class="row">
      <img class="center-block img-responsive" alt="Overview of TEAMMATES - anonymous peer feedback and confidential peer evaluations" src="images/overview.png">
    </div>
    <h2 class="color-orange row h4 text-center color-blue">
      <span id= "submissionsNumber" class="color-orange">5,000,000+</span> feedback entries submitted so far ...
    </h2>
    <div class="row">
      <div class="margin-bottom-10px col-xs-10 col-sm-5 col-xs-offset-1 col-md-4 col-md-offset-2 col-lg-3 col-lg-offset-3">
        <a class="btn btn-default btn-block" href="https://www.youtube.com/embed/mDtfmNmRwBM?autoplay=1&rel=0" target="_blank">
          <span class="glyphicon glyphicon-film" aria-hidden="true"></span> Video Tour
        </a>
      </div>
      <div class="col-xs-10 col-xs-offset-1 col-sm-5 col-sm-offset-0 col-md-4 col-lg-3">
        <a class="btn btn-success btn-block" href="request.jsp">Request a Free Instructor Account</a>
      </div>
    </div>
  </main>

  <img class="center-block img-responsive" id="raisedEdge" src="images/raised-edge.png">

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Praised by Users" src="images/overview_praise.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Praised by Users:</h2>
      <p id="testimonialContainer"></p>
    </div>
  </div>

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Award winning, mature, field-tested and improving all the time" src="images/award_winning.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Award Winning, Mature, Field-tested:</h2>
      <p>
        TEAMMATES has been in operation since 2010. It has benefitted from the work of <a href="about.jsp">hundreds of developers</a>.<br>
        TEAMMATES won the Grand Prize at the OSS Awards World Challenge 2014 and was selected as a mentoring organization for Google Summer of Code Program (2014, 2015, 2016, 2017) and Facebook Open Academy Program (2016).
      </p>
    </div>
  </div>

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Designed for simplicity, flexibility, power" src="images/overview_peerfeedback.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Designed for Simplicity, Flexibility, and Power:</h2>
      <p>
        TEAMMATES was designed by a team of teachers and students, for teachers and students.
        It aims to provide a powerful peer feedback and peer evaluations mechanism with a very high degree of flexibility.
        <a href="features.jsp">More about our features...</a>
      </p>
    </div>
  </div>

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Powered by Google Infrastructure" src="images/overview_google.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Powered by Google Infrastructure:</h2>
      <p>
        TEAMMATES runs on the <a href="https://cloud.google.com/products/" target="_blank" rel="noopener noreferrer">Google App Engine</a>,
        using cutting edge cloud technologies and benefiting from the same systems and infrastructure that power Google's applications.
      </p>
    </div>
  </div>

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Growing global community" src="images/overview_countries.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Growing Global Community:</h2>
      <p>
        TEAMMATES community is growing fast, spanning over 200,000 users from over 1,600 universities from many countries across the globe.
      </p>
      <p>
        <a href="usermap.jsp">See who is using TEAMMATES.</a>
      </p>
    </div>
  </div>

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Not for Profit" src="images/overview_funding.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Not for Profit:</h2>
      <p>
        TEAMMATES does not have commercial ambitions. It is funded mainly by education grants from the National University of Singapore.
        TEAMMATES also received funding support under the <a href="https://developers.google.com/open-source/gsoc/" target="_blank" rel="noopener noreferrer">Google Summer of Code</a> program in 2014, 2015, 2016, and 2017.
      </p>
    </div>
  </div>

  <div class="row">
    <div class="col-xs-12 col-sm-3">
      <img class="center-block img-responsive" alt="TEAMMATES - Well received by Academic community" src="images/overview_conference.png">
    </div>
    <div class="col-xs-12 col-sm-8">
      <h2 class="media-heading">Well Received by the Academic Community:</h2>
      <p>
        TEAMMATES has been presented, and well-received, in education-related conferences such as the
        <a href="http://conferences.computer.org/cseet/2011/CSEET_2011/Index.html" target="_blank" rel="noopener noreferrer">International Conference on Software Engineering Education and Training (CSEET)</a>,
        the <a href="http://www.cdtl.nus.edu.sg/Tlhe/tlhe2011/default.htm" target="_blank" rel="noopener noreferrer">International Conference on Teaching and Learning in Higher Education (TLHE)</a>,
        the <a href="https://iated.org/edulearn13/" target="_blank" rel="noopener noreferrer">EDULEARN13 Conference (Spain)</a>,
        and the <a href="http://cdtl.nus.edu.sg/tel2013/" target="_blank" rel="noopener noreferrer">Tel2013 Symposium (Singapore)</a>.
      </p>
    </div>
  </div>
</t:staticPage>
