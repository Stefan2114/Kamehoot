import { ApiService } from './apiService';
import { GameSessionDTO, CreateGameRequest, SubmitAnswerRequest } from '../types/game';

export class GameService {
  static async createGame(request: CreateGameRequest): Promise<string> {
    return ApiService.post<string>('/api/games/create', request);
  }

  static async joinGame(gameSessionId: string): Promise<void> {
    return ApiService.post<void>(`/api/games/join/${gameSessionId}`);
  }

  static async startGame(gameSessionId: string): Promise<void> {
    return ApiService.post<void>(`/api/games/start/${gameSessionId}`);
  }

  static async submitAnswer(request: SubmitAnswerRequest): Promise<void> {
    return ApiService.post<void>('/api/games/answer', request);
  }

  static async nextQuestion(gameSessionId: string): Promise<void> {
    return ApiService.post<void>(`/api/games/next/${gameSessionId}`);
  }

  static async isHost(gameSessionId: string): Promise<boolean> {
    return ApiService.get<boolean>(`/api/games/${gameSessionId}/is-host`);
  }

  static async getGameSession(gameCode: string): Promise<GameSessionDTO> {
  return ApiService.get<GameSessionDTO>(`/api/games/${gameCode}`);
}
}