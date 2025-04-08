export interface Question {
    id: number;
    creationDate: Date; 
    questionText: string;
    category: string;
    correctAnswer: string;
    wrongAnswers: string[];
    difficulty: number;
  }


export interface QuestionFromBackend {
  id: number;
  creationDate: string; 
  questionText: string;
  category: string;
  correctAnswer: string;
  wrongAnswers: string[];
  difficulty: number;
}