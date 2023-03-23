import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
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
import { FormValidator } from '../../../types/form-validator';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Instructor course student edit page.
 */
@Component({
  selector: 'tm-instructor-course-student-edit-page',
  templateUrl: './instructor-course-student-edit-page.component.html',
  styleUrls: ['./instructor-course-student-edit-page.component.scss'],
})
export class InstructorCourseStudentEditPageComponent implements OnInit, OnDestroy {

  FormValidator: typeof FormValidator = FormValidator; // enum

  @Input() isEnabled: boolean = true;
  courseId: string = '';
  studentEmail: string = '';
  student!: Student;

  isTeamnameFieldChanged: boolean = false;
  isEmailFieldChanged: boolean = false;
  isStudentLoading: boolean = false;
  hasStudentLoadingFailed: boolean = false;
  isFormSaving: boolean = false;

  editForm!: UntypedFormGroup;
  teamFieldSubscription?: Subscription;
  emailFieldSubscription?: Subscription;

  constructor(private route: ActivatedRoute,
              private statusMessageService: StatusMessageService,
              private studentService: StudentService,
              private navigationService: NavigationService,
              private ngbModal: NgbModal,
              private simpleModalService: SimpleModalService) { }

  ngOnInit(): void {
    if (!this.isEnabled) {
      this.student = {
        email: 'alice@email.com',
        courseId: '',
        name: 'Alice Betsy',
        comments: 'Alice is a transfer student.',
        teamName: 'Team A',
        sectionName: 'Section A',
        joinState: JoinState.JOINED,
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
      (this.emailFieldSubscription as Subscription).unsubscribe();
    }
    if (this.teamFieldSubscription) {
      (this.teamFieldSubscription as Subscription).unsubscribe();
    }
  }

  /**
   * Loads student details required for this page.
   */
  loadStudentEditDetails(courseId: string, studentEmail: string): void {
    this.hasStudentLoadingFailed = false;
    this.isStudentLoading = true;
    this.studentService.getStudent(
        courseId, studentEmail,
    ).pipe(finalize(() => {
      this.isStudentLoading = false;
    })).subscribe({
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
      'student-name': new UntypedFormControl(this.student.name,
          [Validators.required, Validators.maxLength(FormValidator.STUDENT_NAME_MAX_LENGTH)]),
      'section-name': new UntypedFormControl(this.student.sectionName,
          [Validators.required, Validators.maxLength(FormValidator.SECTION_NAME_MAX_LENGTH)]),
      'team-name': new UntypedFormControl(this.student.teamName,
          [Validators.required, Validators.maxLength(FormValidator.TEAM_NAME_MAX_LENGTH)]),
      'new-student-email': new UntypedFormControl(this.student.email, // original student email initialized
          [Validators.required, Validators.maxLength(FormValidator.EMAIL_MAX_LENGTH)]),
      comments: new UntypedFormControl(this.student.comments),
    });
    this.teamFieldSubscription =
        (this.editForm.get('team-name') as AbstractControl).valueChanges
            .subscribe(() => {
              this.isTeamnameFieldChanged = true;
            });

    this.emailFieldSubscription =
        (this.editForm.get('new-student-email') as AbstractControl).valueChanges
            .subscribe(() => {
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
      const modalContent: string =
          `Editing these fields will result in some existing responses from this student to be deleted.
          You may download the data before you make the changes.`;
      const modalRef: NgbModalRef = this.simpleModalService.openConfirmationModal(
          'Delete existing responses?', SimpleModalType.WARNING, modalContent);
      modalRef.result.then(() => {
        this.deleteExistingResponses(resendPastLinksModal);
      }, () => {});
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

    this.studentService.updateStudent({
      courseId: this.courseId,
      studentEmail: this.student.email,
      requestBody: reqBody,
    })
      .pipe(finalize(() => {
        this.isFormSaving = false;
      }))
      .subscribe({
        next: (resp: MessageOutput) => {
          this.navigationService.navigateWithSuccessMessage('/web/instructor/courses/details',
              resp.message, { courseid: this.courseId });
        },
        error: (resp: ErrorMessageOutput) => {
          this.statusMessageService.showErrorToast(resp.error.message);
        },
      });
  }
}
