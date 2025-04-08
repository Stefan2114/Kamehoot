// src/components/Layout.tsx

import React from "react";
import { Outlet } from "react-router-dom";
import ConnectionStatus from "./ConnectionStatus";

const Layout: React.FC = () => {
  return (
    <div>
      <ConnectionStatus />
      <Outlet />
    </div>
  );
};

export default Layout;
