package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseArchiveAction extends Action {

	@Override
	protected ActionResult execute() {

		String idOfCourseToArchive = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(idOfCourseToArchive);
		boolean setArchive = getRequestParamAsBoolean(Const.ParamsNames.COURSE_ARCHIVE_STATUS);
		Assumption.assertNotNull(setArchive);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(idOfCourseToArchive, account.googleId),
				logic.getCourse(idOfCourseToArchive));
		
		try {
			if (setArchive) {
				logic.setArchiveStatusOfCourse(idOfCourseToArchive, true);
				
				if (isRedirectToHomePage()) {
					statusToUser.add(String.format(Const.StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE, idOfCourseToArchive));
				} else {
					statusToUser.add(String.format(Const.StatusMessages.COURSE_ARCHIVED, idOfCourseToArchive));
				}
				
				statusToAdmin = "Course archived: " + idOfCourseToArchive;
			} else {
				logic.setArchiveStatusOfCourse(idOfCourseToArchive, false);
				
				statusToUser.add(String.format(Const.StatusMessages.COURSE_UNARCHIVED, idOfCourseToArchive));
				statusToAdmin = "Course unarchived: " + idOfCourseToArchive;
			}
		} catch (Exception e) {
			setStatusForException(e);
		}

		if (isRedirectToHomePage()) {
			return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
		} else {
			return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
		}
	}
	
	private boolean isRedirectToHomePage() {
		String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
		
		if (nextUrl != null && nextUrl.equals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)) {
			return true;
		} else {
			return false;
		}
	}

}
