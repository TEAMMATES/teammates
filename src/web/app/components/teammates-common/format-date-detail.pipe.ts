import { Pipe, PipeTransform, inject } from '@angular/core';
import { FormatDateUtil } from '../../utils/format-date.service.util';

/**
 * Pipe to handle the display of a timestamp in detail.
 */
@Pipe({ name: 'formatDateDetail' })
export class FormatDateDetailPipe implements PipeTransform {
  private formatDateUtil = inject(FormatDateUtil);

  /**
   * Transforms a timestamp to a date string in detail.
   */
  transform(timestamp: number, timeZone: string): string {
    return this.formatDateUtil.formatDateDetail(timestamp, timeZone);
  }
}
