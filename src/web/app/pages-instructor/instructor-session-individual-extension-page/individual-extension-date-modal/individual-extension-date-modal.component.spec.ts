import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus, ResponseVisibleSetting, SessionVisibleSetting,
} from '../../../../types/api-output';
import { InstructorSessionIndividualExtensionPageModule } from '../instructor-session-individual-extension-page.module';
import { IndividualExtensionDateModalComponent, RadioOptions } from './individual-extension-date-modal.component';
import SpyInstance = jest.SpyInstance;

describe('IndividualExtensionDateModalComponent', () => {
    const testFeedbackSession: FeedbackSession = {
      courseId: 'testId1',
      timeZone: 'Asia/Singapore',
      feedbackSessionName: 'Test Session',
      instructions: 'Instructions',
      submissionStartTimestamp: 1000000000000,
      submissionEndTimestamp: 1500000000000,
      gracePeriod: 0,
      sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
      responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
      submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
      publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
      isClosingEmailEnabled: true,
      isPublishedEmailEnabled: true,
      createdAtTimestamp: 0,
      studentDeadlines: {},
      instructorDeadlines: {},
    };

    const testTimeString = '5 Apr 2000 2:00:00';

    let component: IndividualExtensionDateModalComponent;
    let fixture: ComponentFixture<IndividualExtensionDateModalComponent>;
    let simpleModalService: SimpleModalService;
    let timeZoneService: TimezoneService;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
          imports: [
            HttpClientTestingModule,
            InstructorSessionIndividualExtensionPageModule,
          ],
          providers: [NgbActiveModal],
        })
        .compileComponents();
      }));

    beforeEach(() => {
        fixture = TestBed.createComponent(IndividualExtensionDateModalComponent);
        component = fixture.componentInstance;
        simpleModalService = TestBed.inject(SimpleModalService);
        timeZoneService = TestBed.inject(TimezoneService);
        jest.spyOn(timeZoneService, 'formatToString').mockReturnValue(testTimeString);
        fixture.detectChanges();
      });

    it('should create', () => {
        expect(component).toBeTruthy();
      });

    it('should snap with default fields', () => {
        expect(component).toBeTruthy();
    });

    it('should snap with the extended students', () => {
        component.numberOfStudents = 10;
        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });

    it('should snap with the extend by radio option', () => {
        component.numberOfStudents = 10;
        component.extendByDeadlineKey = '12 hours';
        component.radioOption = RadioOptions.EXTEND_TO;
        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
    });

    it('should snap with the extend by radio option with customize', () => {
      component.numberOfStudents = 10;
      component.extendByDeadlineKey = 'Customize';
      component.radioOption = RadioOptions.EXTEND_TO;
      component.extendByDatePicker = { minutes: 10, hours: 20, days: 100 };
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should snap with the extend to radio option with timepicker', () => {
      component.numberOfStudents = 10;
      component.radioOption = RadioOptions.EXTEND_BY;
      component.datePicker = { year: 2022, month: 10, day: 10 };
      component.timePicker = { hour: 10, minute: 30 };
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

    it('should snap with the error modal', () => {
      component.numberOfStudents = 10;
      component.feedbackSessionTimeZone = testFeedbackSession.timeZone;
      component.radioOption = RadioOptions.EXTEND_BY;
      component.datePicker = { year: 2020, month: 10, day: 10 };
      component.DATETIME_FORMAT = 'd MMM YYYY h:mm:ss';
      const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal').mockReturnValue(
        createMockNgbModalRef());

      component.onConfirm();

      expect(modalSpy).toHaveBeenCalledTimes(1);
      fixture.detectChanges();
      expect(fixture).toMatchSnapshot();
    });

});
