import { Component, OnInit } from '@angular/core';
import { PlantService } from '../../../service/plant.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
@Component({
  selector: 'app-nonverifiedplant',
  templateUrl: './nonverifiedplant.component.html',
  styleUrl: './nonverifiedplant.component.css'
})
export class NonverifiedplantComponent implements OnInit {

  plants: any[] = [];
  dtOptions: DataTables.Settings = {};
  dtTrigger: Subject<any> = new Subject<any>();
constructor(private plantService: PlantService, private router: Router) {}
ngOnInit(): void {
  this.dtOptions = {
    pagingType: 'full_numbers',
    pageLength: 10,
    responsive: true
  };
  this.plantService.getUnVerifiedPlants().subscribe((data: any[]) =>{
    console.log(data);
    this.plants = data;
    this.dtTrigger.next(null);
  });
}
verifyPlant(plantId: number): void {
  this.plantService.verifyPlant(plantId).subscribe(
    () => {
      alert('plant verified successfully');
      this.router.navigate(['/vrikshagyan/verifiedplant']);
    },
    error => {
      console.error('Failed to delete plant:', error);
      // Handle error appropriately
    }
  );
}

viewplant(plantId: number): void{
  this.router.navigate(['/vrikshagyan/updatePlant',plantId])
}
}

