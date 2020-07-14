import { Pipe, PipeTransform } from '@angular/core';
import { TimezoneService } from '../../../services/timezone.service';

/**
 * Pipe to handle the display of feedback session start and end time in brief.
 */
@Pipe({
  name: 'formatDateBrief',
})
export class FormatDateBriefPipe implements PipeTransform {

  constructor(private timezoneService: TimezoneService) {}

  /**
   * Transforms a timestamp to a date string briefly.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.timezoneService.formatToString(timestamp, timeZone, 'D MMM h:mm A');
  }

}
