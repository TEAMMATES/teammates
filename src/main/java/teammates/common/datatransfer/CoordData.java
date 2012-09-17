package teammates.common.datatransfer;

import java.util.HashMap;

import teammates.common.Common;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Coordinator;

/**
 * A shallow copy of the actual Coordinator entity
 * 
 * @author Kenny
 * 
 */
public class CoordData extends UserData {
	public HashMap<String, CourseData> courses;
	public String name;
	public String email;

	public CoordData(String id, String name, String email) {
		this();
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public CoordData(Coordinator coord) {
		this();
		this.id = coord.getGoogleID();
		this.name = coord.getName();
		this.email = coord.getEmail();
	}

	public CoordData() {
		isCoord = true;
	}

	public Coordinator toEntity() {
		return new Coordinator(id, name, email);
	}

	public boolean isValid() {

		if (this.id == null || this.id == "" || this.name == null
				|| this.name == "" || this.email == null || this.email == "") {
			return false;
		}

		return true;
	}

	public String getInvalidParametersInfo() {
		String fieldChecks = "";

		if (this.id == null || this.id == "") {
			fieldChecks += "Coord ID cannot be null or empty\n";
		}

		if (this.name == null || this.name == "") {
			fieldChecks += "Coord name cannot be null or empty\n";
		}

		if (this.email == null || this.email == "") {
			fieldChecks += "Coord email cannot be null or empty\n";
		}

		return fieldChecks;
	}
}
