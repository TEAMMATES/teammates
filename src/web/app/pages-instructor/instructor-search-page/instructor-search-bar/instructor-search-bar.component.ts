import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SearchParams } from "../instructor-search-page.component";

/**
 * Search bar on instructor search page
 */
@Component({
  selector: 'tm-instructor-search-bar',
  templateUrl: './instructor-search-bar.component.html',
  styleUrls: ['./instructor-search-bar.component.scss'],
})
export class InstructorSearchBarComponent implements OnInit {

  @Input() searchParams: SearchParams = {
    searchKey: '',
    isSearchForStudents: true,
    isSearchForComments: false
  }

  @Output() searched: EventEmitter<SearchParams> = new EventEmitter<SearchParams>();

  constructor() {}

  ngOnInit(): void {}

  /**
   * send the search data to parent for processing
   */
  search(): void {
    this.searched.emit(this.searchParams);
  }
}
