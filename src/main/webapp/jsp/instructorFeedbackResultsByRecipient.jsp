<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<%@ page import="teammates.common.util.Const"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackResponseCommentAttributes"%>
<%@ page import="teammates.common.datatransfer.FeedbackSessionResponseStatus" %>
<%@ page import="teammates.ui.controller.InstructorFeedbackResultsPageData"%>
<%@ page import="teammates.common.datatransfer.FeedbackAbstractQuestionDetails"%>
<%@ page import="teammates.common.datatransfer.FeedbackQuestionAttributes"%>
<%
	InstructorFeedbackResultsPageData data = (InstructorFeedbackResultsPageData) request.getAttribute("data");
%>
<!DOCTYPE html>
<html>
<head>
<link rel="shortcut icon" href="/favicon.png">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>TEAMMATES - Feedback Session Results</title>
<link rel="stylesheet" href="/stylesheets/common.css" type="text/css" media="screen">
<link rel="stylesheet" href="/stylesheets/common-print.css" type="text/css" media="print">
<link rel="stylesheet" href="/stylesheets/instructorFeedbacks.css" type="text/css" media="screen">

<script type="text/javascript" src="/js/googleAnalytics.js"></script>
<script type="text/javascript" src="/js/jquery-minified.js"></script>
<script type="text/javascript" src="/js/tooltip.js"></script>
<script type="text/javascript" src="/js/AnchorPosition.js"></script>
<script type="text/javascript" src="/js/common.js"></script>
<script type="text/javascript" src="/js/additionalQuestionInfo.js"></script>
<script type="text/javascript" src="/js/feedbackResponseComments.js"></script>
<jsp:include page="../enableJS.jsp"></jsp:include>
</head>

