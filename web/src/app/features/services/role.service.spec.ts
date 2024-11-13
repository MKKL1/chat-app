import { TestBed } from '@angular/core/testing';

import { RoleService } from './role.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {CommunityQuery} from "../store/community/community.query";
import {KeycloakService} from "keycloak-angular";

const communityQueryMock = {
  getActiveId: jest.fn()
};

const mockKeycloakService = {
  isLoggedIn: jest.fn().mockReturnValue(true),
  getKeycloakInstance: jest.fn().mockReturnValue({
    idToken: 'mock-id-token', // Provide a mock token
  }),
};

describe('RoleService', () => {
  let service: RoleService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {provide: CommunityQuery, useValue: communityQueryMock},
        { provide: KeycloakService, useValue: mockKeycloakService },
      ]
    });
    service = TestBed.inject(RoleService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
