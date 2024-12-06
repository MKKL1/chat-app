declare namespace Cypress {
  interface Chainable {
    /**
     * Custom command to login user
     */
    login(): Chainable<void>;
  }
}
