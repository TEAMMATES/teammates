import {HttpClientTestingModule} from '@angular/common/http/testing';
import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {QuestionStatisticsModule} from '../../question-types/question-statistics/question-statistics.module';
import {SingleStatisticsComponent} from './single-statistics.component';
import {FeedbackQuestionType, ResponseOutput} from "../../../../types/api-output";
import {FeedbackResponsesService} from "../../../../services/feedback-responses.service";

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SingleStatisticsComponent],
      imports: [QuestionStatisticsModule, HttpClientTestingModule],
      providers: [{
        provide: FeedbackResponsesService,
        useValue: { isFeedbackResponsesDisplayedOnSection: () => true }
      }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return empty when response is missing & question type is not contrib', () => {
    component.responses = getResponseOutput(true, FeedbackQuestionType.TEXT);
    fixture.detectChanges()
    expect(component.responsesToUse.length).toBe(0)
  })

  it('should return empty when is using response to self', () => {
    component.isStudent = true
    component.responses = getResponseOutput(false, FeedbackQuestionType.NUMSCALE);
    fixture.detectChanges();
    expect(component.responsesToUse.length).toBe(0)
  });

  it('should return response based on selected section',  () => {
    component.isStudent = false
    component.responses = getResponseOutput(false, FeedbackQuestionType.CONTRIB, 'You');
    component.ngOnInit()
    expect(component.responsesToUse.length).toBe(1)
  });

  function getResponseOutput(isMissingResponse: boolean,
                             questionType: FeedbackQuestionType,
                             recipient: string = 'recipient'): ResponseOutput[] {
    return [
      {
        isMissingResponse: isMissingResponse,
        responseId: 'some_id',
        giver: 'some_giver',
        giverTeam: 'some_giverteam',
        giverSection: 'section_1',
        recipient: recipient,
        recipientTeam: 'recipient_1_team',
        recipientSection: 'recipient_1_section',
        responseDetails: {
          questionType: questionType
        },
        instructorComments: []
      }
    ]
  }

});
