import { Component, Input, OnInit } from '@angular/core';
import { CommentTableModel } from "../../../components/comment-box/comment-table/comment-table.component";
import { SearchCommentsTable } from "../instructor-search-page.component";
import { ResponseOutput } from "../../../../types/api-output";

export interface CommentListSectionData {
  questionNumber: number;
  questionText: string;
  responseComments: CommentListResponseCommentData[];
}

export interface CommentListResponseCommentData {
  response: ResponseOutput;
  commentTableModel: CommentTableModel;
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
