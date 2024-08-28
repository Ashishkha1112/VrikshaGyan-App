import { Component, OnInit } from '@angular/core';
import { PlantService } from '../../../service/plant.service';
import { Subject } from 'rxjs';


@Component({
  selector: 'app-deletedplant',
  templateUrl: './deletedplant.component.html',
  styleUrl: './deletedplant.component.css'
})
export class DeletedplantComponent implements OnInit {

  plants: any[] = [];
  dtOptions: DataTables.Settings = {};
  dtTrigger: Subject<any> = new Subject<any>();
  constructor(private plantService: PlantService) {}

  ngOnInit() {
    this.dtOptions = {
      pagingType: 'full_numbers',
      pageLength: 10,
      responsive: true
    };
    this.plantService.getDeletedPlants().subscribe((data: any[]) => {
      this.plants = data;
      this.dtTrigger.next(null);
    });
  }
}
