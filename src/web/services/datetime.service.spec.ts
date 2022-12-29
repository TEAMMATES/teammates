import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { DateTimeService } from './datetime.service';
import { TimezoneService } from './timezone.service';

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

});
