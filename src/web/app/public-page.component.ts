import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';
import { AuthService } from '../services/auth.service';
import { InstructorService } from '../services/instructor.service';
import { StudentService } from '../services/student.service';
import { Instructor, Student } from '../types/api-output';
import { Intent } from '../types/api-request';

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
              private studentService: StudentService,
              private instructorService: InstructorService) {
    if (environment.maintenance) {
      return;
    }
    this.route.queryParams.subscribe((queryParams: any) => {
      this.authService.getAuthUser(queryParams.user).subscribe(() => {
        // No need to do anything with result; this is necessary to get CSRF token

        // Loads institute for student session submission and result
        const courseId: string = queryParams.courseid;
        const regKey: string = queryParams.key;
        const entityType: string = queryParams.entitytype;
        if (courseId && regKey) {
          if (entityType === 'instructor') {
            this.instructorService.getInstructor({
              courseId,
              key: regKey,
              intent: Intent.INSTRUCTOR_SUBMISSION,
            }).subscribe((instructor: Instructor) => {
              this.institute = instructor.institute || '';
            });
          } else {
            this.studentService.getStudent(courseId, '', regKey).subscribe((student: Student) => {
              this.institute = student.institute || '';
            });
          }
        }
      });
    });
  }

}
