import { Pipe, PipeTransform } from '@angular/core';

import { environment } from '../../../environments/environment';

/**
 * Pipe to handle formatting of URL for API request of photo retrieval
 */
@Pipe({
  name: 'formatPhotoUrl',
})
export class FormatPhotoUrlPipe implements PipeTransform {

  transform(email: string, courseId: string): string {
    return `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${email}`;
  }
}
