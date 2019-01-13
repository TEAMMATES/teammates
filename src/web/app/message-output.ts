/**
 * Encapsulates error message from back-end API endpoints.
 */
export interface ErrorMessageOutput {
  error: MessageOutput;
  status: number;
}

/**
 * Encapsulates messages from back-end API endpoints.
 */
export interface MessageOutput {
  message: string;
  requestId?: string;
}
