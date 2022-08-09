import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContextMapDialog } from './context-map-dialog.component';

describe('ContextMapDialogComponent', () => {
  let component: ContextMapDialog;
  let fixture: ComponentFixture<ContextMapDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContextMapDialog ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContextMapDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
