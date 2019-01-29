import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';

import { default as terms } from './student_filter.json';

/**
 * All FAQ related to students in the Instructor help page.
 * Please include a json file of all key terms and tag number for filtering.
 */
@Component({
  selector: 'tm-instructor-help-students-section',
  templateUrl: './instructor-help-students-section.component.html',
  styleUrls: ['./instructor-help-students-section.component.scss'],
})
export class InstructorHelpStudentsSectionComponent implements OnInit, OnChanges {

  @Input() key: String = '';
  show_arr: Boolean[];
  size: number;
  searched_terms: number;

  constructor() {
    this.show_arr = [];
    this.size = 0;
    this.searched_terms = -1;
  }

  ngOnInit(): void {
    this.size = terms.length;
    for (let i: number = 0; i < this.size; i += 1) this.show_arr.push(true);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.key === '') {
      this.reset_faq();
    } else {
      this.filter_faq();
    }
  }

  private reset_faq(): void {
    for (let i: number = 0; i < this.size; i += 1) this.show_arr[i] = true;
    this.searched_terms = -1;
  }

  private filter_faq(): void {
    this.searched_terms = 0;
    for (const term of terms) {
      this.show_arr[term.tag] = term.text.includes(this.key);

      if (this.show_arr[term.tag]) this.searched_terms += 1;
    }
  }
}
