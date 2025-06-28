export interface Question {
  id: string; 
  creationDate: string;
  questionText: string;
  category: string;
  correctAnswer: string;
  wrongAnswers: string[];
  difficulty: number;
}

export interface QuestionDTO {
  id?: string;
  creationDate: string;
  questionText: string;
  category: string;
  correctAnswer: string;
  wrongAnswers: string[];
  difficulty: number;
}