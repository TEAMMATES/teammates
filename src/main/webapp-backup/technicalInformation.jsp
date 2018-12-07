<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:helpPage>
  <h1>Additional Technical Information</h1>
  <a name="#top"></a>
  <div id="contentHolder">
    <ul>
      <li>
        <a href="#calculatePointsContribution">Point Calculation For Contribution Questions</a>
      </li>
      <li>
        <a href="#calculateRanks">Rank Calculation For Rank Questions</a>
      </li>
    </ul>
  </div>
  <div class="separate-content-holder">
    <hr>
  </div>
  <jsp:include page="partials/technicalInformationCalculatePointsContribution.jsp"/>
  <jsp:include page="partials/technicalInformationCalculateRanks.jsp"/>
</t:helpPage>
