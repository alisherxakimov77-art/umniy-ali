#!/bin/bash
set -e

echo "========================================"
echo "  Умный Али — запуск сервера"
echo "========================================"

if [ ! -d ".venv" ]; then
    echo "Создаю виртуальное окружение..."
    python3 -m venv .venv
fi

source .venv/bin/activate

echo "Устанавливаю/обновляю зависимости..."
pip install -r requirements.txt

if [ ! -f ".env" ]; then
    echo ""
    echo "ВНИМАНИЕ: файл .env не найден!"
    echo "Создай .env по примеру .env.example и добавь LLM_API_KEY."
    echo ""
    exit 1
fi

echo "Запускаю сервер..."
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
