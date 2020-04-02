import { Component, Input, OnChanges, OnInit } from '@angular/core';
import { Student } from '../../../types/api-output';

/**
 * Instructor sessions results page No Response Panel.
 */
@Component({
  selector: 'tm-instructor-session-no-response-panel',
  templateUrl: './instructor-session-no-response-panel.component.html',
  styleUrls: ['./instructor-session-no-response-panel.component.scss'],
})
export class InstructorSessionNoResponsePanelComponent implements OnInit, OnChanges {

  @Input() noResponseStudents: Student[] = [];
  @Input() section: string = '';
  @Input() session: any = {};
  isTabExpanded: boolean = false;

  noResponseStudentsInSection: Student[] = [];
  constructor() { }

  ngOnInit(): void {
    this.filterStudentsBySection();
  }

  ngOnChanges(): void {
    this.filterStudentsBySection();
  }

  private filterStudentsBySection(): void {
    if (this.section) {
      this.noResponseStudentsInSection =
          this.noResponseStudents.filter((student: Student) => student.sectionName === this.section);
    } else {
      this.noResponseStudentsInSection = this.noResponseStudents;
    }
  }
  /**
   * Expands the tab of the no response panel.
   */
  expandTab(): void {
    this.isTabExpanded = !this.isTabExpanded;
  }

}
