import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { SingleStatisticsComponent } from './single-statistics.component';
import { FeedbackResponsesService } from '../../../../services/feedback-responses.service';
import {
  FeedbackQuestionType,
} from '../../../../types/api-output';
import {
  InstructorSessionResultSectionType,
} from '../../../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SingleStatisticsComponent],
      imports: [QuestionStatisticsModule, HttpClientTestingModule],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SingleStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // NEW CODE
  describe('filterResponses', () => {
    let feedbackResponsesService: FeedbackResponsesService;
    beforeEach(() => {
      feedbackResponsesService = jest.fn('FeedbackResponsesService',
      ['isFeedbackResponsesDisplayedOnSection']);
      component = new SingleStatisticsComponent(feedbackResponsesService);
    });
    it('should filter responses correctly', () => {
      component.responses = [
        {
          isMissingResponse: true, recipient: 'You', responseId: '0000', giver: '0000', giverTeam: '0000',
         giverSection: '000'
        },
        {
          isMissingResponse: false, recipient: 'You', responseId: '0000', giver: '0000', giverTeam: '0000',
         giverSection: '000'
        },
        {
          isMissingResponse: true, recipient: 'Someone else', responseId: '0000', giver: '0000', giverTeam: '0000',
         giverSection: '000'
        },
        {
          isMissingResponse: false, recipient: 'Someone else', responseId: '0000', giver: '0000', giverTeam: '0000',
         giverSection: '000'
        },
      ];

      component.question = { questionType: FeedbackQuestionType.CONSTSUM };

      component.section = 'example section';
      component.sectionType = InstructorSessionResultSectionType.EITHER;

      // Mock the feedbackResponsesService
      feedbackResponsesService.isFeedbackResponsesDisplayedOnSection.and.returnValue(true);

      // Call the filterResponses method
      component.ngOnInit();

      // Check if responsesToUse is filtered correctly
      expect(component.responsesToUse).toEqual([
        { isMissingResponse: false, recipient: 'You' },
        { isMissingResponse: false, recipient: 'Someone else' },
      ]);
    });
  });
});
