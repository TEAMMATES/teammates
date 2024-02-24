import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { Component } from '@angular/core';
import { QuestionEditDetailsFormComponent } from './question-edit-details-form.component';
import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
} from '../../../../types/api-output';
import { DEFAULT_MSQ_QUESTION_DETAILS } from '../../../../types/default-question-structs';
import { NO_VALUE } from '../../../../types/feedback-response-details';

/**
 * Question details edit form component for Msq question.
 */
@Component({
  selector: 'tm-msq-question-edit-details-form',
  templateUrl: './msq-question-edit-details-form.component.html',
  styleUrls: ['./msq-question-edit-details-form.component.scss', './cdk-drag-drop.scss'],
})
export class MsqQuestionEditDetailsFormComponent
    extends QuestionEditDetailsFormComponent<FeedbackMsqQuestionDetails> {

  readonly PARTICIPANT_TYPES: string[] = [
    FeedbackParticipantType.STUDENTS,
    FeedbackParticipantType.STUDENTS_EXCLUDING_SELF,
    FeedbackParticipantType.TEAMS,
    FeedbackParticipantType.TEAMS_EXCLUDING_SELF,
    FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF,
    FeedbackParticipantType.OWN_TEAM_MEMBERS,
    FeedbackParticipantType.INSTRUCTORS,
  ];

  // Used to store and restore user input when user toggles generate option
  storageModel: FeedbackMsqQuestionDetails = {
    ...DEFAULT_MSQ_QUESTION_DETAILS(),
    msqChoices: ['', ''],
    minSelectableChoices: NO_VALUE,
    maxSelectableChoices: NO_VALUE,
  };

  constructor() {
    super(DEFAULT_MSQ_QUESTION_DETAILS());
  }

  /**
   * Reorders the list on dragging the Msq options.
   */
  onMsqOptionDropped(event: CdkDragDrop<string[]>): void {
    if (!this.isEditable) {
      return;
    }

    const newWeights: number[] = this.model.msqWeights.slice();
    const newOptions: string[] = this.model.msqChoices.slice();
    moveItemInArray(newOptions, event.previousIndex, event.currentIndex);
    moveItemInArray(newWeights, event.previousIndex, event.currentIndex);
    this.triggerModelChangeBatch({
      msqChoices: newOptions,
      msqWeights: newWeights,
    });
  }

  /**
   * Displays new Msq weight at specified index.
   */
  onMsqWeightEntered(event: number, index: number): void {
    const newWeights: number[] = this.model.msqWeights.slice();
    newWeights[index] = event;
    this.triggerModelChange('msqWeights', newWeights);
  }

  /**
   * Increases number of Msq options.
   */
  increaseNumberOfMsqOptions(): void {
    const fieldsToUpdate: any = {};
    const newOptions: string[] = this.model.msqChoices.slice();
    newOptions.push('');
    fieldsToUpdate.msqChoices = newOptions;
    if (this.model.hasAssignedWeights) {
      const newWeights: number[] = this.model.msqWeights.slice();
      newWeights.push(0);
      fieldsToUpdate.msqWeights = newWeights;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Deletes a Msq option.
   */
  onMsqOptionDeleted(event: number): void {
    const fieldsToUpdate: any = {};
    const newOptions: string[] = this.model.msqChoices.slice();
    newOptions.splice(event, 1);
    fieldsToUpdate.msqChoices = newOptions;
    if (this.model.hasAssignedWeights) {
      const newWeights: number[] = this.model.msqWeights.slice();
      newWeights.splice(event, 1);
      fieldsToUpdate.msqWeights = newWeights;
    }
    const totalNewSelectableChoices: number = newOptions.length + (this.model.otherEnabled ? 1 : 0);
    if (this.isMinSelectableChoicesEnabled && this.model.minSelectableChoices > totalNewSelectableChoices) {
      fieldsToUpdate.minSelectableChoices = totalNewSelectableChoices;
    }
    if (this.isMaxSelectableChoicesEnabled && this.model.maxSelectableChoices > totalNewSelectableChoices) {
      fieldsToUpdate.maxSelectableChoices = totalNewSelectableChoices;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Displays maxSelectableOption value.
   */
  get displayValueForMaxSelectableOption(): any {
    return this.isMaxSelectableChoicesEnabled ? this.model.maxSelectableChoices : '';
  }

  /**
   * Displays minSelectableOption value.
   */
  get displayValueForMinSelectableOption(): any {
    return this.isMinSelectableChoicesEnabled ? this.model.minSelectableChoices : '';
  }

  /**
   * Displays new Msq option at specified index.
   */
  onMsqOptionEntered(event: string, index: number): void {
    const newOptions: string[] = this.model.msqChoices.slice();
    newOptions[index] = event;
    this.triggerModelChange('msqChoices', newOptions);
  }

  /**
   * Triggers the setting of choosing other option.
   */
  triggerOtherEnabled(checked: boolean): void {
    const fieldsToUpdate: any = {
      otherEnabled: checked,
      msqOtherWeight: 0,
    };
    const totalNewSelectableChoices: number = this.model.msqChoices.length + (checked ? 1 : 0);
    if (this.isMinSelectableChoicesEnabled && this.model.minSelectableChoices > totalNewSelectableChoices) {
      fieldsToUpdate.minSelectableChoices = totalNewSelectableChoices;
    }
    if (this.isMaxSelectableChoicesEnabled && this.model.maxSelectableChoices > totalNewSelectableChoices) {
      fieldsToUpdate.maxSelectableChoices = totalNewSelectableChoices;
    }
    this.triggerModelChangeBatch(fieldsToUpdate);
  }

  /**
   * Assigns a default value to generateOptionsFor when checkbox is clicked.
   */
  triggerGeneratedOptionsChange(checked: boolean): void {
    if (checked) {
      this.storageModel = this.model;
      this.triggerModelChangeBatch({
        generateOptionsFor: FeedbackParticipantType.STUDENTS,
        msqChoices: [],
        otherEnabled: false,
        hasAssignedWeights: false,
        msqWeights: [],
        msqOtherWeight: 0,
      });
    } else {
      // Exclude maxSelectableChoices and minSelectableChoices because the checkbox shouldn't affect them
      const { maxSelectableChoices, minSelectableChoices, ...others }: FeedbackMsqQuestionDetails = this.storageModel;
      this.triggerModelChangeBatch(others);
    }
  }

  /**
   * Assigns a default value to maxSelectableOptions when checkbox is clicked.
   */
  triggerMaxSelectableOptionsChange(checked: boolean): void {
    if (!checked) {
      this.triggerModelChange('maxSelectableChoices', NO_VALUE);
      return;
    }

    if (this.isMinSelectableChoicesEnabled) {
      this.triggerModelChange('maxSelectableChoices', this.model.minSelectableChoices);
    } else {
      this.triggerModelChange('maxSelectableChoices', 2);
    }
  }

  /**
   * Assigns a default value to minSelectableOptions when checkbox is clicked.
   */
  triggerMinSelectableOptionsChange(checked: boolean): void {
    const minSelectableChoices: number = checked ? 2 : NO_VALUE;
    this.triggerModelChange('minSelectableChoices', minSelectableChoices);
  }

  /**
   * Tracks the Msq option by index.
   */
  trackMsqOption(index: number): string {
    return index.toString();
  }

  /**
   * Tracks the Msq weight by index.
   */
  trackMsqWeight(index: number): string {
    return index.toString();
  }

  /**
   * Checks if the generatedOptionsFor checkbox is enabled.
   */
  get isGeneratedOptionsEnabled(): boolean {
    return this.model.generateOptionsFor !== FeedbackParticipantType.NONE;
  }

  /**
   * Checks if the maxSelectedChoices checkbox is enabled.
   */
  get isMaxSelectableChoicesEnabled(): boolean {
    return this.model.maxSelectableChoices !== NO_VALUE;
  }

  /**
   * Checks if the minSelectedChoices checkbox is enabled.
   */
  get isMinSelectableChoicesEnabled(): boolean {
    return this.model.minSelectableChoices !== NO_VALUE;
  }

  /**
   * Returns maximum value that minSelectable option can take.
   */
  get maxMinSelectableValue(): number {
    if (this.isMaxSelectableChoicesEnabled) {
      return this.model.maxSelectableChoices;
    }
    return this.totalSelectableChoices;
  }

  /**
   * Gets total selectable choices.
   */
  get totalSelectableChoices(): number {
    if (this.isGeneratedOptionsEnabled) {
      return Number.MAX_VALUE;
    }
    return this.model.msqChoices.length + (this.model.otherEnabled ? 1 : 0);
  }

  /**
   * Triggers the display of the weight column for the Msq options if weights option is checked/unchecked.
   */
  triggerWeightsColumn(checked: boolean): void {
    this.triggerModelChangeBatch({
      msqWeights: checked ? Array(this.model.msqChoices.length).fill(0) : [],
      msqOtherWeight: 0,
      hasAssignedWeights: checked,
    });
  }
}
