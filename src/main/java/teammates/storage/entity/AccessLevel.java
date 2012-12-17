package teammates.storage.entity;

import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

public class AccessLevel {
	@PrimaryKey
	@Persistent
	private int accessLevelId = 0;

	@Persistent
	private boolean canEdit = false;

	@Persistent
	private boolean canAddCoordinator = false;

	@Persistent
	private boolean canDeleteCoordinator = false;

	@Persistent
	private boolean canDeleteCourse = false;

	public AccessLevel(int accessLevelId, boolean canEdit,
			boolean canAddCoordinator, boolean canDeleteCoordinator,
			boolean canDeleteCourse) {
		this.accessLevelId = accessLevelId;
		this.canEdit = canEdit;
		this.canAddCoordinator = canAddCoordinator;
		this.canDeleteCoordinator = canDeleteCoordinator;
		this.canDeleteCourse = canDeleteCourse;
	}
}
