package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.api.Logic;

@SuppressWarnings("serial")
public class InstructorEvalExportServlet extends HttpServlet {

	@Override
	public final void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		Common.getLogger().info("Exporting for Course: " + courseID + ", Eval: " + evalName);
		
		if(courseID==null || evalName==null) {
			resp.sendRedirect(Common.PAGE_INSTRUCTOR_HOME);
			return;
		}
		
		Logic server = new Logic();
		try {
			String evalExport = server.getEvaluationExport(courseID, evalName);
			
			// Set headers for Download
			String filename = courseID + "_" + evalName;
			resp.setHeader("Content-Type", "text/csv");
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".csv\"");
			PrintWriter writer = resp.getWriter();
			writer.append(evalExport);
			
		} catch (EntityDoesNotExistException e) {
			Common.getLogger().warning("Export failed: No such Course or Evaluation");
			e.printStackTrace();
			resp.sendRedirect(Common.PAGE_INSTRUCTOR_HOME);
			return;
		}
		
		Common.getLogger().info("Export successful");
	}
}
