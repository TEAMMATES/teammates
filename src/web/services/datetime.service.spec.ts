import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { DateTimeService } from './datetime.service';
import { TimezoneService } from './timezone.service';
import { DateFormat, TimeFormat } from '../types/datetime-const';

describe('DateTimeService', () => {
    let service: DateTimeService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule,
            ],
            providers: [
                { provide: TimezoneService },
            ],
        });
        service = TestBed.inject(DateTimeService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should convert Date to TimeFormat correctly for if hour is 0 and minute is 0', () => {
        const date: Date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(0);
        expect(result.minute).toEqual(0);
    });

    it('should convert Date to TimeFormat correctly for if hour is between 0 and 24 and minute is between 0 and 60',
     () => {
        const date: Date = new Date();
        date.setHours(10);
        date.setMinutes(56);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(10);
        expect(result.minute).toEqual(56);
    });

    it('should give the correct hour if hour exceeds 24', () => {
        const date: Date = new Date();
        date.setHours(25);
        date.setMinutes(0);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(1);
        expect(result.minute).toEqual(0);
    });

    it('should give the correct time if hour is less than 0', () => {
        const date: Date = new Date();
        date.setHours(-1);
        date.setMinutes(0);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(23);
        expect(result.minute).toEqual(0);
    });

    it('should give the correct time if minute is less than 0', () => {
        const date: Date = new Date();
        date.setHours(0);
        date.setMinutes(-1);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(23);
        expect(result.minute).toEqual(59);
    });

    it('should give the correct time if hours and minutes are less than 0', () => {
        const date: Date = new Date();
        date.setHours(-1);
        date.setMinutes(-1);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(22);
        expect(result.minute).toEqual(59);
    });

    it('should return 1 if the first date\'s year is later than the second date\'s year', () => {
        const firstDate : DateFormat = { year: 2023, month: 8, day: 23 };
        const secondDate: DateFormat = { year: 2022, month: 8, day: 23 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(1);
    });

    it('should return -1 if the first date\'s year is earlier than the second date\'s year', () => {
        const firstDate : DateFormat = { year: 2022, month: 8, day: 23 };
        const secondDate: DateFormat = { year: 2023, month: 8, day: 23 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(-1);
    });

    it('should return 1 if year is the same and first date\'s month is later than second date\'s month', () => {
        const firstDate : DateFormat = { year: 2023, month: 9, day: 23 };
        const secondDate: DateFormat = { year: 2023, month: 8, day: 23 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(1);
    });

    it('should return -1 if if year is the same and first date\'s month is earlier than second date\'s month', () => {
        const firstDate : DateFormat = { year: 2023, month: 8, day: 23 };
        const secondDate: DateFormat = { year: 2023, month: 9, day: 23 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(-1);
    });

    it('should return 1 if year and month are the same and first date\'s day is later than second date\'s day', () => {
        const firstDate : DateFormat = { year: 2023, month: 9, day: 28 };
        const secondDate: DateFormat = { year: 2023, month: 9, day: 23 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(1);
    });

    it('should return -1 if year and month are same and first date\'s day is earlier than second date\'s day', () => {
        const firstDate : DateFormat = { year: 2023, month: 9, day: 23 };
        const secondDate: DateFormat = { year: 2023, month: 9, day: 28 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(-1);
    });

    it('should return 0 if both dates have the same year and month and day', () => {
        const firstDate : DateFormat = { year: 2023, month: 9, day: 28 };
        const secondDate: DateFormat = { year: 2023, month: 9, day: 28 };
        expect(DateTimeService.compareDateFormat(firstDate, secondDate)).toEqual(0);
    });

    it('should return 1 if the first timing\'s hour is later than the second timing\'s hour', () => {
        const firstTime : TimeFormat = { hour: 21, minute: 0 };
        const secondTime : TimeFormat = { hour: 19, minute: 0 };
        expect(DateTimeService.compareTimeFormat(firstTime, secondTime)).toEqual(1);
    });

    it('should return -1 if the first timing\'s hour is earlier than the second timing\'s hour', () => {
        const firstTime : TimeFormat = { hour: 20, minute: 0 };
        const secondTime : TimeFormat = { hour: 21, minute: 0 };
        expect(DateTimeService.compareTimeFormat(firstTime, secondTime)).toEqual(-1);
    });

    it('should return 1 if hour is the same and first timing\'s minute is later than second timing\'s minute', () => {
        const firstTime : TimeFormat = { hour: 21, minute: 30 };
        const secondTime : TimeFormat = { hour: 21, minute: 0 };
        expect(DateTimeService.compareTimeFormat(firstTime, secondTime)).toEqual(1);
    });

    it('should return -1 if hour is same and first timing\'s minute is earlier than second timing\'s minute', () => {
        const firstTime : TimeFormat = { hour: 21, minute: 0 };
        const secondTime : TimeFormat = { hour: 21, minute: 30 };
        expect(DateTimeService.compareTimeFormat(firstTime, secondTime)).toEqual(-1);
    });

    it('should return 0 if both timings have the same hour and minute', () => {
        const firstTime : TimeFormat = { hour: 21, minute: 30 };
        const secondTime : TimeFormat = { hour: 21, minute: 30 };
        expect(DateTimeService.compareTimeFormat(firstTime, secondTime)).toEqual(0);
    });

});
