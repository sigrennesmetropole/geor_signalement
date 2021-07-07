import {waitForAsync, ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkflowComponent} from './workflow.component';
import {AppModule} from '../app.module';

describe('WorkflowComponent', () => {
  let component: WorkflowComponent;
  let fixture: ComponentFixture<WorkflowComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [WorkflowComponent],
      imports: [
        AppModule,
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
