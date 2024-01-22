import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';
import { DatePickerFormatter } from './datepicker-formatter';

describe('DatepickerFormatter', () => {
    let formatter : DatePickerFormatter;

    beforeEach(() => {
        formatter = new DatePickerFormatter();
    });

    it('should create an instance', () => {
        expect(formatter).toBeTruthy();
    });

    it('should return an empty string if date is null', () => {
        // Any is used as the type for date here as it is the only way to pass in null to NgbDateStruct
        const date: any = null;
        const formattedDate: string = formatter.format(date);
        expect(formattedDate).toEqual('');
    });

    it('should return a properly formatted date string', () => {
        const date: NgbDateStruct = { year: 2023, month: 12, day: 12 };
        const formattedDate = formatter.format(date);
        expect(formattedDate).toEqual('Tue, 12 Dec, 2023');
    });

    it('should parse the valid date string correctly', () => {
        const date : string = 'Tue, 12 Dec, 2023';
        const parsedDate : NgbDateStruct = formatter.parse(date);
        expect(parsedDate.day).toEqual(12);
        expect(parsedDate.month).toEqual(12);
        expect(parsedDate.year).toEqual(2023);
    });

    it('should return NaN for all the fields if invalid date string format is parsed', () => {
        const date : string = '12th December 2023';
        const parsedDate : NgbDateStruct = formatter.parse(date);
        expect(parsedDate.day).toEqual(NaN);
        expect(parsedDate.month).toEqual(NaN);
        expect(parsedDate.year).toEqual(NaN);
    });
});
