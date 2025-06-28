import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import styles from "../styles/ProfilePage.module.css";

const ProfilePage: React.FC = () => {
  const [qrCodeUrl, setQrCodeUrl] = useState<string>("");
  const [secretKey, setSecretKey] = useState<string>("");
  const [verificationCode, setVerificationCode] = useState("");
  const [disableCode, setDisableCode] = useState("");
  const [showSetup, setShowSetup] = useState(false);
  const [showDisable, setShowDisable] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const { setup2FA, verify2FA, disable2FA, logout } = useAuth();
  const navigate = useNavigate();

  const handleSetup2FA = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await setup2FA();
      setQrCodeUrl(response.qrCodeUrl);
      setSecretKey(response.secretKey);
      setShowSetup(true);
    } catch (error: any) {
      setError("Failed to setup 2FA: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleVerify2FA = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      await verify2FA(parseInt(verificationCode));
      setSuccess("2FA enabled successfully!");
      setShowSetup(false);
      setVerificationCode("");
      setQrCodeUrl("");
      setSecretKey("");
    } catch (error: any) {
      setError("Invalid verification code");
    } finally {
      setLoading(false);
    }
  };

  const handleDisable2FA = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      await disable2FA(parseInt(disableCode));
      setSuccess("2FA disabled successfully!");
      setShowDisable(false);
      setDisableCode("");
    } catch (error: any) {
      setError("Invalid verification code");
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div className={styles["profile-container"]}>
      <div className={styles["profile-header"]}>
        <h2>Profile Settings</h2>
      </div>

      <div className={styles["profile-content"]}>
        <div className={styles["section"]}>
          <h3>Two-Factor Authentication</h3>

          {error && <div className={styles["error-message"]}>{error}</div>}
          {success && (
            <div className={styles["success-message"]}>{success}</div>
          )}

          {!showSetup && !showDisable && (
            <div className={styles["twofa-buttons"]}>
              <button
                onClick={handleSetup2FA}
                disabled={loading}
                className={styles["setup-button"]}
              >
                {loading ? "Setting up..." : "Enable 2FA"}
              </button>
              <button
                onClick={() => setShowDisable(true)}
                className={styles["disable-button"]}
              >
                Disable 2FA
              </button>
            </div>
          )}

          {showSetup && (
            <div className={styles["setup-section"]}>
              <h4>Setup Two-Factor Authentication</h4>
              <div className={styles["setup-steps"]}>
                <p>
                  1. Install an authenticator app (Google Authenticator, Authy,
                  etc.)
                </p>
                <p>2. Scan this QR code with your authenticator app:</p>

                {qrCodeUrl && (
                  <div className={styles["qr-code"]}>
                    <img src={qrCodeUrl} alt="2FA QR Code" />
                  </div>
                )}

                <p>3. Or manually enter this secret key:</p>
                <div className={styles["secret-key"]}>
                  <code>{secretKey}</code>
                </div>

                <p>4. Enter the 6-digit code from your authenticator app:</p>
                <form onSubmit={handleVerify2FA}>
                  <div className={styles["form-group"]}>
                    <input
                      type="text"
                      value={verificationCode}
                      onChange={(e) => setVerificationCode(e.target.value)}
                      placeholder="Enter 6-digit code"
                      maxLength={6}
                      required
                      className={styles["code-input"]}
                    />
                  </div>
                  <div className={styles["form-buttons"]}>
                    <button
                      type="submit"
                      disabled={loading}
                      className={styles["verify-button"]}
                    >
                      {loading ? "Verifying..." : "Verify & Enable"}
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setShowSetup(false);
                        setVerificationCode("");
                        setQrCodeUrl("");
                        setSecretKey("");
                        setError("");
                      }}
                      className={styles["cancel-button"]}
                    >
                      Cancel
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}

          {showDisable && (
            <div className={styles["disable-section"]}>
              <h4>Disable Two-Factor Authentication</h4>
              <p>
                Enter your current 2FA code to disable two-factor
                authentication:
              </p>
              <form onSubmit={handleDisable2FA}>
                <div className={styles["form-group"]}>
                  <input
                    type="text"
                    value={disableCode}
                    onChange={(e) => setDisableCode(e.target.value)}
                    placeholder="Enter 6-digit code"
                    maxLength={6}
                    required
                    className={styles["code-input"]}
                  />
                </div>
                <div className={styles["form-buttons"]}>
                  <button
                    type="submit"
                    disabled={loading}
                    className={styles["disable-confirm-button"]}
                  >
                    {loading ? "Disabling..." : "Disable 2FA"}
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      setShowDisable(false);
                      setDisableCode("");
                      setError("");
                    }}
                    className={styles["cancel-button"]}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>

        <div className={styles["section"]}>
          <h3>Account Actions</h3>
          <button onClick={handleLogout} className={styles["logout-button"]}>
            Logout
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
