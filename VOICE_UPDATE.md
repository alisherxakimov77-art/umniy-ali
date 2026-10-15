VOICE UPDATE (Whisper via Groq)
================================

What changed:
- android/.../MainActivity.kt  -> Mic now RECORDS audio and sends to server
- server/app/api/voice.py      -> NEW: /stt endpoint (Whisper transcription)
- server/app/main.py           -> includes /stt
- server/requirements.txt      -> + python-multipart

INSTALL (on PC):
1. Copy files into project folder (overwrite).
2. cmd:
     cd /d "C:\Users\user\Desktop\UMNIY ALI\server"
     venv\Scripts\python.exe -m pip install python-multipart
3. Restart server (Ctrl+C, then start_server.bat).
4. Push to GitHub (push_to_github.bat), then Actions -> Build Android -> Run workflow.
5. Install the new APK on the phone.

HOW TO USE MIC:
- Press Mic once -> phone records (toast: "Speaking...").
- Speak (Russian or Uzbek).
- Press Mic again -> audio is sent, recognized, and message is sent automatically.
- If too short -> "Too short, try again".
