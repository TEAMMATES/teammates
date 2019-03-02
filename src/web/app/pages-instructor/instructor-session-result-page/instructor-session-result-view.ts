import { Input, OnInit } from '@angular/core';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
export abstract class InstructorSessionResultView implements OnInit {

  @Input() responses: { [key: string]: any } = {};
  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() showStatistics: boolean = true;
  @Input() indicateMissingResponses: boolean = true;

  constructor(protected viewType: InstructorSessionResultViewType) {}

  ngOnInit(): void {
  }

}
