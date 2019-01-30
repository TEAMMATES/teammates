import { Component, OnInit } from '@angular/core';

/**
 * Instructor help page.
 */
@Component({
  selector: 'tm-instructor-help-page',
  templateUrl: './instructor-help-page.component.html',
  styleUrls: ['./instructor-help-page.component.scss'],
})
export class InstructorHelpPageComponent implements OnInit {
  searchTerm: String = '';
  key: String = '';

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * Search method triggered on button click.
   * If not empty, update key input to child components.
   * Used to filter the FAQ.
   */
  search(): void {
    if (this.searchTerm !== '') {
      this.key = this.searchTerm.toLowerCase();
    }
  }

  /**
   * Clear method triggered on button click.
   * Resets the key input to child components.
   * Used to remove any filters within the FAQ.
   */
  clear(): void {
    this.searchTerm = '';
    this.key = '';
  }
}
