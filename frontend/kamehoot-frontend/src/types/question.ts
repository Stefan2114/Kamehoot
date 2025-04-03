export interface Question {
    id: number;
    date: Date; 
    questionText: string;
    category: string;
    correctAnswer: string;
    wrongAnswers: string[];
    difficulty: number;
  }