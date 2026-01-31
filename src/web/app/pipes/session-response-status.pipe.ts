import { Pipe, PipeTransform } from '@angular/core';

/**
 * Processes and displays the response status.
 */
@Pipe({ name: 'sessionResponseStatus' })
export class ResponseStatusPipe implements PipeTransform {
  /**
   * Displays the response status depending on whether the session is published.
   */
  transform(isSessionPublished: boolean): string {
    return isSessionPublished ? 'Published' : 'Not Published';
  }
}
