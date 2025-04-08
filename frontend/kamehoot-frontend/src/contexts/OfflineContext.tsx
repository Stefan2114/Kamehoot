// src/contexts/OfflineContext.tsx

import React, { createContext, useContext, useEffect, useState } from "react";
import {
  offlineService,
  ConnectionState,
  PendingOperation,
} from "../services/OfflineService";

interface OfflineContextType {
  connectionState: ConnectionState;
  pendingOperationsCount: number;
  forceSync: () => Promise<void>;
}

const OfflineContext = createContext<OfflineContextType>({
  connectionState: ConnectionState.ONLINE,
  pendingOperationsCount: 0,
  forceSync: async () => {},
});

export const useOffline = () => useContext(OfflineContext);

export const OfflineProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [connectionState, setConnectionState] = useState<ConnectionState>(
    offlineService.getConnectionState()
  );
  const [pendingOperationsCount, setPendingOperationsCount] = useState<number>(
    offlineService.getPendingOperationsCount()
  );

  useEffect(() => {
    // Listen for connection state changes
    const unsubscribe = offlineService.addConnectionListener((state) => {
      setConnectionState(state);
      // Also update pending operations count when connection state changes
      setPendingOperationsCount(offlineService.getPendingOperationsCount());
    });

    // Set up a periodic check for pending operations count
    const intervalId = setInterval(() => {
      setPendingOperationsCount(offlineService.getPendingOperationsCount());
    }, 5000);

    return () => {
      unsubscribe();
      clearInterval(intervalId);
    };
  }, []);

  const forceSync = async () => {
    await offlineService.forceSync();
    setPendingOperationsCount(offlineService.getPendingOperationsCount());
  };

  return (
    <OfflineContext.Provider
      value={{ connectionState, pendingOperationsCount, forceSync }}
    >
      {children}
    </OfflineContext.Provider>
  );
};
