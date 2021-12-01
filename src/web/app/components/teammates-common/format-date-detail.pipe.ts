import { Pipe, PipeTransform } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';

/**
 * Pipe to handle the display of a timestamp in detail.
 */
@Pipe({
  name: 'formatDateDetail',
})
export class FormatDateDetailPipe implements PipeTransform {

  constructor(private timezoneService: TimezoneService) {}

  /**
   * Transforms a timestamp to a date string in detail.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'ddd, DD MMM YYYY, hh:mm A z');
  }

}
