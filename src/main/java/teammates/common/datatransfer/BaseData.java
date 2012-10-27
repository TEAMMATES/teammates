package teammates.common.datatransfer;

public abstract class BaseData {
	
	protected String trimIfNotNull(String string) {
		return ((string == null) ? "" : string.trim());
	}
	
	protected abstract boolean isValid();
	
	protected abstract String getInvalidStateInfo();
}
