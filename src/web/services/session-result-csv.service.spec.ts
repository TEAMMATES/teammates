import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SessionResultCsvService } from './session-result-csv.service';
import { InstructorSessionResultSectionType } from '../app/pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';
import feedbackSessionResultsAllResults from './test-data/feedback-session-results-all-results';
import feedbackSessionResultsC1S1 from './test-data/feedback-session-results-c1s1';
import feedbackSessionResultsC1S1Q1 from './test-data/feedback-session-results-c1s1q1';
import feedbackSessionResultsC1S1S1 from './test-data/feedback-session-results-c1s1s1';
import feedbackSessionResultsC1S1S1Q2 from './test-data/feedback-session-results-c1s1s1q2';
import feedbackSessionResultsConstsumResults from './test-data/feedback-session-results-constsum-results';
import feedbackSessionResultsConstsumResultsInstructorNoPrivilege from './test-data/feedback-session-results-constsum-results-instructor-no-privilege';
import feedbackSessionResultsContribResults from './test-data/feedback-session-results-contrib-results';
import feedbackSessionResultsContribResultsRestrictedSections from './test-data/feedback-session-results-contrib-results-restricted-sections';
import feedbackSessionResultsMcqResults from './test-data/feedback-session-results-mcq-results';
import feedbackSessionResultsMissingResponsesShown from './test-data/feedback-session-results-missing-responses-shown';
import feedbackSessionResultsMsqResults from './test-data/feedback-session-results-msq-results';
import feedbackSessionResultsNumscaleResults from './test-data/feedback-session-results-numscale-results';
import feedbackSessionResultsRankResults from './test-data/feedback-session-results-rank-results';
import feedbackSessionResultsRubricResults from './test-data/feedback-session-results-rubric-results';
import feedbackSessionResultsSingleQuestion from './test-data/feedback-session-results-single-question';
import feedbackSessionResultsStatistics from './test-data/feedback-session-results-statistics';

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
    service = TestBed.inject(SessionResultCsvService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show responses for feedbackSessionResultsC1S1', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsC1S1, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show responses along with stats for feedbackSessionResultsC1S1', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsC1S1, false, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show missing responses for feedbackSessionResultsC1S1', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsAllResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show responses from/to section for feedbackSessionResultsC1S1S1', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EITHER,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should only show responses from section for feedbackSessionResultsC1S1S1', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.GIVER,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should only show responses to section for feedbackSessionResultsC1S1S1', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EVALUEE,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should only show responses from and to section for feedbackSessionResultsC1S1S1', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.BOTH,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show missing responses', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsMissingResponsesShown, true, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should hide missing responses', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsMissingResponsesShown, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should hide stats', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsStatistics, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should show stats', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsStatistics, false, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question (question 1)', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsC1S1Q1, false, false);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question (question 2)', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsSingleQuestion, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question from/to section', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1Q2,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EITHER,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question with responses from section', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1Q2,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.GIVER,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question with responses to section', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1Q2,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.EVALUEE,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for a specific question with responses from and to section', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsC1S1S1Q2,
      false,
      false,
      'Section 1',
      InstructorSessionResultSectionType.BOTH,
      'section-1',
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for MCQ question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsMcqResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for MSQ question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsMsqResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for NUMSCALE question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsNumscaleResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONSTSUM question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsConstsumResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONSTSUM question (restricted responses)', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsConstsumResultsInstructorNoPrivilege,
      true,
      true,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONTRIB question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsContribResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for CONTRIB question (restricted section)', () => {
    const result: string = service.getCsvForSessionResult(
      feedbackSessionResultsContribResultsRestrictedSections,
      true,
      true,
    );
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for RUBRIC question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsRubricResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });

  it('should generate results for RANK question', () => {
    const result: string = service.getCsvForSessionResult(feedbackSessionResultsRankResults, true, true);
    expect(replaceUnpredictableValuesWithPlaceholders(result)).toMatchSnapshot();
  });
});
