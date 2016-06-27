<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/shared/feedbackSubmissionEdit" prefix="tsfse" %>
<tsfse:feedbackSubmissionEdit isInstructor="${false}" moderatedPersonEmail="${data.studentToViewPageAs.email}"
                              moderatedPersonName="${data.studentToViewPageAs.name}" />
