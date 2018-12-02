/**
 * Encapsulates error message from back-end API endpoints.
 */
export interface ErrorMessageOutput {
  error: MessageOutput;
  status: number;
}

interface MessageOutput {
  message: string;
  requestId?: string;
}
