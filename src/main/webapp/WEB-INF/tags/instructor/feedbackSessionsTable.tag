<%@ tag description="instructorFeedbacks - feedback sessions table/list" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="teammates.common.util.Const" %>
<%@ tag import="teammates.common.util.FieldValidator" %>
<%@ tag import="teammates.logic.core.Emails.EmailType" %>

<%@ attribute name="fsList" type="teammates.ui.template.FeedbackSessionsList" required="true"%>

<table class="table-responsive table table-striped table-bordered" id="table-sessions">
    <thead>
        <tr class="fill-primary">
            <th id="button_sortid" onclick="toggleSort(this,1);"
                class="button-sort-ascending">Course ID <span
                class="icon-sort unsorted"></span>
            </th>
            <th id="button_sortname" onclick="toggleSort(this,2)"
                class="button-sort-none">
                    Session Name <span class="icon-sort unsorted"></span>
            </th>
            <th>Status</th>
            <th>
                <span title="<%= Const.Tooltips.FEEDBACK_SESSION_RESPONSE_RATE %>"
                    data-toggle="tooltip" data-placement="top">
                    Response Rate
                </span>
            </th>
            <th class="no-print">Action(s)</th>
        </tr>
    </thead>
        <c:choose>
            <c:when test="${not empty fsList.existingFeedbackSessions}">
                <c:forEach items="${fsList.existingFeedbackSessions}" var="sessionRow" varStatus="i">
                     <tr id="session${i.index}"
                     <c:forEach items="${newForm.coursesSelectField}" var="option">
                        <c:forEach items="${option.attributes}" var="attr"
                        > ${attr.key}="${attr.value}"</c:forEach> >${option.content}
                     </c:forEach> 
                     
                        <td>
                            ${sessionRow.name}
                        </td>
                        <td>
                            <span title="${sessionRow.tooltip}" data-toggle="tooltip" data-placement="top">
                                ${sessionRow.status}
                            </span>
                        </td>
                        <td class="session-response-for-test${sessionRow.recent}">
                            <a oncontextmenu="return false;" href="${sessionRow.href}">Show</a>
                        </td>
                        <td class="no-print">
                            ${sessionRow.actions}
                        </td>
                      </tr>
        
                </c:forEach> 
            </c:when>
            <c:when test="${empty fsList.existingFeedbackSessions}">    
            <tr>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
            </c:when>
        </c:choose>
    </table>
    <p class="col-md-12 text-muted">Note: The table above doesn't contain sessions from archived courses. To view sessions from an archived course, unarchive the course first.</p>
    <br><br><br>
    
    <c:if test="${empty data.fsList.existingFeedbackSessions}">
        <div class="align-center">No records found.</div>
        <br><br><br>
    </c:if>
    
    