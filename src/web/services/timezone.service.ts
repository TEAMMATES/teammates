import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { default as timezone } from '../data/timezone.json';
import { HttpRequestService } from './http-request.service';

import { ResourceEndpoints } from '../types/api-const';
import { LocalDateTimeAmbiguityStatus, LocalDateTimeInfo, TimeZones } from '../types/api-output';

/**
 * The date time format used in date time resolution.
 */
export const LOCAL_DATE_TIME_FORMAT: string = 'YYYY-MM-DD HH:mm';

/**
 * The resolving result of a local data time.
 */
export interface TimeResolvingResult {
  timestamp: number;
  message: string;
}

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
          const offset: number = moment.tz.zone(tz).utcOffset(d) * -1;
          this.tzOffsets[tz] = offset;
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

  /**
   * Gets the resolved UNIX timestamp from a local data time with time zone.
   */
  getResolvedTimestamp(localDateTime: string, timeZone: string, fieldName: string): Observable<TimeResolvingResult> {
    const params: Record<string, string> = { localdatetime: localDateTime, timezone: timeZone };
    return this.httpRequestService.get(ResourceEndpoints.LOCAL_DATE_TIME, params).pipe(
        map((info: LocalDateTimeInfo) => {
          const resolvingResult: TimeResolvingResult = {
            timestamp: info.resolvedTimestamp,
            message: '',
          };

          const DATE_FORMAT_WITHOUT_ZONE_INFO: any = 'ddd, DD MMM, YYYY hh:mm A';
          const DATE_FORMAT_WITH_ZONE_INFO: any = "ddd, DD MMM, YYYY hh:mm A z ('UTC'Z)";

          switch (info.resolvedStatus) {
            case LocalDateTimeAmbiguityStatus.UNAMBIGUOUS:
              break;
            case LocalDateTimeAmbiguityStatus.GAP:
              resolvingResult.message =
                  `The ${fieldName}, ${moment.format(DATE_FORMAT_WITHOUT_ZONE_INFO)},`
                  + 'falls within the gap period when clocks spring forward at the start of DST. '
                  + `It was resolved to ${moment(info.resolvedTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}.`;
              break;
            case LocalDateTimeAmbiguityStatus.OVERLAP:
              resolvingResult.message =
                  `The ${fieldName}, ${moment.format(DATE_FORMAT_WITHOUT_ZONE_INFO)},`
                  + 'falls within the overlap period when clocks fall back at the end of DST.'
                  + `It can refer to ${moment(info.earlierInterpretationTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}`
                  + `or ${moment(info.laterInterpretationTimestamp).format(DATE_FORMAT_WITH_ZONE_INFO)}.`
                  + 'It was resolved to %s.';
              break;
            default:
          }

          return resolvingResult;
        }),
    );
  }
}
