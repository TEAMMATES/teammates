import { Pipe, PipeTransform } from '@angular/core';
import { environment } from "../../../environments/environment";

@Pipe({
  name: 'formatPhotoUrl'
})
export class FormatPhotoUrlPipe implements PipeTransform {

  transform(courseId: string, email: string): string {
    return `${environment.backendUrl}/webapi/student/profilePic?courseid=${courseId}&studentemail=${email}`;
  }
}
