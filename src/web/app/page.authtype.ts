export enum PageAuthType {
  /**
   * Page is accessible to all users, including unauthenticated users.
   */
  PUBLIC,

  /**
   * Page is accessible to all authenticated users, regardless of their roles.
   */
  AUTHENTICATED,

  /**
   * Page is accessible only to authenticated users with specific roles (e.g., instructor, student, admin, maintainer).
   */
  ROLE_BASED_AUTHENTICATED
}
