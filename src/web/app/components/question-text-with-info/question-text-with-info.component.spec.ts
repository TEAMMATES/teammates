import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';
import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
  FeedbackTextQuestionDetails,
} from '../../../types/api-output';
import {
  QuestionAdditionalInfoModule,
} from '../question-types/question-additional-info/question-additional-info.module';
import { TeammatesRouterModule } from '../teammates-router/teammates-router.module';
import { QuestionTextWithInfoComponent } from './question-text-with-info.component';

describe('QuestionTextWithInfoComponent', () => {
  let component: QuestionTextWithInfoComponent;
  let fixture: ComponentFixture<QuestionTextWithInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [QuestionTextWithInfoComponent],
      imports: [
        RouterTestingModule,
        QuestionAdditionalInfoModule,
        TeammatesRouterModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionTextWithInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const textQuestionDetails: FeedbackTextQuestionDetails = {
    shouldAllowRichText: true,
    questionType: FeedbackQuestionType.TEXT,
    questionText: 'Text question details',
    recommendedLength: 100,
  };

  const mcqQuestionDetails: FeedbackMcqQuestionDetails = {
    questionType: FeedbackQuestionType.MCQ,
    questionText: 'MCQ question details',
    hasAssignedWeights: false,
    mcqWeights: [],
    mcqOtherWeight: 0,
    mcqChoices: ['a', 'b'],
    otherEnabled: false,
    questionDropdownEnabled: false,
    generateOptionsFor: FeedbackParticipantType.NONE,

  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show control link and question details for TEXT questions', () => {
    component.questionDetails = textQuestionDetails;
    fixture.detectChanges();

    const questionDetailControlLink: any = fixture.debugElement.query(By.css('a'));
    const infoWrapper: any = fixture.debugElement.query(By.css('div'));

    expect(questionDetailControlLink).toBeNull();
    expect(infoWrapper).toBeNull();
    expect(fixture).toMatchSnapshot();
  });

  it('should show control link when question type is not TEXT', () => {
    component.questionDetails = mcqQuestionDetails;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should show question detail when click control link', () => {
    component.questionDetails = mcqQuestionDetails;
    fixture.detectChanges();

    const questionDetailControlLink: any = fixture.debugElement.query(By.css('button'));

    questionDetailControlLink.nativeElement.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });
});
