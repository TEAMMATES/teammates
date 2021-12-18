import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstructorData } from "./instructor-data";

/**
 * A single row of data of a new instructor.
 */
@Component({
  selector: 'tr[tm-new-instructor-data-row]',
  templateUrl: './new-instructor-data-row.component.html',
  styleUrls: ['./new-instructor-data-row.component.scss']
})
export class NewInstructorDataRowComponent implements OnInit {
  @Input() instructor!: InstructorData;
  @Input() index!: number;
  @Input() activeRequests!: number;
  @Output() onAddInstructor: EventEmitter<void> = new EventEmitter();
  @Output() onRemoveInstructor: EventEmitter<void> = new EventEmitter();
  @Output() onToggleEditMode: EventEmitter<boolean> = new EventEmitter();

  isBeingEdited: boolean = false;
  editedInstructorName!: string;
  editedInstructorEmail!: string;
  editedInstructorInstitution!: string;

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Adds the instructor at the i-th index.
   */
  addInstructor(): void {
    this.onAddInstructor.emit();
  }

  /**
   * Cancels the instructor at the i-th index.
   */
  removeInstructor(): void {
    this.onRemoveInstructor.emit();
  }

  /**
   * Starts editing the instructor.
   */
  editInstructor(): void {
    this.resetEditedInstructorDetails();
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
    // resetting here might be unnecessary
    this.resetEditedInstructorDetails();
  }

  /**
   * Cancels the edit of the instructor's details.
   */
  cancelEditInstructor(): void {
    this.setEditModeAndAlertParent(false);
    // resetting here might be unnecessary
    this.resetEditedInstructorDetails();
  }

  /**
   * Resets the edited instructor details to the original details.
   */
  resetEditedInstructorDetails(): void {
    this.editedInstructorName = this.instructor.name;
    this.editedInstructorEmail = this.instructor.email;
    this.editedInstructorInstitution = this.instructor.institution;
  }

  /**
   * Sets whether edit mode is enabled and then alerts the parent of the new status.
   *
   * @param isEnabled Whether edit mode is enabled.
   */
  setEditModeAndAlertParent(isEnabled: boolean): void {
    this.isBeingEdited = isEnabled;
    this.alertParentEditModeToggled();
  }

  /**
   * Alerts the parent that the edit mode was toggled and passes whether this is in edit mode or not.
   */
  alertParentEditModeToggled(): void {
    this.onToggleEditMode.emit(this.isBeingEdited);
  }

}
