import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { By } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackParticipantType,
  FeedbackQuestionType,
  NumberOfEntitiesToGiveFeedbackToSetting
} from '../../../types/api-output';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  PerQuestionViewResponsesModule,
} from '../../components/question-responses/per-question-view-responses/per-question-view-responses.module';
import {
  SingleStatisticsModule,
} from '../../components/question-responses/single-statistics/single-statistics.module';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { QuestionTabModel } from './instructor-session-result-page.component';
import { InstructorSessionResultQuestionViewComponent } from './instructor-session-result-question-view.component';

describe('InstructorSessionResultQuestionViewComponent', () => {
  let component: InstructorSessionResultQuestionViewComponent;
  let fixture: ComponentFixture<InstructorSessionResultQuestionViewComponent>;

  const testQuestions: Record<string, QuestionTabModel> = {
    'question 1': {
      question: {
        feedbackQuestionId: 'Q1',
        questionNumber: 1,
        questionBrief: 'brief',
        questionDescription: 'desc',
        questionDetails: {
          questionText: 'questionText',
          questionType: FeedbackQuestionType.TEXT,
        },
        questionType: FeedbackQuestionType.TEXT,
        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.STUDENTS,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
        customNumberOfEntitiesToGiveFeedbackTo: 3,
        showResponsesTo: [],
        showGiverNameTo: [],
        showRecipientNameTo: [],
      },
      responses: [],
      statistics: '32',
      hasPopulated: true,
      errorMessage: 'Error message',
      isTabExpanded: false,
    },
    'question 2': {
      question: {
        feedbackQuestionId: 'Q2',
        questionNumber: 2,
        questionBrief: 'brief',
        questionDescription: 'desc',
        questionDetails: {
          questionText: 'questionText',
          questionType: FeedbackQuestionType.TEXT,
        },
        questionType: FeedbackQuestionType.TEXT,
        giverType: FeedbackParticipantType.STUDENTS,
        recipientType: FeedbackParticipantType.STUDENTS,
        numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM,
        customNumberOfEntitiesToGiveFeedbackTo: 3,
        showResponsesTo: [],
        showGiverNameTo: [],
        showRecipientNameTo: [],
      },
      responses: [],
      statistics: '89',
      hasPopulated: true,
      errorMessage: 'Error message',
      isTabExpanded: false,
    },
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorSessionResultQuestionViewComponent],
      imports: [
        PerQuestionViewResponsesModule,
        QuestionTextWithInfoModule,
        SingleStatisticsModule,
        NgbModule,
        LoadingSpinnerModule,
        PanelChevronModule,
        LoadingRetryModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSessionResultQuestionViewComponent);
    component = fixture.componentInstance;
    component.questions = testQuestions;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('Verify if buttons is rendered', () => {
    fixture.detectChanges();
    const downloadQuestion1 = fixture.debugElement.queryAll(By.css('#btn-donwload-question'))[0].nativeElement;
    const downloadQuestion2 = fixture.debugElement.queryAll(By.css('#btn-donwload-question'))[1].nativeElement;

    expect(downloadQuestion1).toBeTruthy();
    expect(downloadQuestion2).toBeTruthy();
  });

  it('Verify the button trigger download', () => {
    fixture.detectChanges();
    const downloadQuestionResultsSpy = jest.spyOn(component, 'triggerDownloadQuestionResult');
    fixture.debugElement.queryAll(By.css('#btn-donwload-question'))[0].nativeElement.click();

    expect(downloadQuestionResultsSpy).toHaveBeenCalledTimes(1);
  });
});
