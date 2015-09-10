<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<ti:studentList courseId="${data.courseId}" courseIndex="${data.courseIndex}" hasSection="${data.hasSection}"
                sections="${data.sections}" fromStudentListPage="${true}" />