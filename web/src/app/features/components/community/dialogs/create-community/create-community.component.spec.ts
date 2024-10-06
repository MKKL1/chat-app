import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateCommunityComponent } from './create-community.component';
import {MatDialogRef} from "@angular/material/dialog";
import {CommunityService} from "../../../../services/community.service";
import {NoopAnimationsModule} from "@angular/platform-browser/animations";

const communityServiceMock = {
  createCommunity: jest.fn()
};

describe('CreateCommunityComponent', () => {
  let component: CreateCommunityComponent;
  let fixture: ComponentFixture<CreateCommunityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateCommunityComponent, NoopAnimationsModule],
      providers: [
        {provide: MatDialogRef, useValue: {}},
        {provide: CommunityService, useValue: communityServiceMock}
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateCommunityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
