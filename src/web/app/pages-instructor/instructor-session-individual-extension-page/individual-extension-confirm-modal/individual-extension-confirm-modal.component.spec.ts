import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackSession,
  FeedbackSessionPublishStatus, FeedbackSessionSubmissionStatus, ResponseVisibleSetting, SessionVisibleSetting,
} from '../../../../types/api-output';
import { InstructorSessionIndividualExtensionPageModule } from '../instructor-session-individual-extension-page.module';
import { StudentExtensionTableColumnModel } from '../student-extension-table-column-model';
import { IndividualExtensionConfirmModalComponent } from './individual-extension-confirm-modal.component';

describe('IndividualExtensionConfirmModalComponent', () => {
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
    };

    const studentModel1: StudentExtensionTableColumnModel = {
        sectionName: 'Test Section 1',
        teamName: 'Test Section 1',
        studentName: 'Test Student 1',
        studentEmail: 'testStudent1@gmail.com',
        studentExtensionDeadline: 1500000000000,
        hasExtension: false,
        selected: true,
    };

    const studentModel2: StudentExtensionTableColumnModel = {
        sectionName: 'Test Section 2',
        teamName: 'Test Section 2',
        studentName: 'Test Student 2',
        studentEmail: 'testStudent2@gmail.com',
        studentExtensionDeadline: 1510000000000,
        hasExtension: true,
        selected: true,
    };

    const studentModel3: StudentExtensionTableColumnModel = {
        sectionName: 'Test Section 3',
        teamName: 'Test Section 3',
        studentName: 'Test Student 3',
        studentEmail: 'testStudent3@gmail.com',
        studentExtensionDeadline: 1510000000000,
        hasExtension: true,
        selected: true,
    };

    let component: IndividualExtensionConfirmModalComponent;
    let fixture: ComponentFixture<IndividualExtensionConfirmModalComponent>;

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
        fixture = TestBed.createComponent(IndividualExtensionConfirmModalComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
      });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should snap with default fields', () => {
        expect(component).toBeTruthy();
    });

    it('should snap with the extended students', () => {
        component.studentsSelected = [studentModel1, studentModel2, studentModel3];
        component.extensionTimestamp = testFeedbackSession.submissionEndTimestamp;
        fixture.detectChanges();
        expect(fixture).toMatchSnapshot();
      });
});
