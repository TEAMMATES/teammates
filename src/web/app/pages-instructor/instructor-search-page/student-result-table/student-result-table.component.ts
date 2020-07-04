import { Component, Input, OnInit } from '@angular/core';
import { StudentListSectionData } from '../../../components/student-list/student-list-section-data';
import { StudentListRowModel } from '../../../components/student-list/student-list.component';

/**
 * Search result for a list of sections containing students of a course
 */
export interface SearchStudentsTable {
  courseId: string;
  sections: StudentListSectionData[];
}

/**
 * Search result for a list of students in a course
 */
export interface SearchStudentsListRowTable {
  courseId: string;
  students: StudentListRowModel[];
}

/**
 * Table to show student results, grouped by courses
 */
@Component({
  selector: 'tm-student-result-table',
  templateUrl: './student-result-table.component.html',
  styleUrls: ['./student-result-table.component.scss'],
})
export class StudentResultTableComponent implements OnInit {

  @Input() studentTables: SearchStudentsListRowTable[] = [];
  @Input() isActionButtonsEnabled: boolean = true;

  constructor() { }

  ngOnInit(): void {
  }

}
