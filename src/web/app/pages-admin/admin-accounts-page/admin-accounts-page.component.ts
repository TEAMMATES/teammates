import { Component, Input, OnInit, inject } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap/modal';
import { finalize } from 'rxjs/operators';
import { AccountService } from '../../../services/account.service';
import { InstructorService } from '../../../services/instructor.service';
import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { Account, Instructor, Student } from '../../../types/api-output';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { ErrorMessageOutput } from '../../error-message-output';
import { LinkService } from '../../../services/link.service';

/**
 * Admin accounts page.
 */
@Component({
  selector: 'tm-admin-accounts-page',
  templateUrl: './admin-accounts-page.component.html',
  imports: [LoadingSpinnerDirective],
})
export class AdminAccountsPageComponent implements OnInit {
  private instructorService = inject(InstructorService);
  private studentService = inject(StudentService);
  private navigationService = inject(NavigationService);
  private statusMessageService = inject(StatusMessageService);
  private accountService = inject(AccountService);
  private simpleModalService = inject(SimpleModalService);
  private linkService = inject(LinkService);

  accountInfo: Account = {
    accountId: '',
    googleId: '',
    name: '',
    email: '',
    students: [],
    instructors: [],
  };

  @Input({ required: true }) accountId!: string;

  isLoadingAccountInfo = false;

  ngOnInit(): void {
    this.loadAccountInfo(this.accountId);
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
    const modalContent = `This will <strong>delete the account</strong> and unlink all student and instructor profiles.
      It will <strong>not</strong> delete the students or instructors.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      'Delete account?',
      SimpleModalType.DANGER,
      modalContent,
    );

    modalRef.result.then(
      () => {
        const accountId: string = this.accountInfo.accountId;
        this.accountService.deleteAccount(accountId).subscribe({
          next: () => {
            this.navigationService.navigateWithSuccessMessage('/web/admin/search', `Account is successfully deleted.`);
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
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
   * Unlinks the student profile from this account.
   */
  unlinkStudentAccount(student: Student): void {
    this.confirmAndUnlinkAccount(student.userId, student.name, student.courseId, () => {
      this.accountInfo.students = this.accountInfo.students.filter(
        (existingStudent) => existingStudent.userId !== student.userId,
      );
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

  /**
   * Unlinks the instructor profile from this account.
   */
  unlinkInstructorAccount(instructor: Instructor): void {
    this.confirmAndUnlinkAccount(instructor.userId, instructor.name, instructor.courseId, () => {
      this.accountInfo.instructors = this.accountInfo.instructors.filter(
        (existingInstructor) => existingInstructor.userId !== instructor.userId,
      );
    });
  }

  /**
   * Redirects to the instructor home page in masquerade mode.
   */
  masqueradeAsUser(): void {
    const url = this.linkService.generateHomePageLink(
      this.accountInfo.accountId,
      this.linkService.INSTRUCTOR_HOME_PAGE,
    );
    globalThis.location.assign(url);
  }

  private confirmAndUnlinkAccount(userId: string, name: string, courseId: string, onSuccess: () => void): void {
    const modalContent = `Are you sure you want to unlink the account currently associated with
        <strong>${name}</strong> in the course <strong>${courseId}</strong>?
        This will allow the profile to be linked with another account.`;
    const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
      `Unlink <strong>${name}</strong>'s account?`,
      SimpleModalType.WARNING,
      modalContent,
      {
        confirmMessage: 'Yes, unlink account',
      },
    );

    modalRef.result.then(
      () => {
        this.accountService.unlinkAccount(userId).subscribe({
          next: () => {
            onSuccess();
            this.statusMessageService.showSuccessToast(
              `The account has been successfully unlinked from the user profile.`,
            );
          },
          error: (resp: ErrorMessageOutput) => {
            this.statusMessageService.showErrorToast(resp.error.message);
          },
        });
      },
      () => {},
    );
  }
}
