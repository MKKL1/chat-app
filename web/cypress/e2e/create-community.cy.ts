describe('Testing performing operations on community', () => {
  it('Should create community', () => {
    cy.url().then(url => {
      cy.log(url);
    });

    const communityName= 'Test community'

    cy.get('button.add-button').click();
    cy.get('#mat-input-0').type(communityName);
    cy.contains('button', 'Create').click();
    cy.get('h3').contains(communityName).should('be.visible');
  });

  // it('Should edit community', () => {
  //
  // });
  //
  // it('Should delete community', () => {
  //
  // });
})
