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
  charactersLeft = this.characterLimit;
  characterLimitReached = false;

  @Input() searchParams: SearchParams = {
    searchKey: '',
  };

  @Output() searched: EventEmitter<any> = new EventEmitter();

  @Output() searchParamsChange: EventEmitter<SearchParams> = new EventEmitter();

  /**
   * send the search data to parent for processing
   */
  search(): void {
    if (this.characterLimitReached) return;
    this.searched.emit();
  }

  triggerSearchParamsChangeEvent(field: string, data: any): void {
    this.charactersLeft = this.characterLimit - data.length;
    if (this.charactersLeft <= 0) {
      this.characterLimitReached = true;
    } else {
      this.characterLimitReached = false;
    }
    this.searchParamsChange.emit({ ...this.searchParams, [field]: data });
  }

  onSearchKeyChange(newKey: string): void {
    this.triggerSearchParamsChangeEvent('searchKey', newKey);
  }

}
