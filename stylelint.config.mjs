/** @type {import("stylelint").Config} */
export default {
  extends: ['stylelint-config-standard-scss'],
  rules: {
    'no-empty-source': null,
    'selector-pseudo-element-no-unknown': [
      true,
      {
        ignorePseudoElements: ['ng-deep'],
      },
    ],
  },
};
