import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { FeedbackMcqQuestionDetails } from 'src/web/types/api-output';
import { RichTextEditorModule } from '../../rich-text-editor/rich-text-editor.module';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { McqQuestionEditAnswerFormComponent } from './mcq-question-edit-answer-form.component';

describe('McqQuestionEditAnswerFormComponent', () => {
  let component: McqQuestionEditAnswerFormComponent;
  let fixture: ComponentFixture<McqQuestionEditAnswerFormComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionEditAnswerFormComponent],
      imports: [
        FormsModule,
        RichTextEditorModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionEditAnswerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when questionDropdownEnabled is updated', () => {
    component.questionDetails.questionDropdownEnabled = true;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should display all MCQ choices for question dropdown disabled', () => {
    component.questionDetails.mcqChoices = ['Option 1', 'Option 2', 'Option 3'];
    fixture.detectChanges();

    const radioGroup: DebugElement[] = fixture.debugElement.queryAll(By.css('#radioOptionSpan'));
    const mcqChoices: FeedbackMcqQuestionDetails['mcqChoices'] = component.questionDetails.mcqChoices;
    const radioSpans: HTMLSpanElement[] = [];

    radioGroup.forEach((de: DebugElement) => radioSpans.push(de.nativeElement));

    for (let i = 0; i < radioSpans.length; i += 1) {
      expect(radioSpans[i].innerHTML).toStrictEqual(mcqChoices[i]);
    }
  });

  it('should display all MCQ choices for question dropdown enabled', () => {
    component.questionDetails.mcqChoices = ['Option 1', 'Option 2', 'Option 3'];
    component.questionDetails.questionDropdownEnabled = true;
    fixture.detectChanges();

    const select: HTMLSelectElement = fixture.debugElement.query(By.css('#dropdown-option-select')).nativeElement;

    // Removes placeholder option
    select.options.remove(0);

    const mcqChoices: FeedbackMcqQuestionDetails['mcqChoices'] = component.questionDetails.mcqChoices;

    for (let i = 0; i < select.options.length; i += 1) {
      expect(select.options[i].text).toStrictEqual(mcqChoices[i]);
    }
  });
});
