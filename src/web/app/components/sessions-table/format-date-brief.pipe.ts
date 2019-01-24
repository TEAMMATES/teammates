import { Pipe, PipeTransform } from '@angular/core';
import moment from 'moment-timezone';

/**
 * Pipe to handle the display of feedback session start and end time in brief.
 */
@Pipe({
  name: 'formatDateBrief',
})
export class FormatDateBriefPipe implements PipeTransform {

  /**
   * Transforms a timestamp to a date string briefly.
   */
  transform(timestamp: number, timeZone: string): string {
    return moment(timestamp).tz(timeZone).format('D MMM H:mm A');
  }

}
