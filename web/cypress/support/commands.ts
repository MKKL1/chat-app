import {environment} from "../../src/environment";

let domainUrl = environment.domain;
let authUrl = environment.keycloackUrl;

Cypress.Commands.add('login', () => {
  cy.visit(domainUrl);
  cy.contains('button', 'Get started').click();

  cy.url().should('include','http://localhost:8082/realms/szampchat/protocol/openid-connect/auth');

  cy.origin(authUrl, () => {
    cy.fixture('register.json').then((register) => {
      cy.get('#username').type(register.username)
      cy.get('#password').type(register.password);
      cy.get('#kc-login').click();
    })
  }
);

  cy.url().then(url => {
    if(!url.startsWith('http://localhost:4200')){
      cy.origin(authUrl, () => {
        cy.fixture('register.json').then((register) => {
          cy.contains('a', 'Register').click();
          cy.get('#username').type(register.username);
          cy.get('#password').type(register.password);
          cy.get('#password-confirm').type(register.password);
          cy.get('#email').type(register.email);
          cy.get('#firstName').type(register.firstName);
          cy.get('#lastName').type(register.lastName);
          cy.get('input[type="submit"]').click();
        })
      });
    }
  })
});

Cypress.Commands.add('createCommunity', (name: string) => {
  cy.get('button.add-button').click();
  cy.get('#mat-input-0').type(name);
  cy.contains('button', 'Create').click();
  cy.get('h3').contains(name).should('be.visible');
});

Cypress.Commands.add('deleteCommunity', (name: string) => {
  cy.get('mat-icon[routerlink="/app/communities"]').click()
  cy.get('h3').contains(name).click();
  cy.get('span').contains('Delete community').should('be.visible');
  cy.get('span').contains('Delete community').click();
  cy.get('span.p-button-label').contains('Yes').click();
  cy.get('h3').should('not.exist');
});

Cypress.Commands.add('createChannel', (name: string, type: string) => {
  // creating new channel
  cy.get('span').contains('Add new channel').should('be.visible');
  cy.get('span').contains('Add new channel').click();
  cy.get('input:visible').first().type(name);

  // setting channel type
  cy.get('mat-select[formcontrolname="type"]').click();
  cy.get('.mat-mdc-option').contains(type).click();

  cy.get('span').contains('Create').should('be.visible');
  cy.get('span').contains('Create').click();
});

Cypress.Commands.add('deleteChannel', () => {
  cy.get('button').find('mat-icon[fonticon="edit"]').click();
  cy.get('mat-dialog-actions').find('button').eq(2).click();
});

Cypress.Commands.add('createMessage', (text: string) => {
  // testing sending regular message
  cy.get('input[placeholder="Write something here..."]').type(text);
  cy.get('mat-icon[fonticon="send"]').click();
});

Cypress.Commands.add('performOnLastMessage', (func) => {
  cy.get('div.messages')
    .find('div.message-container')
    .last()
    .within(() => {
      func();
    });
})

Cypress.Commands.add('showBottomSheet', () => {
  cy.get('div.message-menu').invoke('show');
  cy.get('mat-icon').contains('more_vert').click();
});

Cypress.Commands.add('clickBottomSheetEl', (elIndex: number) => {
  // this really don't want to be clicked
  cy.get('div.bottom-sheet').should('have.class', 'opened');
  cy.get('div.bottom-sheet.opened div mat-list').should('be.visible');
  cy.get('div.bottom-sheet.opened div mat-list mat-list-item').eq(elIndex).should('exist');
  cy.get('div.bottom-sheet.opened div mat-list mat-list-item').eq(elIndex).click();
});

before(() => {
  cy.login();
});
