import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import { FeedbackQuestionType, ResponseOutput } from '../../../../types/api-output';
import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';
import { SingleStatisticsComponent } from './single-statistics.component';

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SingleStatisticsComponent],
      imports: [QuestionStatisticsModule, HttpClientTestingModule],
      providers: [{
        provide: FeedbackResponsesService,
        useValue: { isFeedbackResponsesDisplayedOnSection: () => true },
      }],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  /**
   * Mock component responses list for all scenarios.
   *
   * @param isMissingResponse - is for setting true/false in the isMissingResponse field if missing response.
   * @param questionType - is for updating feedback question type field
   * @param recipient - is for updating recipient field
   */
  function getResponseOutput(isMissingResponse: boolean,
                             questionType: FeedbackQuestionType,
                             recipient: string = 'recipient'): ResponseOutput[] {
    return [
      {
        isMissingResponse,
        responseId: 'some_id',
        giver: 'some_giver',
        giverTeam: 'some_team',
        giverSection: 'section_1',
        recipient,
        recipientTeam: 'recipient_1_team',
        recipientSection: 'recipient_1_section',
        responseDetails: {
          questionType,
        },
        instructorComments: [],
      },
    ];
  }

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return empty responsesToUse when missing response & feedback question type is not contrib', () => {
    component.responses = getResponseOutput(true, FeedbackQuestionType.TEXT);
    fixture.detectChanges();
    expect(component.responsesToUse.length).toBe(0);
  });

  it('should return empty when is using response to self & response recipient is not You', () => {
    component.isStudent = true;
    component.responses = getResponseOutput(false, FeedbackQuestionType.NUMSCALE);
    fixture.detectChanges();
    expect(component.responsesToUse.length).toBe(0);
  });

  it('should return responses based on selected section when ngOnInit is called', () => {
    component.isStudent = false;
    component.responses = getResponseOutput(false, FeedbackQuestionType.CONTRIB, 'You');
    component.ngOnInit();
    expect(component.responsesToUse.length).toBe(1);
  });
});
