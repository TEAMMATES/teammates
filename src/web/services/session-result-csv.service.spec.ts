import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import {
  InstructorSessionResultSectionType,
} from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import {
  SectionTypeDescriptionPipe,
} from '../app/pages-instructor/instructor-session-result-page/section-type-description.pipe';
import { SessionResults } from '../types/api-output';
import { SessionResultCsvService } from './session-result-csv.service';

/**
 * Loads data for testing.
 */
const loadTestData: (filename: string) => SessionResults = (filename: string): SessionResults => {
  return require(`./test-data/${filename}`);
};

/**
 * Substitutes values that are different across different properties configuration.
 */
const replaceUnpredictableValuesWithPlaceholders: (str: string) => string = (str: string): string => {
  // tslint:disable-next-line:no-invalid-template-strings
  return str.replace(/Anonymous (student|instructor|team) [0-9]{1,10}/g, 'Anonymous $1 ${participant.hash}');
};

describe('replaceUnpredictableValuesWithPlaceholders', () => {
  it('should replace unpredictable values with placeholders', () => {
    const sampleCsvFile: string = `Header 1,Header 2,Header 3
Content 1,Content 2,Content 3
Anonymous student 1234567,Anonymous instructor 4567890,Anonymous team 87654321
`;
    const expectedOutput: string = `Header 1,Header 2,Header 3
Content 1,Content 2,Content 3
Anonymous student \${participant.hash},Anonymous instructor \${participant.hash},Anonymous team \${participant.hash}
`;
    expect(replaceUnpredictableValuesWithPlaceholders(sampleCsvFile)).toEqual(expectedOutput);
  });
});

describe('SessionResultCsvService', () => {
  let service: SessionResultCsvService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        SectionTypeDescriptionPipe,
      ],
      imports: [
        HttpClientTestingModule,
      ],
    });

    service = TestBed.get(SessionResultCsvService);
    expect(service).toBeTruthy();
  });

  it('should show responses for feedbackSessionResultsC1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1_actionTest.csv
  });

  it('should show responses along with stats for feedbackSessionResultsC1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsHideMissingResponses.csv
  });

  it('should show missing responses for feedbackSessionResultsC1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsAllResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsAllResults.csv
  });

  it('should show responses from/to section for feedbackSessionResultsC1S1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.EITHER);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Either_actionTest.csv
  });

  it('should only show responses from section for feedbackSessionResultsC1S1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.GIVER);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Giver_actionTest.csv
  });

  it('should only show responses to section for feedbackSessionResultsC1S1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.EVALUEE);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Recipient_actionTest.csv
  });

  it('should only show responses from and to section for feedbackSessionResultsC1S1S1', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.BOTH);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Both_actionTest.csv
  });

  it('should display student last name displayed properly for feedbackSessionResultsC1S1NewLastName', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1NewLastName.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1NewLastName_actionTest.csv
  });

  it('should show missing responses', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsMissingResponsesShown.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsMissingResponsesShown_actionTest.csv
  });

  it('should hide missing responses', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsMissingResponsesShown.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsMissingResponsesHidden_actionTest.csv
  });

  it('should hide stats', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsStatistics.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsStatisticsHidden_actionTest.csv
  });

  it('should show stats', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsStatistics.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsStatisticsShown_actionTest.csv
  });

  it('should generate results for a specific question (question 1)', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1Q1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1Q1_actionTest.csv
  });

  it('should generate results for a specific question (question 2)', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsSingleQuestion.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsSingleQuestion.csv
  });

  it('should generate results for a specific question from/to section', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.EITHER);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Q2Either_actionTest.csv
  });

  it('should generate results for a specific question with responses from section', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.GIVER);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Q2Giver_actionTest.csv
  });

  it('should generate results for a specific question with responses to section', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.EVALUEE);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Q2Recipient_actionTest.csv
  });

  it('should generate results for a specific question with responses from and to section', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false,
        'Section 1', InstructorSessionResultSectionType.BOTH);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsC1S1S1Q2Both_actionTest.csv
  });

  it('should generate results for MCQ question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsMcqResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsMcqResults.csv
  });

  it('should generate results for MSQ question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsMsqResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsMsqResults.csv
  });

  it('should generate results for NUMSCALE question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsNumscaleResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsNumscaleResults.csv
  });

  it('should generate results for CONSTSUM question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsConstsumResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsConstsumResults.csv
  });

  it('should generate results for CONSTSUM question (restricted responses)', () => {
    const sessionResult: SessionResults =
        loadTestData('feedbackSessionResultsConstsumResultsInstructorNoPrivilege.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsConstsumResultsInstructorNoPrivilege.csv
  });

  it('should generate results for CONTRIB question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsContribResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsContribResults.csv
  });

  it('should generate results for CONTRIB question (restricted section)', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsContribResultsRestrictedSections.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsContribResultsRestrictedSections.csv
  });

  it('should generate results for RUBRIC question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsRubricResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsRubricResults.csv
  });

  it('should generate results for RANK question', () => {
    const sessionResult: SessionResults = loadTestData('feedbackSessionResultsRankResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
    // feedbackSessionResultsRankResults.csv
  });
});
