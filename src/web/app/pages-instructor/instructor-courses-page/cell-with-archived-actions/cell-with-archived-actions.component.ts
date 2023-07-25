import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { Course } from '../../../../types/api-output';
import { TeammatesRouterModule } from '../../../components/teammates-router/teammates-router.module';

interface CourseModel {
    course: Course;
    canModifyCourse: boolean;
    canModifyStudent: boolean;
    isLoadingCourseStats: boolean;
}
@Component({
    selector: 'tm-archived-actions',
    templateUrl: './cell-with-archived-actions.component.html',
    standalone: true,
    imports: [
        CommonModule,
        TeammatesRouterModule,
        NgbTooltipModule,
    ],
})
export class ArchivedActionsComponent {
    @Input() course!: CourseModel;
    @Input() changeArchiveStatus: (courseId: string, toArchive: boolean) => void = () => {};
    @Input() onDelete!: (courseId: string) => Promise<void>;
}
