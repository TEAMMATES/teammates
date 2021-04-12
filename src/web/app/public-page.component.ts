import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { StudentService } from '../services/student.service';
import { Student } from '../types/api-output';

/**
 * Component for publicly available pages.
 */
@Component({
  selector: 'tm-public-page',
  template: '<tm-page [isValidUser]="true" [hideAuthInfo]="true" [institute]="institute"></tm-page>',
})
export class PublicPageComponent {
  institute: string = '';

  constructor(private route: ActivatedRoute,
              private authService: AuthService,
              private studentService: StudentService) {
    if (environment.maintenance) {
      return;
    }
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe(() => {
        // No need to do anything with result; this is necessary to get CSRF token

        // Loads institute for student session submission and result
        const courseId: string = queryParams.courseid;
        const regKey: string = queryParams.key;
        const studentEmail: string = queryParams.studentemail;
        if (courseId && regKey && studentEmail) {
          this.studentService.getStudent(courseId, studentEmail, regKey).subscribe((student: Student) => {
            this.institute = student.institute || '';
          });
        }
      });
    });
  }

}
