import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateInvitationComponent } from './generate-invitation.component';

describe('GenerateInvitationComponent', () => {
  let component: GenerateInvitationComponent;
  let fixture: ComponentFixture<GenerateInvitationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateInvitationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GenerateInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
