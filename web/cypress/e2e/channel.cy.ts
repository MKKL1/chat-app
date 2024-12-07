const communityName = 'Test';

describe('Testing performing operations on text channel', () => {
  it('Should test channel', () => {
    cy.url().then(url => {
      cy.log(url);
    });

    // moving to text channels tab
    cy.get('h3').first().click();
    cy.get('mat-icon[routerlink="/app/text"]').click();

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
    // cy.get('button').find('mat-icon[fonticon="edit"]').click();
    // cy.get('span').contains('Delete').should('be.visible');
    // cy.get('span').contains('Delete').click();
  })
})
