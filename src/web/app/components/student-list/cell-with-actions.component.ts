import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap/tooltip';
import { InstructorPermissionSet } from '../../../types/api-request';

import { RouterLink } from '@angular/router';

@Component({
  selector: 'tm-group-buttons',
  templateUrl: './cell-with-actions.component.html',
  imports: [CommonModule, RouterLink, NgbDropdownModule, NgbTooltipModule],
})
export class CellWithActionsComponent {
  @Input() idx = 0;
  @Input() courseId = '';
  @Input() userId = '';
  @Input() isSendReminderLoading = false;
  @Input() enableRemindButton = false;
  @Input() isActionButtonsEnabled = true;

  @Input() instructorPrivileges: InstructorPermissionSet = {
    canModifyCourse: false,
    canModifyInstructor: false,
    canModifySession: false,
    canModifyStudent: false,
    canViewSession: false,
    canSubmitSession: false,
  };

  @Input() remindStudentFromCourse: () => void = () => {};
  @Input() removeStudentFromCourse: () => void = () => {};
}
