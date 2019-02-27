import { Component, EventEmitter, OnInit, Input, Output } from '@angular/core';
import { SearchQuery } from '../instructor-search-page.component';

@Component({
  selector: 'tm-instructor-search-bar',
  templateUrl: './instructor-search-bar.component.html',
  styleUrls: ['./instructor-search-bar.component.scss']
})
export class InstructorSearchBarComponent implements OnInit {

  @Input() searchKey: string = '';
  searchStudents: boolean = true;
  searchFeedbackSessionData: boolean = false;
  @Output() searched = new EventEmitter<SearchQuery>();

  constructor() { }

  ngOnInit() {
  }

  search(): void {
    this.searched.emit({
      searchKey: this.searchKey,
      searchStudents: this.searchStudents,
      searchFeedbackSessionData: this.searchFeedbackSessionData,
    });
  }

}
