import { AutosaveService } from './autosave.service';
import { FeedbackResponseRecipientSubmissionFormModel } from '../app/components/question-submission-form/question-submission-form-model';
import { FeedbackTextResponseDetails } from '../types/api-output';
import { DEFAULT_TEXT_RESPONSE_DETAILS } from '../types/default-question-structs';

describe('AutosaveService', () => {
  let service: AutosaveService;
  let mockLocalStorage: Record<string, string>;

  beforeEach(() => {
    jest.useFakeTimers();
    mockLocalStorage = {};
    Object.defineProperty(globalThis, 'localStorage', {
      value: {
        getItem: jest.fn((key: string) => mockLocalStorage[key] ?? null),
        setItem: jest.fn((key: string, value: string) => {
          mockLocalStorage[key] = value;
        }),
        removeItem: jest.fn((key: string) => {
          delete mockLocalStorage[key];
        }),
        key: jest.fn((i: number) => Object.keys(mockLocalStorage)[i] ?? null),
        get length() {
          return Object.keys(mockLocalStorage).length;
        },
      },
      writable: true,
    });

    service = new AutosaveService();
  });

  afterEach(() => {
    jest.clearAllTimers();
    jest.useRealTimers();
    mockLocalStorage = {};
  });

  it('should save and retrieve feedback response', () => {
    const userId = 'user1';
    const questionId = 'q1';
    const data: FeedbackResponseRecipientSubmissionFormModel[] = [{
      responseId: 'response-id',
      recipientIdentifier: 'recipient-id',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      isValid: true,
      commentByGiver: undefined,
    }];
    const hasResponseChangedForRecipients = new Map<string, boolean>([['recipient-id', true]]);

    service.setSavedFeedbackResponse(userId, questionId, data, hasResponseChangedForRecipients);
    jest.advanceTimersByTime(500);

    const saved = service.getSavedFeedbackResponse(userId, questionId);
    expect(saved.recipientSubmissionForms).toEqual(data);
    expect(saved.hasResponseChangedForRecipients).toEqual(hasResponseChangedForRecipients);
  });

  it('should clear saved feedback response', () => {
    const userId = 'user2';
    const questionId = 'q2';
    const data: FeedbackResponseRecipientSubmissionFormModel[] = [{
      responseId: 'response-id',
      recipientIdentifier: 'recipient-id',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      isValid: true,
      commentByGiver: undefined,
    }];
    const hasResponseChangedForRecipients = new Map<string, boolean>([['recipient-id', true]]);

    service.setSavedFeedbackResponse(userId, questionId, data, hasResponseChangedForRecipients);
    jest.advanceTimersByTime(500);

    service.clearSavedFeedbackResponse(userId, questionId);

    const saved = service.getSavedFeedbackResponse(userId, questionId);
    expect(saved).toEqual({
      hasResponseChangedForRecipients: new Map<string, boolean>(),
      recipientSubmissionForms: [],
    });
  });

  it('should return null for expired entry', () => {
    const userId = 'user3';
    const questionId = 'q3';
    const key = `autosave_${userId}_${questionId}`;
    const data = 'hello world';
    const expiredPayload = {
      value: data,
      expiry: Date.now() - 1000,
    };
    mockLocalStorage[key] = JSON.stringify(expiredPayload);

    const saved = service.getSavedFeedbackResponse(userId, questionId);
    expect(saved).toEqual({
      hasResponseChangedForRecipients: new Map<string, boolean>(),
      recipientSubmissionForms: [],
    });
    expect(globalThis.localStorage.removeItem).toHaveBeenCalledWith(key);
  });

  it('should clean up expired entries on construction', () => {
    const key = 'autosave_cleanup';
    const data = 'hello world';
    mockLocalStorage[key] = JSON.stringify({
      value: data,
      expiry: Date.now() - 1000,
    });

    service = new AutosaveService();
    expect(globalThis.localStorage.removeItem).toHaveBeenCalledWith(key);
  });

  it('should debounce saves', () => {
    const userId = 'user4';
    const questionId = 'q4';
    const data1: FeedbackResponseRecipientSubmissionFormModel[] = [{
      responseId: 'response-id',
      recipientIdentifier: 'recipient-id',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      isValid: true,
      commentByGiver: undefined,
    }];
    const hasResponseChangedForRecipients1 = new Map<string, boolean>([['recipient-id', true]]);
    const data2: FeedbackResponseRecipientSubmissionFormModel[] = [{
      responseId: 'response-id',
      recipientIdentifier: 'recipient-id',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      isValid: true,
      commentByGiver: undefined,
    }];
    const hasResponseChangedForRecipients2 = new Map<string, boolean>([['recipient-id', false]]);
    (data2[0].responseDetails as FeedbackTextResponseDetails).answer = 'updated response';

    service.setSavedFeedbackResponse(userId, questionId, data1, hasResponseChangedForRecipients1);
    service.setSavedFeedbackResponse(userId, questionId, data2, hasResponseChangedForRecipients2);

    jest.advanceTimersByTime(500);

    const saved = service.getSavedFeedbackResponse(userId, questionId);
    expect(globalThis.localStorage.setItem).toHaveBeenCalledTimes(1);
    expect(saved.recipientSubmissionForms).toEqual(data2);
    expect(saved.hasResponseChangedForRecipients).toEqual(hasResponseChangedForRecipients2);
  });

  it('should isolate debounce per key', () => {
    const data1: FeedbackResponseRecipientSubmissionFormModel[] = [{
      responseId: 'r1',
      recipientIdentifier: 'recipient1',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      isValid: true,
      commentByGiver: undefined,
    }];
    const hasResponseChangedForRecipients1 = new Map<string, boolean>([['recipient1', true]]);

    const data2: FeedbackResponseRecipientSubmissionFormModel[] = [{
      responseId: 'r2',
      recipientIdentifier: 'recipient2',
      responseDetails: DEFAULT_TEXT_RESPONSE_DETAILS(),
      isValid: true,
      commentByGiver: undefined,
    }];
    const hasResponseChangedForRecipients2 = new Map<string, boolean>([['recipient2', true]]);

    service.setSavedFeedbackResponse('userA', 'q1', data1, hasResponseChangedForRecipients1);
    service.setSavedFeedbackResponse('userB', 'q2', data2, hasResponseChangedForRecipients2);

    jest.advanceTimersByTime(500);

    const saved1 = service.getSavedFeedbackResponse('userA', 'q1');
    const saved2 = service.getSavedFeedbackResponse('userB', 'q2');

    expect(saved1.recipientSubmissionForms).toEqual(data1);
    expect(saved1.hasResponseChangedForRecipients).toEqual(hasResponseChangedForRecipients1);
    expect(saved2.recipientSubmissionForms).toEqual(data2);
    expect(saved2.hasResponseChangedForRecipients).toEqual(hasResponseChangedForRecipients2);
    expect(globalThis.localStorage.setItem).toHaveBeenCalledTimes(2);
  });

  it('should handle corrupted localStorage entry gracefully', () => {
    const userId = 'user5';
    const questionId = 'q5';
    const key = `autosave_${userId}_${questionId}`;
    mockLocalStorage[key] = 'not a valid json';

    const saved = service.getSavedFeedbackResponse(userId, questionId);
    expect(saved).toEqual({
      hasResponseChangedForRecipients: new Map<string, boolean>(),
      recipientSubmissionForms: [],
    });
    expect(globalThis.localStorage.removeItem).toHaveBeenCalledWith(key);
  });
});
