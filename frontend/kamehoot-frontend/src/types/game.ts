export interface GameSessionDTO {
  id: string;
  gameCode: string;
  status: GameStatus;
  quizTitle: string;
  currentQuestionIndex: number;
  totalQuestions: number;
  players: PlayerDTO[];
  createdAt: string;
}

export interface PlayerDTO {
  username: string;
  score: number;
  isHost: boolean;
}

export enum GameStatus {
  WAITING = 'WAITING',
  IN_PROGRESS = 'IN_PROGRESS',
  FINISHED = 'FINISHED'
}

export interface CreateGameRequest {
  quizId: string;
  questionTimeLimit: number;
}

export interface SubmitAnswerRequest {
  gameSessionId: string;
  questionId: string;
  answer: string;
}

export interface GameEvent {
  type: GameEventType;
  [key: string]: any;
}

export enum GameEventType {
  PLAYER_JOINED = 'PLAYER_JOINED',
  QUESTION_STARTED = 'QUESTION_STARTED',
  QUESTION_RESULTS = 'QUESTION_RESULTS',
  GAME_ENDED = 'GAME_ENDED',
  PLAYER_ANSWERED = 'PLAYER_ANSWERED'
}

export interface QuestionData {
  questionId: string;
  questionText: string;
  options: string[];
  questionNumber: number;
  totalQuestions: number;
  timeLimit: number;
}

export interface QuestionResults {
  correctAnswer: string;
  playerResults: PlayerResult[];
  leaderboard: LeaderboardEntry[];
}

export interface PlayerResult {
  username: string;
  correct: boolean;
  points: number;
  totalScore: number;
}

export interface LeaderboardEntry {
  username: string;
  score: number;
  position: number;
}