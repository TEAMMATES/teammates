package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import teammates.common.datatransfer.AccountAttributes;

public class AjaxResult extends ActionResult {

	public PageData data;
	
	public AjaxResult(String destination, AccountAttributes account,
			Map<String, String[]> parametersFromPreviousRequest,
			List<String> status) {
		super(destination, account, parametersFromPreviousRequest, status);
	}

	public AjaxResult(String destination, AccountAttributes account,
			Map<String, String[]> parametersFromPreviousRequest,
			List<String> status, PageData data) {
		super(destination, account, parametersFromPreviousRequest, status);
		this.data = data;
	}
	
	@Override
	public void send(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		resp.setContentType("application/json");
		String jsonData = (new Gson()).toJson(data);
		
		resp.getWriter().write(jsonData);		

	}

}
