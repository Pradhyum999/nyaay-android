@echo off
title NYAAYNEETI Android App - Ultra Fast 60FPS
cls
echo ========================================================
echo       NYAAYNEETI - Android Native App Viewer (60 FPS)
echo ========================================================
echo.

set ADB="C:\Users\pradh\AppData\Local\Android\Sdk\platform-tools\adb.exe"
set SCRCPY="C:\Users\pradh\AppData\Local\scrcpy\scrcpy.exe"

%ADB% devices
echo.
echo Launching NYAAYNEETI App...
%ADB% shell am start -n com.nyaayneeti.app/.MainActivity >nul 2>&1

echo.
echo Opening Zero-Latency 60FPS Interactive Window...
%SCRCPY% -s emulator-5554 --window-title "NYAAYNEETI Native Android App (60 FPS)" --always-on-top -m 1080 -b 8M --max-fps 60 --video-codec=h264 --no-audio --stay-awake

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [Notice] If the window closed, press any key to exit.
    pause
)
