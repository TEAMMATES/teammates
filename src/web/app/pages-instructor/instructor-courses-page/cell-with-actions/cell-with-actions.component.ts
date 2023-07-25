import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Course } from '../../../../types/api-output';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';

interface CourseModel {
    course: Course;
    canModifyCourse: boolean;
    canModifyStudent: boolean;
    isLoadingCourseStats: boolean;
}
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
