/*
  Think about various test scenarios
  For now probably all tests require logging into user account
 */


import {environment} from "../../src/environment";

describe('opening website', () => {
  let properlyLoggedIn = false;
  let domainUrl = environment.domain;
  let authUrl = environment.keycloackUrl;

  beforeEach(() => {
    cy.visit(domainUrl);

    // going to keycloak authorization page
    cy.get('button').first().click();
    //cy.url().should('include','http://localhost:8082/realms/szampchat/protocol/openid-connect/auth');

    // for now I assume that user with username makima and password makima is in database
    // I will change it later so test would work with empty database
    // logging onto account
    // must be executed in origin wrapper to work
    cy.origin(authUrl, () => cy.get('#username').type('makima'));
    cy.origin(authUrl, () => cy.get('#password').type('makima'));
    cy.origin(authUrl, () => cy.get('#kc-login').click());

    if(cy.url().should('eq', domainUrl + 'app/communities')){
      properlyLoggedIn = true;
      cy.log("User logged into app");
    } else {
      properlyLoggedIn = false;
      cy.log("User is not properly logged in into app");
      cy.end();
    }
  });

  // it('should visit szampchat webapp', () => {
  //   // going to registration page
  //   //cy.contains('Register').click();
  // });

  it('should add new community', () => {
    cy.contains('add').click();
    cy.get('#mat-input-0').type('Cypress test community');
    cy.get('button[type="submit"]').click();
  });
});
