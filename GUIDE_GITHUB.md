# Как залить проект на GitHub и получить APK

Это самый простой способ получить готовый APK без Android Studio.

## Что понадобится

1. Аккаунт GitHub (бесплатный): https://github.com/signup
2. Git для Windows: https://git-scm.com/download/win
3. Проект `UMNIY ALI` на рабочем столе

---

## Шаг 1. Создать репозиторий на GitHub

1. Зайди на https://github.com
2. Нажми зелёную кнопку **New** (или «+» вверху → **New repository**)
3. В поле **Repository name** напиши: `umniy-ali`
4. Выбери **Public** (или Private — тогда workflow тоже будет работать)
5. НЕ ставь галочки «Add README», «.gitignore», «license» — они уже есть в проекте
6. Нажми **Create repository**

После создания GitHub покажет страницу с командами. Тебе нужна строка вида:
```
https://github.com/ТВОЙ_НИК/umniy-ali.git
```

---

## Шаг 2. Подготовить проект

Открой командную строку (CMD) или Git Bash:

```bash
cd C:\Users\user\Desktop\UMNIY ALI
```

Проверь, что есть файл `.gitignore` — он уже настроен, чтобы не заливать секреты.

Создай файл `android/local.properties` по шаблону:
```bash
cd android
copy local.properties.example local.properties
```

Открой `android/local.properties` в блокноте и напиши адрес своего сервера:
```properties
SERVER_URL=http://192.168.1.100:8000/
```
Замени `192.168.1.100` на IP своего компьютера (см. `ipconfig` в CMD).

---

## Шаг 3. Залить проект через Git

В папке `UMNIY ALI` поочерёдно выполни команды:

```bash
git init
git add .
git commit -m "Первый коммит"
git branch -M main
git remote add origin https://github.com/ТВОЙ_НИК/umniy-ali.git
git push -u origin main
```

Если спросит логин и пароль — введи логин GitHub, а вместо пароля **Personal Access Token** (создаётся в настройках GitHub).

После `git push` проект появится на GitHub.

---

## Шаг 4. Включить GitHub Actions

1. Открой свой репозиторий на GitHub
2. Перейди во вкладку **Actions**
3. Если появится зелёная кнопка **I understand my workflows, go ahead and enable them** — нажми её
4. Теперь workflow активирован

---

## Шаг 5. Запустить сборку APK

1. Во вкладке **Actions** слева увидишь workflow **Build Android APK**
2. Нажми на него
3. Справа сверху будет кнопка **Run workflow** — нажми ▼
4. Выбери ветку `main` и нажми зелёную кнопку **Run workflow**
5. GitHub начнёт сборку — это займёт 5–10 минут

---

## Шаг 6. Скачать APK

1. Когда сборка закончится (зелёная галочка ✅), открой эту сборку
2. Внизу раздел **Artifacts** → файл `umniy-ali-debug-apk`
3. Нажми на него — скачается ZIP-архив
4. Разархивируй на компьютере
5. Внутри будет файл `app-debug.apk`

---

## Шаг 7. Установить APK на телефон

**Способ 1 — через USB:**
1. Включи на телефоне **Режим разработчика** и **Отладку по USB**
2. Подключи телефон к компьютеру
3. В CMD выполни:
   ```bash
   adb install app-debug.apk
   ```

**Способ 2 — через мессенджер:**
1. Перекинь `app-debug.apk` себе в Telegram/WhatsApp
2. Открой файл на телефоне → **Установить**
3. Разреши установку из неизвестных источников, если спросит

---

## Если что-то пошло не так

**Ошибка в GitHub Actions:**
- Открой вкладку **Actions** → кликни на упавшую сборку (красный крест)
- Посмотри лог — обычно там понятно, что сломалось
- Скинь мне лог — я помогу

**Приложение не подключается к серверу:**
- Проверь, что сервер запущен (`start_server.bat`)
- Проверь IP в `local.properties`
- Убедись, что телефон и компьютер в одной Wi-Fi сети
- Проверь, что в `.env` сервера есть `LLM_API_KEY`
