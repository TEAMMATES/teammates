import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { AccountRequest, AccountRequestStatus } from '../../../types/api-output';
import { AjaxLoadingModule } from '../ajax-loading/ajax-loading.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import {
  ProcessAccountRequestPanelComponent,
  ProcessAccountRequestPanelStatus,
} from './process-account-request-panel.component';

const DEFAULT_SUBMITTED_ACCOUNT_REQUEST: AccountRequest = {
  name: 'U.R. Nice',
  institute: 'TEAMMATES Test Institute, Singapore',
  email: 'nice@tmt.tmt',
  homePageUrl: 'https://www.comp.nus.edu.sg/',
  comments: 'Is TEAMMATES free to use?',
  registrationKey: 'nice-123456789',
  status: AccountRequestStatus.SUBMITTED,
  createdAt: new Date('2022-08-05T04:18:00Z').getTime(),
};

const DEFAULT_APPROVED_ACCOUNT_REQUEST: AccountRequest = {
  name: 'Bob Smith',
  institute: 'TMT, Singapore',
  email: 'bob_smith@tmt.tmt',
  homePageUrl: 'Home page url can contain spaces.',
  comments: 'Comments can contain\nnew line\n"quotation mark"\nslash same line: \\n\n\nempty line',
  registrationKey: 'bob-123456789',
  status: AccountRequestStatus.APPROVED,
  createdAt: new Date('2022-08-05T04:25:00Z').getTime(),
  lastProcessedAt: new Date('2022-08-05T05:00:00Z').getTime(),
};

const DEFAULT_REJECTED_ACCOUNT_REQUEST: AccountRequest = {
  name: 'Emily Fung',
  institute: 'TMT, Singapore',
  email: 'emily@tmt.tmt',
  homePageUrl: '',
  comments: '',
  registrationKey: 'emily-123456789',
  status: AccountRequestStatus.REJECTED,
  createdAt: new Date('2022-07-20T10:00:00Z').getTime(),
  lastProcessedAt: new Date('2022-07-21T12:15:00Z').getTime(),
};

const DEFAULT_REGISTERED_ACCOUNT_REQUEST: AccountRequest = {
  name: 'Bernie Pei Yip Hang',
  institute: 'TMT, Singapore',
  email: 'bernie@tmt.tmt',
  homePageUrl: '',
  comments: '',
  registrationKey: 'bernie-123456789',
  status: AccountRequestStatus.REGISTERED,
  createdAt: new Date('2022-08-07T23:59:59Z').getTime(),
  lastProcessedAt: new Date('2022-08-07T23:59:59Z').getTime(),
  registeredAt: new Date('2022-08-08T00:00:00Z').getTime(),
};

