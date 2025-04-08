// src/components/ConnectionStatus.tsx

import React from "react";
import { useOffline } from "../contexts/OfflineContext";
import { ConnectionState } from "../services/OfflineService";
import styles from "../styles/ConnectionStatus.module.css";

const ConnectionStatus: React.FC = () => {
  const { connectionState, pendingOperationsCount, forceSync } = useOffline();

  const getStatusText = () => {
    switch (connectionState) {
      case ConnectionState.ONLINE:
        return "Online";
      case ConnectionState.OFFLINE:
        return "Network Offline";
      case ConnectionState.SERVER_DOWN:
        return "Server Unavailable";
      default:
        return "Unknown";
    }
  };

  const getStatusClass = () => {
    switch (connectionState) {
      case ConnectionState.ONLINE:
        return styles.online;
      case ConnectionState.OFFLINE:
        return styles.offline;
      case ConnectionState.SERVER_DOWN:
        return styles.serverDown;
      default:
        return "";
    }
  };

  const handleForceSync = () => {
    if (
      connectionState === ConnectionState.ONLINE &&
      pendingOperationsCount > 0
    ) {
      forceSync();
    }
  };

  return (
    <div className={styles.container}>
      <div className={`${styles.status} ${getStatusClass()}`}>
        <div className={styles.indicator}></div>
        <div className={styles.text}>{getStatusText()}</div>
      </div>

      {pendingOperationsCount > 0 && (
        <div className={styles.pendingOperations}>
          <span>{pendingOperationsCount} pending operation(s)</span>
          {connectionState === ConnectionState.ONLINE && (
            <button className={styles.syncButton} onClick={handleForceSync}>
              Sync Now
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default ConnectionStatus;
