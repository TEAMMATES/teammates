import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { NgbCalendar, NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { DatetimepickerComponent } from './datetimepicker.component';
import { DateFormat, TimeFormat } from '../../../types/datetime-const';

describe('DatetimepickerComponent', () => {
  let component: DatetimepickerComponent;
  let fixture: ComponentFixture<DatetimepickerComponent>;

  // 2023-10-12T05:00:00Z
  const timestamp = Date.UTC(2023, 9, 12, 5, 0, 0);

  beforeEach(() => {
    fixture = TestBed.createComponent(DatetimepickerComponent);
    component = fixture.componentInstance;
    component.timeZone = 'UTC';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should derive date and time from the timestamp input', () => {
    component.timestamp = timestamp;
    component.ngOnChanges();

    expect(component.date).toEqual({ year: 2023, month: 10, day: 12 });
    expect(component.time).toEqual({ hour: 5, minute: 0 });
  });

  it('should derive min/max date and time from the bound inputs', () => {
    component.minTimestamp = Date.UTC(2023, 9, 12, 5, 0, 0);
    component.maxTimestamp = Date.UTC(2023, 9, 14, 9, 0, 0);
    component.ngOnChanges();

    expect(component.minDate).toEqual({ year: 2023, month: 10, day: 12 });
    expect(component.minTime).toEqual({ hour: 5, minute: 0 });
    expect(component.maxDate).toEqual({ year: 2023, month: 10, day: 14 });
    expect(component.maxTime).toEqual({ hour: 9, minute: 0 });
  });

  it('should emit a timestamp when the date changes', () => {
    component.timestamp = timestamp;
    component.ngOnChanges();
    const emitSpy = vi.spyOn(component.timestampChange, 'emit');

    const date: DateFormat = { year: 2023, month: 10, day: 13 };
    component.changeDate(date);

    expect(emitSpy).toHaveBeenCalledWith(Date.UTC(2023, 9, 13, 5, 0, 0));
  });

  it('should emit a timestamp when the time changes', () => {
    component.timestamp = timestamp;
    component.ngOnChanges();
    const emitSpy = vi.spyOn(component.timestampChange, 'emit');

    const time: TimeFormat = { hour: 9, minute: 0 };
    component.changeTime(time);

    expect(emitSpy).toHaveBeenCalledWith(Date.UTC(2023, 9, 12, 9, 0, 0));
  });

  it("should navigate to today's date and emit on selectTodayDate", () => {
    component.timestamp = timestamp;
    component.ngOnChanges();
    const datepicker = fixture.debugElement.query(By.directive(NgbInputDatepicker)).injector.get(NgbInputDatepicker);
    const today = TestBed.inject(NgbCalendar).getToday();
    const navigateSpy = vi.spyOn(datepicker, 'navigateTo');
    const emitSpy = vi.spyOn(component.timestampChange, 'emit');

    component.selectTodayDate(datepicker);

    expect(navigateSpy).toHaveBeenCalledWith(today);
    expect(emitSpy).toHaveBeenCalled();
    expect(component.date).toEqual({ year: today.year, month: today.month, day: today.day });
  });

  it('should disable time options outside the min/max bounds', () => {
    component.timestamp = Date.UTC(2023, 9, 12, 5, 0, 0);
    component.minTimestamp = Date.UTC(2023, 9, 12, 5, 0, 0);
    component.maxTimestamp = Date.UTC(2023, 9, 12, 10, 0, 0);
    component.ngOnChanges();

    expect(component.isOptionDisabled({ hour: 4, minute: 0 })).toBe(true);
    expect(component.isOptionDisabled({ hour: 7, minute: 0 })).toBe(false);
    expect(component.isOptionDisabled({ hour: 11, minute: 0 })).toBe(true);
  });
});
