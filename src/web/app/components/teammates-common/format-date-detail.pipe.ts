import { Pipe, PipeTransform, inject } from '@angular/core';
import { DateFormatService } from '../../../services/format-date.service';

/**
 * Pipe to handle the display of a timestamp in detail.
 */
@Pipe({ name: 'formatDateDetail' })
export class FormatDateDetailPipe implements PipeTransform {
  private formatDateUtil = inject(DateFormatService);

  /**
   * Transforms a timestamp to a date string in detail.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.formatDateUtil.formatDateDetailed(timestamp, timeZone);
  }
}
