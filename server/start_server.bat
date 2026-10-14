@echo off
chcp 65001 >nul
echo ========================================
echo   Умный Али — запуск сервера
echo ========================================

if not exist ".venv" (
    echo Создаю виртуальное окружение...
    python -m venv .venv
)

call .venv\Scripts\activate.bat

echo Устанавливаю/обновляю зависимости...
pip install -r requirements.txt

if not exist ".env" (
    echo.
    echo ВНИМАНИЕ: файл .env не найден!
    echo Создай .env по примеру .env.example и добавь LLM_API_KEY.
    echo.
    pause
    exit /b 1
)

echo Запускаю сервер...
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload

pause
