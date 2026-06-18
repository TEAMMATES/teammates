import { ChangeDetectionStrategy, Component, inject, input, linkedSignal, output } from '@angular/core';
import { FormField, FormRoot, email, form, maxLength, required } from '@angular/forms/signals';
import { CountryService } from '../../../../services/country.service';
import { AccountVerificationRequestUpdateRequest } from '../../../../types/api-request';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../../types/api-output';
import { SearchableComboboxComponent, ComboboxOption } from '../../../components/searchable-combobox/searchable-combobox.component';
import { CountryNamePipe } from '../../../pipes/country-name.pipe';
import {
  AccountVerificationRequestDraft,
  toAccountVerificationRequestDraft,
  toAccountVerificationRequestUpdateRequest,
} from '../account-verification-request-draft';

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

  readonly request = input.required<AccountVerificationRequest>();
  readonly isEditing = input(false);
  readonly formatTimestamp = input.required<(timestamp: number) => string>();
  readonly submitEdits = input.required<(updateRequest: AccountVerificationRequestUpdateRequest) => Promise<void>>();

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
      maxLength(draft.name, 100, { message: 'Name must be at most 100 characters.' });

      required(draft.email, { message: 'Email is required.' });
      email(draft.email, { message: 'Enter a valid email address.' });

      required(draft.institute, { message: 'Institute is required.' });
      maxLength(draft.institute, 100, { message: 'Institute must be at most 100 characters.' });

      required(draft.country, { message: 'Country is required.' });
      maxLength(draft.comments, 1000, { message: 'Comments must be at most 1000 characters.' });
    },
    {
      submission: {
        action: async () => {
          await this.submitEdits()(
            toAccountVerificationRequestUpdateRequest(this.draftModel(), this.request().status),
          );
          this.requestForm().reset(toAccountVerificationRequestDraft(this.request()));
        },
      },
    },
  );
  isEditable(): boolean {
    return this.request().status === AccountVerificationRequestStatus.PENDING;
  }

  cancel(): void {
    this.requestForm().reset(toAccountVerificationRequestDraft(this.request()));
    this.cancelRequested.emit();
  }
}
