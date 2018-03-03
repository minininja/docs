import {Component, Injectable, OnInit} from '@angular/core';
import {DataService} from "../services/data.service";

@Component({
  selector: 'app-show-docs',
  templateUrl: './show-docs.component.html',
  styleUrls: ['./show-docs.component.css']
})
@Injectable()
export class ShowDocsComponent implements OnInit {

  ds: DataService;
  indices: any;

  constructor(ds: DataService) {
    this.ds = ds;

  }

  ngOnInit() {
    this.ds.get("/_cat/indices?v").then(() =>
      console.log("indices: " + this.indices);

  )


  }

}
