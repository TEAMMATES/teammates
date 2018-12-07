package teammates.ui.template;

import java.util.List;

public class AdminFilter {
    private String rangeStart;
    private List<String> rangeStartHourOptions;
    private List<String> rangeStartMinuteOptions;
    private String rangeEnd;
    private List<String> rangeEndHourOptions;
    private List<String> rangeEndMinuteOptions;
    private List<String> timeZoneOptions;

    public AdminFilter(String rangeStart, List<String> rangeStartHourOptions,
                       List<String> rangeStartMinuteOptions, String rangeEnd,
                       List<String> rangeEndHourOptions, List<String> rangeEndMinuteOptions,
                       List<String> timeZoneOptions) {
        this.rangeStart = rangeStart;
        this.rangeStartHourOptions = rangeStartHourOptions;
        this.rangeStartMinuteOptions = rangeStartMinuteOptions;
        this.rangeEnd = rangeEnd;
        this.rangeEndHourOptions = rangeEndHourOptions;
        this.rangeEndMinuteOptions = rangeEndMinuteOptions;
        this.timeZoneOptions = timeZoneOptions;
    }

    public String getRangeStart() {
        return rangeStart;
    }

    public List<String> getRangeStartHourOptions() {
        return rangeStartHourOptions;
    }

    public List<String> getRangeStartMinuteOptions() {
        return rangeStartMinuteOptions;
    }

    public String getRangeEnd() {
        return rangeEnd;
    }

    public List<String> getRangeEndHourOptions() {
        return rangeEndHourOptions;
    }

    public List<String> getRangeEndMinuteOptions() {
        return rangeEndMinuteOptions;
    }

    public List<String> getTimeZoneOptions() {
        return timeZoneOptions;
    }
}
