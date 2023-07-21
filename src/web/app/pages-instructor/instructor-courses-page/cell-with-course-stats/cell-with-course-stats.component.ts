import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
    selector: 'tm-course-stats',
    templateUrl: './cell-with-course-stats.component.html',
    standalone: true,
    imports: [
      CommonModule,
    ],
})
export class CourseStatsComponent {
    @Input() courseId: string = '';
    @Input() column: string = '';
    @Input() courseStats: Record<string, Record<string, number>> = {};
    @Input() isLoadingCourseStats: boolean = false;
    @Input() getCourseStats: () => void = () => {};
}
