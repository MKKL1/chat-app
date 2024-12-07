declare namespace Cypress {
  interface Chainable {
    /**
     * Custom command to login user
     */
    login(): Chainable<void>;
    createCommunity(name: string): Chainable<void>;
    deleteCommunity(name: string): Chainable<void>;
    createChannel(name: string, type: string): Chainable<void>;
  }
}
