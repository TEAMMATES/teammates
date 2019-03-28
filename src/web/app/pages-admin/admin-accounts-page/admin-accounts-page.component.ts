import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AccountService } from '../../../services/account.service';
import { HttpRequestService } from '../../../services/http-request.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Represents course attributes.
 */
interface CourseAttributes {
  id: string;
  name: string;
}

/**
 * Represents account attributes.
 */
interface AccountAttributes {
  googleId: string;
  name: string;
  email: string;
  institute?: string;
  isInstructor: boolean;
}

/**
 * Represents detailed information of an account.
 */
interface AccountInfo {
  accountInfo: AccountAttributes;
  instructorCourses: CourseAttributes[];
  studentCourses: CourseAttributes[];
}

/**
 * Admin accounts page.
 */
@Component({
  selector: 'tm-admin-accounts-page',
  templateUrl: './admin-accounts-page.component.html',
  styleUrls: ['./admin-accounts-page.component.scss'],
})
export class AdminAccountsPageComponent implements OnInit {

  instructorCourses: CourseAttributes[] = [];
  studentCourses: CourseAttributes[] = [];
  accountInfo: AccountAttributes = {
    googleId: '',
    name: '',
    email: '',
    isInstructor: false,
  };

  constructor(private route: ActivatedRoute, private router: Router, private httpRequestService: HttpRequestService,
      private navigationService: NavigationService, private statusMessageService: StatusMessageService,
              private accountService: AccountService) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadAccountInfo(queryParams.instructorid);
    });
  }

  /**
   * Loads the account information based on the given ID.
   */
  loadAccountInfo(instructorid: string): void {
    const paramMap: { [key: string]: string } = { instructorid };
    this.httpRequestService.get('/accounts', paramMap).subscribe((resp: AccountInfo) => {
      this.instructorCourses = resp.instructorCourses;
      this.studentCourses = resp.studentCourses;
      this.accountInfo = resp.accountInfo;
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Downgrades the instructor account to student.
   */
  downgradeAccountToStudent(): void {
    const id: string = this.accountInfo.googleId;
    this.accountService.downgradeAccount(id).subscribe(() => {
      this.loadAccountInfo(id);
      this.statusMessageService.showSuccessMessage('Instructor account is successfully downgraded to student.');
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Deletes the entire account.
   */
  deleteAccount(): void {
    const id: string = this.accountInfo.googleId;
    this.accountService.deleteAccount(id).subscribe(() => {
      this.navigationService.navigateWithSuccessMessage(this.router, '/web/admin/search',
          `Instructor account "${id}" is successfully deleted.`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(courseId: string): void {
    const id: string = this.accountInfo.googleId;
    const paramMap: { [key: string]: string } = {
      googleid: id,
      courseid: courseId,
    };
    this.httpRequestService.delete('/student', paramMap).subscribe(() => {
      this.studentCourses = this.studentCourses.filter((course: CourseAttributes) => course.id !== courseId);
      this.statusMessageService.showSuccessMessage(`Student is successfully deleted from course "${courseId}"`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

  /**
   * Removes the instructor from course.
   */
  removeInstructorFromCourse(courseId: string): void {
    const id: string = this.accountInfo.googleId;
    const paramMap: { [key: string]: string } = {
      instructorid: id,
      courseid: courseId,
    };
    this.httpRequestService.delete('/instructor', paramMap).subscribe(() => {
      this.instructorCourses = this.instructorCourses.filter((course: CourseAttributes) => course.id !== courseId);
      this.statusMessageService.showSuccessMessage(`Instructor is successfully deleted from course "${courseId}"`);
    }, (resp: ErrorMessageOutput) => {
      this.statusMessageService.showErrorMessage(resp.error.message);
    });
  }

}
