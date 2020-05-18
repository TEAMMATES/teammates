import { EventEmitter, Input, OnInit, Output } from '@angular/core';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { InstructorSessionResultViewType } from './instructor-session-result-view-type.enum';

/**
 * Abstract component for all different view type components of instructor sessions result page.
 */
export abstract class InstructorSessionResultView implements OnInit {

  @Input() section: string = '';
  @Input() sectionType: InstructorSessionResultSectionType = InstructorSessionResultSectionType.EITHER;
  @Input() groupByTeam: boolean = true;
  @Input() showStatistics: boolean = true;
  @Input() indicateMissingResponses: boolean = true;
  @Input() session: any = {};

  @Output() toggleAndLoadTab: EventEmitter<string> = new EventEmitter<string>();

  constructor(protected viewType: InstructorSessionResultViewType) {}

  ngOnInit(): void {
  }

}
