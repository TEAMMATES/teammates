package teammates.servlet;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.InvalidParametersException;
import teammates.jsp.AdminHomeHelper;

@SuppressWarnings("serial")
public class AdministratorHomeServlet extends ActionServlet<AdminHomeHelper> {

	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, AdminHomeHelper helper){
		String coordID = req.getParameter(Common.PARAM_COORD_ID);
		String coordName = req.getParameter(Common.PARAM_COORD_NAME);
		String coordEmail = req.getParameter(Common.PARAM_COORD_EMAIL);
		try {
			if(coordID!=null && coordName!=null && coordEmail!=null){
				helper.server.createCoord(coordID, coordName, coordEmail);
				helper.statusMessage = "Coordinator " + coordName + " has been successfully created";
			}
		} catch (EntityAlreadyExistsException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMINISTRATOR_HOME;
	}

}
