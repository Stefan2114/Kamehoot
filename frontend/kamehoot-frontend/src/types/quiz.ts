import { Question } from "./question";

export interface Quiz{
    id:string,
    title: string,
    description: string,
    creationDate: string,
    questions: Question[],
    maxPossibleScore: number
}


export interface QuizDTO{
    id?:string,
    title: string,
    description: string,
    creationDate: string,
    questions: Question[],
    maxPossibleScore: number
}