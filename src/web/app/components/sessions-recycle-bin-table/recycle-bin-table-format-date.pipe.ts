import { Pipe, PipeTransform } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';

/**
 * Pipe to handle the display of a time in recycle bin table.
 */
@Pipe({
  name: 'recycleBinTableFormatDate',
})
export class RecycleBinTableFormatDatePipe implements PipeTransform {

  constructor(private timezoneService: TimezoneService) {}

  /**
   * Transforms timestamp to a date in a timezone in recycle bin table.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'DD MMM, YYYY');
  }

}
