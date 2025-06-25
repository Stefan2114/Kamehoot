import React, { createContext, useContext, useReducer, ReactNode } from "react";
import {
  GameSessionDTO,
  QuestionData,
  QuestionResults,
  GameStatus,
} from "../types/game";

interface GameState {
  gameSession: GameSessionDTO | null;
  currentQuestion: QuestionData | null;
  questionResults: QuestionResults | null;
  isHost: boolean;
  currentUser: string | null;
  showResults: boolean;
  gameEnded: boolean;
  finalResults: any | null;
}

type GameAction =
  | { type: "SET_GAME_SESSION"; payload: GameSessionDTO }
  | { type: "SET_CURRENT_QUESTION"; payload: QuestionData }
  | { type: "SET_QUESTION_RESULTS"; payload: QuestionResults }
  | { type: "SET_IS_HOST"; payload: boolean }
  | { type: "SET_CURRENT_USER"; payload: string }
  | { type: "SET_SHOW_RESULTS"; payload: boolean }
  | { type: "SET_GAME_ENDED"; payload: boolean }
  | { type: "SET_FINAL_RESULTS"; payload: any }
  | { type: "UPDATE_PLAYERS"; payload: any[] }
  | { type: "RESET_GAME" };

const initialState: GameState = {
  gameSession: null,
  currentQuestion: null,
  questionResults: null,
  isHost: false,
  currentUser: null,
  showResults: false,
  gameEnded: false,
  finalResults: null,
};

const gameReducer = (state: GameState, action: GameAction): GameState => {
  switch (action.type) {
    case "SET_GAME_SESSION":
      return { ...state, gameSession: action.payload };
    case "SET_CURRENT_QUESTION":
      return {
        ...state,
        currentQuestion: action.payload,
        showResults: false,
        questionResults: null,
      };
    case "SET_QUESTION_RESULTS":
      return { ...state, questionResults: action.payload };
    case "SET_IS_HOST":
      return { ...state, isHost: action.payload };
    case "SET_CURRENT_USER":
      return { ...state, currentUser: action.payload };
    case "SET_SHOW_RESULTS":
      return { ...state, showResults: action.payload };
    case "SET_GAME_ENDED":
      return { ...state, gameEnded: action.payload };
    case "SET_FINAL_RESULTS":
      return { ...state, finalResults: action.payload };
    case "UPDATE_PLAYERS":
      return {
        ...state,
        gameSession: state.gameSession
          ? {
              ...state.gameSession,
              players: action.payload,
            }
          : null,
      };
    case "RESET_GAME":
      return initialState;
    default:
      return state;
  }
};

const GameContext = createContext<{
  state: GameState;
  dispatch: React.Dispatch<GameAction>;
} | null>(null);

export const GameProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [state, dispatch] = useReducer(gameReducer, initialState);

  return (
    <GameContext.Provider value={{ state, dispatch }}>
      {children}
    </GameContext.Provider>
  );
};

export const useGame = () => {
  const context = useContext(GameContext);
  if (!context) {
    throw new Error("useGame must be used within a GameProvider");
  }
  return context;
};
