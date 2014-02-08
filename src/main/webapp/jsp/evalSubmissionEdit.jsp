<%@page import="teammates.common.datatransfer.EvaluationAttributes.EvalStatus"%>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.datatransfer.SubmissionAttributes" %>
<%@ page import="teammates.ui.controller.EvalSubmissionEditPageData" %>

<%
	EvalSubmissionEditPageData data = (EvalSubmissionEditPageData)request.getAttribute("data");
%>

<input type="hidden" value="<%=data.eval.courseId%>"
		name="<%=Const.ParamsNames.COURSE_ID%>"
		id="<%=Const.ParamsNames.COURSE_ID%>">
<input type="hidden" value="<%=EvalSubmissionEditPageData.sanitizeForHtml(data.eval.name)%>"
		name="<%=Const.ParamsNames.EVALUATION_NAME%>"
		id="<%=Const.ParamsNames.EVALUATION_NAME%>">
<input type="hidden" value="<%=EvalSubmissionEditPageData.sanitizeForHtml(data.student.team)%>"
		name="<%=Const.ParamsNames.TEAM_NAME%>"
		id="<%=Const.ParamsNames.TEAM_NAME%>">
<input type="hidden" value="<%=data.student.email%>"
		name="<%=Const.ParamsNames.FROM_EMAIL%>"
		id="<%=Const.ParamsNames.FROM_EMAIL%>">
<table class="inputTable">
	<%
		int idx = 0;
		for(SubmissionAttributes sub: data.submissions){
	%>
			<tr style="display: none;">
				<td>
					<input type="text" value="<%=sub.reviewee%>"
							name="<%=Const.ParamsNames.TO_EMAIL%>"
							id="<%=Const.ParamsNames.TO_EMAIL+idx%>">
				</td>
			</tr>
			<tr>
				<td class="bold centeralign reportHeader" colspan="2" id="sectiontitle<%=idx%>">
					<%=data.getEvaluationSectionTitle(sub)%>
				</td>
			</tr>
			
		<%
			if(sub.reviewee.equals(sub.reviewer)) {
		%>
				<tr>
					<td class="label rightalign bold">My Estimated contribution:</td>
					<td>
						<select style="width: 150px;"
								name="<%=Const.ParamsNames.POINTS%>"
								id="<%=Const.ParamsNames.POINTS+idx%>"
								<%=data.disableAttribute%>>
							<%=data.getEvaluationOptions(sub)%>
						</select>
					</td>
				</tr>
				<tr>
					<td class="label rightalign bold middlealign"><%=data.getJustificationInstr(sub)%></td>
					<td>
						<textarea class="textvalue" rows="8" cols="100" 
								name="<%=Const.ParamsNames.JUSTIFICATION%>"
								id="<%=Const.ParamsNames.JUSTIFICATION+idx%>"
								<%=data.disableAttribute%>><%=EvalSubmissionEditPageData.sanitizeForHtml(sub.justification.getValue())%></textarea>
					</td>
				</tr>
				<tr>
					<td class="label rightalign bold middlealign"><%=data.getCommentsInstr(sub)%></td>
				<%
					if(data.eval.p2pEnabled){
				%>
						<td><textarea class = "textvalue"
								rows="8" cols="100"
								name="<%=Const.ParamsNames.COMMENTS%>"
						 		id="<%=Const.ParamsNames.COMMENTS+idx%>"
						 		<%=data.disableAttribute%>><%=data.getP2PComments(sub)%></textarea>
						</td>
				<%
					} else {
				%>
						<td>
							<font color="red">
								<textarea class="textvalue"
										rows="1" cols="100"
										name="<%=Const.ParamsNames.COMMENTS%>"
										id="<%=Const.ParamsNames.COMMENTS+idx%>"
										disabled="disabled">N.A.</textarea>
							</font>
						</td>
				<%
					}
				%>
				</tr>
			
				<%-- Separate self-evaluation from peer-evaluation by creating a new table --%>
				<tr><td colspan="2"></td></tr>
				</table>
				<br>
				<br>
				<br>
				<table class="inputTable">
		<%
			} else {
		%>
				<tr>
					<td class="label rightalign bold">His/Her Estimated contribution:</td>
					<td>
						<select style="width: 150px;"
								name="<%=Const.ParamsNames.POINTS%>"
								id="<%=Const.ParamsNames.POINTS+idx%>"
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
							name="<%=Const.ParamsNames.COMMENTS%>"
					 		id="<%=Const.ParamsNames.COMMENTS+idx%>"
					 		<%=data.disableAttribute%>><%=data.getP2PComments(sub)%></textarea>
					</td>
			<%
				} else {
			%>
					<td>
						<font color="red">
							<textarea class="textvalue"
									rows="1" cols="100"
									name="<%=Const.ParamsNames.COMMENTS%>"
									id="<%=Const.ParamsNames.COMMENTS+idx%>"
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
								name="<%=Const.ParamsNames.JUSTIFICATION%>"
								id="<%=Const.ParamsNames.JUSTIFICATION+idx%>"
								<%=data.disableAttribute%>><%=!data.isPreview ? EvalSubmissionEditPageData.sanitizeForHtml(sub.justification.getValue()) : ""%></textarea>
					</td>
				</tr>
			<%	
				} 
			%>
			<tr><td colspan="2"></td></tr>
	<%		idx++;
		} 
	%>
</table>