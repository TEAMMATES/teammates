import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { default as timezone } from '../data/timezone.json';

/**
 * Handles timezone information provision.
 */
@Injectable({
  providedIn: 'root',
})
export class TimezoneService {

  tzVersion: string = '';
  tzOffsets: { [key: string]: number } = {};

  // These short timezones are not supported by Java
  private readonly badZones: { [key: string]: boolean } = {
    EST: true, 'GMT+0': true, 'GMT-0': true, HST: true, MST: true, ROC: true,
  };

  constructor() {
    const d: Date = new Date();
    moment.tz.load(timezone);
    this.tzVersion = moment.tz.dataVersion;
    moment.tz.names()
        .filter((tz: string) => !this.isBadZone(tz))
        .forEach((tz: string) => {
          const offset: number = moment.tz.zone(tz).utcOffset(d) * -1;
          this.tzOffsets[tz] = offset;
        });
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
  getTzOffsets(): { [key: string]: number } {
    return this.tzOffsets;
  }

  /**
   * Returns true if the specified time zone ID is "bad", i.e. not supported by back-end.
   */
  isBadZone(tz: string): boolean {
    return this.badZones[tz];
  }

}
