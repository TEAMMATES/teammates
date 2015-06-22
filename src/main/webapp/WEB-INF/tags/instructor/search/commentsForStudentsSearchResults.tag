<%@ tag description="instructorSearch.jsp - Search comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="commentsForStudentsTables" type="java.util.Collection" required="true" %>

<div class="panel panel-primary">
    <div class="panel-heading">
        <strong><jsp:doBody/></strong>
    </div>
                
    <div class="panel-body">
        <c:set var="indexCounter" value="${0}"/>
        
        <c:forEach items="${commentsForStudentsTables}" var="commentsForStudentsTable">     
            <div class="panel panel-info student-record-comments">
                <div class="panel-heading">
                    From <b>${commentsForStudentsTable.giverDetails}</b>
                </div>
                
                <ul class="list-group comments"> 
                    <c:forEach items="${commentsForStudentsTable.rows}" var="commentRow">
                        <c:set var="indexCounter" value="${indexCounter + 1}" />
                        
                        <li class="list-group-item list-group-item-warning" 
                            id="form_commentedit-${indexCounter}">
                                    
                            <div id="commentBar-${indexCounter}">
                                <span class="text-muted">
                                    To <b>${commentRow.recipientDetails}</b> on ${commentRow.creationTime}
                                </span>
                                            
                                <a type="button" target="_blank" class="btn btn-default btn-xs icon-button pull-right"
                                    data-toggle="tooltip" data-placement="top" style="display:none;"
                                    <c:forEach items="${commentRow.editButton.attributes}" var="attr"> ${attr.key}="${attr.value}"</c:forEach>>
                                    <span class="glyphicon glyphicon-new-window glyphicon-primary"></span>                                       
                                </a>                                    
                            </div>
                            <div id="plainCommentText${indexCounter}">${commentRow.comment.commentText}</div>                                   
                        </li>                      
                    </c:forEach>                            
                </ul>
                
            </div>                                        
        </c:forEach>                  
    </div>
</div>
