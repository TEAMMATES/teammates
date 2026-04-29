import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorPermissionSet } from '../../../types/api-request';

import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';

@Component({
  selector: 'tm-group-buttons',
  templateUrl: './cell-with-actions.component.html',
  imports: [CommonModule, TeammatesRouterDirective, NgbDropdownModule, NgbTooltipModule],
})
export class CellWithActionsComponent {
  @Input() idx = 0;
  @Input() courseId = '';
  @Input() email = '';
  @Input() isSendReminderLoading = false;
  @Input() enableRemindButton = false;
  @Input() isActionButtonsEnabled = true;

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

  @Input() remindStudentFromCourse: () => void = () => {};
  @Input() removeStudentFromCourse: () => void = () => {};
}
