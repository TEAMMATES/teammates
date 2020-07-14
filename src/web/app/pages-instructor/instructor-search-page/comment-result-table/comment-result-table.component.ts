import { Component, Input, OnInit } from '@angular/core';
import { FeedbackSession, QuestionOutput } from '../../../../types/api-output';

/**
 * Search result for a question, response, comment on response instructor search
 */
export interface SearchCommentsTable {
  feedbackSession: FeedbackSession;
  questions: QuestionOutput[];
}

/**
 * Table to show comment on response search results
 */
@Component({
  selector: 'tm-comment-result-table',
  templateUrl: './comment-result-table.component.html',
  styleUrls: ['./comment-result-table.component.scss'],
})
export class CommentResultTableComponent implements OnInit {

  @Input() commentTables: SearchCommentsTable[] = [];

  constructor() { }

  ngOnInit(): void {
  }
}