describe('ProcessAccountRequestPanelComponent', () => {
  let fixture: ComponentFixture<ProcessAccountRequestPanelComponent>;
  let component: ProcessAccountRequestPanelComponent;
  let accountService: AccountService;
  let timezoneService: TimezoneService;
  let simpleModalService: SimpleModalService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ProcessAccountRequestPanelComponent],
      imports: [
        AjaxLoadingModule,
        HttpClientTestingModule,
        PanelChevronModule,
        NoopAnimationsModule,
        TeammatesCommonModule,
        FormsModule,
        CommonModule,
      ],
      providers: [
        AccountService,
        TimezoneService,
        SimpleModalService,
        StatusMessageService,
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcessAccountRequestPanelComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    timezoneService = TestBed.inject(TimezoneService);
    simpleModalService = TestBed.inject(SimpleModalService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component.accountRequest = DEFAULT_SUBMITTED_ACCOUNT_REQUEST;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default SUBMITTED account request', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with default APPROVED account request', () => {
    component.accountRequest = DEFAULT_APPROVED_ACCOUNT_REQUEST;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with default REJECTED account request showing registeredAt', () => {
    component.accountRequest = DEFAULT_REJECTED_ACCOUNT_REQUEST;
    component.showRegisteredAt = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with default REGISTERED account request showing registeredAt', () => {
    component.accountRequest = DEFAULT_REGISTERED_ACCOUNT_REQUEST;
    component.showRegisteredAt = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when panelStatus is EDITING', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when panelStatus is UNDEFINED', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.UNDEFINED;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when saving changes', () => {
    component.isSavingChanges = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a collapsed tab', waitForAsync(() => {
    component.isTabExpanded = false;
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      expect(fixture).toMatchSnapshot();
    });
  }));

  it('should snap with a non-default Bootstrap background color for panel header and a non-empty error message', () => {
    component.panelHeaderColor = 'info';
    component.errorMessage = 'Some errors!';
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap without showing panel header', () => {
    component.showPanelHeader = false;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should use the returned timezone when timezone can be guessed', () => {
    const timezone: string = 'Asia/Singapore';
    const guessTimezoneSpy = jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue(timezone);

    component.ngOnInit();

    expect(guessTimezoneSpy).toHaveBeenCalledTimes(1);
    expect(component.timezone).toBe(timezone);
  });

  it('should use UTC when timezone cannot be guessed', () => {
    const guessTimezoneSpy = jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue('');

    component.ngOnInit();

    expect(guessTimezoneSpy).toHaveBeenCalledTimes(1);
    expect(component.timezone).toBe('UTC');
  });

  it('should populate panelStatus correctly from account request status', () => {
    expect(component.getPanelStatusFromAccountRequestStatus(AccountRequestStatus.SUBMITTED))
      .toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
    expect(component.getPanelStatusFromAccountRequestStatus(AccountRequestStatus.APPROVED))
      .toBe(ProcessAccountRequestPanelStatus.APPROVED);
    expect(component.getPanelStatusFromAccountRequestStatus(AccountRequestStatus.REJECTED))
      .toBe(ProcessAccountRequestPanelStatus.REJECTED);
    expect(component.getPanelStatusFromAccountRequestStatus(AccountRequestStatus.REGISTERED))
      .toBe(ProcessAccountRequestPanelStatus.REGISTERED);
    expect(component.getPanelStatusFromAccountRequestStatus(undefined!))
      .toBe(ProcessAccountRequestPanelStatus.UNDEFINED);
  });

  it('should call getPanelStatusFromAccountRequestStatus to init panelStatus', () => {
    const getPanelStatusFromAccountRequestStatusSpy = jest.spyOn(component, 'getPanelStatusFromAccountRequestStatus');

    component.ngOnInit();

    expect(getPanelStatusFromAccountRequestStatusSpy).toHaveBeenCalledTimes(1);
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
  });

  it('should display account request fields correctly', () => {
    component.accountRequest = DEFAULT_APPROVED_ACCOUNT_REQUEST;
    component.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
    component.showRegisteredAt = true;
    component.timezone = 'UTC';
    fixture.detectChanges();

    const displayedStatus: string = fixture.debugElement.query(By.css('#status'))
      .nativeElement.textContent;
    const displayedSubmittedAt: string = fixture.debugElement.query(By.css('#submitted-at'))
      .nativeElement.textContent;
    const displayedLastProcessedAt: string = fixture.debugElement.query(By.css('#last-processed-at'))
      .nativeElement.textContent;
    const displayedRegisteredAt: string = fixture.debugElement.query(By.css('#registered-at'))
      .nativeElement.textContent;
    const displayedName: string = fixture.debugElement.query(By.css('#name'))
      .nativeElement.textContent;
    const displayedInstitute: string = fixture.debugElement.query(By.css('#institute'))
      .nativeElement.textContent;
    const displayedEmail: string = fixture.debugElement.query(By.css('#email'))
      .nativeElement.textContent;
    const displayedHomePageUrl: string = fixture.debugElement.query(By.css('#home-page-url'))
      .nativeElement.textContent;
    const displayedComments: string = fixture.debugElement.query(By.css('#comments'))
      .nativeElement.textContent;

    expect(displayedStatus).toBe(DEFAULT_APPROVED_ACCOUNT_REQUEST.status.toString());
    expect(displayedSubmittedAt).toBe('Fri, 05 Aug 2022, 04:25 AM UTC');
    expect(displayedLastProcessedAt).toBe('Fri, 05 Aug 2022, 05:00 AM UTC');
    expect(displayedRegisteredAt).toBe('');
    expect(displayedName).toBe(DEFAULT_APPROVED_ACCOUNT_REQUEST.name);
    expect(displayedInstitute).toBe(DEFAULT_APPROVED_ACCOUNT_REQUEST.institute);
    expect(displayedEmail).toBe(DEFAULT_APPROVED_ACCOUNT_REQUEST.email);
    expect(displayedHomePageUrl).toBe(DEFAULT_APPROVED_ACCOUNT_REQUEST.homePageUrl);
    expect(displayedComments).toBe(DEFAULT_APPROVED_ACCOUNT_REQUEST.comments);
  });

  it('should not display empty error message', () => {
    expect(fixture.debugElement.query(By.css('#error-message'))).toBeNull();
  });

  it('should display non-empty error message correctly', () => {
    const errorMessage: string = 'Error';
    component.errorMessage = errorMessage;
    fixture.detectChanges();

    const displayedErrorMessage: string = fixture.debugElement.query(By.css('#error-message'))
      .nativeElement.textContent;

    expect(displayedErrorMessage).toBe(errorMessage);
  });

  it('should action correctly when it calls editAccountRequest', () => {
    component.errorMessage = 'Previous error';

    component.editAccountRequest();

    expect(component.editedName).toBe(component.accountRequest.name);
    expect(component.editedInstitute).toBe(component.accountRequest.institute);
    expect(component.editedEmail).toBe(component.accountRequest.email);
    expect(component.errorMessage).toBe('');
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.EDITING);
  });

  it('should display input fields correctly', waitForAsync(() => {
    component.editedName = DEFAULT_SUBMITTED_ACCOUNT_REQUEST.name;
    component.editedInstitute = DEFAULT_SUBMITTED_ACCOUNT_REQUEST.institute;
    component.editedEmail = DEFAULT_SUBMITTED_ACCOUNT_REQUEST.email;
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      const displayedEditedName: string = fixture.debugElement.query(By.css('#name-input'))
        .nativeElement.value;
      const displayedEditedInstitute: string = fixture.debugElement.query(By.css('#institute-input'))
        .nativeElement.value;
      const displayedEditedEmail: string = fixture.debugElement.query(By.css('#email-input'))
        .nativeElement.value;

      expect(displayedEditedName).toBe(component.editedName);
      expect(displayedEditedInstitute).toBe(component.editedInstitute);
      expect(displayedEditedEmail).toBe(component.editedEmail);
    });
  }));

  it('should update property values correctly when input fields are changed', waitForAsync(() => {
    const editedName: string = 'My New Name';
    const editedInstitute: string = 'New Institute, Singapore';
    const editedEmail: string = 'new_email@tmt.tmt';
    component.editedName = DEFAULT_SUBMITTED_ACCOUNT_REQUEST.name;
    component.editedInstitute = DEFAULT_SUBMITTED_ACCOUNT_REQUEST.institute;
    component.editedEmail = DEFAULT_SUBMITTED_ACCOUNT_REQUEST.email;
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    fixture.detectChanges();

    fixture.whenStable().then(() => {
      const nameEl: HTMLInputElement = fixture.debugElement.query(By.css('#name-input')).nativeElement;
      nameEl.value = editedName;
      nameEl.dispatchEvent(new Event('input'));

      const instituteEl: HTMLInputElement = fixture.debugElement.query(By.css('#institute-input')).nativeElement;
      instituteEl.value = editedInstitute;
      instituteEl.dispatchEvent(new Event('input'));

      const emailEl: HTMLInputElement = fixture.debugElement.query(By.css('#email-input')).nativeElement;
      emailEl.value = editedEmail;
      emailEl.dispatchEvent(new Event('input'));

      expect(component.editedName).toBe(editedName);
      expect(component.editedInstitute).toBe(editedInstitute);
      expect(component.editedEmail).toBe(editedEmail);
    });
  }));

  it('should action correctly when it calls cancelEditAccountRequest', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    component.errorMessage = 'Previous error';

    component.cancelEditAccountRequest();

    expect(component.errorMessage).toBe('');
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
  });

  it('should action correctly when it calls saveAccountRequest and succeeds', () => {
    const editedName: string = 'My New Name';
    const editedInstitute: string = 'New Institute, Singapore';
    const editedEmail: string = 'new_email@tmt.tmt';
    const editedAccountRequest: AccountRequest = {
      ...DEFAULT_SUBMITTED_ACCOUNT_REQUEST,
      name: editedName,
      institute: editedInstitute,
      email: editedEmail,
      lastProcessedAt: Date.now(),
    };
    component.editedName = editedName;
    component.editedInstitute = editedInstitute;
    component.editedEmail = editedEmail;
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const updateAccountRequestSpy = jest.spyOn(accountService, 'updateAccountRequest')
      .mockReturnValue(of(editedAccountRequest));
    const showSuccessToastSpy = jest.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.saveAccountRequest();

    expect(updateAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(updateAccountRequestSpy).toHaveBeenCalledWith(
      DEFAULT_SUBMITTED_ACCOUNT_REQUEST.email,
      DEFAULT_SUBMITTED_ACCOUNT_REQUEST.institute,
      {
        instructorName: editedName,
        instructorInstitute: editedInstitute,
        instructorEmail: editedEmail,
      },
    );
    expect(component.accountRequest).toEqual(editedAccountRequest);
    expect(component.errorMessage).toBe('');
    expect(showSuccessToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
  });

  it('should populate error message and show error toast when it calls saveAccountRequest and fails', () => {
    component.editedName = 'My New Name';
    component.editedInstitute = 'New Institute, Singapore';
    component.editedEmail = 'new_email@tmt.tmt';
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const errorMessage = 'Test error message';
    const updateAccountRequestSpy = jest.spyOn(accountService, 'updateAccountRequest')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});

    component.saveAccountRequest();

    expect(updateAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequest).toEqual(DEFAULT_SUBMITTED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe(errorMessage);
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.EDITING);
  });

  it('should action correctly when it calls approveAccountRequest and succeeds', () => {
    component.accountRequest = DEFAULT_REJECTED_ACCOUNT_REQUEST;
    component.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const approveAccountRequestSpy = jest.spyOn(accountService, 'approveAccountRequest')
      .mockReturnValue(of(DEFAULT_APPROVED_ACCOUNT_REQUEST));
    const showSuccessToastSpy = jest.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.approveAccountRequest();

    expect(approveAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(approveAccountRequestSpy).toHaveBeenCalledWith(
      DEFAULT_REJECTED_ACCOUNT_REQUEST.email,
      DEFAULT_REJECTED_ACCOUNT_REQUEST.institute,
    );
    expect(component.accountRequest).toEqual(DEFAULT_APPROVED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe('');
    expect(showSuccessToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.APPROVED);
  });

  it('should populate error message and show error toast when it calls approveAccountRequest and fails', () => {
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const errorMessage = 'Test error message';
    const approveAccountRequestSpy = jest.spyOn(accountService, 'approveAccountRequest')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});

    component.approveAccountRequest();

    expect(approveAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequest).toEqual(DEFAULT_SUBMITTED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe(errorMessage);
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
  });

  it('should action correctly when it calls rejectAccountRequest and succeeds', () => {
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const rejectAccountRequestSpy = jest.spyOn(accountService, 'rejectAccountRequest')
      .mockReturnValue(of(DEFAULT_REJECTED_ACCOUNT_REQUEST));
    const showSuccessToastSpy = jest.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.rejectAccountRequest();

    expect(rejectAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(rejectAccountRequestSpy).toHaveBeenCalledWith(
      DEFAULT_SUBMITTED_ACCOUNT_REQUEST.email,
      DEFAULT_SUBMITTED_ACCOUNT_REQUEST.institute,
    );
    expect(component.accountRequest).toEqual(DEFAULT_REJECTED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe('');
    expect(showSuccessToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.REJECTED);
  });

  it('should populate error message and show error toast when it calls rejectAccountRequest and fails', () => {
    component.accountRequest = DEFAULT_APPROVED_ACCOUNT_REQUEST;
    component.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const errorMessage = 'Test error message';
    const rejectAccountRequestSpy = jest.spyOn(accountService, 'rejectAccountRequest')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});

    component.rejectAccountRequest();

    expect(rejectAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequest).toEqual(DEFAULT_APPROVED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe(errorMessage);
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.APPROVED);
  });

  it('should action correctly when it calls deleteAccountRequest and succeeds', waitForAsync(() => {
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const deleteAccountRequestSpy = jest.spyOn(accountService, 'deleteAccountRequest')
      .mockReturnValue(of({
        message: 'This message will not be used',
      }));
    const openConfirmationModalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({}, Promise.resolve());
    });
    const showSuccessToastSpy = jest.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.deleteAccountRequest();

    fixture.whenStable().then(() => {
      expect(openConfirmationModalSpy).toHaveBeenCalledTimes(1);
      expect(deleteAccountRequestSpy).toHaveBeenCalledTimes(1);
      expect(deleteAccountRequestSpy).toHaveBeenCalledWith(
        DEFAULT_SUBMITTED_ACCOUNT_REQUEST.email,
        DEFAULT_SUBMITTED_ACCOUNT_REQUEST.institute,
      );
      expect(component.accountRequest).toEqual(DEFAULT_SUBMITTED_ACCOUNT_REQUEST);
      expect(component.errorMessage).toBe('');
      expect(showSuccessToastSpy).toHaveBeenCalledTimes(1);
      expect(component.isSavingChanges).toBeFalsy();
      expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.UNDEFINED);
    });
  }));

  it(`should not delete the account request when it calls deleteAccountRequest 
  and rejects in the confirmation modal`, waitForAsync(() => {
    const errorMessage: string = 'Previous error';
    component.errorMessage = errorMessage;
    const deleteAccountRequestSpy = jest.spyOn(accountService, 'deleteAccountRequest')
      .mockReturnValue(of({
        message: 'This message will not be used',
      }));
    const openConfirmationModalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({}, Promise.reject());
    });

    component.deleteAccountRequest();

    fixture.whenStable().then(() => {
      expect(openConfirmationModalSpy).toHaveBeenCalledTimes(1);
      expect(deleteAccountRequestSpy).not.toHaveBeenCalled();
      expect(component.accountRequest).toEqual(DEFAULT_SUBMITTED_ACCOUNT_REQUEST);
      expect(component.errorMessage).toBe(errorMessage);
      expect(component.isSavingChanges).toBeFalsy();
      expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
    });
  }));

  it(`should not open confirmation modal when it calls showOptionalResetAccountRequestWarning 
  and account request status is not REGISTERED`, () => {
    const resetAccountRequestSpy = jest.spyOn(component, 'resetAccountRequest').mockImplementation(() => {});
    const openConfirmationModalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({}, Promise.resolve());
    });

    component.showOptionalResetAccountRequestWarning();

    expect(openConfirmationModalSpy).not.toHaveBeenCalled();
    expect(resetAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it(`should call resetAccountRequest when it calls showOptionalResetAccountRequestWarning 
  and account request status is REGISTERED and confirms in the confirmation modal`, waitForAsync(() => {
    component.accountRequest = DEFAULT_REGISTERED_ACCOUNT_REQUEST;
    const resetAccountRequestSpy = jest.spyOn(component, 'resetAccountRequest').mockImplementation(() => {});
    const openConfirmationModalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({}, Promise.resolve());
    });

    component.showOptionalResetAccountRequestWarning();

    fixture.whenStable().then(() => {
      expect(openConfirmationModalSpy).toHaveBeenCalledTimes(1);
      expect(resetAccountRequestSpy).toHaveBeenCalledTimes(1);
    });
  }));

  it(`should not call resetAccountRequest when it calls showOptionalResetAccountRequestWarning 
  and account request status is REGISTERED and rejects in the confirmation modal`, waitForAsync(() => {
    component.accountRequest = DEFAULT_REGISTERED_ACCOUNT_REQUEST;
    const resetAccountRequestSpy = jest.spyOn(component, 'resetAccountRequest').mockImplementation(() => {});
    const openConfirmationModalSpy = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef({}, Promise.reject());
    });

    component.showOptionalResetAccountRequestWarning();

    fixture.whenStable().then(() => {
      expect(openConfirmationModalSpy).toHaveBeenCalledTimes(1);
      expect(resetAccountRequestSpy).not.toHaveBeenCalled();
    });
  }));

  it('should action correctly when it calls resetAccountRequest and succeeds', () => {
    component.accountRequest = DEFAULT_REGISTERED_ACCOUNT_REQUEST;
    component.panelStatus = ProcessAccountRequestPanelStatus.REGISTERED;
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const resetAccountRequestSpy = jest.spyOn(accountService, 'resetAccountRequest')
      .mockReturnValue(of(DEFAULT_SUBMITTED_ACCOUNT_REQUEST));
    const showSuccessToastSpy = jest.spyOn(statusMessageService, 'showSuccessToast').mockImplementation(() => {});

    component.resetAccountRequest();

    expect(resetAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(resetAccountRequestSpy).toHaveBeenCalledWith(
      DEFAULT_REGISTERED_ACCOUNT_REQUEST.email,
      DEFAULT_REGISTERED_ACCOUNT_REQUEST.institute,
    );
    expect(component.accountRequest).toEqual(DEFAULT_SUBMITTED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe('');
    expect(showSuccessToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.SUBMITTED);
  });

  it('should populate error message and show error toast when it calls resetAccountRequest and fails', () => {
    component.accountRequest = DEFAULT_REJECTED_ACCOUNT_REQUEST;
    component.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
    component.errorMessage = 'Previous error';
    component.isSavingChanges = true;
    const errorMessage = 'Test error message';
    const resetAccountRequestSpy = jest.spyOn(accountService, 'resetAccountRequest')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast').mockImplementation(() => {});

    component.resetAccountRequest();

    expect(resetAccountRequestSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequest).toEqual(DEFAULT_REJECTED_ACCOUNT_REQUEST);
    expect(component.errorMessage).toBe(errorMessage);
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(component.isSavingChanges).toBeFalsy();
    expect(component.panelStatus).toBe(ProcessAccountRequestPanelStatus.REJECTED);
  });

  it('should toggle card when panel header is clicked', () => {
    const toggleCardSpy = jest.spyOn(component, 'toggleCard');

    fixture.debugElement.query(By.css('#panel-header')).triggerEventHandler('click', null);
    expect(component.isTabExpanded).toBeFalsy();

    fixture.debugElement.query(By.css('#panel-header')).triggerEventHandler('click', null);
    expect(component.isTabExpanded).toBeTruthy();

    expect(toggleCardSpy).toHaveBeenCalledTimes(2);
  });

  it('should call editAccountRequest when the Edit button is clicked (panelStatus is SUBMITTED)', () => {
    const editAccountRequestSpy = jest.spyOn(component, 'editAccountRequest');

    fixture.debugElement.query(By.css('#submitted-edit-button')).triggerEventHandler('click', null);

    expect(editAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call approveAccountRequest when the Approve button is clicked (panelStatus is SUBMITTED)', () => {
    const approveAccountRequestSpy = jest.spyOn(component, 'approveAccountRequest');

    fixture.debugElement.query(By.css('#submitted-approve-button')).triggerEventHandler('click', null);

    expect(approveAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call rejectAccountRequest when the Reject button is clicked (panelStatus is SUBMITTED)', () => {
    const rejectAccountRequestSpy = jest.spyOn(component, 'rejectAccountRequest');

    fixture.debugElement.query(By.css('#submitted-reject-button')).triggerEventHandler('click', null);

    expect(rejectAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call deleteAccountRequest when the Delete button is clicked (panelStatus is SUBMITTED)', () => {
    const deleteAccountRequestSpy = jest.spyOn(component, 'deleteAccountRequest');
    jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef();
    });

    fixture.debugElement.query(By.css('#submitted-delete-button')).triggerEventHandler('click', null);

    expect(deleteAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call saveAccountRequest when the Save button is clicked (panelStatus is EDITING)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    const saveAccountRequestSpy = jest.spyOn(component, 'saveAccountRequest');
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#editing-save-button')).triggerEventHandler('click', null);

    expect(saveAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call cancelEditAccountRequest when the Cancel button is clicked (panelStatus is EDITING)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.EDITING;
    const cancelEditAccountRequestSpy = jest.spyOn(component, 'cancelEditAccountRequest');
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#editing-cancel-button')).triggerEventHandler('click', null);

    expect(cancelEditAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call rejectAccountRequest when the Reject button is clicked (panelStatus is APPROVED)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
    const rejectAccountRequestSpy = jest.spyOn(component, 'rejectAccountRequest');
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#approved-reject-button')).triggerEventHandler('click', null);

    expect(rejectAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call deleteAccountRequest when the Delete button is clicked (panelStatus is APPROVED)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.APPROVED;
    const deleteAccountRequestSpy = jest.spyOn(component, 'deleteAccountRequest');
    jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef();
    });
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#approved-delete-button')).triggerEventHandler('click', null);

    expect(deleteAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call approveAccountRequest when the Approve button is clicked (panelStatus is REJECTED)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
    const approveAccountRequestSpy = jest.spyOn(component, 'approveAccountRequest');
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#rejected-approve-button')).triggerEventHandler('click', null);

    expect(approveAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call resetAccountRequest when the Reset button is clicked (panelStatus is REJECTED)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
    const resetAccountRequestSpy = jest.spyOn(component, 'resetAccountRequest');
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#rejected-reset-button')).triggerEventHandler('click', null);

    expect(resetAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call deleteAccountRequest when the Delete button is clicked (panelStatus is REJECTED)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.REJECTED;
    const deleteAccountRequestSpy = jest.spyOn(component, 'deleteAccountRequest');
    jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() => {
      return createMockNgbModalRef();
    });
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#rejected-delete-button')).triggerEventHandler('click', null);

    expect(deleteAccountRequestSpy).toHaveBeenCalledTimes(1);
  });

  it('should call resetAccountRequest when the Reset button is clicked (panelStatus is REGISTERED)', () => {
    component.panelStatus = ProcessAccountRequestPanelStatus.REGISTERED;
    const resetAccountRequestSpy = jest.spyOn(component, 'resetAccountRequest');
    fixture.detectChanges();

    fixture.debugElement.query(By.css('#registered-reset-button')).triggerEventHandler('click', null);

    expect(resetAccountRequestSpy).toHaveBeenCalledTimes(1);
  });
});
