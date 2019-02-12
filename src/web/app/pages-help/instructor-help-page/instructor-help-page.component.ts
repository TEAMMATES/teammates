import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
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
  currentSection: String = 'body';

  @ViewChild('helpPage') bodyRef ?: ElementRef;

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
   * Scrolls to the section passed in
   */
  scroll(section: string): void {
    const el: ElementRef = Array.prototype.slice
        .call(this.bodyRef.nativeElement.childNodes).find((x: any) => x.id === section);
    if (el != undefined) {
      el.scrollIntoView();
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
