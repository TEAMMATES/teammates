import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import SpyInstance = jest.SpyInstance;
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { InstructorSessionIndividualExtensionPageModule } from '../instructor-session-individual-extension-page.module';
import { IndividualExtensionDateModalComponent, RadioOptions } from './individual-extension-date-modal.component';

describe('IndividualExtensionDateModalComponent', () => {
  const testTimeString = 'Sat, 5 Apr 2000 2:00 +08';

  let component: IndividualExtensionDateModalComponent;
  let fixture: ComponentFixture<IndividualExtensionDateModalComponent>;
  let simpleModalService: SimpleModalService;
  let timeZoneService: TimezoneService;

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
    jest.spyOn(timeZoneService, 'formatToString').mockReturnValue(testTimeString);
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
    component.radioOption = RadioOptions.EXTEND_TO;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with the extend by radio option with customize', () => {
    component.extendByDeadlineKey = 'Customize';
    component.radioOption = RadioOptions.EXTEND_TO;
    component.extendByDatePicker = { hours: 20, days: 100 };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with the extend to radio option with timepicker', () => {
    component.radioOption = RadioOptions.EXTEND_BY;
    component.datePicker = { year: 2022, month: 10, day: 10 };
    component.timePicker = { hour: 10, minute: 30 };
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with the error modal', () => {
    // Set mocked picked time to be lesser than current system time
    jest.useFakeTimers('modern').setSystemTime(new Date('2021-01-01').getTime());
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
