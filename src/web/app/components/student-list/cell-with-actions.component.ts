import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import {
  NgbDropdownModule,
  NgbTooltipModule,
} from '@ng-bootstrap/ng-bootstrap';
import { InstructorPermissionSet } from 'src/web/types/api-request';


import { TeammatesRouterDirective } from '../teammates-router/teammates-router.directive';

@Component({
    selector: 'tm-group-buttons',
    templateUrl: './cell-with-actions.component.html',
    imports: [
    CommonModule,
    TeammatesRouterDirective,
    NgbDropdownModule,
    NgbTooltipModule
],
})

export class CellWithActionsComponent {
  @Input() idx: number = 0;
  @Input() courseId: string = '';
  @Input() email: string = '';
  @Input() isSendReminderLoading: boolean = false;
  @Input() enableRemindButton: boolean = false;
  @Input() isActionButtonsEnabled: boolean = true;

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
  @Input() removeStudentFromCourse : () => void = () => {};

}
