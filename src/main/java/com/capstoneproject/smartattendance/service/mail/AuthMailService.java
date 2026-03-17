package com.capstoneproject.smartattendance.service.mail;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthMailService {

    private final MailSenderService mailSenderService;

    public void sendOtpMail(String email, String otp) {

        String subject = "Your One-Time Password (OTP) – Smart Attendance System";

        String body = """
                <!DOCTYPE html>
                <html>

                <head>
                    <meta charset="UTF-8" />
                    <style>
                        body {
                            margin: 0;
                            padding: 0;
                            background: #e5e7eb;
                            font-family: Arial, Helvetica, sans-serif;
                        }

                        .wrapper {
                            max-width: 600px;
                            margin: 30px auto;
                            background: #ffffff;
                            border-radius: 8px;
                            overflow: hidden;
                            box-shadow: 0 2px 8px rgba(27, 38, 59, 0.12);
                        }

                        .header {
                            background: #1B263B;
                            padding: 28px 32px;
                            text-align: center;
                        }

                        .header h1 {
                            margin: 0;
                            color: #ffffff;
                            font-size: 22px;
                            letter-spacing: 0.5px;
                        }

                        .header p {
                            margin: 6px 0 0;
                            color: #eeeeee;
                            font-size: 13px;
                        }

                        .badge {
                            display: inline-block;
                            margin-top: 12px;
                            background: #2563eb;
                            color: #ffffff;
                            font-size: 12px;
                            font-weight: bold;
                            padding: 4px 14px;
                            border-radius: 20px;
                            letter-spacing: 0.8px;
                            text-transform: uppercase;
                        }

                        .content {
                            padding: 28px 32px;
                        }

                        .greeting {
                            font-size: 15px;
                            color: #1B263B;
                            margin-bottom: 20px;
                        }

                        .section {
                            margin-bottom: 24px;
                        }

                        .section-title {
                            font-size: 11px;
                            font-weight: bold;
                            color: #354a75;
                            text-transform: uppercase;
                            letter-spacing: 1px;
                            margin-bottom: 10px;
                            padding-bottom: 6px;
                            border-bottom: 2px solid #e5e7eb;
                        }

                        .otp-box {
                            background: #eeeeee;
                            border-left: 4px solid #1B263B;
                            border-radius: 4px;
                            padding: 18px;
                            text-align: center;
                        }

                        .otp-code {
                            font-size: 36px;
                            font-weight: bold;
                            font-family: monospace;
                            color: #1B263B;
                            letter-spacing: 8px;
                        }

                        .otp-validity {
                            margin-top: 8px;
                            font-size: 13px;
                            color: #354a75;
                        }

                        .notice {
                            background: #eeeeee;
                            border: 1px solid #e5e7eb;
                            border-left: 4px solid #f59e0b;
                            border-radius: 4px;
                            padding: 12px 16px;
                            font-size: 13px;
                            color: #1B263B;
                            margin-bottom: 24px;
                        }

                        .notice strong {
                            color: #dc2626;
                        }

                        .footer {
                            background: #e5e7eb;
                            text-align: center;
                            padding: 18px 32px;
                            font-size: 12px;
                            color: #354a75;
                        }
                    </style>
                </head>

                <body>
                    <div class="wrapper">
                        <div class="header">
                            <h1>Smart Attendance System</h1>
                            <p>Automated Account Notification</p>
                            <span class="badge">OTP Verification</span>
                        </div>
                        <div class="content">
                            <p class="greeting">Dear <strong>User</strong>,<br><br>
                                We received a request to verify your identity. Use the OTP below to proceed.</p>

                            <div class="section">
                                <div class="section-title">Your One-Time Password</div>
                                <div class="otp-box">
                                    <div class="otp-code">%s</div>
                                    <div class="otp-validity">&#128336; Valid for <strong>2 minutes</strong> only</div>
                                </div>
                            </div>

                            <div class="notice">
                                <strong>⚠ Important:</strong> Never share this OTP with anyone.
                                If you did not request this, please ignore this email or contact your administrator immediately.
                            </div>
                        </div>
                        <div class="footer">
                            &copy; Smart Attendance System &nbsp;|&nbsp; This is an automated message, please do not reply.
                        </div>
                    </div>
                </body>

                </html>
                """
                .formatted(otp);

        mailSenderService.sendMail(email, subject, body);
    }
}