import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  NgbDropdownModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
} from '../../../types/api-output';
import { InstructorPermissionSet } from '../../../types/api-request';

import { AjaxLoadingComponent } from '../ajax-loading/ajax-loading.component';
import { SortableTableHeaderColorScheme } from '../sortable-table/sortable-table.component';

import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';

@Component({
    selector: 'tm-group-buttons',
    templateUrl: './cell-with-group-buttons.component.html',
    imports: [
    CommonModule,
    TeammatesRouterDirective,
    AjaxLoadingComponent,
    NgbDropdownModule,
    NgbTooltipModule,
],
})
export class GroupButtonsComponent {
  @Input() idx: number = 0;
  @Input() fsName: string = '';
  @Input() courseId: string = '';
  @Input() rowClicked: number = 0;
  @Input() isSendReminderLoading: boolean = false;

  @Input() publishStatus: FeedbackSessionPublishStatus =
    FeedbackSessionPublishStatus.NOT_PUBLISHED;
  @Input() submissionStatus: FeedbackSessionSubmissionStatus =
    FeedbackSessionSubmissionStatus.NOT_VISIBLE;

  @Input() instructorPrivileges: InstructorPermissionSet = {
    canModifyCourse: false,
    canModifyInstructor: false,
    canModifySession: false,
    canModifyStudent: false,
    canViewStudentInSections: false,
    canViewSessionInSections: false,
    canSubmitSessionInSections: false,
    canModifySessionCommentsInSections: false,
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
  FeedbackSessionSubmissionStatus: typeof FeedbackSessionSubmissionStatus =
    FeedbackSessionSubmissionStatus;
  FeedbackSessionPublishStatus: typeof FeedbackSessionPublishStatus =
    FeedbackSessionPublishStatus;
  SortableTableHeaderColorScheme: typeof SortableTableHeaderColorScheme =
    SortableTableHeaderColorScheme;
}
