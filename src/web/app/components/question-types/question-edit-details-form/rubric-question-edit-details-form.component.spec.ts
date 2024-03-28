import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormsModule } from '@angular/forms';
import { RubricQuestionEditDetailsFormComponent } from './rubric-question-edit-details-form.component';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { createBuilder } from '../../../../test-helpers/generic-builder';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { FeedbackQuestionType, FeedbackRubricQuestionDetails } from '../../../../types/api-output';
import { SimpleModalModule } from '../../simple-modal/simple-modal.module';

describe('RubricQuestionEditDetailsFormComponent', () => {
  let component: RubricQuestionEditDetailsFormComponent;
  let fixture: ComponentFixture<RubricQuestionEditDetailsFormComponent>;
  let simpleModalService: SimpleModalService;
  let triggerModelChangeSpy: jest.SpyInstance;
  let triggerModelChangeBatchSpy: jest.SpyInstance;

  const feedbackRubricQuestionDetailsBuilder = createBuilder<FeedbackRubricQuestionDetails>({
    questionType: FeedbackQuestionType.RUBRIC,
    questionText: '',
    hasAssignedWeights: false,
    rubricWeightsForEachCell: [],
    rubricChoices: [],
    rubricSubQuestions: [],
    rubricDescriptions: [],
  });

  // const feedbackRubricResponseDetailsBuilder = createBuilder<FeedbackRubricResponseDetails>({
  //   questionType: FeedbackQuestionType.RUBRIC,
  //   answer: [],
  // });

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        FormsModule,
        SimpleModalModule,
      ],
      declarations: [RubricQuestionEditDetailsFormComponent],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RubricQuestionEditDetailsFormComponent);
    simpleModalService = TestBed.inject(SimpleModalService);
    component = fixture.componentInstance;
    triggerModelChangeSpy = jest.spyOn(component, 'triggerModelChange');
    triggerModelChangeBatchSpy = jest.spyOn(component, 'triggerModelChangeBatch');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('triggerRubricChoiceChange: should call triggerModelChange with the correct newChoices', () => {
    component.model = feedbackRubricQuestionDetailsBuilder.rubricChoices(['a', 'b', 'c']).build();

    component.triggerRubricChoiceChange('e', 1);

    expect(triggerModelChangeSpy).toHaveBeenCalledWith('rubricChoices', ['a', 'e', 'c']);
  });

  it('triggerRubricSubQuestionChange: should call triggerModelChange with the correct newSubQuestions', () => {
    component.model = feedbackRubricQuestionDetailsBuilder.rubricSubQuestions(['a', 'b', 'c']).build();

    component.triggerRubricSubQuestionChange('e', 1);

    expect(triggerModelChangeSpy).toHaveBeenCalledWith('rubricSubQuestions', ['a', 'e', 'c']);
  });

  it('triggerRubricDescriptionChange: should call triggerModelChange with the correct newDescriptions', () => {
    component.model = feedbackRubricQuestionDetailsBuilder.rubricDescriptions([['a'], ['b'], ['c']]).build();

    component.triggerRubricDescriptionChange('e', 1, 0);

    expect(triggerModelChangeSpy).toHaveBeenCalledWith('rubricDescriptions', [['a'], ['e'], ['c']]);
  });

  it('triggerRubricWeightChange: should call triggerModelChange with the correct newWeightsForEachCell', () => {
    component.model = feedbackRubricQuestionDetailsBuilder.rubricWeightsForEachCell([[1], [2], [3]]).build();

    component.triggerRubricWeightChange(4, 0, 0);

    expect(triggerModelChangeSpy).toHaveBeenCalledWith('rubricWeightsForEachCell', [[4], [2], [3]]);
  });

  it('trackByIndex: returns string representation of a number', () => {
    expect(component.trackByIndex(1)).toBe('1');
    expect(component.trackByIndex(-1)).toBe('-1');
    expect(component.trackByIndex(1.5)).toBe('1.5');
  });

  it('addNewSubQuestion: calls triggerModelChangeBatch with the correct'
    + 'parameters when hasAssignedWeights is false', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(false)
                        .rubricChoices(['1', '2'])
                        .rubricSubQuestions(['a', 'b'])
                        .rubricDescriptions([])
                        .build();

    component.addNewSubQuestion();

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricSubQuestions: ['a', 'b', ''],
      rubricDescriptions: [['', '']],
      rubricWeightsForEachCell: [],
    });
  });

  it('addNewSubQuestion: calls triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is true', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(true)
                        .rubricChoices(['1', '2'])
                        .rubricSubQuestions(['a', 'b'])
                        .rubricDescriptions([['desc 1'], ['desc 2']])
                        .rubricWeightsForEachCell([])
                        .build();

    component.addNewSubQuestion();

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricSubQuestions: ['a', 'b', ''],
      rubricDescriptions: [['desc 1'], ['desc 2'], ['', '']],
      rubricWeightsForEachCell: [[0, 0]],
    });
  });

  it('addNewChoice: calls triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is false', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(false)
                        .rubricChoices(['1'])
                        .rubricDescriptions([['desc 1'], ['desc 2']])
                        .build();

    component.addNewChoice();

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricChoices: ['1', ''],
      rubricDescriptions: [['desc 1', ''], ['desc 2', '']],
      rubricWeightsForEachCell: [],
    });
  });

  it('addNewChoice: calls triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is true', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(true)
                        .rubricChoices(['1'])
                        .rubricDescriptions([['desc 1'], ['desc 2']])
                        .rubricWeightsForEachCell([[1], [2]])
                        .build();

    component.addNewChoice();

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricChoices: ['1', ''],
      rubricDescriptions: [['desc 1', ''], ['desc 2', '']],
      rubricWeightsForEachCell: [[1, 0], [2, 0]],
    });
  });

  it('moveChoice: calls triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is false', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(false)
                        .rubricChoices(['1', '2', '3'])
                        .rubricDescriptions([['d1', 'd2', 'd3'], ['d4', 'd5', 'd6'], ['d7', 'd8', 'd9']])
                        .build();

    component.moveChoice(0, 1);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricChoices: ['2', '1', '3'],
      rubricDescriptions: [['d2', 'd1', 'd3'], ['d5', 'd4', 'd6'], ['d8', 'd7', 'd9']],
      rubricWeightsForEachCell: [],
    });
  });

  it('moveChoice: calls triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is true', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(true)
                        .rubricChoices(['1', '2', '3'])
                        .rubricDescriptions([['d1', 'd2', 'd3'], ['d4', 'd5', 'd6'], ['d7', 'd8', 'd9']])
                        .rubricWeightsForEachCell([[1, 2, 3], [4, 5, 6], [7, 8, 9]])
                        .build();

    component.moveChoice(0, 1);

    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricChoices: ['2', '1', '3'],
      rubricDescriptions: [['d2', 'd1', 'd3'], ['d5', 'd4', 'd6'], ['d8', 'd7', 'd9']],
      rubricWeightsForEachCell: [[2, 1, 3], [5, 4, 6], [8, 7, 9]],
    });
  });

  it('deleteSubQuestion: should not call triggerModelChangeBatch or simpleModalService'
    + 'if there is only 1 subQuestion', () => {
    const simpleModalServiceSpy = jest.spyOn(simpleModalService, 'openConfirmationModal');
    component.model = feedbackRubricQuestionDetailsBuilder.rubricSubQuestions(['1']).build();

    component.deleteSubQuestion(0);

    expect(simpleModalServiceSpy).not.toHaveBeenCalled();
    expect(triggerModelChangeBatchSpy).not.toHaveBeenCalled();
  });

  it('deleteSubQuestion: should call simpleModalService and triggerModelChangeBatch'
    + 'with correct paramters when hasAssignedWeights is false', async () => {
    const promise: Promise<void> = Promise.resolve();
    const simpleModalServiceSpy = jest.spyOn(simpleModalService, 'openConfirmationModal')
                                      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(false)
                        .rubricSubQuestions(['1', '2', '3'])
                        .rubricDescriptions([['d1'], ['d2'], ['d3']])
                        .build();

    component.deleteSubQuestion(1);

    await promise;

    expect(simpleModalServiceSpy).toHaveBeenCalled();
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricSubQuestions: ['1', '3'],
      rubricDescriptions: [['d1'], ['d3']],
      rubricWeightsForEachCell: [],
    });
  });

  it('deleteSubQuestion: should call simpleModalService and triggerModelChangeBatch with'
    + 'correct paramters when hasAssignedWeights is true', async () => {
    const promise: Promise<void> = Promise.resolve();
    const simpleModalServiceSpy = jest.spyOn(simpleModalService, 'openConfirmationModal')
                                      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(true)
                        .rubricSubQuestions(['1', '2', '3'])
                        .rubricDescriptions([['d1'], ['d2'], ['d3']])
                        .rubricWeightsForEachCell([[1], [2], [3]])
                        .build();

    component.deleteSubQuestion(1);

    await promise;

    expect(simpleModalServiceSpy).toHaveBeenCalled();
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricSubQuestions: ['1', '3'],
      rubricDescriptions: [['d1'], ['d3']],
      rubricWeightsForEachCell: [[1], [3]],
    });
  });

  it('deleteChoice: should call simpleModalService and triggerModelChangeBatch with'
    + 'correct paramters when hasAssignedWeights is false', async () => {
    const promise: Promise<void> = Promise.resolve();
    const simpleModalServiceSpy = jest.spyOn(simpleModalService, 'openConfirmationModal')
                                      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(false)
                        .rubricChoices(['1', '2', '3'])
                        .rubricDescriptions([['d1', 'd2', 'd3'], ['d4', 'd5', 'd6'], ['d7', 'd8', 'd9']])
                        .build();

    component.deleteChoice(1);

    await promise;

    expect(simpleModalServiceSpy).toHaveBeenCalled();
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricChoices: ['1', '3'],
      rubricDescriptions: [['d1', 'd3'], ['d4', 'd6'], ['d7', 'd9']],
      rubricWeightsForEachCell: [],
    });
  });

  it('deleteChoice: should call simpleModalService and triggerModelChangeBatch with'
    + 'correct paramters when hasAssignedWeights is true', async () => {
    const promise: Promise<void> = Promise.resolve();
    const simpleModalServiceSpy = jest.spyOn(simpleModalService, 'openConfirmationModal')
                                      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(true)
                        .rubricChoices(['1', '2', '3'])
                        .rubricDescriptions([['d1', 'd2', 'd3'], ['d4', 'd5', 'd6'], ['d7', 'd8', 'd9']])
                        .rubricWeightsForEachCell([[1, 2, 3], [4, 5, 6], [7, 8, 9]])
                        .build();

    component.deleteChoice(1);

    await promise;

    expect(simpleModalServiceSpy).toHaveBeenCalled();
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricChoices: ['1', '3'],
      rubricDescriptions: [['d1', 'd3'], ['d4', 'd6'], ['d7', 'd9']],
      rubricWeightsForEachCell: [[1, 3], [4, 6], [7, 9]],
    });
  });

  it('triggerChoicesWeight: should call triggerModelChangeBatch with the'
    + 'correct parameters when isEnabled is true', () => {
    component.model = feedbackRubricQuestionDetailsBuilder.rubricDescriptions([['d1', 'd2'], ['d3', 'd4']]).build();

    component.triggerChoicesWeight(true);
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      hasAssignedWeights: true,
      rubricWeightsForEachCell: [[0, 0], [0, 0]],
    });
  });

  it('triggerChoicesWeight: should call triggerModelChangeBatch with the'
    + 'correct parameters when isEnabled is false', () => {
    component.model = feedbackRubricQuestionDetailsBuilder.rubricDescriptions([['d1', 'd2'], ['d3', 'd4']]).build();

    component.triggerChoicesWeight(false);
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      hasAssignedWeights: false,
      rubricWeightsForEachCell: [],
    });
  });

  it('moveRow: should call triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is false', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(false)
                        .rubricSubQuestions(['1', '2', '3'])
                        .rubricDescriptions([['d1'], ['d2'], ['d3']])
                        .build();

    component.moveRow(0, 1);
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricSubQuestions: ['2', '1', '3'],
      rubricDescriptions: [['d2'], ['d1'], ['d3']],
      rubricWeightsForEachCell: [],
    });
  });

  it('moveRow: should call triggerModelChangeBatch with the correct parameters'
    + 'when hasAssignedWeights is true', () => {
    component.model = feedbackRubricQuestionDetailsBuilder
                        .hasAssignedWeights(true)
                        .rubricSubQuestions(['1', '2', '3'])
                        .rubricDescriptions([['d1'], ['d2'], ['d3']])
                        .rubricWeightsForEachCell([[1], [2], [3]])
                        .build();

    component.moveRow(0, 1);
    expect(triggerModelChangeBatchSpy).toHaveBeenCalledWith({
      rubricSubQuestions: ['2', '1', '3'],
      rubricDescriptions: [['d2'], ['d1'], ['d3']],
      rubricWeightsForEachCell: [[2], [1], [3]],
    });
  });
});
