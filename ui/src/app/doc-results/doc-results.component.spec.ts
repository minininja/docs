import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DocResultsComponent} from './doc-results.component';

describe('DocResultsComponent', () => {
  let component: DocResultsComponent;
  let fixture: ComponentFixture<DocResultsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DocResultsComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
