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
  });

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

before(() => {
  cy.login();
});
