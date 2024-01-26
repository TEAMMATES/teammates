import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MsqQuestionAdditionalInfoComponent } from './msq-question-additional-info.component';
import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { NO_VALUE } from '../../../../types/feedback-response-details';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';

describe('MsqQuestionAdditionalInfoComponent', () => {
  let component: MsqQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<MsqQuestionAdditionalInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [MsqQuestionAdditionalInfoComponent],
      imports: [TeammatesCommonModule],
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
    maxSelectableChoices: NO_VALUE,
    minSelectableChoices: NO_VALUE,
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
    maxSelectableChoices: NO_VALUE,
    minSelectableChoices: NO_VALUE,
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
    maxSelectableChoices: NO_VALUE,
    minSelectableChoices: NO_VALUE,
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

    expect(fixture).toMatchSnapshot();
  });

  it('should show other when "other" option is enabled', () => {
    component.questionDetails = msqDetailsWithOther;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should show generated option message when generateOptionFor is enabled', () => {
    component.questionDetails = msqDetailsWithGeneratedFor;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });
});
