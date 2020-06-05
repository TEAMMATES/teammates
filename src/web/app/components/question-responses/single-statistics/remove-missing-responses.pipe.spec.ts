import { ResponseOutput } from '../../../../types/api-output';
import { DEFAULT_TEXT_RESPONSE_DETAILS } from '../../../../types/default-question-structs';
import { RemoveMissingResponsesPipe } from './remove-missing-responses.pipe';

describe('RemoveMissingResponsesPipe', () => {
  it('create an instance', () => {
    const pipe: RemoveMissingResponsesPipe = new RemoveMissingResponsesPipe();
    expect(pipe).toBeTruthy();
  });

  it('should return empty array for empty input', () => {
    const pipe: RemoveMissingResponsesPipe = new RemoveMissingResponsesPipe();
    expect(pipe.transform([])).toEqual([]);
  });

  it('should remove missing responses', () => {
    const normalResponse: ResponseOutput = {
      isMissingResponse: false,
      responseId: 'id1',
      giver: 'Alice',
      giverTeam: 'Team A',
      giverSection: 'Section 1',
      recipient: 'Bob',
      recipientTeam: 'Team A',
      recipientSection: 'Section 1',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      instructorComments: [],
    };
    const missingResponse: ResponseOutput = {
      isMissingResponse: true,
      responseId: 'id1',
      giver: 'Alice',
      giverTeam: 'Team A',
      giverSection: 'Section 1',
      recipient: 'David',
      recipientTeam: 'Team A',
      recipientSection: 'Section 1',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      instructorComments: [],
    };

    const pipe: RemoveMissingResponsesPipe = new RemoveMissingResponsesPipe();
    expect(pipe.transform([normalResponse])).toEqual([normalResponse]);
    expect(pipe.transform([missingResponse])).toEqual([]);
    expect(pipe.transform([missingResponse, normalResponse])).toEqual([normalResponse]);
  });
});
