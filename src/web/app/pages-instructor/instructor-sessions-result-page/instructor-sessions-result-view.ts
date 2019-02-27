import { Input, OnInit } from '@angular/core';
import { InstructorSessionsResultViewType } from './instructor-sessions-result-view-type.enum';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
export abstract class InstructorSessionsResultView implements OnInit {

  @Input() responses: { [key: string]: any } = {};

  constructor(protected viewType: InstructorSessionsResultViewType) {}

  ngOnInit(): void {
  }

}
