import { TestBed } from '@angular/core/testing';
import { DateTimeService } from './datetime.service';
import { TimezoneService } from './timezone.service';

describe('DateTimeService', () => {
    let service: DateTimeService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                { provide: TimezoneService },
            ],
        });
        service = TestBed.inject(DateTimeService);
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should convert Date to TimeFormat correctly', () => {
        const date: Date = new Date();
        date.setHours(0);
        date.setMinutes(0);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(0);
        expect(result.minute).toEqual(0);
    });

    it('should give the correct hour if hour exceeds 24', () => {
        const date: Date = new Date();
        date.setHours(25);
        date.setMinutes(0);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(1);
        expect(result.minute).toEqual(0);
    });

    it('should give the correct hour if hour is less than 0', () => {
        const date: Date = new Date();
        date.setHours(-1);
        date.setMinutes(0);
        const result = service.convertDateToTimeFormat(date);
        expect(result.hour).toEqual(23);
        expect(result.minute).toEqual(0);
    });
});
