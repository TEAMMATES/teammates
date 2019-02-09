import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

/**
 * Pipe to handle the display of raw HTML.
 */
@Pipe({
  name: 'safeHtml',
})
export class SafeHtmlPipe implements PipeTransform {

  constructor(private domSanitizer: DomSanitizer) {}

  /**
   * Transforms HTML to value that can be displayed without sanitization.
   */
  transform(html: string): any {
    return this.domSanitizer.bypassSecurityTrustHtml(html);
  }

}
