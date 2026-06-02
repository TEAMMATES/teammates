import { Pipe, PipeTransform, inject } from '@angular/core';
import { DateFormatService } from '../../../services/date-format.service';
/**
 * Pipe to handle the display of feedback session start and end time in brief.
 */
@Pipe({ name: 'formatDateBrief' })
export class FormatDateBriefPipe implements PipeTransform {
  private dateFormatService = inject(DateFormatService);

  /**
   * Transforms a timestamp to a date string briefly.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.dateFormatService.formatDateBrief(timestamp, timeZone);
  }
}
