<%@ page import="teammates.api.Common" %>
<%@ page import="teammates.datatransfer.SubmissionData" %>
<%@ page import="teammates.jsp.EvalSubmissionEditHelper" %>
<% EvalSubmissionEditHelper helper = (EvalSubmissionEditHelper)request.getAttribute("helper"); %>
					<input type="hidden" value="<%= helper.eval.course %>"
							name="<%= Common.PARAM_COURSE_ID %>"
							id="<%= Common.PARAM_COURSE_ID %>" />
					<input type="hidden" value="<%=EvalSubmissionEditHelper.escapeForHTML(helper.eval.name)%>"
							name="<%=Common.PARAM_EVALUATION_NAME%>"
							id="<%=Common.PARAM_EVALUATION_NAME%>" />
					<input type="hidden" value="<%=EvalSubmissionEditHelper.escapeForHTML(helper.student.team)%>"
							name="<%=Common.PARAM_TEAM_NAME%>"
							id="<%=Common.PARAM_TEAM_NAME%>" />
					<input type="hidden" value="<%=helper.student.email%>"
							name="<%=Common.PARAM_FROM_EMAIL%>"
							id="<%=Common.PARAM_FROM_EMAIL%>" />
					<table class="headerform">
						<%
							int idx = 0;
											for(SubmissionData sub: helper.submissions){
						%>
							<tr style="display:none">
								<td>
		 							<input type="text" value="<%=sub.reviewee%>"
		 									name="<%=Common.PARAM_TO_EMAIL%>"
		 									id="<%=Common.PARAM_TO_EMAIL+idx%>" />
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="reportheader" colspan="2" id="sectiontitle<%=idx%>">
		 							<%=helper.getEvaluationSectionTitle(sub)%>
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="lhs">Estimated contribution:</td>
		 						<td>
		 							<select style="width: 150px"
		 									name="<%=Common.PARAM_POINTS%>"
		 									id="<%=Common.PARAM_POINTS+idx%>">
		 								<%=helper.getEvaluationOptions(sub)%>
		 							</select>
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="lhs"><%=helper.getJustificationInstr(sub)%></td>
		 						<td>
		 							<textarea class="textvalue" rows="8" cols="100" 
		 									name="<%=Common.PARAM_JUSTIFICATION%>"
		 									id="<%=Common.PARAM_JUSTIFICATION+idx%>"><%=EvalSubmissionEditHelper.escapeForHTML(sub.justification.getValue())%></textarea>
		 						</td>
		 					</tr>
		 					<tr>
		 						<td class="lhs"><%=helper.getCommentsInstr(sub)%></td>
								<%
									if(helper.eval.p2pEnabled){
								%>
									<td><textarea class = "textvalue"
											rows="8" cols="100"
											name="<%=Common.PARAM_COMMENTS%>"
									 		id="<%=Common.PARAM_COMMENTS+idx%>"><%=EvalSubmissionEditHelper.escapeForHTML(sub.p2pFeedback.getValue())%></textarea>
									</td>
								<%	} else { %>
									<td>
										<font color="red">
											<textarea class="textvalue"
													rows="1" cols="100"
													name="<%= Common.PARAM_COMMENTS %>"
													id="<%= Common.PARAM_COMMENTS+idx %>"
													disabled="disabled">N.A.</textarea>
										</font>
									</td>
								<%	} %>
							</tr>
							<tr><td colspan="2"></td></tr>
						<%		idx++;
							} %>
					</table>