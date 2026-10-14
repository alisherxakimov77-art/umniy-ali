@echo off
setlocal
cd /d "%~dp0"

if not exist local.properties (
  echo Creating local.properties...
  (
    echo sdk.dir=C:\\Users\\%USERNAME%\\AppData\\Local\\Android\\Sdk
    echo SERVER_URL=http://192.168.1.100:8000/
  ) > local.properties
)

if not exist .git (
  git init
)
git branch -M main
git add .
git commit -m "update"

set /p GH_USER=GitHub login: 
set REPO=umniy-ali
set /p REPO=Repo name [%REPO%]: 

git remote remove origin
git remote add origin https://github.com/alisherxakimov77-art/umniy-ali.git
git push -u origin main

echo.
echo DONE. Now enable Actions on GitHub and run Build Android.
pause