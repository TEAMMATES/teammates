/**
 * Contains some configuration values required to build and run the web application.
 */
export const config: any = {
  /**
   * The application version.
   */
  version: '8.0.0',

  /**
   * The URL of page to be loaded for the account request page.
   */
  accountRequestFormUrl: '',

  /**
   * The support email shown to the user in various pages of the web application.
   */
  supportEmail: 'teammates@comp.nus.edu.sg',

  /**
   * The public site key for the reCAPTCHA on the recover session links page.
   * You can get a pair of keys from the Google reCAPTCHA website.
   * e.g. "6LeIxAcTAAAAAJcZVRqyHh71UMIEGNQ_MXjiZKhI" is a site key for test environments.
   */
  captchaSiteKey: '',

  /**
   * This flag determines if the system is in maintenance mode.
   * Under maintenance mode, all requests to the front-end will be routed to the "under maintenance" page.
   */
  maintenance: false,

  /**
   * Set to true if Firebase login is to be supported.
   * Note that the backend needs to be configured separately for Firebase login to be fully supported.
   */
  allowFirebaseLogin: false,

  firebaseConfig: {},

};
