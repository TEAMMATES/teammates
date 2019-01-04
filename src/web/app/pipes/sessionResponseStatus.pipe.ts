import { Pipe, PipeTransform } from '@angular/core';

/**
 * Displays the response status depending on whether the session is published
 */
@Pipe({name: 'sessionResponseStatus'})
export class ResponseStatusPipe implements PipeTransform {
  transform(isSessionPublished: boolean): string {
    return isSessionPublished ? "Published" : "Not Published";
  }
}
