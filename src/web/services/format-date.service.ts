import { TimezoneService } from './timezone.service';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class DateFormatService {
  private timezoneService = inject(TimezoneService);

  formatDateBrief(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM h:mm A');
  }

  formatDateDetailed(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'ddd, DD MMM YYYY, hh:mm A z');
  }
}
