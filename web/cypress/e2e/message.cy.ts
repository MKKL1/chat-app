describe('Testing performing operations on messages', () => {
  it('Should test message', () => {
    // navigating to text channel
    cy.get('h3').first().click();
    cy.get('mat-icon[routerlink="/app/text"]').click();
    cy.get('mat-list-item').first().click();

    const message = 'Hello world';

    // testing sending regular message
    cy.get('input[placeholder="Write something here..."]').type(message);
    cy.get('mat-icon[fonticon="send"]').click();

    // checking if message was added
    cy.get('div.messages')
      .find('div.message-container')
      .then($messages => {
        const count = $messages.length;

      });
  })
})
