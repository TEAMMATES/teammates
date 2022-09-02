import { SearchTermsHighlighterPipe } from './search-terms-highlighter.pipe';

describe('ResponseStatusPipe', () => {
    let highlighterPipe: SearchTermsHighlighterPipe;

    beforeEach(() => {
        highlighterPipe = new SearchTermsHighlighterPipe();
    });

    it('should be instantiated', () => {
        expect(highlighterPipe).toBeTruthy();
    });

    it('should highlight text by case insenstive word match', () => {
        const consolidatedSamples = [
            {
                sampleSearch: 'Student',
                sampleValue: 'Student',
                expected: '<span class="highlighted-text">Student</span>',

            },
            {
                sampleSearch: 'Student Student2',
                sampleValue: 'Student',
                expected: '<span class="highlighted-text">Student</span>',
            },
            {
                sampleSearch: 'Student',
                sampleValue: 'Student A',
                expected: '<span class="highlighted-text">Student</span> A',
            },
            {
                sampleSearch: 'student@gmail.com',
                sampleValue: 'student@gmail.com',
                expected: '<span class="highlighted-text">student@gmail.com</span>',
            },
            {
                sampleSearch: 'student',
                sampleValue: 'Student',
                expected: '<span class="highlighted-text">Student</span>',
            },
        ];
        for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
            expect(highlighterPipe.transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch))
            .toEqual(consolidatedSamples[i].expected);
        }
    });

    it('should not highlight text if there is no match', () => {
        const consolidatedSamples = [
            {
                sampleSearch: 'Student',
                sampleValue: 'Studen',
            },
            {
                sampleSearch: 'Student',
                sampleValue: 'Studen t',
            },
            {
                sampleSearch: '',
                sampleValue: 'Student',
            },
        ];
        for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
            expect(highlighterPipe.transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch))
                .toEqual(consolidatedSamples[i].sampleValue);
        }
    });

    it('should highlight text if there are exact phrases', () => {
        const sampleSearch = '"student one" "studentemail@gmail.com"';
        const sampleValue = 'student one studentemail@gmail.com';
        expect(highlighterPipe.transform(sampleValue, sampleSearch))
        .toBe('<span class="highlighted-text">student one</span> '
        + '<span class="highlighted-text">studentemail@gmail.com</span>');
    });

    it('should not highlight text if there are no matches to exact phrases', () => {
        const sampleSearch = '"student one" "studentmail@gmail.com"';
        const sampleValue = 'student ne one student stuentemail@gmail.com';
        expect(highlighterPipe.transform(sampleValue, sampleSearch)).toBe(sampleValue);
    });

    it('should not highlight text if there is only partial match', () => {
        const sampleSearch = 's t u dent';
        const sampleValue = 'student';
        expect(highlighterPipe.transform(sampleValue, sampleSearch)).toBe(sampleValue);
    });

    it('should not highlight any text if search terms are empty', () => {
        const sampleSearch = '""  ';
        const sampleValue = 'Student A';
        expect(highlighterPipe.transform(sampleValue, sampleSearch)).toBe(sampleValue);
    });
});
