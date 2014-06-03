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
<%
    int idx = 0;
    for(SubmissionAttributes sub: data.submissions){
%>
        <div class="panel panel-primary">
            <div style="display: none;">
                    <input type="text" value="<%=sub.reviewee%>"
                            name="<%=Const.ParamsNames.TO_EMAIL%>"
                            id="<%=Const.ParamsNames.TO_EMAIL+idx%>">
            </div>
            <div class="panel-heading"><%=data.getEvaluationSectionTitle(sub)%></div>
        <%
            if(sub.reviewee.equals(sub.reviewer)) {
        %>
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">My estimated contribution:</label>
                        <div class="col-sm-10">
                            <select class="form-control"
                                    name="<%=Const.ParamsNames.POINTS%>"
                                    id="<%=Const.ParamsNames.POINTS+idx%>"
                                    <%=data.disableAttribute%>>
                                <%=data.getEvaluationOptions(sub)%>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label"><%=data.getJustificationInstr(sub)%></label>
                        <div class="col-sm-10">
                            <textarea class="form-control" rows="8" 
                                    name="<%=Const.ParamsNames.JUSTIFICATION%>"
                                    id="<%=Const.ParamsNames.JUSTIFICATION+idx%>"
                                    <%=data.disableAttribute%>><%=EvalSubmissionEditPageData.sanitizeForHtml(sub.justification.getValue())%></textarea>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label"><%=data.getCommentsInstr(sub)%></label>
                    <%
                        if(data.eval.p2pEnabled){
                    %>
                            <div class="col-sm-10">
                                <textarea class="form-control" rows="8"
                                        name="<%=Const.ParamsNames.COMMENTS%>"
                                         id="<%=Const.ParamsNames.COMMENTS+idx%>"
                                         <%=data.disableAttribute%>><%=data.getP2PComments(sub)%></textarea>
                            </div>
                    <%
                        } else {
                    %>
                            <div class="col-sm-10">
                                <font color="red">
                                    <textarea class="form-control" rows="1"
                                            name="<%=Const.ParamsNames.COMMENTS%>"
                                            id="<%=Const.ParamsNames.COMMENTS+idx%>"
                                            disabled="disabled">N.A.</textarea>
                                </font>
                            </div>
                    <%
                        }
                    %>
                    </div>
                </div>
        <%
            } else {
        %>
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-sm-2 control-label">His/Her estimated contribution:</label>
                        <div class="col-sm-10">
                            <select class="form-control"
                                    name="<%=Const.ParamsNames.POINTS%>"
                                    id="<%=Const.ParamsNames.POINTS+idx%>"
                                    <%=data.disableAttribute%>>
                                <%=data.getEvaluationOptions(sub)%>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label"><%=data.getCommentsInstr(sub)%></label>
                <%
                    if(data.eval.p2pEnabled){
                %>
                        <div class="col-sm-10">
                            <textarea class = "form-control" rows="8"
                                    name="<%=Const.ParamsNames.COMMENTS%>"
                                     id="<%=Const.ParamsNames.COMMENTS+idx%>"
                                     <%=data.disableAttribute%>><%=data.getP2PComments(sub)%></textarea>
                        </div>
                <%
                    } else {
                %>
                        <div class="col-sm-10">
                            <font color="red">
                                <textarea class="form-control" rows="1"
                                        name="<%=Const.ParamsNames.COMMENTS%>"
                                        id="<%=Const.ParamsNames.COMMENTS+idx%>"
                                        disabled="disabled">N.A.</textarea>
                            </font>
                        </div>
                <%
                    }
                %>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-2 control-label"><%=data.getJustificationInstr(sub)%></label>
                        <div class="col-sm-10">
                            <textarea class="form-control" rows="8" 
                                    name="<%=Const.ParamsNames.JUSTIFICATION%>"
                                    id="<%=Const.ParamsNames.JUSTIFICATION+idx%>"
                                    <%=data.disableAttribute%>><%=!data.isPreview ? EvalSubmissionEditPageData.sanitizeForHtml(sub.justification.getValue()) : ""%></textarea>
                        </div>
                    </div>
                </div>
        <%    
           }
        %>
<%        idx++; %>
        </div>
<%
    }
%>