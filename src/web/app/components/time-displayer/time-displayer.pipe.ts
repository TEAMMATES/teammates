import { Pipe, PipeTransform } from '@angular/core';
import moment from 'moment-timezone';

/**
 * Pipe to display time.
 */
@Pipe({
  name: 'tmTimeDisplayer',
})
export class TimeDisplayerPipe implements PipeTransform {

  /**
   * Transforms a date string into another, more human-readable date string.
   */
  transform(value: string, offset: number): string {
    const date: any = new Date(value);
    if (date.toString() === 'Invalid Date') {
      return value;
    }
    const momentObj: any = moment(value).utcOffset(offset);
    if (momentObj.hour() === 0 && momentObj.minute() === 0) {
      // Display 00:00 as 23:59 of the previous day
      momentObj.add(-1, 'minute');
    }
    return momentObj.format('ddd, DD MMM YYYY, HH:mm');
  }

}
