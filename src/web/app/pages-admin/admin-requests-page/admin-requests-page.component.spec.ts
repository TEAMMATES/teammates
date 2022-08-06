import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of, throwError } from 'rxjs';
import { AccountService } from '../../../services/account.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { AccountRequest, AccountRequestStatus } from '../../../types/api-output';
import { DateFormat } from '../../components/datepicker/datepicker.component';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  ProcessAccountRequestPanelComponent,
} from '../../components/process-account-request-panel/process-account-request-panel.component';
import {
  ProcessAccountRequestPanelModule,
} from '../../components/process-account-request-panel/process-account-request-panel.module';
import { AdminRequestsPageComponent } from './admin-requests-page.component';

describe('AdminRequestsPageComponent', () => {
  const accountRequest1: AccountRequest = {
    name: 'U.R. Nice',
    institute: 'TEAMMATES Test Institute, Singapore',
    email: 'nice@tmt.tmt',
    homePageUrl: 'https://www.comp.nus.edu.sg/',
    comments: 'Is TEAMMATES free to use?',
    registrationKey: 'nice-123456789',
    status: AccountRequestStatus.SUBMITTED,
    createdAt: new Date('2022-08-05T04:18:00Z').getTime(),
  };

  const accountRequest2: AccountRequest = {
    name: 'Olive Yew',
    institute: 'TMT, Singapore',
    email: 'olive@tmt.tmt',
    homePageUrl: '',
    comments: '',
    registrationKey: 'olive-123456789',
    status: AccountRequestStatus.SUBMITTED,
    createdAt: new Date('2022-08-06T03:42:00Z').getTime(),
  };

  const accountRequest3: AccountRequest = {
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

  const accountRequest4: AccountRequest = {
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

  let component: AdminRequestsPageComponent;
  let fixture: ComponentFixture<AdminRequestsPageComponent>;
  let accountService: AccountService;
  let timezoneService: TimezoneService;
  let statusMessageService: StatusMessageService;

  beforeEach(waitForAsync(() => {
    const accountServiceStub: Partial<AccountService> = {
      getAccountRequestsPendingProcessing: () => of({ accountRequests: [] }),
      getAccountRequestsWithinPeriod: () => of({ accountRequests: [] }),
    };
    const timezoneServiceStub: Partial<TimezoneService> = {
      guessTimezone: () => 'Asia/Singapore',
      formatToString: () => 'Time123456',
      resolveLocalDateTime: () => 123456789,
    };
    const statusMessageServiceStub: Partial<StatusMessageService> = {
      showSuccessToast: () => {},
      showErrorToast: () => {},
    };

    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        FormsModule,
        HttpClientTestingModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        PanelChevronModule,
        NoopAnimationsModule,
        ProcessAccountRequestPanelModule,
      ],
      declarations: [AdminRequestsPageComponent],
      providers: [
        { provide: AccountService, useValue: accountServiceStub },
        { provide: TimezoneService, useValue: timezoneServiceStub },
        { provide: StatusMessageService, useValue: statusMessageServiceStub },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminRequestsPageComponent);
    component = fixture.componentInstance;
    accountService = TestBed.inject(AccountService);
    timezoneService = TestBed.inject(TimezoneService);
    statusMessageService = TestBed.inject(StatusMessageService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with account requests pending processing and account requests within period', () => {
    component.accountRequestsPendingProcessing = [accountRequest1, accountRequest2];
    component.accountRequestsWithinPeriod = [accountRequest1, accountRequest2, accountRequest3, accountRequest4];
    component.hasQueried = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it(`should snap with collapsed account requests pending processing 
  and expanded account requests within period`, waitForAsync(() => {
    component.accountRequestsPendingProcessing = [accountRequest1];
    component.accountRequestsWithinPeriod = [accountRequest2];
    component.hasQueried = true;
    fixture.detectChanges();

    let processAccountRequestPanelComponent: ProcessAccountRequestPanelComponent;
    processAccountRequestPanelComponent = fixture.debugElement
      .query(By.css('#ar-pending-processing-0')).componentInstance;
    processAccountRequestPanelComponent.isTabExpanded = false;

    processAccountRequestPanelComponent = fixture.debugElement
      .query(By.css('#ar-within-period-0')).componentInstance;
    processAccountRequestPanelComponent.isTabExpanded = true;

    fixture.detectChanges();

    // TODO: problem - cannot wait for the collapse animation to finish when using BrowserAnimationsModule
    fixture.whenStable().then(() => {
      expect(fixture).toMatchSnapshot();
    });
  }));

  it('should snap when loading account requests pending processing and account requests within period', () => {
    component.isLoadingAccountRequestsPendingProcessing = true;
    component.isLoadingAccountRequestsWithinPeriod = true;
    component.hasQueried = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should snap when loading account requests pending processing and account requests within period fails', () => {
    component.hasAccountRequestsPendingProcessingLoadingFailed = true;
    component.hasAccountRequestsWithinPeriodLoadingFailed = true;
    component.hasQueried = true;
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

  it('should call loadAccountRequestsPendingProcessing when component inits', () => {
    const loadAccountRequestsPendingProcessingSpy = jest.spyOn(component, 'loadAccountRequestsPendingProcessing');

    component.ngOnInit();

    expect(loadAccountRequestsPendingProcessingSpy).toHaveBeenCalledTimes(1);
  });

  it('should set dates correctly when component inits', () => {
    const dateToday: DateFormat = { year: 2022, month: 8, day: 8 };
    jest.useFakeTimers();
    jest.setSystemTime(new Date(dateToday.year, dateToday.month - 1, dateToday.day));

    component.ngOnInit();

    expect(component.dateToday).toEqual(dateToday);
    expect(component.formModel.fromDate).toEqual(dateToday);
    expect(component.formModel.toDate).toEqual(dateToday);

    jest.useRealTimers();
  });

  it('should load account requests pending processing correctly', () => {
    const loadAccountRequestsPendingProcessingSpy = jest.spyOn(component, 'loadAccountRequestsPendingProcessing');
    const getAccountRequestsPendingProcessingSpy = jest.spyOn(accountService, 'getAccountRequestsPendingProcessing')
      .mockReturnValue(of({
        accountRequests: [accountRequest1, accountRequest2],
      }));

    component.loadAccountRequestsPendingProcessing();

    expect(loadAccountRequestsPendingProcessingSpy).toHaveBeenCalledTimes(1);
    expect(getAccountRequestsPendingProcessingSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequestsPendingProcessing).toEqual([accountRequest1, accountRequest2]);
    expect(component.hasAccountRequestsPendingProcessingLoadingFailed).toBeFalsy();
    expect(component.isLoadingAccountRequestsPendingProcessing).toBeFalsy();
  });

  it('should action correctly when loading account requests pending processing fails', () => {
    const errorMessage: string = 'Some errors!';
    const loadAccountRequestsPendingProcessingSpy = jest.spyOn(component, 'loadAccountRequestsPendingProcessing');
    const getAccountRequestsPendingProcessingSpy = jest.spyOn(accountService, 'getAccountRequestsPendingProcessing')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast');

    component.loadAccountRequestsPendingProcessing();

    expect(loadAccountRequestsPendingProcessingSpy).toHaveBeenCalledTimes(1);
    expect(getAccountRequestsPendingProcessingSpy).toHaveBeenCalledTimes(1);
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequestsPendingProcessing.length).toBe(0);
    expect(component.hasAccountRequestsPendingProcessingLoadingFailed).toBeTruthy();
    expect(component.isLoadingAccountRequestsPendingProcessing).toBeFalsy();
  });

  it('should load account requests within period correctly', () => {
    const fromDate: DateFormat = { year: 2022, month: 8, day: 8 };
    const toDate: DateFormat = { year: 2023, month: 7, day: 26 };
    const timezone: string = 'UTC';
    const loadAccountRequestsWithinPeriodSpy = jest.spyOn(component, 'loadAccountRequestsWithinPeriod');
    const getAccountRequestsWithinPeriodSpy = jest.spyOn(accountService, 'getAccountRequestsWithinPeriod')
      .mockReturnValue(of({
        accountRequests: [accountRequest3, accountRequest4],
      }));
    const resolveLocalDateTimeSpy = jest.spyOn(timezoneService, 'resolveLocalDateTime');
    component.formModel.fromDate = fromDate;
    component.formModel.toDate = toDate;
    component.timezone = timezone;

    component.loadAccountRequestsWithinPeriod();

    expect(loadAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(1);
    expect(getAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(1);
    expect(resolveLocalDateTimeSpy).toHaveBeenCalledTimes(2);
    expect(resolveLocalDateTimeSpy).toHaveBeenNthCalledWith(1, fromDate, { hour: 0, minute: 0 }, timezone);
    expect(resolveLocalDateTimeSpy).toHaveBeenNthCalledWith(2, toDate, { hour: 23, minute: 59 }, timezone);
    expect(component.accountRequestsWithinPeriod).toEqual([accountRequest3, accountRequest4]);
    expect(component.hasQueried).toBeTruthy();
    expect(component.hasAccountRequestsWithinPeriodLoadingFailed).toBeFalsy();
    expect(component.isLoadingAccountRequestsWithinPeriod).toBeFalsy();
  });

  it('should action correctly when loading account requests within period fails', () => {
    const errorMessage: string = 'Some errors!';
    const loadAccountRequestsWithinPeriodSpy = jest.spyOn(component, 'loadAccountRequestsWithinPeriod');
    const getAccountRequestsWithinPeriodSpy = jest.spyOn(accountService, 'getAccountRequestsWithinPeriod')
      .mockReturnValue(throwError({
        error: {
          message: errorMessage,
        },
      }));
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast');
    component.accountRequestsWithinPeriod = [accountRequest3, accountRequest4];

    component.loadAccountRequestsWithinPeriod();

    expect(loadAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(1);
    expect(getAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(1);
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(component.accountRequestsWithinPeriod).toEqual([]);
    expect(component.hasQueried).toBeTruthy();
    expect(component.hasAccountRequestsWithinPeriodLoadingFailed).toBeTruthy();
    expect(component.isLoadingAccountRequestsWithinPeriod).toBeFalsy();
  });

  it('should call loadAccountRequestsWithinPeriod when the Show Account Requests button is clicked', () => {
    const loadAccountRequestsWithinPeriodSpy = jest.spyOn(component, 'loadAccountRequestsWithinPeriod');

    fixture.debugElement.query(By.css('#show-account-requests-button')).triggerEventHandler('click', null);

    expect(loadAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(1);
  });

  it('should update fromDate and toDate in formModel correctly', () => {
    const fromDate: DateFormat = { year: 2022, month: 7, day: 26 };
    const toDate: string = 'Invalid date';

    const fromDatepicker: HTMLInputElement = fixture.debugElement.query(By.css('#from-datepicker')).nativeElement;
    fromDatepicker.value = `${fromDate.year}-${fromDate.month}-${fromDate.day}`;
    fromDatepicker.dispatchEvent(new Event('input'));

    const toDatepicker: HTMLInputElement = fixture.debugElement.query(By.css('#to-datepicker')).nativeElement;
    toDatepicker.value = toDate;
    toDatepicker.dispatchEvent(new Event('input'));

    expect(component.formModel.fromDate).toEqual(fromDate);
    expect(component.formModel.toDate).toEqual(toDate);
  });

  it('should fail when fromDate is empty', () => {
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast');
    const getAccountRequestsWithinPeriodSpy = jest.spyOn(accountService, 'getAccountRequestsWithinPeriod');

    const fromDatepicker: HTMLInputElement = fixture.debugElement.query(By.css('#from-datepicker')).nativeElement;
    fromDatepicker.value = '';
    fromDatepicker.dispatchEvent(new Event('input'));

    fixture.debugElement.query(By.css('#show-account-requests-button')).triggerEventHandler('click', null);

    expect(component.formModel.fromDate).toBeNull();
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(getAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(0);
    expect(component.accountRequestsWithinPeriod).toEqual([]);
    expect(component.hasQueried).toBeFalsy();
  });

  it('should fail when toDate is empty', () => {
    const showErrorToastSpy = jest.spyOn(statusMessageService, 'showErrorToast');
    const getAccountRequestsWithinPeriodSpy = jest.spyOn(accountService, 'getAccountRequestsWithinPeriod');

    const toDatepicker: HTMLInputElement = fixture.debugElement.query(By.css('#to-datepicker')).nativeElement;
    toDatepicker.value = '';
    toDatepicker.dispatchEvent(new Event('input'));

    fixture.debugElement.query(By.css('#show-account-requests-button')).triggerEventHandler('click', null);

    expect(component.formModel.toDate).toBeNull();
    expect(showErrorToastSpy).toHaveBeenCalledTimes(1);
    expect(getAccountRequestsWithinPeriodSpy).toHaveBeenCalledTimes(0);
    expect(component.accountRequestsWithinPeriod).toEqual([]);
    expect(component.hasQueried).toBeFalsy();
  });
});
