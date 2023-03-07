import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewChild } from '@angular/core';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { FeedbackSessionsService } from '../../../services/feedback-sessions.service';
import { CourseService } from '../../../services/course.service';
import { ProgressBarService } from '../../../services/progress-bar.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Course, FeedbackSessions } from '../../../types/api-output';
import { ErrorMessageOutput } from '../../error-message-output';
import { CopyCourseModalResult } from '../copy-course-modal/copy-course-modal-model';
import { CopyCourseModalComponent } from '../copy-course-modal/copy-course-modal.component';
import { SimpleModalType } from '../simple-modal/simple-modal-type';
import { CourseAddFormModel, DEFAULT_COURSE_ADD_FORM_MODEL } from './course-copy-model';

interface SessionTimestampData {
  submissionStartTimestamp: string;
  submissionEndTimestamp: string;
  sessionVisibleTimestamp: string;
  responseVisibleTimestamp: string;
}

interface TweakedTimestampData {
  oldTimestamp: SessionTimestampData;
  newTimestamp: SessionTimestampData;
}

/**
 * Course edit form component.
 */
@Component({
  selector: '[course-copy]',
  templateUrl: './course-copy.component.html',
  styleUrls: ['./course-copy.component.scss'],
})
export class CourseCopyComponent implements OnInit {
  @ViewChild('modifiedTimestampsModal') modifiedTimestampsModal!: TemplateRef<any>;

  @Output() isCopyingCourse = new EventEmitter<boolean>(false);
  @Output() onCourseCopy = new EventEmitter<Course>(false);

  @Input() allCoursesList: Course[] = [];

  hasLoadingFailed: boolean = false;
  modifiedSessions: Record<string, TweakedTimestampData> = {};
  copyProgressPercentage: number = 0;
  totalNumberOfSessionsToCopy: number = 0;
  numberOfSessionsCopied: number = 0;
  courseFormModel: CourseAddFormModel = DEFAULT_COURSE_ADD_FORM_MODEL();

  constructor(private ngbModal: NgbModal,
    private statusMessageService: StatusMessageService,
    private courseService: CourseService,
    private simpleModalService: SimpleModalService,
    private feedbackSessionsService: FeedbackSessionsService,
    private progressBarService: ProgressBarService
  ) { }

  setIsCopyingCourse(value: boolean): void {
    this.isCopyingCourse.emit(value);
    this.courseFormModel.isCopying = value;
  }

  ngOnInit() {
    this.courseService.isCopyingCourse.subscribe(v => this.setIsCopyingCourse(v));
    this.courseService.copyProgress.subscribe(v => this.progressBarService.updateProgress(v));
  }

  /**
   * Creates a copy of a course including the selected sessions.
   */
  onCopy(courseId: string, courseName: string, timeZone: string): void {
    if (!courseId) {
      this.statusMessageService.showErrorToast('Course is not found!');
      return;
    }

    this.feedbackSessionsService.getFeedbackSessionsForInstructor(courseId).subscribe({
      next: (response: FeedbackSessions) => {
        const modalRef: NgbModalRef = this.ngbModal.open(CopyCourseModalComponent);
        modalRef.componentInstance.oldCourseId = courseId;
        modalRef.componentInstance.oldCourseName = courseName;
        modalRef.componentInstance.allCourses = this.allCoursesList;
        modalRef.componentInstance.newTimeZone = timeZone;
        modalRef.componentInstance.courseToFeedbackSession[courseId] = response.feedbackSessions;
        modalRef.componentInstance.selectedFeedbackSessions = new Set(response.feedbackSessions);
        modalRef.result.then((result: CopyCourseModalResult) => this.createCopiedCourse(result));
      },
      error: (resp: ErrorMessageOutput) => {
        this.statusMessageService.showErrorToast(resp.error.message);
      },
    });
  }

  createCopiedCourse(result: CopyCourseModalResult) {
    this.courseService.createCopiedCourse(result).subscribe({
      next: ({ course, modified }) => {
        this.modifiedSessions = modified;
        this.onCourseCopy.next(course);

        if (Object.keys(this.modifiedSessions).length > 0) {
          this.simpleModalService.openInformationModal('Note On Modified Session Timings',
            SimpleModalType.WARNING, this.modifiedTimestampsModal);
        } else {
          this.statusMessageService.showSuccessToast('The course has been added.');
        }

      },
      error: (resp) => {
        this.statusMessageService.showErrorToast(resp.error.message);
        this.setIsCopyingCourse(false);
        this.hasLoadingFailed = true;
      }
    })
  }
}
