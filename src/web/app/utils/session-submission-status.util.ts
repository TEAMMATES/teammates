export function sessionSubmissionStatusDisplay(
  isOpened: boolean,
  isSubmitted: boolean,
  hasExtension?: boolean,
): string {
  if (!isOpened) {
    return 'Closed';
  }

  let msg = '';
  if (isSubmitted) {
    msg += 'Submitted';
  } else {
    msg += 'Pending';
  }

  if (hasExtension) {
    msg += ' (with Extension)';
  }

  return msg;
}
