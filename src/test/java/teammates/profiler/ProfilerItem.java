package teammates.profiler;

public final class ProfilerItem {
	public String label;
	public long startTime;
	
	public ProfilerItem(String label, long startTime) {
		this.label = label;
		this.startTime = startTime;
	}
}

