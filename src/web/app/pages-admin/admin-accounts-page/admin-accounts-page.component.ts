import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { CourseService } from '../../../services/course.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Account, Course, Courses } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin accounts page.
 */
@Component({
  selector: 'tm-admin-accounts-page',
  templateUrl: './admin-accounts-page.component.html',
  styleUrls: ['./admin-accounts-page.component.scss'],
})
export class AdminAccountsPageComponent implements OnInit {

  instructorCourses: Course[] = [];
  studentCourses: Course[] = [];
  accountInfo: Account = {
    googleId: '',
    name: '',
    email: '',
    isInstructor: false,
    institute: '',
    createdAtTimeStamp: 0,
  };

  isLoadingAccountInfo: boolean = false;
  isLoadingStudentCourses: boolean = false;
  isLoadingInstructorCourses: boolean = false;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private instructorService: InstructorService,
              private studentService: StudentService,
              private navigationService: NavigationService,
              private statusMessageService: StatusMessageService,
              private accountService: AccountService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadAccountInfo(queryParams.instructorid);
    });
  }

  /**
   * Loads the account information based on the given ID.
   */
  loadAccountInfo(instructorid: string): void {
    this.isLoadingAccountInfo = true;
    this.accountService.getAccount(instructorid)
        .pipe(finalize(() => this.isLoadingAccountInfo = false))
        .subscribe((resp: Account) => {
          this.accountInfo = resp;
        }, (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        });

    this.isLoadingStudentCourses = true;
    this.courseService.getStudentCoursesInMasqueradeMode(instructorid)
        .pipe(finalize(() => this.isLoadingStudentCourses = false))
        .subscribe((resp: Courses) => {
          this.studentCourses = resp.courses;
        }, (resp: ErrorMessageOutput) => {
          if (resp.status !== 403) {
            this.statusMessageService.showErrorToast(resp.error.message);
          }
        });

    this.isLoadingInstructorCourses = true;
    this.courseService.getInstructorCoursesInMasqueradeMode(instructorid)
        .pipe(finalize(() => this.isLoadingInstructorCourses = false))
        .subscribe((resp: Courses) => {
          this.instructorCourses = resp.courses;
        }, (resp: ErrorMessageOutput) => {
          if (resp.status !== 403) {
            this.statusMessageService.showErrorToast(resp.error.message);
          }
        });
  }

  /**
   * Downgrades the instructor account to student.
   */
  downgradeAccountToStudent(): void {
    const id: string = this.accountInfo.googleId;
    this.accountService.downgradeAccount(id).subscribe(() => {
      this.instructorCourses = [];
      this.loadAccountInfo(id);
      this.statusMessageService.showSuccessToast('Instructor account is successfully downgraded to student.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Deletes the entire account.
   */
  deleteAccount(): void {
    const id: string = this.accountInfo.googleId;
    this.accountService.deleteAccount(id).subscribe(() => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/admin/search',
          `Account "${id}" is successfully deleted.`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(courseId: string): void {
    this.studentService.deleteStudent({
      courseId,
      googleId: this.accountInfo.googleId,
    }).subscribe(() => {
      this.studentCourses = this.studentCourses.filter((course: Course) => course.courseId !== courseId);
      this.statusMessageService.showSuccessToast(`Student is successfully deleted from course "${courseId}"`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

  /**
   * Removes the instructor from course.
   */
  removeInstructorFromCourse(courseId: string): void {
    this.instructorService.deleteInstructor({
      courseId,
      instructorId: this.accountInfo.googleId,
    }).subscribe(() => {
      this.instructorCourses = this.instructorCourses.filter((course: Course) => course.courseId !== courseId);
      this.statusMessageService.showSuccessToast(`Instructor is successfully deleted from course "${courseId}"`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorToast(resp.error.message);
    });
  }

}
