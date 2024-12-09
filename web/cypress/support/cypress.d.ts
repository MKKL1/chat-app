declare namespace Cypress {
  interface Chainable {
    /**
     * Custom command to login or register user
     */
    login(): Chainable<void>;
    /**
     * Custom command for creating new community with given name
     *
     * @param name - name of community
     * @example cy.createCommunity('New community')
     */
    createCommunity(name: string): Chainable<void>;
    /**
     * Custom command for deleting community
     *
     * @param name - name of community which should be deleted
     * @example cy.deleteCommunity('New community')
     */
    deleteCommunity(name: string): Chainable<void>;
    /**
     * Custom command for creating channel
     *
     * @param name - name of channel
     * @param type - type of channel (Text or Voice)
     * @xample cy.createChannel('New channel', 'Voice')
     */
    createChannel(name: string, type: string): Chainable<void>;
    /**
     *
     * Custom command for first channel
     *
     * @example cy.deleteChannel()
     */
    deleteChannel(): Chainable<void>;
    /**
     * Custom command for creating new text message
     *
     * @param text - message text
     * @example cy.createMessage('Hello world')
     */
    createMessage(text: string): Chainable<void>;
    /**
     * Custom command for performing action on last created message
     *
     * @param func - function which will perform on last message
     * @example cy.performOnLastMessage(() => {
     *   cy.get('div.gif-wrapper img')
     *     .should('be.visible')
     * });
     */
    performOnLastMessage(func: () => void): Chainable<void>;
    /**
     * Custom command for opening bottom sheet in messages view
     *
     *  @xample cy.showBottomSheet()
     */
    showBottomSheet(): Chainable<void>;
    /**
     * Custom command for clicking one of bottom sheet elements
     *
     * @param elIndex - index of element which should be clicked
     * @example cy.clickBottomSheetEl(0)
     */
    clickBottomSheetEl(elIndex: number): Chainable<void>;
  }
}
