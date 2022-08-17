import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { NavigationService } from '../../../services/navigation.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { AccountRequestCreateErrorResults } from '../../../types/api-output';
import { AccountRequestCreateRequest, AccountRequestType } from '../../../types/api-request';
import { FormValidator } from '../../../types/form-validator';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { MockReCaptcha2Component } from './mock-re-captcha2.component';
import { RequestPageComponent } from './request-page.component';

describe('RequestPageComponent', () => {
  let component: RequestPageComponent;
  let fixture: ComponentFixture<RequestPageComponent>;
  let accountService: AccountService;
  let statusMessageService: StatusMessageService;
  let navigationService: NavigationService;

  /**
   * Populates all fields in the account request form with valid values.
   */
  function fillInForm(componentFixture: ComponentFixture<RequestPageComponent>): void {
    const nameEl: HTMLInputElement = componentFixture.debugElement.query(By.css('#name')).nativeElement;
    nameEl.value = 'Olive Yew';
    nameEl.dispatchEvent(new Event('input'));

    const instituteEl: HTMLInputElement = componentFixture.debugElement.query(By.css('#institute')).nativeElement;
    instituteEl.value = 'TEAMMATES Test Institute';
    instituteEl.dispatchEvent(new Event('input'));

    const countryEl: HTMLInputElement = componentFixture.debugElement.query(By.css('#country')).nativeElement;
    countryEl.value = 'Singapore';
    countryEl.dispatchEvent(new Event('input'));

    const emailEl: HTMLInputElement = componentFixture.debugElement.query(By.css('#email')).nativeElement;
    emailEl.value = 'olive@tmt.tmt';
    emailEl.dispatchEvent(new Event('input'));

    const urlEl: HTMLInputElement = componentFixture.debugElement.query(By.css('#url')).nativeElement;
    urlEl.value = 'https://www.google.com/';
    urlEl.dispatchEvent(new Event('input'));

    const instructorAccountTypeEl: HTMLInputElement =
      componentFixture.debugElement.query(By.css('#instructor-account-type')).nativeElement;
    instructorAccountTypeEl.checked = true;
    instructorAccountTypeEl.dispatchEvent(new Event('change'));

    const commentsEl: HTMLTextAreaElement = componentFixture.debugElement.query(By.css('#comments')).nativeElement;
    commentsEl.value = 'Is TEAMMATES free to use?';
    commentsEl.dispatchEvent(new Event('input'));

    const recaptchaEl: HTMLInputElement =
      componentFixture.debugElement.query(By.css('ngx-recaptcha2 input')).nativeElement;
    recaptchaEl.click();

    componentFixture.componentInstance.form.markAllAsTouched();
  }

  beforeEach(waitForAsync(() => {
    const accountServiceStub: Partial<AccountService> = {
      createAccountRequestAsPublic: () => of({ message: 'Successful' }),
    };
    const statusMessageServiceStub: Partial<StatusMessageService> = {
      showWarningToast: () => {},
    };
    const navigationServiceStub: Partial<NavigationService> = {
      navigateWithSuccessMessage: () => {},
    };

    TestBed.configureTestingModule({
      declarations: [RequestPageComponent, MockReCaptcha2Component],
      imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        AjaxLoadingModule,
      ],
      providers: [
        { provide: AccountService, useValue: accountServiceStub },
        { provide: StatusMessageService, useValue: statusMessageServiceStub },
        { provide: NavigationService, useValue: navigationServiceStub },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestPageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    statusMessageService = TestBed.inject(StatusMessageService);
    navigationService = TestBed.inject(NavigationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with valid fields', () => {
    fillInForm(fixture);
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with error messages', () => {
    component.name!.setErrors({
      invalidField: 'Invalid Name',
    });
    component.institute!.setErrors({
      invalidField: 'Invalid Institute',
    });
    component.country!.setErrors({
      invalidField: 'Invalid Country',
    });
    component.email!.setErrors({
      invalidField: 'Invalid Email',
    });
    component.url!.setErrors({
      invalidField: 'Invalid URL',
    });
    component.accountType!.setErrors({
      notExpected: true,
    });
    component.comments!.setErrors({
      invalidField: 'Invalid Comments',
    });
    component.recaptcha!.setErrors({
      unchecked: true,
    });
    component.form.markAllAsTouched();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when submitting form', () => {
    component.isSubmitting = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('name validity', () => {
    // initially empty
    expect(component.name!.valid).toBeFalsy();
    expect(component.name!.hasError('empty')).toBeTruthy();

    component.name!.setValue('   ');
    expect(component.name!.hasError('empty')).toBeTruthy();

    component.name!.setValue('a'.repeat(FormValidator.PERSON_NAME_MAX_LENGTH + 1));
    expect(component.name!.hasError('maxLength')).toBeTruthy();

    component.name!.setValue(`   ${'a'.repeat(FormValidator.PERSON_NAME_MAX_LENGTH)}   `);
    expect(component.name!.valid).toBeTruthy();
  });

  it('institute validity', () => {
    // initially empty
    expect(component.institute!.valid).toBeFalsy();
    expect(component.institute!.hasError('empty')).toBeTruthy();

    component.institute!.setValue('   ');
    expect(component.institute!.hasError('empty')).toBeTruthy();

    component.institute!.setValue('a'.repeat(FormValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH + 1));
    expect(component.institute!.hasError('maxLength')).toBeTruthy();

    component.institute!.setValue(`   ${'a'.repeat(FormValidator.ACCOUNT_REQUEST_INSTITUTE_NAME_MAX_LENGTH)}   `);
    expect(component.institute!.valid).toBeTruthy();
  });

  it('country validity', () => {
    // initially empty
    expect(component.country!.valid).toBeFalsy();
    expect(component.country!.hasError('empty')).toBeTruthy();

    component.country!.setValue('   ');
    expect(component.country!.hasError('empty')).toBeTruthy();

    component.country!.setValue('a'.repeat(FormValidator.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH + 1));
    expect(component.country!.hasError('maxLength')).toBeTruthy();

    component.country!.setValue(`   ${'a'.repeat(FormValidator.ACCOUNT_REQUEST_COUNTRY_NAME_MAX_LENGTH)}   `);
    expect(component.country!.valid).toBeTruthy();
  });

  it('email validity', () => {
    // initially empty
    expect(component.email!.valid).toBeFalsy();
    expect(component.email!.hasError('empty')).toBeTruthy();

    component.email!.setValue('   ');
    expect(component.email!.hasError('empty')).toBeTruthy();

    component.email!.setValue('a'.repeat(FormValidator.EMAIL_MAX_LENGTH + 1));
    expect(component.email!.hasError('maxLength')).toBeTruthy();

    // email format is not checked in the frontend
    component.email!.setValue(`   ${'a'.repeat(FormValidator.EMAIL_MAX_LENGTH)}   `);
    expect(component.email!.valid).toBeTruthy();
  });

  it('home page url validity', () => {
    // initially empty, which is valid
    expect(component.url!.valid).toBeTruthy();

    component.url!.setValue('   ');
    expect(component.url!.valid).toBeTruthy();

    component.url!.setValue(`   ${'a'.repeat(FormValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH)}   `);
    expect(component.url!.valid).toBeTruthy();

    component.url!.setValue('a'.repeat(FormValidator.ACCOUNT_REQUEST_HOME_PAGE_URL_MAX_LENGTH + 1));
    expect(component.url!.valid).toBeFalsy();
    expect(component.url!.hasError('maxLength')).toBeTruthy();
  });

  it('account type validity', () => {
    // initially 'instructor', which is valid
    expect(component.accountType!.valid).toBeTruthy();

    component.accountType!.setValue('student');
    expect(component.accountType!.valid).toBeFalsy();
    expect(component.accountType!.hasError('notExpected')).toBeTruthy();

    component.accountType!.setValue('unknown-123456');
    expect(component.accountType!.valid).toBeFalsy();
    expect(component.accountType!.hasError('notExpected')).toBeTruthy();
  });

  it('comments validity', () => {
    // initially empty, which is valid
    expect(component.comments!.valid).toBeTruthy();

    component.comments!.setValue('   ');
    expect(component.comments!.valid).toBeTruthy();

    component.comments!.setValue(`   ${'a'.repeat(FormValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH)}   `);
    expect(component.comments!.valid).toBeTruthy();

    component.comments!.setValue('a'.repeat(FormValidator.ACCOUNT_REQUEST_COMMENTS_MAX_LENGTH + 1));
    expect(component.comments!.valid).toBeFalsy();
    expect(component.comments!.hasError('maxLength')).toBeTruthy();
  });

  it('recaptcha validity', () => {
    // recaptcha errors are only set checked inside onSubmit
    expect(component.recaptcha!.valid).toBeTruthy();

    component.onSubmit();

    expect(component.recaptcha!.valid).toBeFalsy();
    expect(component.recaptcha!.hasError('unchecked')).toBeTruthy();

    // setting value will clear the validation error
    component.recaptcha!.setValue('');
    expect(component.recaptcha!.valid).toBeTruthy();
    expect(component.recaptcha!.hasError('unchecked')).toBeFalsy();

    // simulation of checking recaptcha box, but it expires by the time of calling onSubmit again
    component.onSubmit();
    expect(component.recaptcha!.valid).toBeFalsy();
    expect(component.recaptcha!.hasError('unchecked')).toBeTruthy();

    // simulation of checking recaptcha box and calling onSubmit again
    component.recaptcha!.setValue('response-123456');
    component.onSubmit();
    expect(component.recaptcha!.valid).toBeTruthy();
  });

  it('should action correctly when it calls onSubmit and succeeds', () => {
    fillInForm(fixture);
    fixture.detectChanges();
    const createAccountRequestAsPublicSpy = jest.spyOn(accountService, 'createAccountRequestAsPublic');
    const navigateWithSuccessMessageSpy = jest.spyOn(navigationService, 'navigateWithSuccessMessage');
    const recaptchaEl: MockReCaptcha2Component = fixture.debugElement.query(By.css('ngx-recaptcha2')).componentInstance;
    const request: AccountRequestCreateRequest = {
      instructorName: 'Olive Yew',
      instructorInstitute: 'TEAMMATES Test Institute',
      instructorCountry: 'Singapore',
      instructorEmail: 'olive@tmt.tmt',
      instructorHomePageUrl: 'https://www.google.com/',
      comments: 'Is TEAMMATES free to use?',
    };

    component.onSubmit();

    expect(createAccountRequestAsPublicSpy).toHaveBeenCalledTimes(1);
    expect(createAccountRequestAsPublicSpy).toHaveBeenCalledWith({
      accountRequestType: AccountRequestType.INSTRUCTOR_ACCOUNT,
      captchaResponse: recaptchaEl.getResponse(),
      requestBody: request,
    });
    expect(navigateWithSuccessMessageSpy).toHaveBeenCalledTimes(1);
    expect(navigateWithSuccessMessageSpy).toHaveBeenCalledWith('/web/front/home', component.successMessage);
  });

  it('should action correctly when it calls onSubmit and the form is invalid', () => {
    fillInForm(fixture);
    fixture.detectChanges();
    const createAccountRequestAsPublicSpy = jest.spyOn(accountService, 'createAccountRequestAsPublic');
    const navigateWithSuccessMessageSpy = jest.spyOn(navigationService, 'navigateWithSuccessMessage');
    const showWarningToastSpy = jest.spyOn(statusMessageService, 'showWarningToast');
    component.name!.setErrors({
      invalidField: 'Invalid Name',
    });

    component.onSubmit();

    expect(component.form.valid).toBeFalsy();
    expect(showWarningToastSpy).toHaveBeenCalledTimes(1);
    expect(createAccountRequestAsPublicSpy).not.toHaveBeenCalled();
    expect(navigateWithSuccessMessageSpy).not.toHaveBeenCalled();
    // recaptcha is not reset if it does not call the backend
    expect(component.recaptcha!.value).toBeTruthy();
  });

  it(`should action correctly when it calls onSubmit and fails to create account request,
  response is of type ErrorMessageOutput`, () => {
    fillInForm(fixture);
    fixture.detectChanges();
    const errorMessage: string = 'Some errors';
    const createAccountRequestAsPublicSpy = jest.spyOn(accountService, 'createAccountRequestAsPublic')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const navigateWithSuccessMessageSpy = jest.spyOn(navigationService, 'navigateWithSuccessMessage');
    const showWarningToastSpy = jest.spyOn(statusMessageService, 'showWarningToast');

    component.onSubmit();

    expect(createAccountRequestAsPublicSpy).toHaveBeenCalledTimes(1);
    expect(showWarningToastSpy).toHaveBeenCalledTimes(1);
    expect(showWarningToastSpy).toHaveBeenCalledWith(component.failureMessage);
    expect(navigateWithSuccessMessageSpy).not.toHaveBeenCalled();
    expect(component.isSubmitting).toBeFalsy();
    expect(component.backendOtherErrorMessage).toBe(errorMessage);

    expect(component.recaptcha!.value).toBeFalsy();
    // the form is still valid because no errors are set for any specific field
    expect(component.form.valid).toBeTruthy();
  });

  it(`should action correctly when it calls onSubmit and fails to create account request,
  response is of type AccountRequestCreateErrorResultsWrapper`, () => {
    fillInForm(fixture);
    fixture.detectChanges();
    const errorMessages: AccountRequestCreateErrorResults = {
      invalidNameMessage: 'Invalid name',
      invalidInstituteMessage: 'Invalid institute',
      invalidCountryMessage: 'Invalid country',
      invalidEmailMessage: 'Invalid email',
      invalidHomePageUrlMessage: 'Invalid home page URL',
      invalidCommentsMessage: 'Invalid comments',
    };
    const createAccountRequestAsPublicSpy = jest.spyOn(accountService, 'createAccountRequestAsPublic')
      .mockReturnValue(throwError({
        error: errorMessages,
      }));
    const navigateWithSuccessMessageSpy = jest.spyOn(navigationService, 'navigateWithSuccessMessage');
    const showWarningToastSpy = jest.spyOn(statusMessageService, 'showWarningToast');

    component.onSubmit();

    expect(createAccountRequestAsPublicSpy).toHaveBeenCalledTimes(1);
    expect(showWarningToastSpy).toHaveBeenCalledTimes(1);
    expect(showWarningToastSpy).toHaveBeenCalledWith(component.failureMessage);
    expect(navigateWithSuccessMessageSpy).not.toHaveBeenCalled();
    expect(component.isSubmitting).toBeFalsy();
    expect(component.backendOtherErrorMessage).toBe(component.invalidFieldsMessage);

    expect(component.form.valid).toBeFalsy();
    expect(component.name!.getError('invalidField')).toBe(errorMessages.invalidNameMessage);
    expect(component.institute!.getError('invalidField')).toBe(errorMessages.invalidInstituteMessage);
    expect(component.country!.getError('invalidField')).toBe(errorMessages.invalidCountryMessage);
    expect(component.email!.getError('invalidField')).toBe(errorMessages.invalidEmailMessage);
    expect(component.url!.getError('invalidField')).toBe(errorMessages.invalidHomePageUrlMessage);
    expect(component.comments!.getError('invalidField')).toBe(errorMessages.invalidCommentsMessage);
  });

  it('should call onSubmit when the Submit button is clicked', () => {
    const onSubmitSpy = jest.spyOn(component, 'onSubmit');

    fixture.debugElement.query(By.css('#btn-submit')).nativeElement.click();

    expect(onSubmitSpy).toHaveBeenCalledTimes(1);
  });

  it('should not display errors when fields are not touched', () => {
    const displayedNameErrorDe = fixture.debugElement.query(By.css('#name-error'));
    const displayedInstituteErrorDe = fixture.debugElement.query(By.css('#institute-error'));
    const displayedCountryErrorDe = fixture.debugElement.query(By.css('#country-error'));
    const displayedEmailErrorDe = fixture.debugElement.query(By.css('#email-error'));
    const displayedUrlErrorDe = fixture.debugElement.query(By.css('#url-error'));
    const displayedCommentsErrorDe = fixture.debugElement.query(By.css('#comments-error'));
    const displayedReCaptchaErrorDe = fixture.debugElement.query(By.css('#recaptcha-error'));

    expect(displayedNameErrorDe).toBeNull();
    expect(displayedInstituteErrorDe).toBeNull();
    expect(displayedCountryErrorDe).toBeNull();
    expect(displayedEmailErrorDe).toBeNull();
    expect(displayedUrlErrorDe).toBeNull();
    expect(displayedCommentsErrorDe).toBeNull();
    expect(displayedReCaptchaErrorDe).toBeNull();
  });

  it('should display errors after clicking the Submit button for the first time', () => {
    const markAllAsTouchedSpy = jest.spyOn(component.form, 'markAllAsTouched');

    component.onSubmit();
    fixture.detectChanges();

    const displayedNameError: string = fixture.debugElement.query(By.css('#name-error span:not([hidden])'))
      .nativeElement.textContent;
    const displayedInstituteError: string = fixture.debugElement.query(By.css('#institute-error span:not([hidden])'))
      .nativeElement.textContent;
    const displayedCountryError: string = fixture.debugElement.query(By.css('#country-error span:not([hidden])'))
      .nativeElement.textContent;
    const displayedEmailError: string = fixture.debugElement.query(By.css('#email-error span:not([hidden])'))
      .nativeElement.textContent;
    const displayedReCaptchaError: string = fixture.debugElement.query(By.css('#recaptcha-error span:not([hidden])'))
      .nativeElement.textContent;

    expect(markAllAsTouchedSpy).toHaveBeenCalledTimes(1);
    expect(displayedNameError).toBe(component.emptyFieldMessage);
    expect(displayedInstituteError).toBe(component.emptyFieldMessage);
    expect(displayedCountryError).toBe(component.emptyFieldMessage);
    expect(displayedEmailError).toBe(component.emptyFieldMessage);
    expect(displayedReCaptchaError).toContain('Please check the box');
  });
});
