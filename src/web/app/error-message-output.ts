import { MessageOutput } from '../types/api-output';

/**
 * Encapsulates error message from back-end API endpoints.
 */
export interface ErrorMessageOutput {
  error: MessageOutput;
  status: number;
}
