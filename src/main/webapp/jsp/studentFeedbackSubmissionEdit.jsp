<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="tsfse" %>
<tsfse:feedbackSubmissionEdit isInstructor="${false}" moderatedPersonEmail="${data.studentToViewPageAs.email}"
                              moderatedPersonName="${fn:escapeXml(data.studentToViewPageAs.name)}" />
