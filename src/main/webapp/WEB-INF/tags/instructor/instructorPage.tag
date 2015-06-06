<%@ tag description="Generic Instructor Page" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib tagdir="/WEB-INF/tags/instructor" prefix="ti" %>
<%@ attribute name="pageTitle" required="true" %>
<%@ attribute name="bodyTitle" required="true" %>
<t:page pageTitle="${pageTitle}" bodyTitle="${bodyTitle}">
    <jsp:attribute name="navBar">
        <ti:nav />
    </jsp:attribute>
    <jsp:attribute name="headerJs">
        <script type="text/javascript" src="/js/instructor.js"></script>
        <script type="text/javascript" src="/js/instructorHome.js"></script>
        <script type="text/javascript" src="/js/ajaxResponseRate.js"></script>
        <script type="text/javascript" src="/js/instructorFeedbackAjaxRemindModal.js"></script>
    </jsp:attribute>
    <jsp:body>
        <jsp:doBody />
    </jsp:body>
</t:page>