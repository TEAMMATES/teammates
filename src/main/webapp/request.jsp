<%@ page trimDirectiveWhitespaces="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<t:staticPage>
  <c:set var="googleDocURL" value="https://docs.google.com/forms/d/e/1FAIpQLSfmiNsVnVANdB1-cOwkfn9l8Ts8eN-CtolLQwi93Nrug0sngw/viewform?embedded=true&formkey=dDNsQmU4QXVYTVRhMjA2dEJWYW82Umc6MQ"></c:set>
  <h1 class="color-orange">
    Request for an Account
  </h1>
  <div id="contentHolder">
    <p> Cannot see the request form below? Click <a href="${googleDocURL}" target="_blank" rel="noopener noreferrer">here.</a></p>
    <iframe src="${googleDocURL}"
        width="760px" height="880px" frameborder="0" marginheight="0" marginwidth="0">
      Loading...
    </iframe>
  </div>
</t:staticPage>
