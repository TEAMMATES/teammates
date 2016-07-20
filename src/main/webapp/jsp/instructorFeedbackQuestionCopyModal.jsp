<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="table-responsive table table-hover table-bordered margin-0" id="copyTableModal">
    <thead class="fill-primary">
        <tr>
            <th style="width:30px;">&nbsp;</th>
            <th onclick="toggleSort(this);" id="button_sortid" class="button-sort-ascending" style="width:100px"> 
                Course ID <span class="icon-sort sorted-ascending"></span>
            </th>
            <th onclick="toggleSort(this);" id="button_sortfsname" class="button-sort-none" style="width:17%;">
                Session Name <span class="icon-sort unsorted"></span>
            </th>
            <th onclick="toggleSort(this);" id="button_sortfqtype" class="button-sort-none"> 
                Question Type <span class="icon-sort unsorted"></span>
            </th>
            <th onclick="toggleSort(this);" id="button_sortfqtext" class="button-sort-none"> 
                Question Text <span class="icon-sort unsorted"></span>
            </th>
        </tr>
    </thead>
    <c:forEach items="${data.copyQnForm.questionRows}" var="row">
        <tr style="cursor:pointer;">
            <td><input type="checkbox"></td>
            <td>${row.courseId}</td>
            <td>${row.fsName}</td>
            <td>${row.qnType}</td>
            <td>${row.qnText}</td>
            <input type="hidden" value="${row.qnId}">
            <input type="hidden" class="courseid" value="${row.courseId}">
            <input type="hidden" class="fsname" value="${row.fsName}">
        </tr>
    </c:forEach>
</table>