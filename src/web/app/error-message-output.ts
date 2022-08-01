import { AccountRequestCreateErrorResults, MessageOutput } from '../types/api-output';

/**
 * Encapsulates error message from back-end API endpoints.
 */
export interface ErrorMessageOutput {
  error: MessageOutput;
  status: number;
}

/**
 * Encapsulates account request create error results from back-end API endpoint.
 */
export interface AccountRequestCreateErrorResultsWrapper {
  error: AccountRequestCreateErrorResults;
  status: number;
}
