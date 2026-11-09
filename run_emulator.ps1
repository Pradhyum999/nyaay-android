param()

$adb = "C:\Users\pradh\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$emulator = "C:\Users\pradh\AppData\Local\Android\Sdk\emulator\emulator.exe"
$scrcpy = "C:\Users\pradh\AppData\Local\scrcpy\scrcpy.exe"

Write-Host "Starting Android Emulator..."
Start-Process -FilePath $emulator -ArgumentList "-avd", "medium_phone", "-gpu", "angle9"

Write-Host "Waiting for device to connect..."
& $adb wait-for-device

Write-Host "Waiting for system boot..."
while ($true) {
    $boot = (& $adb shell getprop sys.boot_completed 2>$null)
    if ($boot -eq "1") { break }
    Start-Sleep -Seconds 2
}

Write-Host "Launching Nyaay Android App..."
& $adb shell am start -n com.nyaayneeti.app/.MainActivity

Write-Host "Starting Interactive Desktop Mirror (scrcpy)..."
Start-Process -FilePath $scrcpy -ArgumentList "-s", "emulator-5554", "--window-title", "`"NYAAYNEETI Android App`"", "--always-on-top"
