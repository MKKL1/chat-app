describe('Testing performing operations on voice channel', () => {
  const communityName = 'Test (edited)';
  it('Should test voice channel', () => {
    cy.url().then(url => {
      cy.log(url);
    });

    // moving to voice channels tab
    cy.get('h3').first().click();
    cy.get('h1').first().should('have.text', communityName);
    cy.get('mat-icon[routerlink="/app/voice"]').click();
    cy.url().should('include', '/app/voice');

    // creating voice channel
    const name = 'voice channel';
    cy.createChannel(name, 'Voice');
    cy.get('span').contains(name).should('be.visible');
    cy.get('span').contains(name).click();
    cy.get('h3').first()
      .should('be.visible')
      .should('have.text', name);

    // deleting channel
    cy.deleteChannel();
  })
})
