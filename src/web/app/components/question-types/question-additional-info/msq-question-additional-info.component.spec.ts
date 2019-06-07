import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { MsqQuestionAdditionalInfoComponent } from './msq-question-additional-info.component';

describe('MsqQuestionAdditionalInfoComponent', () => {
  let component: MsqQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<MsqQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionAdditionalInfoComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MsqQuestionAdditionalInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  const msqDetails: FeedbackMsqQuestionDetails = {
    msqChoices: ['1', '2', '3'],
    otherEnabled: false,
    generateOptionsFor: FeedbackParticipantType.NONE,
    maxSelectableChoices: Number.MIN_VALUE,
    minSelectableChoices: Number.MIN_VALUE,
    hasAssignedWeights: false,
    msqWeights: [],
    msqOtherWeight: 0,
    questionType: FeedbackQuestionType.MSQ,
    questionText: 'MSQ question detail',
  };

  const msqDetailsWithGeneratedFor: FeedbackMsqQuestionDetails = {
    msqChoices: ['1', '2', '3'],
    otherEnabled: false,
    generateOptionsFor: FeedbackParticipantType.STUDENTS,
    maxSelectableChoices: Number.MIN_VALUE,
    minSelectableChoices: Number.MIN_VALUE,
    hasAssignedWeights: false,
    msqWeights: [],
    msqOtherWeight: 0,
    questionType: FeedbackQuestionType.MSQ,
    questionText: 'MSQ question detail',
  };

  const msqDetailsWithOther: FeedbackMsqQuestionDetails = {
    msqChoices: ['1', '2', '3'],
    otherEnabled: true,
    generateOptionsFor: FeedbackParticipantType.NONE,
    maxSelectableChoices: Number.MIN_VALUE,
    minSelectableChoices: Number.MIN_VALUE,
    hasAssignedWeights: false,
    msqWeights: [],
    msqOtherWeight: 0,
    questionType: FeedbackQuestionType.MSQ,
    questionText: 'MSQ question detail',
  };

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should show msq options', () => {
    component.questionDetails = msqDetails;
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
    component.questionDetails = msqDetailsWithOther;
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

  it('should show generated option message when generateOptionFor is enabled', () => {
    component.questionDetails = msqDetailsWithGeneratedFor;
    fixture.detectChanges();
    const generateOptionFor: HTMLElement = fixture.nativeElement.querySelector('div');
    const choices: HTMLElement[] = fixture.nativeElement.querySelectorAll('li');
    expect(generateOptionFor.textContent).toContain('The options for this question is' +
        ' automatically generated from the list of all %s in this course.');
    expect(choices.length).toEqual(0);
    expect(fixture).toMatchSnapshot();
  });
});
