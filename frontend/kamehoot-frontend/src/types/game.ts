export interface GameSessionDTO {
  id: string;
  status: string;
  quizTitle: string;
  currentQuestionIndex: number;
  totalQuestions: number;
}

export interface PlayerDTO {
  username: string;
  totalScore: number;
  hasAnswered: boolean;
}


export interface CreateGameRequest {
  quizId: string;
  questionTimeLimit: number;
}

export interface SubmitAnswerRequest {
  gameSessionId: string;
  questionId: string;
  answer: string;
  answerTime: string;
}

export interface GameQuestionDTO{
   questionId: string;
   questionText: string;
   options: string[];
   questionNumber: number;
   timeLimit: number;
}

export interface WebSocketDTO{
  type: string,
  gameSessionStatus: string,
  info:  GameQuestionDTO | number | PlayerDTO[] | string
}