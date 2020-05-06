import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

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
  @Output() searched: EventEmitter<string> = new EventEmitter<string>();

  constructor() {}

  ngOnInit(): void {}

  /**
   * send the search data to parent for processing
   */
  search(): void {
    this.searched.emit(this.searchKey);
  }
}
