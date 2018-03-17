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
    return "/storage/v1/storage/" + hit._source.bucket + "/" + hit._source.fileId + "/" + hit._source.fields.originalFilename;
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
    this.ds.get("/es/mfc/_search?q=" + search).subscribe(
      data => {
        this.results = data;
      }
    )
  }

  updateDocument(hit: any) {
    console.log("Updating document: " + hit._id);

    let doc = hit._source;

    var tags = [];
    for (let t of doc.tags) {
      if (t instanceof Object) {
        tags.push(t.value);
      }
      else {
        tags.push(t);
      }
    }
    if (null == tags.filter(x => x == "indexed")[0]) {
      tags.push("indexed");
    }
    doc.tags = tags;

    var posted = {
      "doc": doc,
      "doc_as_upsert": true
    };

    this.ds.post("/es/mfc/mfc/" + hit._id + "/_update", 'application/json', posted).subscribe(
      data => {
        console.log("Updated record");
      }
    )
  }

  ngOnInit() {
  }

}
