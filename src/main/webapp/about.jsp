<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="/js/about.js"></script>
</c:set>
<t:staticPage jsIncludes="${jsIncludes}" currentPage="about">
  <div class="container">
    <section class="row">
      <h1 class="color-orange">About Us</h1>
      <p class="h3">Acknowledgements:</p>
      <p>
        TEAMMATES team wishes to thank the following invaluable contributions:
      </p>
      <ul>
        <li>
          <a href="http://www.comp.nus.edu.sg/" target="_blank" rel="noopener noreferrer"><b>School of Computing, National University of Singapore (NUS)</b></a>, for providing us with the infrastructure support to run the project.
        </li>
        <li>
          <a href="http://www.cdtl.nus.edu.sg/" target="_blank" rel="noopener noreferrer"><b>Centre for Development of Teaching and Learning (CDTL)</b></a> of NUS, for supporting us with several Teaching Enhancement Grants over the years.
        </li>
        <li>
          <b>Learning Innovation Fund-Technology (LIF-T)</b> initiative of NUS, for funding us for the 2015-2018 period.
        </li>
        <li>
          <b>Google Summer of Code</b> Program, for including TEAMMATES as a mentor organization in GSoC2014, GSoC2015, GSoC2016, GSoC2017 and GSoC2018 editions.
        </li>
        <li>
          <b>Facebook Open Academy</b> Program, for including TEAMMATES as a mentor organization in FBOA 2016.
        </li>
        <li>
          <b>Jet Brains</b>, for the <a href="https://www.jetbrains.com/idea/" target="_blank" rel="noopener noreferrer">Intellij IDEA</a> licences.
        </li>
        <li>
          <b>YourKit LLC</b>, for providing us with free licenses for the <a href="https://www.yourkit.com/java/profiler/" target="_blank" rel="noopener noreferrer">YourKit Java Profiler</a>
          <img src="https://www.yourkit.com/images/yklogo.png" width='70'>.
        </li>
      </ul>
      <p>
        TEAMMATES has benefitted from the work of <span id="contributors-count"></span> developers.
      </p>
    </section>

    <section class="row">
      <h3>Core Team:</h3>
      <div id="teammembers-current"></div>
    </section>

    <section class="row">
      <h3>Committers:</h3>
      <ol id="committers-current"></ol>
    </section>

    <section class="row">
      <h3>Past Team Members:</h3>
      <ol id="teammembers-past"></ol>
    </section>

    <section class="row">
      <h3>Past Committers:</h3>
      <ol id="committers-past"></ol>
    </section>

    <section class="row">
      <h3>Contributors</h3>
      <h4>Major contributions:</h4>
      <ol id="contributors-major"></ol>
      <h4>Multiple contributions:</h4>
      <ol id="contributors-multiple"></ol>
      <h4>One-time contributions:</h4>
      <ol id="contributors-single"></ol>
    </section>
    <p>
      Would you like to join the TEAMMATES team as a contributor? <a href="contact.jsp">Contact Us</a>.
    </p>
  </div>
</t:staticPage>
