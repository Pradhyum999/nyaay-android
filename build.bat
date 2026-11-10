@echo off
set JAVA_HOME=C:\Users\pradh\.jdks\jbr-21.0.11
call C:\Users\pradh\Documents\antigravity\nyaay-android\gradlew.bat -p C:\Users\pradh\Documents\antigravity\nyaay-android clean assembleDebug testDebugUnitTest --console=plain > C:\Users\pradh\Documents\antigravity\nyaay-android\build_result.log 2>&1
