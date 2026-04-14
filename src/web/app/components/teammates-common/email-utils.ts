/**
 * Normalizes an email by trimming whitespace and lowercasing it.
 */
export function normalizeEmail(email: string | null | undefined): string {
  return (email ?? '').trim().toLowerCase();
}

/**
 * Returns true if two emails are equal after normalization.
 */
export function areEmailsEqual(firstEmail: string | null | undefined, secondEmail: string | null | undefined): boolean {
  return normalizeEmail(firstEmail) === normalizeEmail(secondEmail);
}
