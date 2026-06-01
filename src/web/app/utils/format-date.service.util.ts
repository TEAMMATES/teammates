import { TimezoneService } from '../../services/timezone.service';
import { inject, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class FormatDateUtil {
  private timezoneService = inject(TimezoneService);

  formatDateBrief(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM h:mm A');
  }

  formatDateDetail(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'ddd, DD MMM YYYY, hh:mm A z');
  }
}
