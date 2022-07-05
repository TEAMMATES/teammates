import { Component, OnDestroy, OnInit } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { FormValidator } from 'src/web/types/form-validator';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { StudentService } from '../../../services/student.service';
import { MessageOutput } from '../../../types/api-output';
import { StudentUpdateRequest } from '../../../types/api-request';
import { ErrorMessageOutput } from '../../error-message-output';

/**
 * Account request page.
 */
@Component({
  selector: 'tm-request-page',
  templateUrl: './request-page.component.html',
  styleUrls: ['./request-page.component.scss'],
})
export class RequestPageComponent implements OnInit, OnDestroy {

  FormValidator: typeof FormValidator = FormValidator; // enum

  readonly emptyFieldMessage : string = 'This field should not be empty';

  courseId: string = 'ABCDE';
  studentEmail: string = '';

  isTeamnameFieldChanged: boolean = false;
  isEmailFieldChanged: boolean = false;
  isFormSaving: boolean = false;

  form!: FormGroup;
  teamFieldSubscription?: Subscription;
  emailFieldSubscription?: Subscription;

  constructor(private statusMessageService: StatusMessageService,
              private studentService: StudentService,
              private navigationService: NavigationService) { }

  ngOnInit(): void {
    this.initForm();
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
   * Initializes the student details edit form with the fields fetched from the backend.
   * Subscriptions are set up to listen to changes in the 'teamname' fields and 'newstudentemail' fields.
   */
  private initForm(): void {
    this.form = new FormGroup({
      name: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.PERSON_NAME_MAX_LENGTH)]),
      institute: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.SECTION_NAME_MAX_LENGTH)]),
      country: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.TEAM_NAME_MAX_LENGTH)]),
      email: new FormControl('',
        [Validators.required, Validators.maxLength(FormValidator.EMAIL_MAX_LENGTH), Validators.email]),
      url: new FormControl(''),
      comments: new FormControl(''),
    });
    this.teamFieldSubscription =
      (this.form.get('teamname') as AbstractControl).valueChanges
        .subscribe(() => {
          this.isTeamnameFieldChanged = true;
        });

    this.emailFieldSubscription =
      (this.form.get('newstudentemail') as AbstractControl).valueChanges
        .subscribe(() => {
          this.isEmailFieldChanged = true;
        });
  }

  /**
   * Displays message to user stating that the field exceeds the max length.
   */
  displayExceedMaxLengthMessage(fieldName: string, maxLength: number): string {
    return `${fieldName} should not exceed ${maxLength} characters`;
  }

  /**
   * Submits the account request form.
   */
  onSubmit(): void {
    const reqBody: StudentUpdateRequest = {
      name: this.form.value.name,
      email: this.form.value.newstudentemail,
      team: this.form.value.teamname,
      section: this.form.value.sectionname,
      comments: this.form.value.comments,
      isSessionSummarySendEmail: false,
    };

    this.isFormSaving = true;

    this.studentService.updateStudent({
      courseId: this.courseId,
      studentEmail: '',
      requestBody: reqBody,
    })
      .pipe(finalize(() => {
        this.isFormSaving = false;
      }))
      .subscribe((resp: MessageOutput) => {
        this.navigationService.navigateWithSuccessMessage('/web/instructor/courses/details',
          resp.message, { courseid: this.courseId });
      }, (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      });
  }

  get name() {
    return this.form.get('name');
  }

  get institute() {
    return this.form.get('institute');
  }

  get country() {
    return this.form.get('country');
  }

  get email() {
    return this.form.get('email');
  }

  get url() {
    return this.form.get('url');
  }

  get comments() {
    return this.form.get('comments');
  }
}
