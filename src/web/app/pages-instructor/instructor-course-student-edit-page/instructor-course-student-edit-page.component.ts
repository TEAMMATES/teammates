import { NgClass } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { NavigationService } from '../../../services/navigation.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { JoinState, MessageOutput, Student } from '../../../types/api-output';
import { StudentUpdateRequest } from '../../../types/api-request';
import {
  STUDENT_NAME_MAX_LENGTH,
  SECTION_NAME_MAX_LENGTH,
  TEAM_NAME_MAX_LENGTH,
  EMAIL_MAX_LENGTH,
} from '../../../types/field-validator';
import { LoadingRetryComponent } from '../../components/loading-retry/loading-retry.component';
import { LoadingSpinnerDirective } from '../../components/loading-spinner/loading-spinner.directive';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';
import { noWhitespaceValidator } from '../../validators/no-whitespace.validator';
/**
 * Instructor course student edit page.
 */
@Component({
  selector: 'tm-instructor-course-student-edit-page',
  templateUrl: './instructor-course-student-edit-page.component.html',
  styleUrls: ['./instructor-course-student-edit-page.component.scss'],
  imports: [LoadingRetryComponent, LoadingSpinnerDirective, FormsModule, ReactiveFormsModule, NgClass],
})
export class InstructorCourseStudentEditPageComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private statusMessageService = inject(StatusMessageService);
  private studentService = inject(StudentService);
  private navigationService = inject(NavigationService);
  private ngbModal = inject(NgbModal);
  private simpleModalService = inject(SimpleModalService);

  readonly STUDENT_NAME_MAX_LENGTH = STUDENT_NAME_MAX_LENGTH;
  readonly SECTION_NAME_MAX_LENGTH = SECTION_NAME_MAX_LENGTH;
  readonly TEAM_NAME_MAX_LENGTH = TEAM_NAME_MAX_LENGTH;
  readonly EMAIL_MAX_LENGTH = EMAIL_MAX_LENGTH;

  @Input() isEnabled = true;
  courseId = '';
  studentEmail = '';
  student!: Student;

  isTeamnameFieldChanged = false;
  isEmailFieldChanged = false;
  isStudentLoading = false;
  hasStudentLoadingFailed = false;
  isFormSaving = false;

  editForm!: UntypedFormGroup;
  teamFieldSubscription?: Subscription;
  emailFieldSubscription?: Subscription;

  ngOnInit(): void {
    if (!this.isEnabled) {
      this.student = {
        userId: '00000000-0000-4000-9000-000000000001',
        email: 'alice@email.com',
        courseId: '',
        name: 'Alice Betsy',
        comments: 'Alice is a transfer student.',
        teamName: 'Team A',
        sectionName: 'Section A',
        joinState: JoinState.JOINED,
        institute: 'NUS',
        courseName: 'CS3281',
      };
      this.initEditForm();
      return;
    }

    this.route.queryParams.subscribe((queryParams: any) => {
      this.courseId = queryParams.courseid;
      this.studentEmail = queryParams.studentemail;
      this.loadStudentEditDetails(queryParams.courseid, queryParams.studentemail);
    });
  }

  ngOnDestroy(): void {
    if (this.emailFieldSubscription) {
      this.emailFieldSubscription.unsubscribe();
    }
    if (this.teamFieldSubscription) {
      this.teamFieldSubscription.unsubscribe();
    }
  }

  /**
   * Loads student details required for this page.
   */
  loadStudentEditDetails(courseId: string, studentEmail: string): void {
    this.hasStudentLoadingFailed = false;
    this.isStudentLoading = true;
    this.studentService
      .getStudent(courseId, studentEmail)
      .pipe(
        finalize(() => {
          this.isStudentLoading = false;
        }),
      )
      .subscribe({
        next: (student: Student) => {
          this.student = student;
          this.initEditForm();
        },
        error: (resp: ErrorMessageOutput) => {
          this.hasStudentLoadingFailed = true;
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }

  /**
   * Initializes the student details edit form with the fields fetched from the backend.
   * Subscriptions are set up to listen to changes in the 'teamname' fields and 'newstudentemail' fields.
   */
  private initEditForm(): void {
    this.editForm = new UntypedFormGroup({
      'student-name': new UntypedFormControl(this.student.name, [
        Validators.required,
        Validators.maxLength(STUDENT_NAME_MAX_LENGTH),
      ]),
      'section-name': new UntypedFormControl(this.student.sectionName, [
        Validators.required,
        Validators.maxLength(SECTION_NAME_MAX_LENGTH),
      ]),
      'team-name': new UntypedFormControl(this.student.teamName, [
        Validators.required,
        noWhitespaceValidator,
        Validators.maxLength(TEAM_NAME_MAX_LENGTH),
      ]),
      'new-student-email': new UntypedFormControl(
        this.student.email, // original student email initialized
        [Validators.required, Validators.maxLength(EMAIL_MAX_LENGTH)],
      ),
      comments: new UntypedFormControl(this.student.comments),
    });
    this.teamFieldSubscription = this.editForm.get('team-name')!.valueChanges.subscribe(() => {
      this.isTeamnameFieldChanged = true;
    });

    this.emailFieldSubscription = this.editForm.get('new-student-email')!.valueChanges.subscribe(() => {
      this.isEmailFieldChanged = true;
    });
  }

  /**
   * Displays message to user stating that the field is empty.
   */
  displayEmptyFieldMessage(fieldName: string): string {
    return `The field '${fieldName}' should not be empty.`;
  }

  /**
   * Displays message to user stating that the field exceeds the max length.
   */
  displayExceedMaxLengthMessage(fieldName: string, maxLength: number): string {
    return `The field '${fieldName}' should not exceed ${maxLength} characters.`;
  }

  /**
   * Handles logic related to showing the appropriate modal boxes
   * upon submission of the form. Submits the form otherwise.
   */
  onSubmit(resendPastLinksModal: any): void {
    if (!this.isEnabled) {
      return;
    }

    if (this.isTeamnameFieldChanged) {
      const modalContent = `Editing these fields will result in some existing responses from this student to be deleted.
          You may download the data before you make the changes.`;
      const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
        'Delete existing responses?',
        SimpleModalType.WARNING,
        modalContent,
      );
      modalRef.result.then(
        () => {
          this.deleteExistingResponses(resendPastLinksModal);
        },
        () => {},
      );
    } else if (this.isEmailFieldChanged) {
      this.ngbModal.open(resendPastLinksModal);
    } else {
      this.submitEditForm(false);
    }
  }

  /**
   * Shows the `resendPastSessionLinks` modal if email field has changed.
   * Submits the form  otherwise.
   */
  deleteExistingResponses(resendPastLinksModal: any): void {
    if (this.isEmailFieldChanged) {
      this.ngbModal.open(resendPastLinksModal);
    } else {
      this.submitEditForm(false);
    }
  }

  /**
   * Submits the form data to edit the student details.
   */
  submitEditForm(shouldResendPastSessionLinks: boolean): void {
    const reqBody: StudentUpdateRequest = {
      name: this.editForm.value['student-name'],
      email: this.editForm.value['new-student-email'],
      team: this.editForm.value['team-name'],
      section: this.editForm.value['section-name'],
      comments: this.editForm.value.comments,
      isSessionSummarySendEmail: shouldResendPastSessionLinks,
    };

    this.isFormSaving = true;

    this.studentService
      .updateStudent(
        {
          studentId: this.student.userId,
        },
        reqBody,
      )
      .pipe(
        finalize(() => {
          this.isFormSaving = false;
        }),
      )
      .subscribe({
        next: (resp: MessageOutput) => {
          this.navigationService.navigateWithSuccessMessage('/web/instructor/courses/details', resp.message, {
            courseid: this.courseId,
          });
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
