import { Pipe, PipeTransform } from '@angular/core';
import moment from 'moment-timezone';

/**
 * Pipe to handle the display of a time in recycle bin table.
 */
@Pipe({
  name: 'recycleBinTableFormatDate',
})
export class RecycleBinTableFormatDatePipe implements PipeTransform {

  /**
   * Transforms timestamp to a date in a timezone in recycle bin table.
   */
  transform(timestamp: number, timeZone: string): string {
    return moment(timestamp).tz(timeZone).format('DD MMM, YYYY');
  }

}
