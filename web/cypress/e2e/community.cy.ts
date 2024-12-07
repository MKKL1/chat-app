describe('Testing performing operations on community', () => {
  const communityName= 'Test'

  it('Should test community', () => {
    cy.url().then(url => {
      cy.log(url);
    });

    // creating community
    cy.createCommunity(communityName);

    // checking if all data is valid
    cy.get('h3').contains(communityName).click();
    cy.get('.card-value').eq(0).should('have.text', 'cypress-test-user');
    cy.get('.card-value').eq(1).should('have.text', '1');
    cy.get('.card-value').eq(2).should('have.text', '0');
    cy.get('.card-value').eq(3).should('have.text', '0');

    // editing community
    cy.get('span').contains('Edit community').should('be.visible');
    cy.get('span').contains('Edit community').click();
    cy.get('input:visible').first().type(' (edited)');
    cy.get('button[type="submit"]').first().click();
    cy.get('body').type('{esc}');
    cy.get('h1').first().should('have.text', communityName + " (edited)");

    // creating invitation
    cy.get('span').contains('Create link').should('be.visible');
    cy.get('span').contains('Create link').click();
    cy.get('input:visible').first().type('3');
    cy.get('button[type="submit"]').first().click();
    cy.get('p').contains(/^http:\/\/localhost:4200\/community\//).should('exist');
    cy.get('body').type('{esc}');

    // deleting community
    //cy.deleteCommunity(communityName);

  });
})
