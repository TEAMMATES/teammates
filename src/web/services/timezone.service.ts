import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { Observable } from 'rxjs';

import { HttpRequestService } from './http-request.service';
import { default as timezone } from '../data/timezone.json';
import { ResourceEndpoints } from '../types/api-const';
import { TimeZones } from '../types/api-output';
import { DateFormat, TimeFormat } from '../types/datetime-const';

/**
 * Handles timezone information provision.
 */
@Injectable({
  providedIn: 'root',
})
export class TimezoneService {

  tzVersion: string = '';
  tzOffsets: Record<string, number> = {};
  guessedTimezone: string = '';

  // These short timezones are not supported by Java
  private readonly badZones: Record<string, boolean> = {
    EST: true, 'GMT+0': true, 'GMT-0': true, HST: true, MST: true, 'US/Pacific-New': true, ROC: true,
  };

  constructor(private httpRequestService: HttpRequestService) {
    const d: Date = new Date();
    moment.tz.load(timezone);
    this.tzVersion = (moment.tz as any).dataVersion;
    moment.tz.names()
        .filter((tz: string) => !this.isBadZone(tz))
        .forEach((tz: string) => {
          const zone: moment.MomentZone | null = moment.tz.zone(tz);
          if (zone) {
            this.tzOffsets[tz] = zone.utcOffset(d.getTime()) * -1;
          }
        });
    this.guessedTimezone = moment.tz.guess();
  }

  /**
   * Gets the timezone database version.
   */
  getTzVersion(): string {
    return this.tzVersion;
  }

  /**
   * Gets the mapping of time zone ID to offset values.
   */
  getTzOffsets(): Record<string, number> {
    return this.tzOffsets;
  }

  /**
   * Guesses the timezone based on the web browser's settings.
   */
  guessTimezone(): string {
    return this.guessedTimezone;
  }

  /**
   * Returns true if the specified time zone ID is "bad", i.e. not supported by back-end.
   */
  isBadZone(tz: string): boolean {
    return this.badZones[tz];
  }

  getTimeZone(): Observable<TimeZones> {
    return this.httpRequestService.get(ResourceEndpoints.TIMEZONE);
  }

  formatToString(timestamp: number, timeZone: string, format: string): string {
    const inst: moment.Moment = moment(timestamp).tz(timeZone);
    // Midnight time (00:00 AM) will be displayed as 11:59 PM of the previous day
    if (inst.hour() === 0 && inst.minute() === 0) {
      inst.subtract(1, 'minute');
    }
    return inst.format(format);
  }

  getMomentInstance(timestamp: number | null, timeZone: string): moment.Moment {
    if (!timestamp) {
      return moment.tz(timeZone);
    }
    return moment(timestamp).tz(timeZone);
  }

  /**
   * Resolves the local date time to a UNIX timestamp.
   */
  resolveLocalDateTime(date: DateFormat, time: TimeFormat, timeZone?: string,
      resolveMidnightTo0000: boolean = false): number {
    const inst: moment.Moment = this.getMomentInstance(null, timeZone || this.guessTimezone());
    inst.set('year', date.year);
    inst.set('month', date.month - 1); // moment month is from 0-11
    inst.set('date', date.day);
    inst.set('hour', time.hour);
    inst.set('minute', time.minute);
    inst.set('second', 0);
    inst.set('millisecond', 0);

    if (resolveMidnightTo0000 && inst.hour() === 23 && inst.minute() === 59) {
      inst.add(1, 'minute');
    }

    return inst.toDate().getTime();
  }

}
