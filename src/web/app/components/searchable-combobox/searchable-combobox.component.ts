import { NgClass } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter,
  forwardRef,
  Input,
  Output,
  Signal,
  ViewChild,
  computed,
  signal,
  inject,
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
  disabled?: boolean;
  data?: TData;
}

/**
 * A Bootstrap-compatible single-select combobox with type-to-filter support.
 */
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
  private readonly changeDetectorRef = inject(ChangeDetectorRef);

  @Input()
  options: ComboboxOption<TValue, TData>[] = [];

  @Input()
  inputId = '';

  @Input()
  ariaLabel = '';

  @Input()
  placeholder = '';

  @Input()
  disabled = false;

  @Input()
  clearable = false;

  @Input()
  clearValue: TValue | null = null;

  @Input()
  compareWith: (firstValue: TValue | null, secondValue: TValue | null) => boolean = Object.is;

  @Output()
  optionSelected: EventEmitter<ComboboxOption<TValue, TData>> = new EventEmitter();

  @ViewChild('comboboxInput')
  private readonly comboboxInput?: ElementRef<HTMLInputElement>;

  readonly selectedValue = signal<TValue | null>(null);
  readonly inputValue = signal('');
  readonly selectedValues = signal<TValue[]>([]);
  readonly filteredOptions: Signal<ComboboxOption<TValue, TData>[]> = computed(() => {
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

  private readonly isShowingAllOptions = signal(false);
  private onChange: (value: TValue | null) => void = () => {};
  private onTouched: () => void = () => {};

  get hasSelectedDisplayValue(): boolean {
    return this.getSelectedLabel() !== '';
  }

  writeValue(value: TValue | null): void {
    this.selectedValue.set(value);
    this.selectedValues.set(this.hasMatchingOption(value) ? [value as TValue] : []);
    this.inputValue.set(this.getSelectedLabel());
  }

  registerOnChange(onChange: (value: TValue | null) => void): void {
    this.onChange = onChange;
  }

  registerOnTouched(onTouched: () => void): void {
    this.onTouched = onTouched;
  }

  setDisabledState(disabled: boolean): void {
    this.disabled = disabled;
    this.changeDetectorRef.markForCheck();
  }

  onInputValueChange(value: string): void {
    this.isShowingAllOptions.set(false);
    this.inputValue.set(value);
  }

  onInputClick(combobox: Combobox<TValue>): void {
    if (this.disabled) {
      return;
    }

    this.isShowingAllOptions.set(true);
    combobox.open();
  }

  clearSelection(event: MouseEvent): void {
    event.stopPropagation();

    if (this.disabled) {
      return;
    }

    this.isShowingAllOptions.set(false);
    this.selectedValue.set(this.clearValue);
    this.selectedValues.set(this.hasMatchingOption(this.clearValue) ? [this.clearValue as TValue] : []);
    this.inputValue.set(this.getSelectedLabel());
    this.onChange(this.clearValue);
    this.onTouched();
  }

  onValuesChange(values: TValue[]): void {
    if (values.length === 0) {
      const selectedValue: TValue | null = this.selectedValue();
      this.selectedValues.set(this.hasMatchingOption(selectedValue) ? [selectedValue as TValue] : []);
      return;
    }

    const value: TValue = values[0];
    const selectedOption: ComboboxOption<TValue, TData> | undefined = this.findOption(value);

    this.isShowingAllOptions.set(false);
    this.selectedValue.set(value);
    this.selectedValues.set(value === null ? [] : [value]);
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

  focus(): void {
    this.comboboxInput?.nativeElement.focus();
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

  private hasMatchingOption(value: TValue | null): boolean {
    return value !== null && this.findOption(value) !== undefined;
  }

  private getSelectedLabel(): string {
    return this.findOption(this.selectedValue())?.label ?? '';
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase();
  }
}
