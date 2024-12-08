describe('Testing performing operations on messages', () => {
  const communityName = 'Test';

  before(() => {
    cy.createCommunity(communityName);
  });

  after(() => {
    cy.deleteCommunity(communityName);
  });

  it('Should test message', () => {
    // navigating to text channel
    cy.get('h3').first().click();
    cy.get('h1').first().should('have.text', communityName);
    cy.get('mat-icon[routerlink="/app/text"]').click();
    cy.url().should('include', '/app/text');

    const name = 'Test channel';
    cy.createChannel(name, 'Text');
    cy.get('mat-list-item').first().click();

    // testing creating basic message
    const message = 'Hello world';
    cy.createMessage(message);

    // checking if message was added
    cy.performOnLastMessage(() => {
      cy.get('span.username').should('have.text', 'cypress-test-user');
      cy.get('p.message-text').should('have.text', message);
    });

    // creating message with gif
    cy.get('mat-icon[fonticon="gif"]').click();
    cy.get('input[placeholder="Search for gif..."]').type("cat");
    cy.get('input[placeholder="Search for gif..."]').type('{enter}');
    cy.get('div.gifs img').should('have.length.at.least', 1).then(($gifs) => {
      const count = $gifs.length;
      const randomIndex = Math.floor(Math.random() * count);
      cy.wrap($gifs[randomIndex]).click();
    });
    cy.get('div.gif-container img').should('be.visible');
    const messageWithGif = 'Message with gif';
    cy.get('input[placeholder="Write something here..."]').type(messageWithGif);
    cy.get('mat-icon[fonticon="send"]').click();
    cy.performOnLastMessage(() => {
      cy.get('div.gif-wrapper img')
        .should('be.visible')
        .should('exist');
    });

    // editing message
    const messageToEdit = 'Message to edit';
    cy.createMessage(messageToEdit);
    cy.performOnLastMessage(() => {
      cy.showBottomSheet();
    });

    cy.clickBottomSheetEl(2);
    cy.get('textarea').type(' (edited)');
    cy.get('mat-dialog-actions button').eq(1).should('be.visible');
    cy.get('mat-dialog-actions button').eq(1).click();

    cy.performOnLastMessage(() => {
      cy.get('span.username').should('have.text', 'cypress-test-user');
      cy.get('p.message-text').should('have.text', messageToEdit + " (edited)");
    });

    // delete message
    const messageToDelete = 'Message to delete';
    cy.createMessage(messageToDelete);
    cy.performOnLastMessage(() => {
      cy.get('span.username').should('have.text', 'cypress-test-user');
      cy.get('p.message-text').should('have.text', messageToDelete);
      cy.showBottomSheet();
    });

    // of course, it can't be clicked because last element in list is somehow different??
    cy.get('mat-list-item').parents().first().invoke('css', 'overflow', 'visible');
    cy.get('div').contains('Delete this message').click({force: true});
    cy.get('mat-dialog-actions').find('button').eq(1).click();
    cy.get('div.messages')
      .find('app-message')
      .should('have.length', 3);

    cy.deleteChannel();
  });
});
