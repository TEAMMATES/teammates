import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { SingleStatisticsComponent } from './single-statistics.component';
import { QuestionStatisticsModule } from '../../question-types/question-statistics/question-statistics.module';

describe('SingleStatisticsComponent', () => {
  let component: SingleStatisticsComponent;
  let fixture: ComponentFixture<SingleStatisticsComponent>;
    
    //
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
      feedbackResponsesService = jasmine.createSpyObj('FeedbackResponsesService', ['isFeedbackResponsesDisplayedOnSection']);
  
      component = new SingleStatisticsComponent(feedbackResponsesService);
    });
    it('should filter responses correctly', () => {
      // Set up initial responses
      component.responses = [
        { isMissingResponse: true, recipient: 'You' },
        { isMissingResponse: false, recipient: 'You' },
        { isMissingResponse: true, recipient: 'Someone else' },
        { isMissingResponse: false, recipient: 'Someone else' },
      ];
      component.question = { questionType: FeedbackQuestionType.CONSTSUM };
      component.section = 'example section';
      component.sectionType = InstructorSessionResultSectionType.EITHER;

      // Mock the feedbackResponsesService
      feedbackResponsesService.isFeedbackResponsesDisplayedOnSection.and.returnValue(true);

      // Call the filterResponses method
      component.ngOnInit();
      component.filterResponses();

      // Check if responsesToUse is filtered correctly
      expect(component.responsesToUse).toEqual([
        { isMissingResponse: false, recipient: 'You' },
        { isMissingResponse: false, recipient: 'Someone else' },
      ]);
    });
  });

  describe('isUsingResponsesToSelf', () => {
    it('should return true when isStudent is true and questionType is NUMSCALE', () => {
      component.isStudent = true;
      component.question = { questionType: FeedbackQuestionType.NUMSCALE };

      const result = component.isUsingResponsesToSelf();

      expect(result).toBe(true);
    });

    it('should return false when isStudent is true and questionType is not NUMSCALE', () => {
      component.isStudent = true;
      component.question = { questionType: FeedbackQuestionType.CONSTSUM };

      const result = component.isUsingResponsesToSelf();

      expect(result).toBe(false);
    });
  });
});
