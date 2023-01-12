import { SearchTermsHighlighterPipe } from './search-terms-highlighter.pipe';

describe('SearchTermsHighlighterPipe', () => {
    let highlighterPipe: SearchTermsHighlighterPipe;

    beforeEach(() => {
        highlighterPipe = new SearchTermsHighlighterPipe();
    });

    it('should be instantiated', () => {
        expect(highlighterPipe).toBeTruthy();
    });

    it('should highlight text by case insensitive word match', () => {
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

    it('should not highlight text if there is only partial match and partial parameter not specified', () => {
        const sampleSearch = 's t u dent';
        const sampleValue = 'student';
        expect(highlighterPipe.transform(sampleValue, sampleSearch)).toBe(sampleValue);
    });

    it('should not highlight any text if search terms are empty', () => {
        const sampleSearch = '""  ';
        const sampleValue = 'Student A';
        expect(highlighterPipe.transform(sampleValue, sampleSearch)).toBe(sampleValue);
    });

    it('should highlight with mix of exact phrases and search terms', () => {
        const consolidatedSamples = [
            {
                sampleSearch: '"new student" team',
                sampleValue: 'new student team a',
                expected: '<span class="highlighted-text">new student</span>'
                + ' <span class="highlighted-text">team</span> a',

            },
            {
                sampleSearch: 'team "new student"',
                sampleValue: 'new student team a',
                expected: '<span class="highlighted-text">new student</span>'
                + ' <span class="highlighted-text">team</span> a',
            },
        ];
        for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
            expect(highlighterPipe
            .transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch))
            .toEqual(consolidatedSamples[i].expected);
        }
    });

    it('should highlight case-insensitive partial matches if partial parameter is specified', () => {
        const consolidatedSamples = [
            {
                sampleSearch: 'Test',
                sampleValue: 'TestCourse',
                expected: '<span class="highlighted-text">Test</span>Course',

            },
            {
                sampleSearch: 'Te',
                sampleValue: 'TestCourse',
                expected: '<span class="highlighted-text">Te</span>stCourse',
            },
            {
                sampleSearch: 'test',
                sampleValue: 'TestCourse',
                expected: '<span class="highlighted-text">Test</span>Course',
            },
            {
                sampleSearch: 'Test',
                sampleValue: 'testcourse',
                expected: '<span class="highlighted-text">test</span>course',
            },
            {
                sampleSearch: 'estc',
                sampleValue: 'TestCourse',
                expected: 'T<span class="highlighted-text">estC</span>ourse',
            },
            {
                sampleSearch: 'e r',
                sampleValue: 'TestCourse',
                expected: 'T<span class="highlighted-text">e</span>stCou<span class="highlighted-text">'
                + 'r</span>s<span class="highlighted-text">e</span>',
            },
        ];
        for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
            expect(highlighterPipe
            .transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch, true))
            .toEqual(consolidatedSamples[i].expected);
        }
    });

    it('should not highlight if there are no case-insensitive partial matches', () => {
        const consolidatedSamples = [
            {
                sampleSearch: 'esr',
                sampleValue: 'TestCourse',
                expected: 'TestCourse',
            },
            {
                sampleSearch: 'a',
                sampleValue: 'TestCourse',
                expected: 'TestCourse',
            },
            {
                sampleSearch: ' ',
                sampleValue: 'Test Course',
                expected: 'Test Course',
            },
            {
                sampleSearch: '',
                sampleValue: 'TestCourse',
                expected: 'TestCourse',
            },
            {
                sampleSearch: '',
                sampleValue: '',
                expected: '',
            },
        ];
        for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
            expect(highlighterPipe
                .transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch, true))
                .toEqual(consolidatedSamples[i].expected);
        }
    });

    it('should highlight case-insensitive partial matches with exact phrases in search term '
        + 'if partial parameter is specified', () => {
            const consolidatedSamples = [
                {
                    sampleSearch: '"Test"',
                    sampleValue: 'TestCourse',
                    expected: '<span class="highlighted-text">Test</span>Course',

                },
                {
                    sampleSearch: '"Te"',
                    sampleValue: 'TestCourse',
                    expected: '<span class="highlighted-text">Te</span>stCourse',
                },
                {
                    sampleSearch: '"test"',
                    sampleValue: 'TestCourse',
                    expected: '<span class="highlighted-text">Test</span>Course',
                },
                {
                    sampleSearch: '"Test"',
                    sampleValue: 'testcourse',
                    expected: '<span class="highlighted-text">test</span>course',
                },
                {
                    sampleSearch: '"estc"',
                    sampleValue: 'TestCourse',
                    expected: 'T<span class="highlighted-text">estC</span>ourse',
                },
                {
                    sampleSearch: '"est C"',
                    sampleValue: 'Test Course',
                    expected: 'T<span class="highlighted-text">est C</span>ourse',
                },
                {
                    sampleSearch: '"Test Course"',
                    sampleValue: 'Test Course',
                    expected: '<span class="highlighted-text">Test Course</span>',
                },
                {
                    sampleSearch: '"Test "',
                    sampleValue: 'Test Course',
                    expected: '<span class="highlighted-text">Test </span>Course',
                },
                {
                    sampleSearch: '" Course"',
                    sampleValue: 'Test Course',
                    expected: 'Test<span class="highlighted-text"> Course</span>',
                },
            ];
            for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
                expect(highlighterPipe
                    .transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch, true))
                    .toEqual(consolidatedSamples[i].expected);
            }
        });

    it('should not highlight if there are no case-insensitive partial matches for exact phrases', () => {
        const consolidatedSamples = [
            {
                sampleSearch: '"Course Test"',
                sampleValue: 'Test Course',
                expected: 'Test Course',
            },
            {
                sampleSearch: '"Course "',
                sampleValue: 'Test Course',
                expected: 'Test Course',
            },
        ];
        for (let i: number = 0; i < consolidatedSamples.length; i += 1) {
            expect(highlighterPipe
                .transform(consolidatedSamples[i].sampleValue, consolidatedSamples[i].sampleSearch, true))
                .toEqual(consolidatedSamples[i].expected);
        }
    });
});
