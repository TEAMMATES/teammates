<%@ tag description="adminEmailLog.jsp - email log table" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="logs" type="java.util.Collection" required="true" %>
<%@ attribute name="shouldShowAll" type="java.lang.Boolean" required="true" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <strong>Email Log</strong>
    </div>
    
    <div class="table-responsive">  
        <table class="table dataTable" id="emailLogsTable">
            <thead>
                <tr>
                    <td><strong>Receiver</strong></td>
                    <td><strong>Subject</strong></td>
                    <td><strong>Date</strong></td>     
                </tr>
            </thead>
            
            <tbody>
                <c:forEach items="${logs}" var="log">
                    <c:if test="${shouldShowAll or (not fn:endsWith(log.receiver, '.tmt'))}">
                        <tr class="log">
                            <td>${log.receiver}</td>
                            <td>${log.subject}</td>
                            <td>${log.timeForDisplay}</td>
                        </tr>
                        
                        <tr id="small">
                            <td colspan="3">
                                <ul class="list-group">
                                    <li class="list-group-item list-group-item-info">
                                        <input type="text" value="${log.content}" class="form-control"
                                               readonly="readonly">
                                    </li>
                                </ul>    
                            </td>
                        </tr>
                        
                        <tr id="big" style="display:none;">
                          <td colspan="3">
                            <div class="well well-sm">
                                <ul class="list-group">
                                    <li class="list-group-item list-group-item-success emailLog-text">
                                        <small>${log.unsanitizedContent}</small>
                                    </li>
                                </ul>
                            </div>
                           </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </tbody>
        </table>  
    </div>
</div>