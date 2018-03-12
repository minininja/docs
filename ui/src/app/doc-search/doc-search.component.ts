import {Component, Injectable, OnInit, Pipe} from '@angular/core';
import {DataService} from "../services/data.service";

@Pipe({
  name: 'prettyprint'
})
export class PrettyPrintPipe {
  transform(val) {
    return JSON.stringify(val, null, 2)
      .replace(/ /g, '&nbsp;')
      .replace(/\n/g, '<br/>');
  }
}

@Component({
  selector: 'app-doc-search',
  templateUrl: './doc-search.component.html',
  styleUrls: ['./doc-search.component.css']
})
@Injectable()
export class DocSearchComponent implements OnInit {

  ds: DataService;
  results: any;

  constructor(ds: DataService) {
    this.ds = ds;
  }

  url(hit: any) {
    return "http://localhost:8082/v1/storage/" + hit._source.bucket + "/" + hit._source.fileId + "/" + hit._source.fields.originalFilename;
  }

  value(hit: any, field: string) {
    return hit._source.fields[field];
  }

  show(hit: any) {
    if (hit.show === null || !hit.show) {
      console.log("showing row");
      hit.show = true;
    }
    else {
      console.log("hiding row");
      hit.show = false;
    }
  }

  doSearch(search: string) {
    console.log("Searching for " + search);
    this.ds.get("/mfc/_search?q=" + search).subscribe(
      data => {
        this.results = data;
      }
    )
  }

  updateDocument(hit: any) {
    console.log("Updating document: " + hit._id);
    this.ds.post("/mfc/_doc/" + hit._id + "/_update", hit._source).subscribe(
      data => {
        console.log("Updated record");
      }
    )
  }

  ngOnInit() {
  }

}
