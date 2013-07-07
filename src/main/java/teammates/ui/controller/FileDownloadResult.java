package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AccountAttributes;

public class FileDownloadResult extends ActionResult {
	
	String fileContent = "";
	String fileName ="";

	public FileDownloadResult(String destination, AccountAttributes account,
			Map<String, String[]> parametersFromPreviousRequest,
			List<String> status) {
		super(destination, account, parametersFromPreviousRequest, status);
	}
	
	public FileDownloadResult(
			String destination, AccountAttributes account,
			Map<String, String[]> parametersFromPreviousRequest, List<String> status, 
			String fileName, String fileContent) {
		super(destination, account, parametersFromPreviousRequest, status);
		this.fileName = fileName;
		this.fileContent = fileContent;
	}

	
	@Override
	public void send(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setHeader("Content-Type", "text/csv");
		resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv\"");
		PrintWriter writer = resp.getWriter();
		writer.append(fileContent);
	}

}
