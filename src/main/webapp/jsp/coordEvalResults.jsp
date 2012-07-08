<%@ page import="teammates.common.Common"%>
<%@ page import="teammates.common.datatransfer.EvaluationData"%>
<%@ page import="teammates.common.datatransfer.StudentData"%>
<%@ page import="teammates.common.datatransfer.TeamData"%>
<%@ page import="teammates.common.datatransfer.SubmissionData"%>
<%@ page import="teammates.ui.controller.CoordEvalResultsHelper"%>
<%	CoordEvalResultsHelper helper = (CoordEvalResultsHelper)request.getAttribute("helper"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link rel="shortcut icon" href="/favicon.png" />
	<meta http-equiv="X-UA-Compatible" content="IE=8" />
	<title>Teammates - Coordinator</title>
	<link rel=stylesheet href="/stylesheets/main.css" type="text/css" />
	<link rel=stylesheet href="/stylesheets/evaluation.css" type="text/css" />
	
	<script language="JavaScript" src="/js/jquery-1.6.2.min.js"></script>
	<script language="JavaScript" src="/js/tooltip.js"></script>
	<script language="JavaScript" src="/js/date.js"></script>
	<script language="JavaScript" src="/js/CalendarPopup.js"></script>
	<script language="JavaScript" src="/js/AnchorPosition.js"></script>
	<script language="JavaScript" src="/js/helper.js"></script>
	<script language="JavaScript" src="/js/common.js"></script>
	
	<script language="JavaScript" src="/js/coordinator.js"></script>
	<script language="JavaScript" src="/js/coordEval.js"></script>

</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%= Common.JSP_COORD_HEADER %>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Evaluation Results</h1>
			</div>
			<div id="coordinatorEvaluationInformation">
				<table class="headerform">
					<tr>
						<td class="fieldname">Course ID:</td>
						<td><%= helper.evaluation.course %></td>
					</tr>
					<tr>
						<td class="fieldname">Evaluation name:</td>
						<td><%=CoordEvalResultsHelper.escapeForHTML(helper.evaluation.name)%></td>
					</tr>
					<tr>
						<td class="fieldname">Opening time:</td>
						<td><%=Common.formatTime(helper.evaluation.startTime)%></td>
					</tr>
					<tr>
						<td class="fieldname">Closing time:</td>
						<td><%=Common.formatTime(helper.evaluation.endTime)%></td>
					</tr>
					<tr>
						<td class="centeralign" colspan=2>
							<b>Report Type:</b> 
							<input type="radio" name="radio_reporttype" id="radio_summary" value="coordinatorEvaluationSummaryTable" checked="checked"
									onclick="showReport(this.value)" />
							<label for="radio_summary">Summary</label>
							<input type="radio" name="radio_reporttype" id="radio_reviewer" value="coordinatorEvaluationDetailedReviewerTable"
									onclick="showReport(this.value)" />
							<label for="radio_reviewer">Detailed: By Reviewer</label>
							<input type="radio" name="radio_reporttype" id="radio_reviewee" value="coordinatorEvaluationDetailedRevieweeTable"
									onclick="showReport(this.value)" />
							<label for="radio_reviewee">Detailed: By Reviewee</label>
						</td>
					</tr> 
					<tr>
						<td></td>
						<td>
						<%
							if(CoordEvalResultsHelper.getCoordStatusForEval(helper.evaluation).equals(Common.EVALUATION_STATUS_CLOSED)) {
						%>
							<input type="button" class="button"
								id = "button_publish"
								value = "Publish"
								onclick = "if(togglePublishEvaluation('<%=helper.evaluation.name%>')) window.location.href='<%=helper.getCoordEvaluationPublishLink(helper.evaluation.course,helper.evaluation.name,false)%>';" />
						<%
							} else if (CoordEvalResultsHelper.getCoordStatusForEval(helper.evaluation).equals(Common.EVALUATION_STATUS_PUBLISHED)) {
						%>
							<input type="button" class="button"
								id = "button_unpublish"
								value = "Unpublish"
								onclick = "if(toggleUnpublishEvaluation('<%=helper.evaluation.name%>')) window.location.href='<%=helper.getCoordEvaluationUnpublishLink(helper.evaluation.course,helper.evaluation.name,false)%>';" />
						<%
							}
						%>
						</td>
					</tr>
				</table>
			</div>
			<jsp:include page="<%=Common.JSP_STATUS_MESSAGE%>" />
			<%
				out.flush();
			%>
			<div id="coordinatorEvaluationSummaryTable" class="evaluation_result">
				<div style="text-align:right; font-style:italic; font-size: small; padding:0 35px;">CC = Claimed Contribution; PC = Perceived Contribution; E = Equal Share</div>
				<table id="dataform">
					<tr>
						<th class="centeralign" width="13%"><input class="buttonSortAscending" type="button" id="button_sortteamname"
								onclick="toggleSort(this,1)"/>Team</th>
						<th class="centeralign"><input class="buttonSortNone" type="button" id="button_sortname" 
								onclick="toggleSort(this,2)"/>Student</th>
						<th class="centeralign" width="6.5%"><input class="buttonSortNone" type="button" id="button_sortclaimed"
								onclick="toggleSort(this,3,sortByPoint)"/>CC</th>
						<th class="centeralign" width="6.5%"><input class="buttonSortNone" type="button" id="button_sortperceived"
								onclick="toggleSort(this,4,sortByPoint)"/>PC</th>
						<th class="centeralign" width="6.5%"><input class="buttonSortNone" type="button" id="button_sortdiff"
								onclick="toggleSort(this,5,sortByDiff)"/>
							<span onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_DIFF%>')"
									onmouseout="hideddrivetip()">Diff</span>
						</th>
						<th class="centeralign" width="18%">
							<span onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_POINTS_GIVEN%>')" onmouseout="hideddrivetip()">
							Points Given</span>
						</th>
						<th class="centeralign" width="18%">
							<span onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_POINTS_RECEIVED%>')" onmouseout="hideddrivetip()">
							Points Received</span>
						</th>
						<th class="centeralign" width="11%">Action(s)</th>
					</tr>
					<%
						int idx = 0;
												for(TeamData team: helper.evaluation.teams){
													for(StudentData student: team.students){
					%>
						<tr class="student_row" id="student<%=idx%>">
							<td><%=CoordEvalResultsHelper.escapeForHTML(team.name)%></td>
							<td id="<%=Common.PARAM_STUDENT_NAME%>">
								<span onmouseover="ddrivetip('<%=CoordEvalResultsHelper.escapeForJavaScript(student.comments)%>')"
										onmouseout="hideddrivetip()">
									<%=student.name%>
								</span>
							</td>
							<td><%=CoordEvalResultsHelper.colorizePoints(student.result.claimedToCoord)%></td>
							<td><%=CoordEvalResultsHelper.colorizePoints(student.result.perceivedToCoord)%></td>
							<td><%=CoordEvalResultsHelper.printDiff(student.result)%></td>
							<td><%=CoordEvalResultsHelper.getPointsList(student.result.outgoing, true)%></td>
							<td><%=CoordEvalResultsHelper.getPointsList(student.result.incoming, true)%></td>
							<td class="centeralign">
								<a name="viewEvaluationResults<%=idx%>" id="viewEvaluationResults<%=idx%>"
										target="_blank"
										href="<%=helper.getCoordEvaluationSubmissionViewLink(helper.evaluation.course, helper.evaluation.name, student.email)%>"
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_VIEW_REVIEWER%>')"
										onmouseout="hideddrivetip()">
										View</a>
								<a name="editEvaluationResults<%=idx%>" id="editEvaluationResults<%=idx%>"
										target="_blank"
										href="<%=helper.getCoordEvaluationSubmissionEditLink(helper.evaluation.course, helper.evaluation.name, student.email)%>"
										onclick="return openChildWindow(this.href)"
										onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_EVALUATION_SUBMISSION_EDIT%>')"
										onmouseout="hideddrivetip()"
										>Edit</a></td>
						</tr>
					<%
						idx++;
										}
									}
					%>
				</table>
				<br /><br /><br /><br />
			</div>
			<%
				out.flush();
			%>
			<%
				for(boolean byReviewer = true, repeat=true; repeat; repeat = byReviewer, byReviewer=false){
			%>
				<div id="coordinatorEvaluationDetailed<%=byReviewer ? "Reviewer" : "Reviewee"%>Table" class="evaluation_result"
						style="display:none">
					<div><h1>Detailed Evaluation Results - By <%=byReviewer ? "Reviewer" : "Reviewee"%></h1></div>
					<div id="detail">
						<%
							boolean firstTeam = true;
											for(TeamData team: helper.evaluation.teams){
						%>
							<%
								if(firstTeam) firstTeam = false; else out.print("<br />");
							%>
							<div class="result_team">
								<p><%=CoordEvalResultsHelper.escapeForHTML(team.name)%></p><br />
								<%
									boolean firstStudent = true;
															for(StudentData student: team.students){
								%>
									<%
										if(firstStudent) firstStudent = false; else out.print("<br />");
									%>
									<table class="result_table">
										<thead><tr>
											<th colspan="2" width="10%">
												<span class="fontcolor"><%=byReviewer ? "Reviewer" : "Reviewee"%>: </span><%=student.name%></th>
											<th><span class="fontcolor"
													onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_CLAIMED%>')"
													onmouseout="hideddrivetip()">
												Claimed Contributions: </span><%=CoordEvalResultsHelper.printSharePoints(student.result.claimedToCoord,true)%></th>
											<th><span class="fontcolor"
													onmouseover="ddrivetip('<%=Common.HOVER_MESSAGE_PERCEIVED%>')"
													onmouseout="hideddrivetip()">
												Perceived Contributions: </span><%=CoordEvalResultsHelper.printSharePoints(student.result.perceivedToCoord,true)%>
												<%
													if(byReviewer){
												%>
													<a style="float:right; font-weight:normal" target="_blank"
															href="<%=helper.getCoordEvaluationSubmissionEditLink(student.course, helper.evaluation.name, student.email)%>"
															onclick="return openChildWindow(this.href)">
															Edit</a>
												<%
													}
												%>
											</th>
										</tr></thead>
										<tr>
											<td colspan="4"><b>Self evaluation:</b><br />
		 										<%=CoordEvalResultsHelper.printJustification(student.result.getSelfEvaluation())%></td>
		 								</tr>
		 								<tr>
		 									<td colspan="4"><b>Comments about team:</b><br />
		 										<%=CoordEvalResultsHelper.printComments(student.result.getSelfEvaluation(), helper.evaluation.p2pEnabled)%></td>
		 								</tr>
										<tr class="result_subheader">
											<td width="15%"><%=byReviewer ? "To" : "From"%> Student</td>
											<td width="5%">Contribution</td>
											<td width="40%">Comments</td>
											<td width="40%">Messages</td>
										</tr>
										<%
											for(SubmissionData sub: (byReviewer ? student.result.outgoing : student.result.incoming)){ if(sub.reviewer.equals(sub.reviewee)) continue;
										%>
											<tr>
												<td><b><%=CoordEvalResultsHelper.escapeForHTML(byReviewer ? sub.revieweeName : sub.reviewerName)%></b></td>
												<td><%= CoordEvalResultsHelper.printSharePoints(sub.normalizedToCoord,false) %></td>
												<td><%= CoordEvalResultsHelper.printJustification(sub) %></td>
												<td><%= CoordEvalResultsHelper.printComments(sub, helper.evaluation.p2pEnabled) %></td>
											</tr>
										<%	} %>
									</table>
								<%	} %>
							</div>
						<%	} %>
					</div><br /><br />
					<input type="button" class ="button" name="button_back" id="button_back" value="Back"
							onclick="window.location.href='<%= helper.getCoordEvaluationLink() %>'"/>
					<input type="button" class ="button" name="button_top" id="button_top" value="To Top"
							onclick="scrollToTop()"/>
					<br /><br /><br /><br />
				</div>
			<%	} %>
			<% out.flush(); %>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%= Common.JSP_FOOTER %>" />
	</div>
	<script>setStatusMessage("");</script>
</body>
</html>