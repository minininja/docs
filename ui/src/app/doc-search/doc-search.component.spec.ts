import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {DocSearchComponent} from './doc-search.component';

describe('DocSearchComponent', () => {
  let component: DocSearchComponent;
  let fixture: ComponentFixture<DocSearchComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DocSearchComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DocSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
