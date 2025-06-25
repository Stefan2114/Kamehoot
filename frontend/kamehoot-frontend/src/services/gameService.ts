import { ApiService } from './apiService';
import { GameSessionDTO, CreateGameRequest, SubmitAnswerRequest } from '../types/game';

export class GameService {
  static async createGame(request: CreateGameRequest): Promise<GameSessionDTO> {
    return ApiService.post<GameSessionDTO>('/api/games/create', request);
  }

  static async joinGame(gameCode: string): Promise<GameSessionDTO> {
    return ApiService.post<GameSessionDTO>(`/api/games/join/${gameCode}`);
  }

  static async startGame(gameCode: string): Promise<void> {
    return ApiService.post<void>(`/api/games/start/${gameCode}`);
  }

  static async submitAnswer(request: SubmitAnswerRequest): Promise<void> {
    return ApiService.post<void>('/api/games/answer', request);
  }

  static async nextQuestion(gameCode: string): Promise<void> {
    return ApiService.post<void>(`/api/games/next/${gameCode}`);
  }

  static async getGameSession(gameCode: string): Promise<GameSessionDTO> {
  return ApiService.get<GameSessionDTO>(`/api/games/${gameCode}`);
}
}