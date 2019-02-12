import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Instructor help page.
 */
@Component({
  selector: 'tm-instructor-help-page',
  templateUrl: './instructor-help-page.component.html',
  styleUrls: ['./instructor-help-page.component.scss'],
})
export class InstructorHelpPageComponent implements OnInit {
  readonly supportEmail: string = environment.supportEmail;
  searchTerm: String = '';
  key: String = '';
  currentSection = 'body';

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Filters the help contents and displays only those that matches the filter.
   */
  search(): void {
    if (this.searchTerm !== '') {
      this.key = this.searchTerm.toLowerCase();
    } else {
      this.clear();
    }
  }

  /**
   * Scrolls the screen to the section name.
   * @param section
   */
  scrollTo(section: string) {
    let query = document.querySelector('#' + section);
    if (query != null) {
      query.scrollIntoView();
      this.currentSection = section;
    }
  }

  /**
   * Clears the filter used for search.
   */
  clear(): void {
    this.searchTerm = '';
    this.key = '';
  }
}
