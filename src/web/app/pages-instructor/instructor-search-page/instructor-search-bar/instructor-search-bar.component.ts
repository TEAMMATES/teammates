import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SearchQuery } from '../instructor-search-page.component';

/**
 * Search bar on instructor search page
 */
@Component({
  selector: 'tm-instructor-search-bar',
  templateUrl: './instructor-search-bar.component.html',
  styleUrls: ['./instructor-search-bar.component.scss'],
})
export class InstructorSearchBarComponent implements OnInit {

  @Input() searchKey: string = '';
  searchStudents: boolean = true;
  searchFeedbackSessionData: boolean = false;
  @Output() searched: EventEmitter<SearchQuery> = new EventEmitter<SearchQuery>();

  constructor() { }

  ngOnInit(): void {
  }

  /**
   * send the search data to parent for processing
   */
  search(): void {
    this.searched.emit({
      searchKey: this.searchKey,
      searchStudents: this.searchStudents,
      searchFeedbackSessionData: this.searchFeedbackSessionData,
    });
  }

}
