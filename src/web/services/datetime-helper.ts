
const MINUTES_TO_MILLI = 60 * 1000;
const HOURS_TO_MILLI = 60 * MINUTES_TO_MILLI;
const DAY_TO_MILLI = 24 * HOURS_TO_MILLI;

/**
 * CSV related utility functions.
 */
export class DateTimeHelper {
    static addTime(timestamp: number, minutes: number, hours: number, days: number): number {
        return timestamp + (minutes * MINUTES_TO_MILLI) + (hours * HOURS_TO_MILLI) + (days * DAY_TO_MILLI);
    }
    // TODO: Refactor resolveLocalDateTime, getDateTimeAtTimezone, magic numbers
}                                                                                                                                                                                                                                   
