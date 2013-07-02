<%@page import="teammates.common.datatransfer.EvaluationAttributes.EvalStatus"%>
<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.EvalSubmissionEditPageData" %>

<%
	EvalSubmissionEditPageData data = (EvalSubmissionEditPageData)request.getAttribute("data");
%>

<input type="hidden" value="<%=data.eval.courseId%>"
		name="<%=Config.PARAM_COURSE_ID%>"
		id="<%=Config.PARAM_COURSE_ID%>">
<input type="hidden" value="<%=EvalSubmissionEditPageData.escapeForHTML(data.eval.name)%>"
		name="<%=Config.PARAM_EVALUATION_NAME%>"
		id="<%=Config.PARAM_EVALUATION_NAME%>">
<input type="hidden" value="<%=EvalSubmissionEditPageData.escapeForHTML(data.student.team)%>"
		name="<%=Config.PARAM_TEAM_NAME%>"
		id="<%=Config.PARAM_TEAM_NAME%>">
<input type="hidden" value="<%=data.student.email%>"
		name="<%=Config.PARAM_FROM_EMAIL%>"
		id="<%=Config.PARAM_FROM_EMAIL%>">
<table class="inputTable">
	<%
		int idx = 0;
		for(SubmissionAttributes sub: data.submissions){
	%>
		<tr style="display: none;">
			<td>
					<input type="text" value="<%=sub.reviewee%>"
							name="<%=Config.PARAM_TO_EMAIL%>"
							id="<%=Config.PARAM_TO_EMAIL+idx%>">
				</td>
			</tr>
			<tr>
				<td class="bold centeralign reportHeader" colspan="2" id="sectiontitle<%=idx%>">
					<%=data.getEvaluationSectionTitle(sub)%>
				</td>
			</tr>
			
			<%
							if(sub.reviewee.equals(sub.reviewer)){
						%>
				<tr>
					<td class="label rightalign bold">My Estimated contribution:</td>
					<td>
						<select style="width: 150px;"
								name="<%=Config.PARAM_POINTS%>"
								id="<%=Config.PARAM_POINTS+idx%>"
								<%=data.disableAttribute%>>
							<%=data.getEvaluationOptions(sub)%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="label rightalign bold middlealign"><%=data.getJustificationInstr(sub)%></td>
					<td>
						<textarea class="textvalue" rows="8" cols="100" 
								name="<%=Config.PARAM_JUSTIFICATION%>"
								id="<%=Config.PARAM_JUSTIFICATION+idx%>"
								<%=data.disableAttribute%>><%=EvalSubmissionEditPageData.escapeForHTML(sub.justification.getValue())%></textarea>
					</td>
				</tr>
				<tr>
					<td class="label rightalign bold middlealign"><%=data.getCommentsInstr(sub)%></td>
				<%
					if(data.eval.p2pEnabled){
				%>
					<td><textarea class = "textvalue"
							rows="8" cols="100"
							name="<%=Config.PARAM_COMMENTS%>"
					 		id="<%=Config.PARAM_COMMENTS+idx%>"
					 		<%=data.disableAttribute%>><%=data.getP2PComments(sub)%></textarea>
					</td>
				<%
					} else {
				%>
					<td>
						<font color="red">
							<textarea class="textvalue"
									rows="1" cols="100"
									name="<%=Config.PARAM_COMMENTS%>"
									id="<%=Config.PARAM_COMMENTS+idx%>"
									disabled="disabled">N.A.</textarea>
						</font>
					</td>
				<%
					}
				%>
			</tr>
			<%
				} else {
			%>
				<tr>
					<td class="label rightalign bold">His/Her Estimated contribution:</td>
					<td>
						<select style="width: 150px;"
								name="<%=Config.PARAM_POINTS%>"
								id="<%=Config.PARAM_POINTS+idx%>"
								<%=data.disableAttribute%>>
							<%=data.getEvaluationOptions(sub)%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="label rightalign bold middlealign"><%=data.getCommentsInstr(sub)%></td>
				<%
					if(data.eval.p2pEnabled){
				%>
					<td><textarea class = "textvalue"
							rows="8" cols="100"
							name="<%=Config.PARAM_COMMENTS%>"
					 		id="<%=Config.PARAM_COMMENTS+idx%>"
					 		<%=data.disableAttribute%>><%=data.getP2PComments(sub)%></textarea>
					</td>
				<%
					} else {
				%>
					<td>
						<font color="red">
							<textarea class="textvalue"
									rows="1" cols="100"
									name="<%=Config.PARAM_COMMENTS%>"
									id="<%=Config.PARAM_COMMENTS+idx%>"
									disabled="disabled">N.A.</textarea>
						</font>
					</td>
				<%
					}
				%>
			</tr>
			<tr>
					<td class="label rightalign bold middlealign"><%=data.getJustificationInstr(sub)%></td>
					<td>
						<textarea class="textvalue" rows="8" cols="100" 
								name="<%=Config.PARAM_JUSTIFICATION%>"
								id="<%=Config.PARAM_JUSTIFICATION+idx%>"
								<%=data.disableAttribute%>><%=EvalSubmissionEditPageData.escapeForHTML(sub.justification.getValue())%></textarea>
					</td>
				</tr>
			<%	} %>
			<tr><td colspan="2"></td></tr>
	<%		idx++;
		} %>
</table>