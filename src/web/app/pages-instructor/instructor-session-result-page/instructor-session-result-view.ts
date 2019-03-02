import { Input, OnInit } from '@angular/core';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
export abstract class InstructorSessionResultView implements OnInit {

  @Input() responses: { [key: string]: any } = {};

  constructor(protected viewType: InstructorSessionResultViewType) {}

  ngOnInit(): void {
  }

}
