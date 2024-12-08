describe('Testing performing operations on text channel', () => {
  const communityName = 'Test (edited)';

  it('Should test channel', () => {
    cy.url().then(url => {
      cy.log(url);
    });

    // moving to text channels tab
    cy.get('h3').first().click();
    cy.get('h1').first().should('have.text', communityName);
    cy.get('mat-icon[routerlink="/app/text"]').click();
    cy.url().should('include', '/app/text');

    const name = 'Test channel';
    cy.createChannel(name, 'Text');

    cy.get('mat-list').find('div').should('have.length.greaterThan', 1)

    // selecting channel
    cy.get('span').contains(name).should('be.visible');
    cy.get('span').contains(name).click();
    cy.get('div.messages').should('be.visible');

    // editing channel
    cy.get('button').find('mat-icon[fonticon="edit"]').click();
    cy.get('input[formcontrolname="name"]').type(' (edited)');
    cy.get('span').contains('Edit').should('be.visible');
    cy.get('span').contains('Edit').click();
    cy.get('body').type('{esc}');
    cy.get('span').contains(name + " (e...").should('be.visible');

    // deleting channel
    cy.deleteChannel();
  })
})
