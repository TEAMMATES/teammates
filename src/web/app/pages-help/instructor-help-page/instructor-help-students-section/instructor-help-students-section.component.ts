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
  showArr: Boolean[];
  size: number;
  searchedTerms: number;

  constructor() {
    this.showArr = [];
    this.size = 0;
    this.searchedTerms = -1;
  }

  ngOnInit(): void {
    this.size = terms.length;
    for (let i: number = 0; i < this.size; i += 1) this.showArr.push(true);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.key === '') {
      this.reset_faq();
    } else {
      this.filter_faq(changes.key.currentValue);
    }
  }

  private reset_faq(): void {
    for (let i: number = 0; i < this.size; i += 1) this.showArr[i] = true;
    this.searchedTerms = -1;
  }

  private filter_faq(val: String): void {
    this.searchedTerms = 0;
    for (const term of terms) {
      this.showArr[term.tag] = term.text.includes(val);

      if (this.showArr[term.tag]) this.searchedTerms += 1;
    }
  }
}
