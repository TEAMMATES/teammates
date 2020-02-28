import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../../../../types/api-output';
import { TeammatesCommonModule } from '../../teammates-common/teammates-common.module';
import { McqQuestionAdditionalInfoComponent } from './mcq-question-additional-info.component';

describe('McqQuestionAdditionalInfoComponent', () => {
  let component: McqQuestionAdditionalInfoComponent;
  let fixture: ComponentFixture<McqQuestionAdditionalInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [McqQuestionAdditionalInfoComponent],
      imports: [
        TeammatesCommonModule,
      ],
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

  it('should snap with default view', () => {
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

    expect(fixture).toMatchSnapshot();
  });

  it('should show other when "other" option is enabled', () => {
    component.questionDetails = mcqDetailWithOther;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

  it('should show generator option message when "generateOptionsFor" is enabled', () => {
    component.questionDetails = mcqDetailWithGenerateOption;
    fixture.detectChanges();

    expect(fixture).toMatchSnapshot();
  });

});
