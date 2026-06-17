import { Component, ChangeDetectionStrategy, EventEmitter, Input, OnInit, Output, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { Hours, Milliseconds } from '../../../../types/datetime-const';
import { DatetimepickerComponent } from '../../../components/datetimepicker/datetimepicker.component';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { DateFormatService } from '../../../../services/date-format.service';

const DEADLINE_OPTIONS = [
  { label: '12 hours', hours: Hours.TWELVE },
  { label: '1 day', hours: Hours.IN_ONE_DAY },
  { label: '3 days', hours: Hours.IN_THREE_DAYS },
  { label: '1 week', hours: Hours.IN_ONE_WEEK },
  { label: 'Custom', hours: Hours.ZERO },
] as const;

type DeadlineLabel = (typeof DEADLINE_OPTIONS)[number]['label'];

@Component({
  selector: 'tm-individual-extension-date-modal',
  templateUrl: './individual-extension-date-modal.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [FormsModule, DatetimepickerComponent],
})
export class IndividualExtensionDateModalComponent implements OnInit {
  activeModal = inject(NgbActiveModal);
  private readonly simpleModalService = inject(SimpleModalService);
  private readonly dateFormatService = inject(DateFormatService);

  @Input({ required: true }) numStudents = 0;
  @Input({ required: true }) numInstructors = 0;
  @Input({ required: true }) feedbackSessionEndingTimestamp = 0;
  @Input({ required: true }) feedbackSessionTimeZone = '';

  @Output() confirmCallbackEvent: EventEmitter<number> = new EventEmitter();

  readonly DEADLINE_OPTIONS = DEADLINE_OPTIONS;

  readonly selectedPreset = signal<DeadlineLabel>(DEADLINE_OPTIONS[0].label);
  readonly customTimestamp = signal(0);

  ngOnInit(): void {
    this.customTimestamp.set(this.feedbackSessionEndingTimestamp);
  }

  onConfirm(): void {
    if (this.getExtensionTimestamp() >= Date.now()) {
      this.confirmCallbackEvent.emit(this.getExtensionTimestamp());
      return;
    }

    const extensionTimeString = this.formatTimestamp(this.getExtensionTimestamp());
    const currentTimeString = this.formatTimestamp(Date.now());

    this.simpleModalService
      .openConfirmationModal(
        'Are you sure you wish to set the new deadline to before the current time?',
        SimpleModalType.WARNING,
        '<b>Any users affected will have their sessions closed immediately.</b>' +
          ` The current time now is ${currentTimeString} and you are extending to` +
          ` ${extensionTimeString}. Do you wish to proceed?`,
      )
      .result.then(
        () => this.confirmCallbackEvent.emit(this.getExtensionTimestamp()),
        () => {},
      );
  }

  onChangeDateTime(timestamp: number): void {
    this.customTimestamp.set(timestamp);
  }

  getExtensionTimestamp(): number {
    if (this.isCustom()) {
      return this.customTimestamp();
    }
    const { hours } = this.DEADLINE_OPTIONS.find((o) => o.label === this.selectedPreset())!;
    return this.feedbackSessionEndingTimestamp + hours * Milliseconds.IN_ONE_HOUR;
  }

  extendAndFormatEndTimeBy(hours: number): string {
    return this.formatTimestamp(this.feedbackSessionEndingTimestamp + hours * Milliseconds.IN_ONE_HOUR);
  }

  isCustom(): boolean {
    return this.selectedPreset() === 'Custom';
  }

  isValidForm(): boolean {
    return this.getExtensionTimestamp() > this.feedbackSessionEndingTimestamp;
  }

  private formatTimestamp(time: number): string {
    return this.dateFormatService.formatDateDetailed(time, this.feedbackSessionTimeZone);
  }
}
