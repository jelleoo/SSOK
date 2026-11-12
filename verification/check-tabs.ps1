$ErrorActionPreference = 'Stop'
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
$outputDir = $PSScriptRoot

function Invoke-Adb {
    param([Parameter(ValueFromRemainingArguments = $true)][string[]]$CommandArgs)
    $result = & $adbPath -s emulator-5554 @CommandArgs 2>&1
    if ($LASTEXITCODE -ne 0) { throw ($result -join "`n") }
    return ($result -join "`n")
}

function Get-AppUi {
    param([string]$Name)
    $null = Invoke-Adb @('shell','uiautomator','dump','/sdcard/reststop-check.xml')
    $xmlText = Invoke-Adb @('shell','cat','/sdcard/reststop-check.xml')
    Set-Content -LiteralPath (Join-Path $outputDir "$Name.xml") -Value $xmlText -Encoding utf8
    return [xml]$xmlText
}

function Assert-Text {
    param([xml]$Ui,[string]$Text)
    if (-not $Ui.SelectSingleNode("//node[@text='$Text']")) { throw "Missing text: $Text" }
}

function Wait-AppUi {
    param([string]$Name,[string]$Text)
    for ($attempt=0; $attempt -lt 5; $attempt++) {
        $snapshot = Get-AppUi $Name
        if ($snapshot.SelectSingleNode("//node[@text='$Text']")) { return $snapshot }
    }
    throw "Screen did not become ready: $Text"
}

function Get-Bounds {
    param($Node)
    $values = [regex]::Matches($Node.bounds, '\d+') | ForEach-Object { [int]$_.Value }
    return ,$values
}

function Tap-Text {
    param([xml]$Ui,[string]$Text)
    $node = $Ui.SelectSingleNode("//node[@text='$Text']")
    if (-not $node) { throw "Cannot tap: $Text" }
    $bounds = Get-Bounds $node
    $null = Invoke-Adb @('shell','input','tap',"$([int](($bounds[0]+$bounds[2])/2))","$([int](($bounds[1]+$bounds[3])/2))")
}

function Save-Screen {
    param([string]$Name)
    $null = Invoke-Adb @('shell','screencap','-p','/sdcard/reststop-check.png')
    $null = Invoke-Adb @('pull','/sdcard/reststop-check.png',(Join-Path $outputDir "$Name.png"))
}

function Check-Drag {
    param([xml]$Ui,[string]$Text,[string]$Name)
    $candidates = $Ui.SelectNodes("//node[@scrollable='true'][.//node[@text='$Text']]")
    $row = $candidates | Sort-Object { $b=Get-Bounds $_; $b[3]-$b[1] } | Select-Object -First 1
    if (-not $row) { throw "Missing scrollable row: $Name" }
    $bounds = Get-Bounds $row
    $before = $row.OuterXml
    $width = $bounds[2]-$bounds[0]
    $dragY = [int](($bounds[1]+$bounds[3])/2)
    $null = Invoke-Adb @('shell','input','swipe',"$([int]($bounds[0]+$width*0.85))","$dragY","$([int]($bounds[0]+$width*0.15))","$dragY",'650')
    $after = Get-AppUi "$Name-after"
    $rowsAfter = $after.SelectNodes("//node[@scrollable='true']") | Where-Object { $_.bounds -eq $row.bounds }
    $changed = @($rowsAfter | Where-Object { $_.OuterXml -ne $before }).Count -gt 0
    if (-not $changed) { throw "Touch drag did not change the row: $Name" }
    Save-Screen "$Name-after"
    Write-Host "PASS: touch drag changed $Name"
    return $after
}

$null = Invoke-Adb @('shell','settings','put','global','window_animation_scale','0')
$null = Invoke-Adb @('shell','settings','put','global','transition_animation_scale','0')
$null = Invoke-Adb @('shell','settings','put','global','animator_duration_scale','0')
$null = Invoke-Adb @('shell','am','force-stop','--user','current','com.yourssu.reststopmate')
$null = Invoke-Adb @('shell','pm','clear','--user','current','com.yourssu.reststopmate')
$null = Invoke-Adb @('shell','am','start','--user','current','-n','com.yourssu.reststopmate/.MainActivity')
$ui = Wait-AppUi 'tabs-01-login' '게스트로 시작하기'
Assert-Text $ui '게스트로 시작하기'
Save-Screen 'tabs-01-login'
Tap-Text $ui '게스트로 시작하기'
$ui = Wait-AppUi 'tabs-02-home' '휴게소 검색하기'
Assert-Text $ui '게스트로 이용 중'
Assert-Text $ui '휴게소 검색하기'
Save-Screen 'tabs-02-home'
Write-Output 'PASS: guest login opens Home'

Tap-Text $ui 'Search'
$ui = Wait-AppUi 'tabs-03-search' '최근 검색 항목'
Assert-Text $ui '최근 검색 항목'
Assert-Text $ui '3일 전 방문'
Assert-Text $ui '2주 전 방문'
Save-Screen 'tabs-03-search'
Write-Output 'PASS: Search tab and mock visit records'

Tap-Text $ui 'My'
$ui = Wait-AppUi 'tabs-04-my' '이번 달 휴게소 먹방 횟수'
Assert-Text $ui '자주 방문한 휴게소'
Assert-Text $ui '이번 달 휴게소 먹방 횟수'
Assert-Text $ui '8회'
Assert-Text $ui '매운철판돈까스'
Save-Screen 'tabs-04-my'
Write-Output 'PASS: My tab and mock meals'
$ui = Check-Drag $ui '김천휴게소' 'tabs-05-frequent-drag'
$ui = Check-Drag $ui '매운철판돈까스' 'tabs-06-meal-drag'

Tap-Text $ui 'Home'
$ui = Get-AppUi 'tabs-07-return-home'
Assert-Text $ui '휴게소 검색하기'
$null = Invoke-Adb @('shell','am','force-stop','--user','current','com.yourssu.reststopmate')
$null = Invoke-Adb @('shell','am','start','--user','current','-n','com.yourssu.reststopmate/.MainActivity')
$ui = Wait-AppUi 'tabs-08-relaunch' '휴게소 검색하기'
Assert-Text $ui '게스트로 이용 중'
Assert-Text $ui '휴게소 검색하기'
Write-Output 'PASS: relaunch preserves guest and opens Home'
Tap-Text $ui 'My'
$ui = Get-AppUi 'tabs-09-final-my'
Save-Screen 'tabs-09-final-my'
Write-Output 'PASS: verification complete; emulator left on My'
