import { Component, EventEmitter, Input, Output } from '@angular/core';

/**
 * Parameters inputted by user to be used in search
 */
export interface SearchParams {
  searchKey: string;
}

/**
 * Search bar on instructor search page
 */
@Component({
  selector: 'tm-instructor-search-bar',
  templateUrl: './instructor-search-bar.component.html',
  styleUrls: ['./instructor-search-bar.component.scss'],
})
export class InstructorSearchBarComponent {

  characterLimit = 100;

  @Input() searchParams: SearchParams = {
    searchKey: '',
  };

  @Output() searched: EventEmitter<any> = new EventEmitter();

  @Output() searchParamsChange: EventEmitter<SearchParams> = new EventEmitter();

  /**
   * send the search data to parent for processing
   */
  search(): void {
    this.searched.emit();
  }

  triggerSearchParamsChangeEvent(field: string, data: any): void {
    this.searchParamsChange.emit({ ...this.searchParams, [field]: data });
  }

  onSearchKeyChange(newKey: string): void {
    this.triggerSearchParamsChangeEvent('searchKey', newKey);
  }

}
