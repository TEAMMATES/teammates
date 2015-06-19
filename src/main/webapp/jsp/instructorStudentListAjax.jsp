<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor/studentList" prefix="tisl" %>
<tisl:ajaxResult courseId="${data.courseId}" courseIndex="${data.courseIndex}" hasSection="${data.hasSection}" sections="${data.sections}" />