import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import {
  QuestionAdditionalInfoModule,
} from '../question-types/question-additional-info/question-additional-info.module';
import { QuestionTextWithInfoComponent } from './question-text-with-info.component';
import {By} from "@angular/platform-browser";
import {FeedbackQuestionType} from "../../../types/api-output";

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
    const questionDetailControlLink = fixture.debugElement.query(By.css('a'));
    const explicitQuestionDetail = fixture.debugElement.query(By.css('div'));
    expect(questionDetailControlLink).toBeNull();
    expect(explicitQuestionDetail).toBeNull();
  });

  it('should show control link when question type is not TEXT',() => {
    component.questionDetails.questionType = FeedbackQuestionType.MCQ;
    component.questionDetails.questionText = 'MCQ question details';
    fixture.detectChanges();
    const questionDetailControlLink = fixture.debugElement.query(By.css('a'));
    const explicitQuestionDetail = fixture.debugElement.query(By.css('div'));
    expect(questionDetailControlLink).not.toBeNull();
    expect(explicitQuestionDetail).toBeNull();
  });

  it('should show question detail when click control link',() => {
    component.questionDetails.questionType = FeedbackQuestionType.MCQ;
    component.questionDetails.questionText = 'MCQ question details';
    fixture.detectChanges();
    const questionDetailControlLink = fixture.debugElement.query(By.css('a'));
    questionDetailControlLink.nativeElement.click();
    fixture.detectChanges();
    const explicitQuestionDetail = fixture.debugElement.query(By.css('div'));
    const mcqQuestionDetail = fixture.debugElement.query(By.css('tm-mcq-question-additional-info'));
    expect(questionDetailControlLink).not.toBeNull();
    expect(explicitQuestionDetail).not.toBeNull();
    expect(mcqQuestionDetail).not.toBeNull();
  });
});
