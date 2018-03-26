<%@ tag trimDirectiveWhitespaces="true" %>
<%@ tag description="studentProfile - Edit profile div" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ attribute name="profile" type="teammates.ui.template.StudentProfileEditBox" required="true" %>
<%@ attribute name="sessionToken" required="true" %>
<c:set var="MALE" value="<%= Const.GenderTypes.MALE %>" />
<c:set var="FEMALE" value="<%= Const.GenderTypes.FEMALE %>" />
<c:set var="OTHER" value="<%= Const.GenderTypes.OTHER %>" />
<div id="editProfileDiv" class="well well-plain well-narrow well-sm-wide">
  <h3 id="studentName">
    <strong>${profile.name}</strong>
  </h3>
  <br>
  <div class="form-group row">
    <div class="col-xs-6 col-sm-5 col-md-3 cursor-pointer"
        title="<%= Const.Tooltips.STUDENT_PROFILE_PICTURE %>"
        data-toggle="tooltip"
        data-placement="top">
      <img id="profilePic"
          src="${profile.pictureUrl}"
          class="profile-pic"
          data-toggle="modal"
          data-target="#studentPhotoUploader"
          data-edit="${profile.editingPhoto}">
    </div>
    <div class="">
      <button id="uploadEditPhoto"
          class="btn btn-primary"
          type="button"
          data-toggle="modal"
          data-target="#studentPhotoUploader">
        Upload/Edit Photo
      </button>
    </div>
  </div>
  <form class="form center-block"
      role="form"
      method="post"
      action="<%= Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE %>">
    <div class="form-group"
        title="<%= Const.Tooltips.STUDENT_PROFILE_SHORTNAME %>"
        data-toggle="tooltip"
        data-placement="top">
      <label for="studentNickname">
        The name you prefer to be called by Instructors
      </label>
      <input id="studentShortname"
          name="<%= Const.ParamsNames.STUDENT_SHORT_NAME %>"
          class="form-control"
          type="text"
          data-actual-value="<c:out value="${profile.shortName}"/>"
          value="<c:out value="${profile.shortName}"/>"
          placeholder="How the instructor should call you">
    </div>
    <div class="form-group"
        title="<%= Const.Tooltips.STUDENT_PROFILE_EMAIL %>"
        data-toggle="tooltip"
        data-placement="top">
      <label for="studentEmail">
        Long term contact email <em class="font-weight-normal emphasis text-muted small">- only visible to your instructors</em>
      </label>
      <input id="studentEmail"
          name="<%= Const.ParamsNames.STUDENT_PROFILE_EMAIL %>"
          class="form-control"
          type="email"
          data-actual-value="<c:out value="${profile.email}"/>"
          value="<c:out value="${profile.email}"/>"
          placeholder="Contact Email (for your instructors to contact you beyond graduation)">
    </div>
    <div class="form-group"
        title="<%= Const.Tooltips.STUDENT_PROFILE_INSTITUTION %>"
        data-toggle="tooltip"
        data-placement="top">
      <label for="studentInstitution">
        Institution
      </label>
      <input id="studentInstitution"
          name="<%= Const.ParamsNames.STUDENT_PROFILE_INSTITUTION %>"
          class="form-control"
          type="text"
          data-actual-value="<c:out value="${profile.institute}"/>"
          value="<c:out value="${profile.institute}"/>"
          placeholder="Your Institution">
    </div>
    <div class="form-group"
        title="<%= Const.Tooltips.STUDENT_PROFILE_NATIONALITY %>"
        data-toggle="tooltip"
        data-placement="top">
      <label for="studentNationality">
        Nationality
      </label>
      <select id="studentNationality"
          name="<%=Const.ParamsNames.STUDENT_NATIONALITY%>"
          class="form-control" style="width: 300px">
        <c:forEach items="${profile.nationalitySelectField}" var="option">
          <option ${option.attributesToString}>
            ${option.content}
          </option>
        </c:forEach>
      </select>
      <input type="hidden" name="existingNationality" value="${profile.nationality}">
      <c:if test="${not empty profile.legacyNationalityInstructions}">
        <div class="text-color-red">
          ${profile.legacyNationalityInstructions}
        </div>
      </c:if>
    </div>
    <div class="form-group">
      <label for="studentGender">
        Gender
      </label>
      <div id="studentGender">
        <label for="genderMale" class="radio-inline">
          <input id="genderMale"
              name="<%= Const.ParamsNames.STUDENT_GENDER %>"
              class="radio"
              type="radio"
              value="<%= Const.GenderTypes.MALE %>"
              <c:if test="${profile.gender == MALE}">checked=""</c:if>> Male
        </label>
        <label for="genderFemale" class="radio-inline">
          <input id="genderFemale"
              name="<%= Const.ParamsNames.STUDENT_GENDER %>"
              class="radio"
              type="radio"
              value="<%= Const.GenderTypes.FEMALE %>"
              <c:if test="${profile.gender == FEMALE}">checked=""</c:if>> Female
        </label>
        <label class="radio-inline" for="genderOther">
          <input id="genderOther"
              name="<%= Const.ParamsNames.STUDENT_GENDER %>"
              class="radio"
              type="radio"
              value="<%= Const.GenderTypes.OTHER %>"
              <c:if test="${profile.gender == OTHER}">checked=""</c:if>> Not Specified
        </label>
      </div>
    </div>
    <div class="form-group"
        title="<%= Const.Tooltips.STUDENT_PROFILE_MOREINFO %>"
        data-toggle="tooltip"
        data-placement="top">
      <label for="studentNationality">
        More info about yourself
      </label>
      <%-- Do not add whitespace between the opening and closing tags --%>
      <textarea id="studentMoreInfo"
          name="<%= Const.ParamsNames.STUDENT_PROFILE_MOREINFO %>"
          rows="4"
          class="form-control"
          placeholder="<%= Const.Tooltips.STUDENT_PROFILE_MOREINFO %>">${profile.moreInfo}</textarea>
    </div>
    <br>
    <button type="submit" id="profileEditSubmit" class="btn btn-primary center-block">
      Save Profile
    </button>
    <br>
    <p class="text-muted text-color-disclaimer">
      <i>* This profile will be visible to all your Instructors and Coursemates</i>
    </p>
    <input type="hidden" name="<%= Const.ParamsNames.USER_ID %>" value="${profile.googleId}">
    <input type="hidden" name="<%= Const.ParamsNames.SESSION_TOKEN %>" value="${sessionToken}">
  </form>
</div>
