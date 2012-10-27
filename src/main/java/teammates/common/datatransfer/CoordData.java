package teammates.common.datatransfer;

import java.util.HashMap;

import teammates.common.Common;
import teammates.storage.entity.Coordinator;

public class CoordData extends BaseData {
	public HashMap<String, CourseData> courses;
	public String id;
	public String name;
	public String email;
	
	public static final String ERROR_FIELD_ID = "Coordinator ID is invalid\n";
	public static final String ERROR_FIELD_NAME = "Coordinator name cannot be null or empty\n";
	public static final String ERROR_FIELD_EMAIL = "Coordinator email is invalid\n";
	
	public CoordData(String id, String name, String email) {
		this();
		this.id = trimIfNotNull(id);
		this.name = trimIfNotNull(name);
		this.email = trimIfNotNull(email);
	}

	public CoordData(Coordinator coord) {
		this();
		this.id = coord.getGoogleID();
		this.name = coord.getName();
		this.email = coord.getEmail();
	}

	public CoordData() {
		
	}

	public Coordinator toEntity() {
		return new Coordinator(id, name, email);
	}

	public boolean isValid() {
		String stateCheck = getInvalidStateInfo();
		return stateCheck.length() == 0;
	}

	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidGoogleId(id)) {
			errorMessage += ERROR_FIELD_ID;
		}

		if (!Common.isValidName(name)) {
			errorMessage += ERROR_FIELD_NAME;
		}

		if (!Common.isValidEmail(email)) {
			errorMessage += ERROR_FIELD_EMAIL;
		}

		return errorMessage;
	}
}
