import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { AccountWithUsers } from '../../../types/api-output';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Admin accounts page.
 */
@Component({
  selector: 'tm-admin-accounts-page',
  templateUrl: './admin-accounts-page.component.html',
  styleUrls: ['./admin-accounts-page.component.scss'],
  imports: [LoadingSpinnerDirective],
})
export class AdminAccountsPageComponent implements OnInit {
  accountWithUsersInfo: AccountWithUsers = {
    accountId: '',
    googleId: '',
    name: '',
    email: '',
    students: [],
    instructors: [],
  };

  isLoadingAccountWithUsersInfo = false;

  constructor(
    private route: ActivatedRoute,
    private instructorService: InstructorService,
    private studentService: StudentService,
    private navigationService: NavigationService,
    private statusMessageService: StatusMessageService,
    private accountService: AccountService,
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadAccountInfo(queryParams.instructorid);
    });
  }

  /**
   * Loads the account information based on the given ID.
   */
  loadAccountInfo(instructorid: string): void {
    this.isLoadingAccountWithUsersInfo = true;
    this.accountService
      .getAccountWithUsers(instructorid)
      .pipe(
        finalize(() => {
          this.isLoadingAccountWithUsersInfo = false;
        }),
      )
      .subscribe({
        next: (resp: AccountWithUsers) => {
          this.accountWithUsersInfo = resp;
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Deletes the entire account.
   */
  deleteAccount(): void {
    const id: string = this.accountWithUsersInfo.googleId;
    this.accountService.deleteAccount(id).subscribe({
      next: () => {
        this.navigationService.navigateWithSuccessMessage(
          '/web/admin/search',
          `Account "${id}" is successfully deleted.`,
        );
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(courseId: string): void {
    this.studentService
      .deleteStudent({
        courseId,
        googleId: this.accountWithUsersInfo.googleId,
      })
      .subscribe({
        next: () => {
          this.accountWithUsersInfo.students = this.accountWithUsersInfo.students.filter(
            (student) => student.courseId !== courseId,
          );
          this.statusMessageService.showSuccessToast(`Student is successfully deleted from course "${courseId}"`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Removes the instructor from course.
   */
  removeInstructorFromCourse(courseId: string): void {
    this.instructorService
      .deleteInstructor({
        courseId,
        instructorId: this.accountWithUsersInfo.googleId,
      })
      .subscribe({
        next: () => {
          this.accountWithUsersInfo.instructors = this.accountWithUsersInfo.instructors.filter(
            (instructor) => instructor.courseId !== courseId,
          );
          this.statusMessageService.showSuccessToast(`Instructor is successfully deleted from course "${courseId}"`);
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
