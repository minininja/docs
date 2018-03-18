import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-doc-results',
  templateUrl: './doc-results.component.html',
  styleUrls: ['./doc-results.component.css']
})
export class DocResultsComponent implements OnInit {

  constructor() {
  }

  @Input() hits: any;

  records() {
    return this.hits.hits.hits;
  }

  document(hit) {
    return hit._source;
  }

  url(hit) {
    return "/storage/v1/storage/" + hit._source.field['bucket'] + "/" + hit._source.field['fileId'];
  }

  ngOnInit() {
  }

}
