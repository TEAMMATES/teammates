import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe to strip off HTML tags from text.
 */
@Pipe({ name: 'stripHtmlTags' })
export class StripHtmlTagsPipe implements PipeTransform {
  /**
   * Transforms HTML to plain text.
   */
  transform(html: string): any {
    return html
      .replace(/(<img([^>]+)>)/gi, '[Image]')
      .replace(/(<table((.|\s)*)<\/table>)/gi, '[Table]')
      .replace(/(<([^>]+)>)/gi, '');
  }
}
