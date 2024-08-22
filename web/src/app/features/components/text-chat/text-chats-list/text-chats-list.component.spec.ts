import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextChatsListComponent } from './text-chats-list.component';

describe('TextChatsListComponent', () => {
  let component: TextChatsListComponent;
  let fixture: ComponentFixture<TextChatsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TextChatsListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TextChatsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
