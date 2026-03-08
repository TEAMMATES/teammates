import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ComboboxComponent, ComboboxOption } from './combobox.component';

describe('ComboboxComponent', () => {
  let component: ComboboxComponent;
  let fixture: ComponentFixture<ComboboxComponent>;

  const mockOptions: ComboboxOption[] = [
    { label: 'Apple', value: 'apple' },
    { label: 'Banana', value: 'banana' },
    { label: 'Cherry', value: 'cherry' },
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComboboxComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ComboboxComponent);
    component = fixture.componentInstance;
    component.options = mockOptions;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display placeholder when no option is selected', () => {
    component.placeholder = 'Choose a fruit';
    fixture.detectChanges();
    const input = fixture.debugElement.query(By.css('input'));
    expect(input.nativeElement.placeholder).toEqual('Choose a fruit');
  });

  it('should open dropdown on input click', () => {
    expect(component.isOpen).toBeFalsy();
    fixture.debugElement.query(By.css('input')).nativeElement.click();
    expect(component.isOpen).toBeTruthy();
  });

  it('should show all options when first opened', () => {
    component.openDropdown();
    expect(component.filteredOptions.length).toEqual(mockOptions.length);
  });

  it('should filter options when user types', () => {
    component.openDropdown();
    component.inputText = 'an';
    component.onInputChange();
    // Only 'Banana' contains 'an'
    expect(component.filteredOptions.length).toEqual(1);
    expect(component.filteredOptions[0].label).toEqual('Banana');
  });

  it('should filter options case-insensitively', () => {
    component.openDropdown();
    component.inputText = 'APPLE';
    component.onInputChange();
    expect(component.filteredOptions.length).toEqual(1);
    expect(component.filteredOptions[0].label).toEqual('Apple');
  });

  it('should show no-options message when filter has no matches', () => {
    component.openDropdown();
    component.inputText = 'xyz';
    component.onInputChange();
    fixture.detectChanges();
    expect(component.filteredOptions.length).toEqual(0);
    const noOptionsEl = fixture.debugElement.query(By.css('.dropdown-item.disabled'));
    expect(noOptionsEl).toBeTruthy();
  });

  it('should select an option and emit selectionChange', () => {
    const emitSpy = jest.spyOn(component.selectionChange, 'emit');
    component.openDropdown();
    component.selectOption(mockOptions[1]);
    expect(component.selectedValue).toEqual('banana');
    expect(component.inputText).toEqual('Banana');
    expect(component.isOpen).toBeFalsy();
    expect(emitSpy).toHaveBeenCalledWith('banana');
  });

  it('should close dropdown and restore selected label on Escape key', () => {
    component.selectedValue = 'apple';
    component.ngOnChanges();
    component.openDropdown();
    component.inputText = 'ban';
    component.onKeydown(new KeyboardEvent('keydown', { key: 'Escape' }));
    expect(component.isOpen).toBeFalsy();
    expect(component.inputText).toEqual('Apple');
  });

  it('should navigate options with ArrowDown and ArrowUp keys', () => {
    component.openDropdown();
    component.onKeydown(new KeyboardEvent('keydown', { key: 'ArrowDown' }));
    expect(component.activeIndex).toEqual(0);
    component.onKeydown(new KeyboardEvent('keydown', { key: 'ArrowDown' }));
    expect(component.activeIndex).toEqual(1);
    component.onKeydown(new KeyboardEvent('keydown', { key: 'ArrowUp' }));
    expect(component.activeIndex).toEqual(0);
  });

  it('should not navigate below 0 or above options length', () => {
    component.openDropdown();
    component.onKeydown(new KeyboardEvent('keydown', { key: 'ArrowUp' }));
    expect(component.activeIndex).toEqual(0);
    // Navigate to last item
    component.activeIndex = mockOptions.length - 1;
    component.onKeydown(new KeyboardEvent('keydown', { key: 'ArrowDown' }));
    expect(component.activeIndex).toEqual(mockOptions.length - 1);
  });

  it('should select the active option on Enter key', () => {
    const emitSpy = jest.spyOn(component.selectionChange, 'emit');
    component.openDropdown();
    component.activeIndex = 2; // Cherry
    component.onKeydown(new KeyboardEvent('keydown', { key: 'Enter' }));
    expect(component.selectedValue).toEqual('cherry');
    expect(emitSpy).toHaveBeenCalledWith('cherry');
  });

  it('should not select when Enter is pressed with no active index', () => {
    const emitSpy = jest.spyOn(component.selectionChange, 'emit');
    component.openDropdown();
    component.activeIndex = -1;
    component.onKeydown(new KeyboardEvent('keydown', { key: 'Enter' }));
    expect(emitSpy).not.toHaveBeenCalled();
  });

  it('should open dropdown on ArrowDown when closed', () => {
    expect(component.isOpen).toBeFalsy();
    component.onKeydown(new KeyboardEvent('keydown', { key: 'ArrowDown' }));
    expect(component.isOpen).toBeTruthy();
  });

  it('should close dropdown on outside click', () => {
    component.openDropdown();
    expect(component.isOpen).toBeTruthy();
    component.onDocumentClick(new MouseEvent('click'));
    expect(component.isOpen).toBeFalsy();
  });

  it('should not open dropdown when disabled', () => {
    component.disabled = true;
    component.openDropdown();
    expect(component.isOpen).toBeFalsy();
  });

  it('should sync inputText when selectedValue changes externally', () => {
    component.selectedValue = 'cherry';
    component.ngOnChanges();
    expect(component.inputText).toEqual('Cherry');
  });

  it('should toggle dropdown on toggle button click', () => {
    expect(component.isOpen).toBeFalsy();
    const mockEvent = { stopPropagation: jest.fn() } as unknown as MouseEvent;
    component.onToggleClick(mockEvent);
    expect(component.isOpen).toBeTruthy();
    component.onToggleClick(mockEvent);
    expect(component.isOpen).toBeFalsy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when open with options', () => {
    component.openDropdown();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when an option is selected', () => {
    component.selectedValue = 'banana';
    component.ngOnChanges();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when no options match the filter', () => {
    component.openDropdown();
    component.inputText = 'xyz';
    component.onInputChange();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

});
