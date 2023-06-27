import {SafeUrl} from "@angular/platform-browser"

export interface PuzzlePiece {
    file:File,
    url:SafeUrl,
    rotation:number,
    contentType:string,
    name:string,
    position:number,
    currentPosition:number
}
