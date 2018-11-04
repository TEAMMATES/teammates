/**
 * Authentication info.
 */
export interface AuthInfo {
  studentLoginUrl?: string;
  instructorLoginUrl?: string;
  adminLoginUrl?: string;
  user?: UserInfo;
  logoutUrl?: string;
}

/**
 * Logged-in user info.
 */
interface UserInfo {
  id: string;
  isStudent: boolean;
  isInstructor: boolean;
  isAdmin: boolean;
}
