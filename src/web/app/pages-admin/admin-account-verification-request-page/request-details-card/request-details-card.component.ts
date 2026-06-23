import { ChangeDetectionStrategy, Component, computed, inject, input, linkedSignal, output } from '@angular/core';
import { FormField, FormRoot, email, form, maxLength, required } from '@angular/forms/signals';
import { CountryService } from '../../../../services/country.service';
import { DateFormatService } from '../../../../services/date-format.service';
import { LinkService } from '../../../../services/link.service';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../../types/api-output';
import {
  SearchableComboboxComponent,
  ComboboxOption,
} from '../../../components/searchable-combobox/searchable-combobox.component';
import { CountryNamePipe } from '../../../pipes/country-name.pipe';
import {
  AccountVerificationRequestDraft,
  toAccountVerificationRequestDraft,
} from '../account-verification-request-draft';
import { EMAIL_MAX_LENGTH, INSTITUTE_NAME_MAX_LENGTH, PERSON_NAME_MAX_LENGTH } from '../../../../types/field-validator';

/**
 * Request details card with view and edit modes.
 */
@Component({
  selector: 'tm-admin-account-verification-request-details-card',
  templateUrl: './request-details-card.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CountryNamePipe, FormField, FormRoot, SearchableComboboxComponent],
})
export class RequestDetailsCardComponent {
  private readonly countryService = inject(CountryService);
  private readonly dateFormatService = inject(DateFormatService);
  private readonly linkService = inject(LinkService);

  readonly request = input.required<AccountVerificationRequest>();
  readonly isEditing = input(false);
  readonly timezone = input.required<string>();
  readonly submitDraft = input.required<(draft: AccountVerificationRequestDraft) => Promise<void>>();

  readonly editStarted = output<void>();
  readonly cancelRequested = output<void>();

  readonly countryOptions: ComboboxOption<string>[] = this.countryService.getCountryOptions().map((country) => ({
    value: country.code,
    label: country.name,
  }));
  readonly draftModel = linkedSignal<AccountVerificationRequestDraft>(() =>
    toAccountVerificationRequestDraft(this.request()),
  );
  readonly requestForm = form(
    this.draftModel,
    (draft) => {
      required(draft.name, { message: 'Name is required.' });
      maxLength(draft.name, PERSON_NAME_MAX_LENGTH, {
        message: `Name must be at most ${PERSON_NAME_MAX_LENGTH} characters.`,
      });

      required(draft.email, { message: 'Email is required.' });
      maxLength(draft.email, EMAIL_MAX_LENGTH, { message: `Email must be at most ${EMAIL_MAX_LENGTH} characters.` });
      email(draft.email, { message: 'Enter a valid email address.' });

      required(draft.institute, { message: 'Institute is required.' });
      maxLength(draft.institute, INSTITUTE_NAME_MAX_LENGTH, {
        message: `Institute must be at most ${INSTITUTE_NAME_MAX_LENGTH} characters.`,
      });

      required(draft.country, { message: 'Country is required.' });
    },
    {
      submission: {
        action: async () => {
          await this.submitDraft()(this.draftModel());
          this.requestForm().reset();
        },
      },
    },
  );
  readonly isEditable = computed(() => this.request().status === AccountVerificationRequestStatus.PENDING);

  startEditing(): void {
    this.requestForm().reset(toAccountVerificationRequestDraft(this.request()));
    this.editStarted.emit();
  }

  cancel(): void {
    this.requestForm().reset(toAccountVerificationRequestDraft(this.request()));
    this.cancelRequested.emit();
  }

  formatTimestamp(timestamp: number): string {
    return this.dateFormatService.formatDateDetailed(timestamp, this.timezone());
  }

  welcomeLink(): string {
    return this.linkService.generateInstructorWelcomeLink(this.request().accountVerificationRequestId);
  }

  copyWelcomeLink(): void {
    navigator.clipboard.writeText(this.welcomeLink());
  }
}
