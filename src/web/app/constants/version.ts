/**
 * Single source of truth for the application version.
 *
 * This value is hardcoded to prevent inconsistencies caused by
 * user-configurable settings. It is used across the application
 * (e.g., in the footer) to display the current version.
 *
 * This also prepares the system for future features such as
 * automated update checks.
 */
export const APP_VERSION = '8.0.0';