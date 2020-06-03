import { Component, Input, OnInit } from '@angular/core';
import { FeedbackSession, QuestionOutput } from "../../../../types/api-output";

/**
 * Data object for communication with child comment result component
 */
export interface SearchCommentsTable {
  feedbackSession: FeedbackSession,
  questions: QuestionOutput[],
}

@Component({
  selector: 'tm-comment-result-table',
  templateUrl: './comment-result-table.component.html',
  styleUrls: ['./comment-result-table.component.scss']
})
export class CommentResultTableComponent implements OnInit {

  @Input() commentTables: SearchCommentsTable[] = [];

  constructor() { }

  ngOnInit() {
  }
}
