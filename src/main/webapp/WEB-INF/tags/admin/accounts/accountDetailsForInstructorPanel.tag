<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="Instructor Account Details" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="accountInformation" type="teammates.common.datatransfer.attributes.AccountAttributes" required="true" %>

<div class="well well-plain">
  <form class="form-horizontal" role="form">
    <div class="panel-heading">
      <div class="form-group">
        <label class="col-sm-2 control-label">Google ID:</label>
        <div class="col-sm-10">
          <p class="form-control-static">${accountInformation.googleId}</p>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Name:</label>
        <div class="col-sm-10">
          <p class="form-control-static">${accountInformation.name}</p>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Email:</label>
        <div class="col-sm-10">
          <p class="form-control-static">${fn:escapeXml(accountInformation.email)}</p>
        </div>
      </div>

      <div class="form-group">
        <label class="col-sm-2 control-label">Institute:</label>
        <div class="col-sm-10">
          <p class="form-control-static">${accountInformation.institute}</p>
        </div>
      </div>
    </div>
  </form>
</div>
