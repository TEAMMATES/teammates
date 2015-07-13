<%@ tag description="instructorSearch.jsp - Search comments for students" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/shared" prefix="shared" %>
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
                        <shared:comment comment="${commentRow}" commentIndex="${indexCounter}" />
                    </c:forEach>                            
                </ul>
                
            </div>                                        
        </c:forEach>                  
    </div>
</div>
