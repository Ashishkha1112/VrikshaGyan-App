import { Component, OnInit } from '@angular/core';
import { PlantService } from '../../../service/plant.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
declare var $: any;

@Component({
  selector: 'app-viewplant',
  templateUrl: './viewplant.component.html',
  styleUrls: ['./viewplant.component.css']
})
export class ViewplantComponent implements OnInit {
  plants: any[] = [];
  dtOptions: DataTables.Settings = {};
  dtTrigger: Subject<any> = new Subject<any>();

  constructor(private plantService: PlantService, private router: Router) {}

  ngOnInit() {
    this.dtOptions = {
      pagingType: 'full_numbers',
      pageLength: 10,
      responsive: true
    };
    this.plantService.getPlants().subscribe((data: any[]) => {
      console.log(data);
      this.plants = data;
      this.dtTrigger.next(null);
    });
  }
  updatePlant(plantId: number): void{
    this.router.navigate(['/vrikshagyan/updatePlant',plantId])
  }
  deletePlant(plantId: number): void {
    this.plantService.deletePlant(plantId).subscribe(
      () => {
        alert('plant deleted successfully');
        this.router.navigate(['/vrikshagyan/deletedplant']);
      },
      error => {
        console.error('Failed to delete plant:', error);
        // Handle error appropriately
      }
    );
  }
}
