<%@ tag description="instructorSearch.jsp - Search comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<br>
<div class="panel panel-primary">
    <div class="panel-heading">
        <strong><jsp:doBody/></strong>
    </div>
                
    <div class="panel-body">
        <c:set var="commentIdx" value="${0}"/>
        
        <c:forEach items="${data.searchCommentsForStudentsTables}" var="searchCommentsForStudentsTable">     
            <div class="panel panel-info student-record-comments">
                <div class="panel-heading">
                    From <b>${searchCommentsForStudentsTable.giverDetails}</b>
                </div>
                
                <ul class="list-group comments"> 
                    <c:forEach items="${searchCommentsForStudentsTable.rows}" var="commentRow">
                        <c:set var="commentIdx" value="${commentIdx + 1}" />
                        
                        <li class="list-group-item list-group-item-warning form_comment" 
                            id="form_commentedit-${commentIdx}>">
                                    
                            <div id="commentBar-${commentIdx}">
                                <span class="text-muted">
                                    To <b>${commentRow.recipientDetails}</b> on ${commentRow.creationTime}
                                </span>
                                            
                                <a type="button" target="_blank" class="btn btn-default btn-xs icon-button pull-right"
                                    data-toggle="tooltip" data-placement="top" style="display:none;"
                                    <c:forEach items="${commentRow.editButton.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach>>
                                    <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>                                       
                                </a>                                    
                            </div>
                            <div id="plainCommentText${commentIdx}">${commentRow.comment.commentText}</div>                                   
                        </li>                      
                    </c:forEach>                            
                </ul>
                
            </div>                                        
        </c:forEach>                  
    </div>
</div>