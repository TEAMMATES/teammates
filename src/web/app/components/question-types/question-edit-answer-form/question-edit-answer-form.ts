import { AfterViewInit, ElementRef, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FeedbackQuestionDetails, FeedbackResponseDetails } from '../../../../types/api-output';

/**
 * The abstract recipient submission form.
 */
export abstract class QuestionEditAnswerFormComponent<
    Q extends FeedbackQuestionDetails, R extends FeedbackResponseDetails> implements OnInit, AfterViewInit {

  @Input()
  isDisabled: boolean = false;

  @Input()
  questionDetails: Q;

  @Input()
  responseDetails: R;

  @Output()
  responseDetailsChange: EventEmitter<FeedbackResponseDetails> = new EventEmitter();

  protected elementRef: ElementRef | null = null;

  protected constructor(questionDetails: Q, responseDetails: R) {
    this.questionDetails = questionDetails;
    this.responseDetails = responseDetails;
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    if (this.elementRef !== null) {
      // handle all mouse wheel events to prevent unintended changes to number inputs
      this.elementRef.nativeElement.querySelectorAll('input[type="number"]').forEach((elem: HTMLElement) => {
        elem.addEventListener('wheel', (e: Event) => {
          e.preventDefault();
        });
      });
    }
  }

  /**
   * Triggers the change of the response details for the form.
   */
  triggerResponseDetailsChange(field: string, data: any): void {
    this.responseDetailsChange.emit(Object.assign({}, this.responseDetails, { [field]: data }));
  }

  /**
   * Triggers changes of the response details for the form.
   */
  triggerResponseDetailsChangeBatch(obj: {[key: string]: any}): void {
    this.responseDetailsChange.emit(Object.assign({}, this.responseDetails, obj));
  }
}
