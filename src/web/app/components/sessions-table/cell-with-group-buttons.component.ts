import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap/tooltip';
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  InstructorFeedbackSessionPermissions,
} from '../../../types/api-output';

import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { SortableTableHeaderColorScheme } from '../sortable-table/sortable-table.component';

import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';

@Component({
  selector: 'tm-group-buttons',
  templateUrl: './cell-with-group-buttons.component.html',
  imports: [CommonModule, TeammatesRouterDirective, AjaxLoadingComponent, NgbDropdownModule, NgbTooltipModule],
})
export class GroupButtonsComponent {
  @Input() idx = 0;
  @Input() fsId = '';
  @Input() rowClicked = 0;
  @Input() isSendReminderLoading = false;

  @Input() publishStatus: FeedbackSessionPublishStatus = FeedbackSessionPublishStatus.NOT_PUBLISHED;
  @Input() submissionStatus: FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus.NOT_VISIBLE;

  @Input() instructorPrivileges: InstructorFeedbackSessionPermissions = {
    canModifySession: false,
    canSubmitSession: false,
    canViewSession: false,
  };

  @Input() copySession: () => void = () => {};
  @Input() moveSessionToRecycleBin: () => void = () => {};
  @Input() unpublishSession: () => void = () => {};
  @Input() publishSession: () => void = () => {};
  @Input() remindResultsLinkToStudent: () => void = () => {};
  @Input() downloadSessionResults: () => void = () => {};
  @Input() sendRemindersToAllNonSubmitters: () => void = () => {};
  @Input() sendRemindersToSelectedNonSubmitters: () => void = () => {};
  @Input() setRowClicked: () => void = () => {};
  @Input() onSubmitSessionAsInstructor: () => void = () => {};

  // enum
  FeedbackSessionSubmissionStatus!: typeof FeedbackSessionSubmissionStatus;
  FeedbackSessionPublishStatus!: typeof FeedbackSessionPublishStatus;
  SortableTableHeaderColorScheme!: typeof SortableTableHeaderColorScheme;

  constructor() {
    this.FeedbackSessionSubmissionStatus = FeedbackSessionSubmissionStatus;
    this.FeedbackSessionPublishStatus = FeedbackSessionPublishStatus;
    this.SortableTableHeaderColorScheme = SortableTableHeaderColorScheme;
  }
}
