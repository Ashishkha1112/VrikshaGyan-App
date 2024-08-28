import { Component, OnInit } from '@angular/core';
import { PlantService } from '../../../service/plant.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-verifiedplant',
  templateUrl: './verifiedplant.component.html',
  styleUrl: './verifiedplant.component.css'
})
export class VerifiedplantComponent implements OnInit {
  
plants: any[] = [];
dtOptions: DataTables.Settings = {};
dtTrigger: Subject<any> = new Subject<any>();

constructor(private plantService: PlantService) {}
ngOnInit(): void {
  this.dtOptions = {
    pagingType: 'full_numbers',
    pageLength: 10,
    responsive: true
  };

  this.plantService.getVerifiedPlants().subscribe((data: any[]) =>{
    console.log(data);
    this.plants = data;
    this.dtTrigger.next(null);
  });
}
}
