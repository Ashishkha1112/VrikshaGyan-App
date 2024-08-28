import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component } from '@angular/core';
import { PlantService } from '../../../service/plant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-addplantbulk',
  templateUrl: './addplantbulk.component.html',
  styleUrls: ['./addplantbulk.component.css'] // Make sure this points to the correct CSS file
})
export class AddplantbulkComponent {
  tableData: string[][] = [];
  selectedCSVFile: File | null = null;
  selectedImageFile: File | null = null;
  errorMessage: string | null = null; // To display error messages in the template

  constructor(private plantService: PlantService, private router : Router) {}

  onCSVFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedCSVFile = input.files ? input.files[0] : null;

    if (this.selectedCSVFile && this.selectedCSVFile.type === 'text/csv') {
      const reader = new FileReader();
      reader.onload = () => {
        const text = reader.result as string;
        this.parseCSV(text);
      };
      reader.readAsText(this.selectedCSVFile);
    } else {
      this.errorMessage = 'Please upload a valid CSV file.';
    }
  }

  onImageFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedImageFile = input.files ? input.files[0] : null;
  }

  onUpload(event: Event): void {
    event.preventDefault();
    if (this.selectedCSVFile && this.selectedImageFile) {
      const formData = new FormData();
      formData.append('csvFile', this.selectedCSVFile);
      formData.append('imageFile', this.selectedImageFile);

      this.plantService.uploadBulkData(formData).subscribe({
        next: (response) => {
          this.errorMessage = response;
          alert('Files uploaded successfully.');
          this.router.navigate(['/vrikshagyan/viewplant']);
          this.errorMessage = null; // Clear any previous errors
        },
        error: (err: HttpErrorResponse) => {
              this.errorMessage = err.error;
          alert(console.log('File Upload Failed.'))
        }
      });
    } else {
      alert('Please select both CSV and ZIP files.');
    }
  }

  parseCSV(text: string): void {
    const rows = text.split('\n');
    this.tableData = rows.map(row => row.split(','));
  }
}
