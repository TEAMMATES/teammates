<%@ tag description="instructorFeedbackResults - by question" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag import="teammates.common.util.Const" %>

<%@ taglib tagdir="/WEB-INF/tags/instructor/results" prefix="results" %>

<%@ attribute name="showAll" type="java.lang.Boolean" required="true" %>
<%@ attribute name="sectionIndex" type="java.lang.Integer" required="true" %>
<%@ attribute name="shouldCollapsed" type="java.lang.Boolean" required="true" %>
<%@ attribute name="sectionPanel" type="teammates.ui.template.FeedbackResultsSectionPanel" required="true" %>

<c:set var="groupByTeamEnabled" value = "${data.groupByTeam != null || data.groupByTeam.equals(\"on\"))}"/>
<div class="panel panel-success">
    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-9 panel-heading-text">
                <strong>${sectionPanel.sectionName}</strong>                        
            </div>
            <div class="col-sm-3">
                <div class="pull-right">
                    <a class="btn btn-success btn-xs" id="collapse-panels-button-section-${sectionIndex}" data-toggle="tooltip" title='Collapse or expand all ${groupByTeamEnabled? "team" : "student"} panels. You can also click on the panel heading to toggle each one individually.'>
                        ${shouldCollapsed ? "Expand " : "Collapse "}
                        ${groupByTeamEnabled ? "Teams" : "Students"}
                    </a>
                    &nbsp;
                    <span class="glyphicon glyphicon-chevron-up"></span>
                </div>
            </div>
        </div>
    </div>
    <div class="panel-collapse collapse in">
    <div class="panel-body" id="sectionBody-<%=sectionIndex%>">

