import { Injectable } from '@angular/core';
import { SortBy, SortOrder } from '../types/sort-properties';

/**
 * Handles comparison logic between sortable table elements
 */
@Injectable({
  providedIn: 'root',
})
export class TableComparatorService {

  constructor() { }

  /**
   * Compares two strings lexicographically depending on order given.
   */
  compareLexicographically(strA: string, strB: string, order: SortOrder): number {
    if (order === SortOrder.ASC) {
      return strA.localeCompare(strB);
    }
    if (order === SortOrder.DESC) {
      return strB.localeCompare(strA);
    }
    return 0;
  }

  /**
   * Compares two strings naturally depending on the order given.
   */
  compareNaturally(strA: string, strB: string, order: SortOrder): number {
    if (order === SortOrder.ASC) {
      return strA.localeCompare(strB, undefined, { numeric: true });
    }
    if (order === SortOrder.DESC) {
      return strB.localeCompare(strA, undefined, { numeric: true });
    }
    return 0;
  }

  /**
   * Compares two strings depending on element to sort by and the order given.
   */
  compare(sortBy: SortBy, order: SortOrder, strA: string, strB: string): number {
    switch (sortBy) {
      case SortBy.SECTION_NAME:
      case SortBy.TEAM_NAME:
      case SortBy.SESSION_NAME:
        return this.compareNaturally(strA, strB, order);
      case SortBy.STUDENT_NAME:
      case SortBy.EMAIL:
      case SortBy.JOIN_STATUS:
      case SortBy.COURSE_ID:
      case SortBy.COURSE_NAME:
      case SortBy.COURSE_CREATION_DATE:
      case SortBy.SESSION_COMPLETION_STATUS:
      case SortBy.SESSION_START_DATE:
      case SortBy.SESSION_END_DATE:
      case SortBy.SESSION_CREATION_DATE:
      case SortBy.SESSION_DELETION_DATE:
      case SortBy.QUESTION_TYPE:
      case SortBy.QUESTION_TEXT:
        return this.compareLexicographically(strA, strB, order);
      default:
        return 0;
    }
  }
}
