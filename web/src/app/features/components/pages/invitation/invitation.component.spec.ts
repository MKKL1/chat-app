import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvitationComponent } from './invitation.component';
import {provideRouter} from "@angular/router";
import {CommunityQuery} from "../../../store/community/community.query";
import {CommunityService} from "../../../services/community.service";
import {of} from "rxjs";

// todo finished working on this component before testing

const communityServiceMock = {
  fetchCommunity: jest.fn(),
  acceptInvitation: jest.fn().mockReturnValue(of({}))
}

const communityQueryMock = {
  getActive: jest.fn().mockReturnValue({ name: 'Test Community', ownerId: 'owner123' })
};

describe('InvitationComponent', () => {
  let component: InvitationComponent;
  let fixture: ComponentFixture<InvitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvitationComponent],
      providers: [
        provideRouter([]),
        {provide: CommunityService, useValue: communityServiceMock},
        {provide: CommunityQuery, useValue: communityQueryMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
