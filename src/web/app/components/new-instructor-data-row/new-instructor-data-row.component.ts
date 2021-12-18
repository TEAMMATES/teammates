import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InstructorData} from "./instructor-data";

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
  @Output() onCancelInstructor: EventEmitter<void> = new EventEmitter();

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
  cancelInstructor(): void {
    this.onCancelInstructor.emit();
  }

}
