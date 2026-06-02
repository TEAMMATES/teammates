import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SearchableComboboxComponent } from './searchable-combobox.component';

interface Student {
  id: string;
  name: string;
  email: string;
}

describe('SearchableComboboxComponent', () => {
  let component: SearchableComboboxComponent<string, Student>;
  let fixture: ComponentFixture<SearchableComboboxComponent<string, Student>>;

  const studentAlice: Student = {
    id: 'student-1',
    name: 'Alice Tan',
    email: 'alice@example.com',
  };
  const studentBob: Student = {
    id: 'student-2',
    name: 'Bob Ng',
    email: 'bob@example.com',
  };

  beforeEach(() => {
    fixture = TestBed.createComponent<SearchableComboboxComponent<string, Student>>(SearchableComboboxComponent);
    component = fixture.componentInstance;
    component.options = [
      { value: studentAlice.id, label: studentAlice.name, keywords: [studentAlice.email], data: studentAlice },
      { value: studentBob.id, label: studentBob.name, keywords: [studentBob.email], data: studentBob },
    ];
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('writeValue: should display the selected option label', () => {
    component.writeValue(studentAlice.id);

    expect(component.inputValue()).toBe(studentAlice.name);
    expect(component.selectedValues()).toEqual([studentAlice.id]);
  });

  it('filteredOptions: should filter by label', () => {
    component.onInputValueChange('bob');

    expect(component.filteredOptions()).toEqual([
      { value: studentBob.id, label: studentBob.name, keywords: [studentBob.email], data: studentBob },
    ]);
  });

  it('filteredOptions: should show all options when input is empty', () => {
    expect(component.filteredOptions()).toEqual([
      { value: studentAlice.id, label: studentAlice.name, keywords: [studentAlice.email], data: studentAlice },
      { value: studentBob.id, label: studentBob.name, keywords: [studentBob.email], data: studentBob },
    ]);
  });

  it('filteredOptions: should filter by keywords', () => {
    component.onInputValueChange('alice@example.com');

    expect(component.filteredOptions()).toEqual([
      { value: studentAlice.id, label: studentAlice.name, keywords: [studentAlice.email], data: studentAlice },
    ]);
  });

  it('onValuesChange: should update value and emit selected option', () => {
    const optionSelectedSpy = vi.spyOn(component.optionSelected, 'emit');
    const onChangeSpy = vi.fn();
    component.registerOnChange(onChangeSpy);

    component.onValuesChange([studentBob.id]);

    expect(component.selectedValue()).toBe(studentBob.id);
    expect(component.selectedValues()).toEqual([studentBob.id]);
    expect(component.inputValue()).toBe(studentBob.name);
    expect(onChangeSpy).toHaveBeenCalledWith(studentBob.id);
    expect(optionSelectedSpy).toHaveBeenCalledWith({
      value: studentBob.id,
      label: studentBob.name,
      keywords: [studentBob.email],
      data: studentBob,
    });
  });

  it('onComboboxFocusOut: should restore selected label when typed text is not selected', () => {
    component.writeValue(studentAlice.id);
    component.onInputValueChange('unmatched text');

    component.onComboboxFocusOut({
      relatedTarget: null,
      currentTarget: fixture.nativeElement.querySelector('.searchable-combobox'),
    } as FocusEvent);

    expect(component.inputValue()).toBe(studentAlice.name);
  });

  it('onValuesChange: should support values that are not in the option list', () => {
    const onChangeSpy = vi.fn();
    component.registerOnChange(onChangeSpy);

    component.onValuesChange(['']);

    expect(component.selectedValue()).toBe('');
    expect(component.selectedValues()).toEqual(['']);
    expect(component.inputValue()).toBe('');
    expect(onChangeSpy).toHaveBeenCalledWith('');
  });

  it('onValuesChange: should not clear a selected value when the listbox emits no values', () => {
    const onChangeSpy = vi.fn();
    component.registerOnChange(onChangeSpy);
    component.writeValue(studentAlice.id);
    component.onInputValueChange('');

    component.onValuesChange([]);

    expect(component.selectedValue()).toBe(studentAlice.id);
    expect(component.selectedValues()).toEqual([studentAlice.id]);
    expect(onChangeSpy).not.toHaveBeenCalled();
  });

  it('clearSelection: should emit the configured clear value', () => {
    const onChangeSpy = vi.fn();
    const onTouchedSpy = vi.fn();
    component.clearValue = '';
    component.registerOnChange(onChangeSpy);
    component.registerOnTouched(onTouchedSpy);
    component.writeValue(studentAlice.id);

    component.clearSelection({
      stopPropagation: vi.fn(),
    } as unknown as MouseEvent);

    expect(component.selectedValue()).toBe('');
    expect(component.selectedValues()).toEqual([]);
    expect(component.inputValue()).toBe('');
    expect(onChangeSpy).toHaveBeenCalledWith('');
    expect(onTouchedSpy).toHaveBeenCalled();
  });

  it('onInputClick: should open all options for a selected value', () => {
    component.writeValue(studentAlice.id);
    const combobox = {
      open: vi.fn(),
    } as unknown as Parameters<typeof component.onInputClick>[0];

    component.onInputClick(combobox);

    expect(component.inputValue()).toBe(studentAlice.name);
    expect(component.filteredOptions().map((option) => option.label)).toEqual([studentAlice.name, studentBob.name]);
    expect(combobox.open).toHaveBeenCalled();
  });

  it('should disable the input when disabled', () => {
    component.setDisabledState(true);
    fixture.detectChanges();

    const input: HTMLInputElement = fixture.nativeElement.querySelector('input');
    expect(input.disabled).toBeTruthy();
  });
});
