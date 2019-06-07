import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { McqQuestionAdditionalInfoComponent } from './mcq-question-additional-info.component';

describe('McqQuestionAdditionalInfoComponent', () => {
  let component: McqQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<McqQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(McqQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  const mcqDetail: FeedbackMcqQuestionDetails = {
    hasAssignedWeights: false,
    mcqWeights: [],
    mcqOtherWeight: 0,
    numOfMcqChoices: 3,
    mcqChoices: ['1', '2', '3'],
    otherEnabled: false,
    generateOptionsFor: FeedbackParticipantType.NONE,
    questionType: FeedbackQuestionType.MCQ,
    questionText: 'MCQ question detail',
  };

  const mcqDetailWithOther: FeedbackMcqQuestionDetails = {
    hasAssignedWeights: false,
    mcqWeights: [],
    mcqOtherWeight: 0,
    numOfMcqChoices: 3,
    mcqChoices: ['1', '2', '3'],
    otherEnabled: true,
    generateOptionsFor: FeedbackParticipantType.NONE,
    questionType: FeedbackQuestionType.MCQ,
    questionText: 'MCQ question detail',
  };

  const mcqDetailWithGenerateOption: FeedbackMcqQuestionDetails = {
    hasAssignedWeights: false,
    mcqWeights: [],
    mcqOtherWeight: 0,
    numOfMcqChoices: 3,
    mcqChoices: ['1', '2', '3'],
    otherEnabled: false,
    generateOptionsFor: FeedbackParticipantType.STUDENTS,
    questionType: FeedbackQuestionType.MCQ,
    questionText: 'MCQ with generate options question detail',
  };

  it('should show mcq options', () => {
    component.questionDetails = mcqDetail;
    fixture.detectChanges();
    const generateOptionFor: HTMLElement = fixture.nativeElement.querySelector('div');
    const choices: HTMLElement[] = fixture.nativeElement.querySelectorAll('li');
    expect(generateOptionFor).toBeNull();
    expect(choices.length).toEqual(3);
    expect(choices[0].textContent).toEqual('1');
    expect(choices[1].textContent).toEqual('2');
    expect(choices[2].textContent).toEqual('3');
    expect(fixture).toMatchSnapshot();
  });

  it('should show other when "other" option is enabled', () => {
    component.questionDetails = mcqDetailWithOther;
    fixture.detectChanges();
    const generateOptionFor: HTMLElement = fixture.nativeElement.querySelector('div');
    const choices: HTMLElement[] = fixture.nativeElement.querySelectorAll('li');
    expect(generateOptionFor).toBeNull();
    expect(choices.length).toEqual(4);
    expect(choices[0].textContent).toEqual('1');
    expect(choices[1].textContent).toEqual('2');
    expect(choices[2].textContent).toEqual('3');
    expect(choices[3].textContent).toEqual('Other');
    expect(fixture).toMatchSnapshot();
  });

  it('should show generator option message when "generateOptionsFor" is enabled', () => {
    component.questionDetails = mcqDetailWithGenerateOption;
    fixture.detectChanges();
    const generateOptionFor: HTMLElement = fixture.nativeElement.querySelector('div');
    const choices: HTMLElement[] = fixture.nativeElement.querySelectorAll('li');
    expect(generateOptionFor.textContent).toContain('The options for this question is ' +
        'automatically generated from the list of all %s in this course.');
    expect(choices.length).toEqual(0);
    expect(fixture).toMatchSnapshot();
  });

});
