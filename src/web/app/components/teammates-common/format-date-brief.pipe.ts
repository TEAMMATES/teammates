import { Pipe, PipeTransform, inject } from '@angular/core';
import { DateFormatService } from '../../../services/format-date.service';
/**
 * Pipe to handle the display of feedback session start and end time in brief.
 */
@Pipe({ name: 'formatDateBrief' })
export class FormatDateBriefPipe implements PipeTransform {
  private formatDateUtil = inject(DateFormatService);

  /**
   * Transforms a timestamp to a date string briefly.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.formatDateUtil.formatDateBrief(timestamp, timeZone);
  }
}
