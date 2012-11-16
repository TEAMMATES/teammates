package teammates.common.datatransfer;

public abstract class BaseData {
	
	protected String trimIfNotNull(String string) {
		return ((string == null) ? "" : string.trim());
	}
	
	public boolean isValid() {
		return getInvalidStateInfo().isEmpty();
	}
	
	public abstract String getInvalidStateInfo();
}
