import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {By} from '@angular/platform-browser';
import {RouterTestingModule} from '@angular/router/testing';
import {FeedbackQuestionType} from '../../../types/api-output';
import {QuestionAdditionalInfoModule,} from '../question-types/question-additional-info/question-additional-info.module';
import {QuestionTextWithInfoComponent} from './question-text-with-info.component';

describe('QuestionTextWithInfoComponent', () => {
  let component: QuestionTextWithInfoComponent;
  let fixture: ComponentFixture<QuestionTextWithInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [QuestionTextWithInfoComponent],
      imports: [
        RouterTestingModule,
        QuestionAdditionalInfoModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(QuestionTextWithInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not show control link and question details for TEXT questions', () => {
    component.questionDetails.questionType = FeedbackQuestionType.TEXT;
    const questionDetailControlLink: any = fixture.debugElement.query(By.css('a'));
    const infoWrapper: any = fixture.debugElement.query(By.css('div'));

    expect(questionDetailControlLink).toBeNull();
    expect(infoWrapper).toBeNull();
  });

  it('should show control link when question type is not TEXT', () => {
    component.questionDetails.questionType = FeedbackQuestionType.MCQ;
    component.questionDetails.questionText = 'MCQ question details';
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should show question detail when click control link', () => {
    component.questionDetails.questionType = FeedbackQuestionType.MCQ;
    component.questionDetails.questionText = 'MCQ question details';
    fixture.detectChanges();

    const questionDetailControlLink: any = fixture.debugElement.query(By.css('a'));

    questionDetailControlLink.nativeElement.click();
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });
});
