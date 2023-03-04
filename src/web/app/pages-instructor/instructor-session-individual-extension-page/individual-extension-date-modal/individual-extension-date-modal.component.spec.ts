import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import SpyInstance = jest.SpyInstance;
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { Hours, Milliseconds } from '../../../../types/datetime-const';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { InstructorSessionIndividualExtensionPageModule } from '../instructor-session-individual-extension-page.module';
import { IndividualExtensionDateModalComponent, RadioOptions } from './individual-extension-date-modal.component';

describe('IndividualExtensionDateModalComponent', () => {
  const testTimeString = 'Sat, 5 Apr 2000 2:00 +08';
  const MAX_EPOCH_TIME_IN_DAYS = 100000000;

  let component: IndividualExtensionDateModalComponent;
  let fixture: ComponentFixture<IndividualExtensionDateModalComponent>;
  let simpleModalService: SimpleModalService;
  let timeZoneService: TimezoneService;
  let formatSpy: SpyInstance;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, InstructorSessionIndividualExtensionPageModule],
        providers: [NgbActiveModal],
      }).compileComponents();
    }),
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(IndividualExtensionDateModalComponent);
    component = fixture.componentInstance;
    simpleModalService = TestBed.inject(SimpleModalService);
    timeZoneService = TestBed.inject(TimezoneService);
    formatSpy = jest.spyOn(timeZoneService, 'formatToString').mockReturnValue(testTimeString);
    component.numStudents = 10;
    component.numInstructors = 20;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with the extended students', () => {
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with the extend by radio option', () => {
    component.extendByDeadlineKey = '12 hours';
    component.radioOption = RadioOptions.EXTEND_BY;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display correct time with the extend by radio option', () => {
    formatSpy.mockRestore();
    component.radioOption = RadioOptions.EXTEND_BY;
    component.feedbackSessionTimeZone = 'Asia/Singapore';
    component.feedbackSessionEndingTimestamp = 1600000000000;
    expect(component.extendAndFormatEndTimeBy(0, 0)).toEqual('Sun, 13 Sep 2020, 08:26 PM +08');

    component.extendByDeadlineKey = '12 hours';
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + (Hours.TWELVE * Milliseconds.IN_ONE_HOUR));
    expect(component.extendAndFormatEndTimeBy(Hours.TWELVE, 0))
      .toEqual('Mon, 14 Sep 2020, 08:26 AM +08');

    component.extendByDeadlineKey = '1 day';
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + (Hours.IN_ONE_DAY * Milliseconds.IN_ONE_HOUR));
    expect(component.extendAndFormatEndTimeBy(Hours.IN_ONE_DAY, 0))
      .toEqual('Mon, 14 Sep 2020, 08:26 PM +08');

    component.extendByDeadlineKey = '3 days';
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + (Hours.IN_THREE_DAYS * Milliseconds.IN_ONE_HOUR));
    expect(component.extendAndFormatEndTimeBy(Hours.IN_THREE_DAYS, 0))
      .toEqual('Wed, 16 Sep 2020, 08:26 PM +08');

    component.extendByDeadlineKey = '1 week';
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + (Hours.IN_ONE_WEEK * Milliseconds.IN_ONE_HOUR));
    expect(component.extendAndFormatEndTimeBy(Hours.IN_ONE_WEEK, 0))
      .toEqual('Sun, 20 Sep 2020, 08:26 PM +08');
  });

  it('should snap with the extend by radio option with customize', () => {
    component.extendByDeadlineKey = 'Customize';
    component.radioOption = RadioOptions.EXTEND_BY;
    component.extendByDatePicker = { hours: 20, days: 100 };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display correct time with the extend by customize option', () => {
    formatSpy.mockRestore();
    component.radioOption = RadioOptions.EXTEND_BY;
    component.extendByDeadlineKey = 'Customize';
    component.feedbackSessionTimeZone = 'Asia/Singapore';
    component.feedbackSessionEndingTimestamp = 1600000000000;
    expect(component.extendAndFormatEndTimeBy(0, 0)).toEqual('Sun, 13 Sep 2020, 08:26 PM +08');
    expect(component.extendAndFormatEndTimeBy(20, 0)).toEqual('Mon, 14 Sep 2020, 04:26 PM +08');
    expect(component.extendAndFormatEndTimeBy(0, 20)).toEqual('Sat, 03 Oct 2020, 08:26 PM +08');
    expect(component.extendAndFormatEndTimeBy(20, 20)).toEqual('Sun, 04 Oct 2020, 04:26 PM +08');
    expect(component.extendAndFormatEndTimeBy(0, MAX_EPOCH_TIME_IN_DAYS)).toEqual('Invalid date');
  });

  it('should display only the message for date to be later than session ending timestamp as default', () => {
    component.extendByDeadlineKey = 'Customize';
    component.radioOption = RadioOptions.EXTEND_BY;
    component.extendByDatePicker = { hours: 0, days: 0 };
    component.feedbackSessionEndingTimestamp = new Date(testTimeString).getTime();
    fixture.detectChanges();
    expect(component.isCustomizeBeforeMaxDate()).toBeTruthy();
    expect(component.isCustomizeDateTimeIntegers()).toBeTruthy();
    expect(component.isDateSelectedLaterThanCurrentEndingTimestamp()).toBeFalsy();
  });

  it('should display the message for date to be later than session ending timestamp', () => {
    component.extendByDeadlineKey = 'Customize';
    component.radioOption = RadioOptions.EXTEND_BY;
    component.extendByDatePicker = { hours: -10, days: 0 };
    component.feedbackSessionEndingTimestamp = new Date(testTimeString).getTime();
    fixture.detectChanges();
    expect(component.isDateSelectedLaterThanCurrentEndingTimestamp()).toBeFalsy();
  });

  it('should display the message for date to be before maximum date', () => {
    component.extendByDeadlineKey = 'Customize';
    component.radioOption = RadioOptions.EXTEND_BY;
    component.extendByDatePicker.days = MAX_EPOCH_TIME_IN_DAYS;
    component.feedbackSessionEndingTimestamp = new Date(testTimeString).getTime();
    fixture.detectChanges();
    expect(component.isCustomizeBeforeMaxDate()).toBeFalsy();
  });

  it('should display the message for non-integer date or time', () => {
    component.extendByDeadlineKey = 'Customize';
    component.radioOption = RadioOptions.EXTEND_BY;
    component.feedbackSessionEndingTimestamp = new Date(testTimeString).getTime();
    component.extendByDatePicker = { hours: 0.5, days: 0 };
    fixture.detectChanges();
    expect(component.isCustomizeDateTimeIntegers()).toBeFalsy();
    component.extendByDatePicker = { hours: 0, days: 0.5 };
    fixture.detectChanges();
    expect(component.isCustomizeDateTimeIntegers()).toBeFalsy();
    component.extendByDatePicker = { hours: 0.5, days: 0.5 };
    fixture.detectChanges();
    expect(component.isCustomizeDateTimeIntegers()).toBeFalsy();
  });

  it('should snap with the extend to radio option with timepicker', () => {
    component.radioOption = RadioOptions.EXTEND_TO;
    component.extendToDatePicker = { year: 2022, month: 10, day: 10 };
    component.extendToTimePicker = { hour: 10, minute: 30 };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with the warning modal', () => {
    // Set mocked picked time to be lesser than current system time
    jest.useFakeTimers().setSystemTime(new Date('2021-01-01').getTime());
    jest.spyOn(component, 'getExtensionTimestamp').mockReturnValue(new Date('2020-10-10').valueOf());
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(
      () => createMockNgbModalRef({
        header: 'mock header', content: 'mock content', type: SimpleModalType.WARNING,
      }),
    );

    component.onConfirm();

    expect(Date.now()).toEqual(new Date('2021-01-01').valueOf());
    expect(component.getExtensionTimestamp()).toBeLessThan(Date.now());
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Are you sure you wish to set the new deadline to before the current time?',
      SimpleModalType.WARNING,
      '<b>Any users affected will have their sessions closed immediately.</b> '
      + 'The current time now is Sat, 5 Apr 2000 2:00 +08 and you are extending to Sat, 5 Apr 2000 2:00 +08.'
      + ' Do you wish to proceed?');
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
