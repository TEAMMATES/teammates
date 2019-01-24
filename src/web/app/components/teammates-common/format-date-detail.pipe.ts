import { Pipe, PipeTransform } from '@angular/core';
import moment from 'moment-timezone';

/**
 * Pipe to handle the display of a timestamp in detail.
 */
@Pipe({
  name: 'formatDateDetail',
})
export class FormatDateDetailPipe implements PipeTransform {

  /**
   * Transforms a timestamp to a date string in detail.
   */
  transform(timestamp: number, timeZone: string): string {
    return moment(timestamp).tz(timeZone).format('ddd, DD MMM YYYY, HH:mm A z');
  }

}
