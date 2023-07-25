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
    selector: 'tm-deleted-actions',
    templateUrl: './cell-with-deleted-actions.component.html',
    standalone: true,
    imports: [
        CommonModule,
        TeammatesRouterModule,
        NgbTooltipModule,
    ],
})
export class DeletedActionsComponent {
    @Input() course!: CourseModel;
    @Input() onRestore: (courseId: string) => void = () => {};
    @Input() onDeletePermanently!: (courseId: string) => Promise<void>;
}
