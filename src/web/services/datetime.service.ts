import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { TimezoneService } from './timezone.service';
import { DateFormat, TimeFormat } from '../types/datetime-const';

@Injectable({
    providedIn: 'root',
})
export class DateTimeService {
    constructor(private timezoneService: TimezoneService) { }

    /**
     * Get the local date and time of timezone from timestamp.
     */
    getDateTimeAtTimezone(
        timestamp: number,
        timeZone: string,
        resolveMidnightTo2359: boolean,
    ): { date: DateFormat, time: TimeFormat } {
        let momentInstance: moment.Moment = this.timezoneService.getMomentInstance(
            timestamp,
            timeZone,
        );
        if (
            resolveMidnightTo2359
            && momentInstance.hour() === 0
            && momentInstance.minute() === 0
        ) {
            momentInstance = momentInstance.subtract(1, 'minute');
        }
        const date: DateFormat = {
            year: momentInstance.year(),
            month: momentInstance.month() + 1, // moment return 0-11 for month
            day: momentInstance.date(),
        };
        const time: TimeFormat = {
            minute: momentInstance.minute(),
            hour: momentInstance.hour(),
        };
        return {
            date,
            time,
        };
    }

    /**
     * Converts a Date object to a TimeFormat object.
     *
     * @param date Date to be converted into TimeFormat object.
     */
    convertDateToTimeFormat(date: Date): TimeFormat {
        return { hour: date.getHours(), minute: date.getMinutes() };
    }

    /**
     * Converts a DateFormat object and a TimeFormat object to a single Date instance.
     */
    convertDateFormatAndTimeFormatToDate(date: DateFormat, time: TimeFormat): Date {
        return new Date(date.year, date.month - 1, date.day, time.hour, time.minute);
    }

    /**
     * Converts a single Date instance to a DateFormat object and a TimeFormat object.
     */
    convertDateToDateFormatAndTimeFormat(date: Date): [DateFormat, TimeFormat] {
        const newDate: DateFormat = { year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate() };
        const newTime: TimeFormat = { hour: date.getHours(), minute: date.getMinutes() };
        return [newDate, newTime];
    }

    /**
     * Gets a DateFormat and a TimeFormat from a DateFormat and a TimeFormat Delta changed by deltaMinutes.
     *
     * @param date current date to change.
     * @param time current time to change.
     * @param deltaMinutes accepts a postive or negative number.
     */
    getDateTimeFromDateTimeDeltaMinutes(
        date: DateFormat, time: TimeFormat, deltaMinutes: number): [DateFormat, TimeFormat] {
        const changedDate = this.convertDateFormatAndTimeFormatToDate(date, time);
        changedDate.setMinutes(changedDate.getMinutes() + deltaMinutes);
        return this.convertDateToDateFormatAndTimeFormat(changedDate);
    }

    /**
     * Gets a moment instance from a date.
     */
    getMomentInstanceFromDate(date: DateFormat): moment.Moment {
        const inst: moment.Moment = moment();
        inst.set('year', date.year);
        inst.set('month', date.month - 1); // moment month is from 0-11
        inst.set('date', date.day);
        return inst;
    }

    /**
     * Gets a moment instance from a time.
     */
    getMomentInstanceFromTime(time: TimeFormat): moment.Moment {
        const inst: moment.Moment = moment();
        inst.set('hour', time.hour);
        inst.set('minute', time.minute);
        return inst;
    }

    /**
     * Gets a date instance from a moment.
     */
    getDateInstance(mmt: moment.Moment): DateFormat {
        return {
            year: mmt.year(),
            month: mmt.month() + 1, // moment month is from 0-11
            day: mmt.date(),
        };
    }

    /**
     * Gets a time instance from a moment.
     */
    getTimeInstance(mmt: moment.Moment): TimeFormat {
        return {
            hour: mmt.hour(),
            minute: mmt.minute(),
        };
    }

    /**
     * Compares the first date with the second date and checks whether the first
     * date is earlier, same or later than the second date.
     * Returns 1 if the first date is later than second date, 0 if the first date is the
     * same as the second date and -1 if the first date is earlier than the second date.
     */
    static compareDateFormat(firstDate : DateFormat, secondDate : DateFormat) : number {
        if (firstDate.year > secondDate.year) {
            return 1;
        }
        if (firstDate.year < secondDate.year) {
            return -1;
        }
        if (firstDate.month > secondDate.month) {
            return 1;
        }
        if (firstDate.month < secondDate.month) {
            return -1;
        }
        if (firstDate.day > secondDate.day) {
            return 1;
        }
        if (firstDate.day < secondDate.day) {
            return -1;
        }
        return 0;
    }

    /**
     * Compares the first timing with the second timing and checks whether the first
     * timing is earlier, same or later than the second timing.
     * Returns 1 if the first timing is later than second timing, 0 if the first timing is the
     * same as the second timing and -1 if the first timing is earlier than the second timing.
     */
    static compareTimeFormat(firstTiming : TimeFormat, secondTiming : TimeFormat) : number {
        if (firstTiming.hour > secondTiming.hour) {
            return 1;
        }
        if (firstTiming.hour < secondTiming.hour) {
            return -1;
        }
        if (firstTiming.minute > secondTiming.minute) {
            return 1;
        }
        if (firstTiming.minute < secondTiming.minute) {
            return -1;
        }
        return 0;
    }
}
