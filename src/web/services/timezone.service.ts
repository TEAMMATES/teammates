import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { Observable } from 'rxjs';
import { default as timezone } from '../data/timezone.json';
import { HttpRequestService } from './http-request.service';

import { ResourceEndpoints } from '../types/api-const';
import { TimeZones } from '../types/api-output';

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
    EST: true, 'GMT+0': true, 'GMT-0': true, HST: true, MST: true, ROC: true,
  };

  constructor(private httpRequestService: HttpRequestService) {
    const d: Date = new Date();
    moment.tz.load(timezone);
    this.tzVersion = moment.tz.dataVersion;
    moment.tz.names()
        .filter((tz: string) => !this.isBadZone(tz))
        .forEach((tz: string) => {
          this.tzOffsets[tz] = moment.tz.zone(tz).utcOffset(d) * -1;
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
    return moment(timestamp).tz(timeZone).format(format);
  }

  getMomentInstance(timestamp: number | null, timeZone: string): any {
    if (!timestamp) {
      return moment.tz(timeZone);
    }
    return moment(timestamp).tz(timeZone);
  }

}
