import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { IndividualExtensionDateModalComponent } from './individual-extension-date-modal.component';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { Hours, Milliseconds } from '../../../../types/datetime-const';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { Mock } from 'vitest';

describe('IndividualExtensionDateModalComponent', () => {
  const testTimeString = 'Sat, 5 Apr 2000 2:00 +08';

  let component: IndividualExtensionDateModalComponent;
  let fixture: ComponentFixture<IndividualExtensionDateModalComponent>;
  let simpleModalService: SimpleModalService;
  let timeZoneService: TimezoneService;
  let formatSpy: Mock;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [NgbActiveModal, provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(IndividualExtensionDateModalComponent);
    component = fixture.componentInstance;
    simpleModalService = TestBed.inject(SimpleModalService);
    timeZoneService = TestBed.inject(TimezoneService);
    formatSpy = vi.spyOn(timeZoneService, 'formatToString').mockReturnValue(testTimeString);
    component.numStudents = 10;
    component.numInstructors = 20;
    component.feedbackSessionEndingTimestamp = 0;
    component.feedbackSessionTimeZone = '';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with the extended students', () => {
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a preset selected', () => {
    component.selectedPreset.set('12 hours');
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with custom selected', () => {
    component.feedbackSessionTimeZone = 'UTC';
    component.selectedPreset.set('Custom');
    component.customTimestamp.set(Date.UTC(2022, 9, 10, 10, 30));
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should display correct time with preset options', () => {
    formatSpy.mockRestore();
    component.feedbackSessionTimeZone = 'Asia/Singapore';
    component.feedbackSessionEndingTimestamp = 1600000000000;
    expect(component.extendAndFormatEndTimeBy(0)).toEqual('Sun, 13 Sep 2020, 08:26 PM +08');

    component.selectedPreset.set('12 hours');
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + Hours.TWELVE * Milliseconds.IN_ONE_HOUR,
    );
    expect(component.extendAndFormatEndTimeBy(Hours.TWELVE)).toEqual('Mon, 14 Sep 2020, 08:26 AM +08');

    component.selectedPreset.set('1 day');
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + Hours.IN_ONE_DAY * Milliseconds.IN_ONE_HOUR,
    );
    expect(component.extendAndFormatEndTimeBy(Hours.IN_ONE_DAY)).toEqual('Mon, 14 Sep 2020, 08:26 PM +08');

    component.selectedPreset.set('3 days');
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + Hours.IN_THREE_DAYS * Milliseconds.IN_ONE_HOUR,
    );
    expect(component.extendAndFormatEndTimeBy(Hours.IN_THREE_DAYS)).toEqual('Wed, 16 Sep 2020, 08:26 PM +08');

    component.selectedPreset.set('1 week');
    expect(component.getExtensionTimestamp()).toEqual(
      component.feedbackSessionEndingTimestamp + Hours.IN_ONE_WEEK * Milliseconds.IN_ONE_HOUR,
    );
    expect(component.extendAndFormatEndTimeBy(Hours.IN_ONE_WEEK)).toEqual('Sun, 20 Sep 2020, 08:26 PM +08');
  });

  it('should snap with the warning modal', () => {
    vi.useFakeTimers().setSystemTime(new Date('2021-01-01').getTime());
    vi.spyOn(component, 'getExtensionTimestamp').mockReturnValue(new Date('2020-10-10').valueOf());
    const modalSpy = vi.spyOn(simpleModalService, 'openConfirmationModal').mockImplementation(() =>
      createMockNgbModalRef({
        header: 'mock header',
        content: 'mock content',
        type: SimpleModalType.WARNING,
      }),
    );

    component.onConfirm();

    expect(Date.now()).toEqual(new Date('2021-01-01').valueOf());
    expect(component.getExtensionTimestamp()).toBeLessThan(Date.now());
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Are you sure you wish to set the new deadline to before the current time?',
      SimpleModalType.WARNING,
      '<b>Any users affected will have their sessions closed immediately.</b> ' +
        'The current time now is Sat, 5 Apr 2000 2:00 +08 and you are extending to Sat, 5 Apr 2000 2:00 +08.' +
        ' Do you wish to proceed?',
    );
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
