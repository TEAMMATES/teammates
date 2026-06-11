import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  forwardRef,
  Input,
  Output,
  ViewChild,
  computed,
  input,
  signal,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { Combobox, ComboboxInput, ComboboxPopupContainer } from '@angular/aria/combobox';
import { Listbox, Option } from '@angular/aria/listbox';

/**
 * Option model for the searchable combobox.
 */
export interface ComboboxOption<TValue, TData = unknown> {
  value: TValue;
  label: string;
  keywords?: string[];
  data?: TData;
}

/**
 * A Bootstrap-compatible single-select combobox with type-to-filter support.
 *
 * Integrates with Angular forms via `ngModel` or `formControl`. The generic
 * `TValue` is the type of each option's value; `TData` is optional arbitrary
 * data attached to an option and surfaced through `optionSelected`.
 *
 * @example
 * <tm-searchable-combobox
 *   inputId="student-select"
 *   ariaLabel="Select student"
 *   placeholder="Search..."
 *   [options]="studentOptions"
 *   [clearable]="true"
 *   clearValue=""
 *   [(ngModel)]="selectedStudentId"
 *   (optionSelected)="onStudentSelected($event)"
 * />
 *
 * Each entry in `options` must implement `ComboboxOption<TValue, TData>`:
 *   - `value`    — the form value emitted on selection
 *   - `label`    — display text shown in the input and dropdown
 *   - `keywords` — (optional) extra searchable strings (e.g. email)
 *   - `data`     — (optional) arbitrary payload forwarded via `optionSelected`
 *
 * Set `clearable` to show a clear button. `clearValue` controls the value
 * emitted when clearing; defaults to `null`, use `""` for string-typed forms.
 *
 * Supply `compareWith` when value equality cannot use `Object.is` (e.g. when
 * comparing objects by id rather than reference).
 * */
@Component({
  selector: 'tm-searchable-combobox',
  templateUrl: './searchable-combobox.component.html',
  styleUrls: ['./searchable-combobox.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [NgClass, Combobox, ComboboxInput, ComboboxPopupContainer, Listbox, Option],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SearchableComboboxComponent),
      multi: true,
    },
  ],
})
export class SearchableComboboxComponent<TValue, TData = unknown> implements ControlValueAccessor {
  @Input()
  options: ComboboxOption<TValue, TData>[] = [];

  @Input()
  inputId = '';

  @Input()
  ariaLabel = '';

  @Input()
  placeholder = '';

  readonly disabled = input(false);

  @Input()
  clearable = false;

  @Input()
  clearValue: TValue | null = null;

  @Input()
  compareWith: (firstValue: TValue | null, secondValue: TValue | null) => boolean = Object.is;

  @Output()
  optionSelected: EventEmitter<ComboboxOption<TValue, TData>> = new EventEmitter();

  @ViewChild(Combobox)
  private readonly combobox?: Combobox<TValue>;

  private readonly _formDisabled = signal(false);
  private readonly selectedValue = signal<TValue | null>(null);
  private readonly isShowingAllOptions = signal(false);
  private onChange: (value: TValue | null) => void = () => {};
  private onTouched: () => void = () => {};

  protected readonly isDisabled = computed(() => this.disabled() || this._formDisabled());
  protected readonly inputValue = signal('');
  protected readonly selectedValues = computed(() => {
    const value = this.selectedValue();
    return this.hasMatchingOption(value) ? [value] : [];
  });
  readonly filteredOptions = computed(() => {
    if (this.isShowingAllOptions()) {
      return this.options;
    }

    const searchValue: string = this.normalize(this.inputValue());
    if (!searchValue) {
      return this.options;
    }

    return this.options.filter((option: ComboboxOption<TValue, TData>) => {
      if (this.normalize(option.label).includes(searchValue)) {
        return true;
      }
      return option.keywords?.some((keyword: string) => this.normalize(keyword).includes(searchValue)) ?? false;
    });
  });
  protected readonly hasSelectedDisplayValue = computed(() => this.getSelectedLabel() !== '');

  writeValue(value: TValue | null): void {
    this.selectedValue.set(value);
    this.inputValue.set(this.getSelectedLabel());
  }

  registerOnChange(onChange: (value: TValue | null) => void): void {
    this.onChange = onChange;
  }

  registerOnTouched(onTouched: () => void): void {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean): void {
    this._formDisabled.set(disabled);
  }

  onInputValueChange(value: string): void {
    this.isShowingAllOptions.set(false);
    this.inputValue.set(value);
  }

  onInputClick(): void {
    if (this.isDisabled()) {
      return;
    }
    this.isShowingAllOptions.set(true);
    this.combobox?.open();
  }

  clearSelection(event: MouseEvent): void {
    event.stopPropagation();
    if (this.isDisabled()) {
      return;
    }

    this.combobox?.close();
    this.isShowingAllOptions.set(false);
    this.selectedValue.set(this.clearValue);
    this.inputValue.set(this.getSelectedLabel());
    this.onChange(this.clearValue);
    this.onTouched();
  }

  onValuesChange(values: TValue[]): void {
    if (values.length === 0) {
      return;
    }

    const value: TValue = values[0];
    const selectedOption: ComboboxOption<TValue, TData> | undefined = this.findOption(value);

    this.isShowingAllOptions.set(false);
    this.selectedValue.set(value);
    this.inputValue.set(selectedOption?.label ?? '');
    this.onChange(value);

    if (selectedOption) {
      this.optionSelected.emit(selectedOption);
    }
  }

  onComboboxFocusOut(event: FocusEvent): void {
    const relatedTarget: Node | null = event.relatedTarget as Node | null;
    const currentTarget: Node = event.currentTarget as Node;
    if (relatedTarget && currentTarget.contains(relatedTarget)) {
      return;
    }

    this.isShowingAllOptions.set(false);
    this.inputValue.set(this.getSelectedLabel());
    this.onTouched();
  }

  isSelected(option: ComboboxOption<TValue, TData>): boolean {
    const selectedValue: TValue | null = this.selectedValue();
    return selectedValue !== null && this.compareWith(option.value, selectedValue);
  }

  private findOption(value: TValue | null): ComboboxOption<TValue, TData> | undefined {
    if (value === null) {
      return undefined;
    }
    return this.options.find((option: ComboboxOption<TValue, TData>) => this.compareWith(option.value, value));
  }

  private hasMatchingOption(value: TValue | null): value is TValue {
    return value !== null && this.findOption(value) !== undefined;
  }

  private getSelectedLabel(): string {
    return this.findOption(this.selectedValue())?.label ?? '';
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase();
  }
}
