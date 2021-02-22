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
   * Compares two strings which are expected to be numbers depending on the order given.
   * If either string cannot be parsed into number, it will be seen as 'smaller'
   * If both strings cannot be parsed into number, strA will always be seen as 'larger'
   */
  compareNumbers(strA: string, strB: string, order: SortOrder): number {
    const numA: number = Number(strA);
    const numB: number = Number(strB);
    if (Number.isNaN(numA)) {
      return 1;
    }

    if (Number.isNaN(numB)) {
      return -1;
    }

    return (order === SortOrder.DESC ? -1 : 1) * Math.sign(numA - numB);
  }

  /**
   * Compares two strings depending on element to sort by and the order given.
   */
  compare(sortBy: SortBy, order: SortOrder, strA: string, strB: string): number {
    switch (sortBy) {
      case SortBy.CONTRIBUTION_VALUE:
      case SortBy.RUBRIC_SUBQUESTION:
      case SortBy.RUBRIC_CHOICE:
      case SortBy.RANK_RECIPIENTS_TEAM:
      case SortBy.RANK_RECIPIENTS_RECIPIENT:
      case SortBy.RANK_OPTIONS_OVERALL_RANK:
      case SortBy.NUMERICAL_SCALE_MAX:
      case SortBy.NUMERICAL_SCALE_MIN:
      case SortBy.MCQ_RESPONSE_COUNT:
      case SortBy.MCQ_OPTION_SELECTED_TIMES:
      case SortBy.MSQ_RESPONSE_COUNT:
      case SortBy.MSQ_OPTION_SELECTED_TIMES:
      case SortBy.SECTION_NAME:
      case SortBy.TEAM_NAME:
      case SortBy.SESSION_NAME:
        return this.compareNaturally(strA, strB, order);
      case SortBy.CONSTSUM_OPTIONS_OPTION:
      case SortBy.CONTRIBUTION_TEAM:
      case SortBy.CONTRIBUTION_RECIPIENT:
      case SortBy.COMMENTS_CREATION_DATE:
      case SortBy.RANK_RECIPIENTS_SELF_RANK:
      case SortBy.RANK_RECIPIENTS_OVERALL_RANK:
      case SortBy.RANK_RECIPIENTS_OVERALL_RANK_EXCLUDING_SELF:
      case SortBy.RANK_RECIPIENTS_TEAM_RANK:
      case SortBy.RANK_RECIPIENTS_TEAM_RANK_EXCLUDING_SELF:
      case SortBy.RANK_OPTIONS_OPTION:
      case SortBy.MCQ_CHOICE:
      case SortBy.MCQ_TEAM:
      case SortBy.MCQ_RECIPIENT_NAME:
      case SortBy.MSQ_CHOICE:
      case SortBy.MSQ_TEAM:
      case SortBy.MSQ_RECIPIENT_NAME:
      case SortBy.RESPONDENT_NAME:
      case SortBy.RESPONDENT_EMAIL:
      case SortBy.STUDENT_GENDER:
      case SortBy.INSTITUTION:
      case SortBy.NATIONALITY:
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
      case SortBy.GIVER_TEAM:
      case SortBy.GIVER_NAME:
      case SortBy.RECIPIENT_TEAM:
      case SortBy.RECIPIENT_NAME:
        return this.compareLexicographically(strA, strB, order);
      case SortBy.CONSTSUM_OPTIONS_POINTS:
      case SortBy.CONSTSUM_RECIPIENTS_POINTS:
      case SortBy.RUBRIC_WEIGHT_AVERAGE:
      case SortBy.RUBRIC_TOTAL_CHOSEN_WEIGHT:
      case SortBy.NUMERICAL_SCALE_AVERAGE:
      case SortBy.NUMERICAL_SCALE_AVERAGE_EXCLUDE_SELF:
      case SortBy.MCQ_WEIGHT:
      case SortBy.MCQ_PERCENTAGE:
      case SortBy.MCQ_WEIGHTED_PERCENTAGE:
      case SortBy.MCQ_WEIGHT_TOTAL:
      case SortBy.MCQ_WEIGHT_AVERAGE:
      case SortBy.MSQ_WEIGHT:
      case SortBy.MSQ_PERCENTAGE:
      case SortBy.MSQ_WEIGHTED_PERCENTAGE:
      case SortBy.MSQ_WEIGHT_TOTAL:
      case SortBy.MSQ_WEIGHT_AVERAGE:
        return this.compareNumbers(strA, strB, order);
      default:
        return 0;
    }
  }
}
