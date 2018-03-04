import {Component, Injectable, OnInit} from '@angular/core';
import {DataService} from "../services/data.service";

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.css'],
  providers: [],
})
@Injectable()
export class IndexComponent implements OnInit {

  ds: DataService;

  index: any;

  constructor(ds: DataService) {
    this.ds = ds;
  }

  ngOnInit() {
    console.log("loading indicies");
    this.ds.get("/mfc/_stats").subscribe(
      data => {
        this.index = data;
      }
    )
  }

}
