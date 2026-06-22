import { AccountVerificationRequestUpdateRequest } from '../../../types/api-request';
import { AccountVerificationRequest, AccountVerificationRequestStatus } from '../../../types/api-output';

export interface AccountVerificationRequestDraft {
  name: string;
  email: string;
  institute: string;
  country: string;
  comments: string;
}

/**
 * Creates an editable draft from the API response.
 */
export function toAccountVerificationRequestDraft(
  request: AccountVerificationRequest,
): AccountVerificationRequestDraft {
  return {
    name: request.name,
    email: request.email,
    institute: request.institute,
    country: request.country,
    comments: request.comments ?? '',
  };
}

/**
 * Creates an update payload while preserving the current request status.
 */
export function toAccountVerificationRequestUpdateRequest(
  draft: AccountVerificationRequestDraft,
  status: AccountVerificationRequestStatus,
): AccountVerificationRequestUpdateRequest {
  return {
    name: draft.name,
    email: draft.email,
    institute: draft.institute,
    country: draft.country,
    status,
    comments: draft.comments,
  };
}
