import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstructorData } from '../instructor-data';

/**
 * A single row of data of a new instructor.
 */
@Component({
  // The following selector code style violation of https://angular.io/guide/styleguide#style-05-02 and
  // https://angular.io/guide/styleguide#style-05-03 seems necessary according to
  // https://stackoverflow.com/questions/55446740/how-to-add-row-component-in-table-in-angular-7
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'tr[tm-new-instructor-data-row]',
  templateUrl: './new-instructor-data-row.component.html',
  styleUrls: ['./new-instructor-data-row.component.scss'],
})
export class NewInstructorDataRowComponent implements OnInit {
  @Input() instructor!: InstructorData;
  @Input() index!: number;
  @Input() isAddDisabled!: boolean;
  @Output() addInstructorEvent: EventEmitter<void> = new EventEmitter();
  @Output() removeInstructorEvent: EventEmitter<void> = new EventEmitter();
  @Output() toggleEditModeEvent: EventEmitter<boolean> = new EventEmitter();

  isBeingEdited: boolean = false;
  editedInstructorName!: string;
  editedInstructorEmail!: string;
  editedInstructorInstitution!: string;

  ngOnInit(): void {
    this.resetEditedInstructorDetails();
  }

  addInstructor(): void {
    this.addInstructorEvent.emit();
  }

  removeInstructor(): void {
    this.removeInstructorEvent.emit();
  }

  /**
   * Starts editing the instructor.
   */
  editInstructor(): void {
    this.setEditModeAndAlertParent(true);
  }

  /**
   * Confirms the edit of the instructor's details.
   */
  confirmEditInstructor(): void {
    this.instructor.name = this.editedInstructorName;
    this.instructor.email = this.editedInstructorEmail;
    this.instructor.institution = this.editedInstructorInstitution;
    this.setEditModeAndAlertParent(false);
  }

  /**
   * Cancels the edit of the instructor's details.
   */
  cancelEditInstructor(): void {
    this.setEditModeAndAlertParent(false);
    this.resetEditedInstructorDetails();
  }

  /**
   * Resets the edited instructor details to the original details.
   */
  private resetEditedInstructorDetails(): void {
    this.editedInstructorName = this.instructor.name;
    this.editedInstructorEmail = this.instructor.email;
    this.editedInstructorInstitution = this.instructor.institution;
  }

  /**
   * Sets whether edit mode is enabled and then alerts the parent of the new status.
   *
   * @param isEnabled Whether edit mode is enabled.
   */
  private setEditModeAndAlertParent(isEnabled: boolean): void {
    this.isBeingEdited = isEnabled;
    this.alertParentEditModeToggled();
  }

  /**
   * Alerts the parent that the edit mode was toggled and passes whether this is in edit mode or not.
   */
  private alertParentEditModeToggled(): void {
    this.toggleEditModeEvent.emit(this.isBeingEdited);
  }

}
