import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Account, Instructor, Student } from '../../../types/api-output';
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
  private route = inject(ActivatedRoute);
  private instructorService = inject(InstructorService);
  private studentService = inject(StudentService);
  private navigationService = inject(NavigationService);
  private statusMessageService = inject(StatusMessageService);
  private accountService = inject(AccountService);

  accountInfo: Account = {
    accountId: '',
    googleId: '',
    name: '',
    email: '',
    students: [],
    instructors: [],
  };

  isLoadingAccountInfo = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.loadAccountInfo(queryParams.accountid);
    });
  }

  /**
   * Loads the account information based on the given ID.
   */
  loadAccountInfo(accountId: string): void {
    this.isLoadingAccountInfo = true;
    this.accountService
      .getAccount(accountId)
      .pipe(
        finalize(() => {
          this.isLoadingAccountInfo = false;
        }),
      )
      .subscribe({
        next: (resp: Account) => {
          this.accountInfo = resp;
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
    const accountId: string = this.accountInfo.accountId;
    this.accountService.deleteAccount(accountId).subscribe({
      next: () => {
        this.navigationService.navigateWithSuccessMessage('/web/admin/search', `Account is successfully deleted.`);
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  /**
   * Removes the student from course.
   */
  removeStudentFromCourse(studentToDelete: Student): void {
    this.studentService
      .deleteStudent({
        userId: studentToDelete.userId,
      })
      .subscribe({
        next: () => {
          this.accountInfo.students = this.accountInfo.students.filter(
            (student) => student.userId !== studentToDelete.userId,
          );
          this.statusMessageService.showSuccessToast(
            `Student is successfully deleted from course "${studentToDelete.courseId}"`,
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Removes the instructor from course.
   */
  removeInstructorFromCourse(instructorToDelete: Instructor): void {
    this.instructorService
      .deleteInstructor({
        userId: instructorToDelete.userId,
      })
      .subscribe({
        next: () => {
          this.accountInfo.instructors = this.accountInfo.instructors.filter(
            (instructor) => instructor.userId !== instructorToDelete.userId,
          );
          this.statusMessageService.showSuccessToast(
            `Instructor is successfully deleted from course "${instructorToDelete.courseId}"`,
          );
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
