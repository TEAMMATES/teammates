import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SessionResultCsvService } from './session-result-csv.service';
import { InstructorSessionResultSectionType } from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import { SessionResults } from '../types/api-output';

/**
 * Loads data for testing.
 */
const loadTestData: (filename: string) => Promise<SessionResults> = async (filename: string): Promise<SessionResults> => {
  const testDataModule = await import(`./test-data/${filename}`);
  return testDataModule.default;
};

/**
 * Substitutes values that are different across different properties configuration.
 */
const replaceUnpredictableValuesWithPlaceholders: (str: string) => string = (str: string): string => {
  return str.replace(/Anonymous (student|instructor|team) [0-9]{1,10}/g, 'Anonymous $1 ${participant.hash}');
};

describe('replaceUnpredictableValuesWithPlaceholders', () => {
  it('should replace unpredictable values with placeholders', () => {
    const sampleCsvFile = `Header 1,Header 2,Header 3
Content 1,Content 2,Content 3
Anonymous student 1234567,Anonymous instructor 4567890,Anonymous team 87654321
`;
    const expectedOutput = `Header 1,Header 2,Header 3
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
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  it('should be created', () => {
    service = TestBed.inject(SessionResultCsvService);
    expect(service).toBeTruthy();
  });

  it('should show responses for feedbackSessionResultsC1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show responses along with stats for feedbackSessionResultsC1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show missing responses for feedbackSessionResultsC1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsAllResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show responses from/to section for feedbackSessionResultsC1S1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EITHER,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should only show responses from section for feedbackSessionResultsC1S1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.GIVER,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should only show responses to section for feedbackSessionResultsC1S1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EVALUEE,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should only show responses from and to section for feedbackSessionResultsC1S1S1', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.BOTH,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show missing responses', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsMissingResponsesShown.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should hide missing responses', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsMissingResponsesShown.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should hide stats', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsStatistics.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show stats', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsStatistics.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question (question 1)', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1Q1.json');

    const result: string = service.getCsvForSessionResult(sessionResult, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question (question 2)', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsSingleQuestion.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question from/to section', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EITHER,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question with responses from section', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.GIVER,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question with responses to section', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EVALUEE,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question with responses from and to section', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsC1S1S1Q2.json');

    const result: string = service.getCsvForSessionResult(
      sessionResult,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.BOTH,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for MCQ question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsMcqResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for MSQ question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsMsqResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for NUMSCALE question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsNumscaleResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONSTSUM question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsConstsumResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONSTSUM question (restricted responses)', async () => {
    const sessionResult: SessionResults = await loadTestData(
      'feedbackSessionResultsConstsumResultsInstructorNoPrivilege.json',
    );

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONTRIB question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsContribResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONTRIB question (restricted section)', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsContribResultsRestrictedSections.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for RUBRIC question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsRubricResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for RANK question', async () => {
    const sessionResult: SessionResults = await loadTestData('feedbackSessionResultsRankResults.json');

    const result: string = service.getCsvForSessionResult(sessionResult, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });
});
