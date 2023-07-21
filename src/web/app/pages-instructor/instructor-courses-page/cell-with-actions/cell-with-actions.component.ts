import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { CourseModel } from '../../../../types/api-output';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';

@Component({
    selector: 'tm-actions',
    templateUrl: './cell-with-actions.component.html',
    standalone: true,
    imports: [
        CommonModule,
        TeammatesRouterModule,
        NgbDropdownModule,
        NgbTooltipModule,
    ],
})
export class ActionsComponent {
    @Input() course!: CourseModel;
    @Input() isCopyingCourse!: boolean;
    @Input() onCopy: (courseId: string, courseName: string, timeZone: string) => void = () => {};
    @Input() changeArchiveStatus: (courseId: string, toArchive: boolean) => void = () => {};
    @Input() onDelete!: (courseId: string) => Promise<void>;
}
