import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import moment from 'moment-timezone';

/**
 * Handles timezone information provision.
 */
@Injectable({
  providedIn: 'root',
})
export class TimezoneService {

  tzVersion: { [key: string]: string } = {};
  tzOffsets: { [key: string]: number } = {};

  constructor(private httpClient: HttpClient) {
    const d: Date = new Date();
    this.httpClient.get('./assets/data/timezone.json').subscribe((res: any) => {
      moment.tz.load(res);
      this.tzVersion.version = moment.tz.dataVersion;
      moment.tz.names()
          .filter((tz: string) => {
            // These short timezones are not supported by Java
            const badZones: { [key: string]: boolean } = {
              EST: true, 'GMT+0': true, 'GMT-0': true, HST: true, MST: true, ROC: true,
            };
            return !badZones[tz];
          })
          .forEach((tz: string) => {
            const offset: number = moment.tz.zone(tz).utcOffset(d) * -1;
            this.tzOffsets[tz] = offset;
          });
    });
  }

  /**
   * Gets the timezone database version.
   */
  getTzVersion(): { [key: string]: string } {
    return this.tzVersion;
  }

  /**
   * Gets the mapping of time zone ID to offset values.
   */
  getTzOffsets(): { [key: string]: number } {
    return this.tzOffsets;
  }

}
