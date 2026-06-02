export function sessionSubmissionStatusDisplay(
  isOpened: boolean,
  isWaitingToOpen: boolean,
  isSubmitted: boolean,
  hasExtension?: boolean,
): string {
  if (isWaitingToOpen) {
    return 'Awaiting';
  }

  if (!isOpened) {
    return 'Closed';
  }

  let msg = '';
  if (isOpened) {
    if (isSubmitted) {
      msg += 'Submitted';
    } else {
      msg += 'Pending';
    }
  }

  if (hasExtension) {
    msg += ' (with Extension)';
  }

  return msg;
}