<body>
	<div id="dhtmltooltip"></div>
	<div id="frameTop">
		<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_HEADER%>" />
	</div>

	<div id="frameBody">
		<div id="frameBodyWrapper">
			<div id="topOfPage"></div>
			<div id="headerOperation">
				<h1>Feedback Results - Instructor</h1>
			</div>
			<jsp:include page="<%=Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_TOP%>" />
			<br>
		<%
			Map<String, Map<String, List<FeedbackResponseAttributes>>> allResponses = data.bundle.getResponsesSortedByRecipient();
			int recipientIndex = 0;
			for (Map.Entry<String, Map<String, List<FeedbackResponseAttributes>>> responsesForRecipient : allResponses.entrySet()) {
				recipientIndex++;
		%>
				<div class="backgroundBlock">
					<h2 class="color_white">To: <%=responsesForRecipient.getKey()%></h2>
				<%
					int giverIndex = 0;
					for (Map.Entry<String, List<FeedbackResponseAttributes>> responsesForRecipientFromGiver : responsesForRecipient.getValue().entrySet()) {
						giverIndex++;
				%>
						<table class="resultTable" style="width: 100%">
							<thead>
								<tr>
									<th class="leftalign"><span class="bold">From: </span><%=responsesForRecipientFromGiver.getKey()%></th>
								</tr>
							</thead>
							<%
								int qnIndx = 1;
								for (FeedbackResponseAttributes singleResponse : responsesForRecipientFromGiver.getValue()) {
									Map<String, FeedbackQuestionAttributes> questions = data.bundle.questions;
									FeedbackQuestionAttributes question = questions.get(singleResponse.feedbackQuestionId);
									FeedbackAbstractQuestionDetails questionDetails = question.getQuestionDetails();
							%>
									<tr class="resultSubheader">
										<td class="multiline"><span class="bold">Question <%=question.questionNumber%>: </span><%
												out.print(InstructorFeedbackResultsPageData.sanitizeForHtml(questionDetails.questionText));
												out.print(questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "giver-"+giverIndex+"-recipient-"+recipientIndex));
										%></td>
									</tr>
									<tr>
										<td class="multiline"><span class="bold">Response: </span><%=singleResponse.getResponseDetails().getAnswerHtml()%></td>
									</tr>
									<tr>
										<td>
											<span class="bold">Comments: </span>
											<table class="responseCommentTable" id="responseCommentTable-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
											<%
												List<FeedbackResponseCommentAttributes> responseComments = data.bundle.responseComments.get(singleResponse.getId());
												if (responseComments != null) {
													int responseCommentIndex = 1;
													for (FeedbackResponseCommentAttributes comment : responseComments) {
											%>
														<tr id="responseCommentRow-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>">
															<td class="feedbackResponseCommentText"><%=comment.commentText.getValue()%></td>
															<td class="feedbackResponseCommentGiver"><%=comment.giverEmail%></td>
															<td class="feedbackResponseCommentTime"><%=comment.createdAt%></td>
															
														<% 
															if (comment.giverEmail.equals(data.instructor.email)) {
														%>
																<td class="rightalign">
																	<a href="#" class="color_blue" onclick="showResponseCommentEditForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>,<%=responseCommentIndex%>)">Edit</a>
																</td>
																<td class="rightalign">
																	<form class="responseCommentDeleteForm">
																		<a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_DELETE%>" class="color_red pad_right">Delete</a>
																		<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%=comment.getId()%>">
																		<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
																		<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
																		<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
																	</form>
																</td>
														<%
															}
														%>
														</tr>
														<tr id="responseCommentEditForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>" style="display: none;">
															<td colspan="5">
																<form class="responseCommentEditForm">
																	<textarea rows="4" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT %>"
																		id="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>-<%=responseCommentIndex%>"><%=comment.commentText.getValue()%></textarea>
																	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID %>" value="<%=comment.getId()%>">
																	<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
																	<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
																	<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
																	<a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_EDIT%>" class="button floatright">Save Changes</a>
																</form>
															</td>
														</tr>
											<%
														responseCommentIndex++;
													}
												}
											%>
												<tr id="showResponseCommentAddFormButton-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>">
													<td colspan="5">
														<a href="#" class="color_gray"
															onclick="showResponseCommentAddForm(<%=recipientIndex%>,<%=giverIndex%>,<%=qnIndx%>)">
															<textarea rows="1" disabled="disabled" style="cursor:text;">Add a comment...</textarea>
														</a>
													</td>
												</tr>
												<tr style="display: none;"
													id="responseCommentAddForm-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>" >
													<td colspan="5">
														<form class="responseCommentAddForm">
															<textarea rows="4" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>"
																id="<%=Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_TEXT%>-<%=recipientIndex%>-<%=giverIndex%>-<%=qnIndx%>"></textarea>
															<input type="hidden" name="<%=Const.ParamsNames.COURSE_ID %>" value="<%=singleResponse.courseId %>">
															<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_SESSION_NAME %>" value="<%=singleResponse.feedbackSessionName %>">
															<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_QUESTION_ID %>" value="<%=singleResponse.feedbackQuestionId %>">											
															<input type="hidden" name="<%=Const.ParamsNames.FEEDBACK_RESPONSE_ID %>" value="<%=singleResponse.getId() %>">
															<input type="hidden" name="<%=Const.ParamsNames.USER_ID%>" value="<%=data.account.googleId %>">
															<a href="<%=Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESPONSE_COMMENT_ADD%>" class="button floatright">Submit Comment</a>
														</form>
													</td>
												</tr>
											</table>
										</td>
									</tr>
							<%
									qnIndx++;
								}
								if (responsesForRecipientFromGiver.getValue().isEmpty()) {
							%>
							<tr>
								<td class="bold color_red">No feedback from this user.</td>
							</tr>
							<%
								}
							%>
						</table>
						<br>
				<%
					}
				%>
				</div>
				<br>
				<br>
		<%
			}
		%>

		<%
			// Only output the list of students who haven't responded when there are responses.
			FeedbackSessionResponseStatus responseStatus = data.bundle.responseStatus;
			if (!responseStatus.hasResponse.isEmpty()) {
		%>
				<div class="backgroundBlock">
					<h2 class="color_white">Student Response Information</h2>
					
					<table class="resultTable" style="width: 100%">
						<thead>
							<tr>
								<th>Students Who Did Not Respond to Any Question</th>
							</tr>
						</thead>
						<tbody>
						<%
							for (String studentName : responseStatus.getStudentsWhoDidNotRespondToAnyQuestion()) {
						%>
								<tr>
									<td><%=studentName%></td>
								</tr>
						<%
							}
						%>
						</tbody>
					</table>
				</div>
				<br> <br>
		<%
			}
		%>
		</div>
	</div>

	<div id="frameBottom">
		<jsp:include page="<%=Const.ViewURIs.FOOTER%>" />
	</div>
</body>
</html>