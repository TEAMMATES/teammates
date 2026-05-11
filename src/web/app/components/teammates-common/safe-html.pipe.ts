import { Pipe, PipeTransform, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

/**
 * Pipe to handle the display of raw HTML.
 */
@Pipe({ name: 'safeHtml' })
export class SafeHtmlPipe implements PipeTransform {
  private domSanitizer = inject(DomSanitizer);

  /**
   * Transforms HTML to value that can be displayed without sanitization.
   */
  transform(html: string): any {
    return this.domSanitizer.bypassSecurityTrustHtml(html);
  }
}
