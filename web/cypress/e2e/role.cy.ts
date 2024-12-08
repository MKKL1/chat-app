describe('Testing performing operations on roles', () => {
  it('Should test roles', () => {
    const roleName = "Test role";
    const username = "cypress-test-user";

    // creating role
    cy.get('h3').first().click();
    cy.get('span').contains('Roles').click();
    cy.get('span').contains('Add new role').click();
    cy.get('input').first().type(roleName);
    cy.get('div.p-selectbutton.p-buttonset.p-component').each(($el, index, $list) => {
      cy.wrap($el)
        .find('div')
        .its('length')
        .then((buttonCount) => {
          const randomIndex = Math.floor(Math.random() * buttonCount);
          if(randomIndex !== 1){
            cy.wrap($el)
              .find('div')
              .eq(randomIndex)
              .click();
          }
        });
    });
    cy.get('span').contains('Create').click();

    // checking if role was added
    cy.get('div.role p').should('have.text', roleName);

    // assigning member to role
    cy.get('mat-icon').contains('manage_accounts').first().click();
    cy.get('h2').first().should('have.text', `Manage role ${roleName}`);
    cy.get('mat-select').click();
    cy.get('mat-option').first().click();
    // clicking to times to lose focus from mat-select
    cy.get('body').click();
    cy.get('span').contains('Add role to members').click();

    //cy.get('mat-card-content p').should('be.visible').should('have.text', username);
    cy.get('span').contains('Save changes').click();
    cy.get('span').contains('Members').click();
    cy.get('mat-card-title span')
      .should('be.visible')
      .should('have.text', username);
    cy.get('mat-chip').first()
      .find('span').eq(1)
      .get('span span span')
      .should('be.visible')
      .should('have.text', roleName);

    // removing role from members
    cy.get('span').contains('Roles').click();
    cy.get('mat-icon').contains('manage_accounts').first().click();
    cy.get('mat-card-content button').should('be.visible');
    cy.get('mat-icon').contains('person_off').click();
    cy.get('span').contains('Save changes').click();
    cy.get('span').contains('Members').click();
    cy.get('mat-card-title span')
      .should('be.visible')
      .should('have.text', username);
    cy.get('mat-chip').should('not.exist');

    // editing role
    cy.get('span').contains('Roles').click();
    cy.get('mat-icon').contains('edit_note').first().click();
    cy.get('input').first().type(' (edited)');
    cy.get('span').contains('Edit').click();
    cy.get('div.role p').should('have.text', roleName + " (edited)");

    // delete role
    cy.get('mat-icon').contains('delete').first().click();
    cy.get('div.p-dialog-footer').find('button').eq(1).click();
    cy.get('div.role').should('not.exist');
  })
})
