import { Component, Input } from '@angular/core';
import { PuzzlePiece } from '../puzzle-piece';
import { PuzzleService } from '../puzzle.service';

@Component({
  selector: 'app-puzzle',
  templateUrl: './puzzle.component.html',
  styleUrls: ['./puzzle.component.css']
})
export class PuzzleComponent {
  @Input() piece!: PuzzlePiece;

  constructor(private puzzleService:PuzzleService){

  }

  rotateImage(image:PuzzlePiece){
    image.rotation++;
    this.puzzleService.isValid();
  }

  getRotation(image:PuzzlePiece){
    return 'rotate(' + (image.rotation*90) + 'deg)';
  }
  
}
