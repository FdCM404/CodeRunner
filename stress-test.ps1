$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$JarPath = Join-Path $Root "client\build\libs\client-1.0-all.jar"
$ScriptPath = Join-Path $Root "scripts\programa.py"
$InputPath = Join-Path $Root "input.txt"
$HostIp = "127.0.0.1"
$ManagerPort = 5000
$NumberOfClients = 3
$ExpectedOutput = @(
    "Estado: FINISHED",
    "count: 1",
    "sum: 1030.0"
)

foreach ($path in @($JarPath, $ScriptPath, $InputPath)) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
        throw "Ficheiro necessário não encontrado: $path"
    }
}

$OutputDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("coderunner-stress-" + [guid]::NewGuid())
$processes = @()

try {
    New-Item -ItemType Directory -Path $OutputDirectory | Out-Null

    for ($i = 1; $i -le $NumberOfClients; $i++) {
        $outputPath = Join-Path $OutputDirectory "client-$i.output.log"
        $statusPath = Join-Path $OutputDirectory "client-$i.status"
        $quote = { param($value) "'" + $value.Replace("'", "''") + "'" }
        $command = @"
`$OutputEncoding = [Console]::OutputEncoding = [Text.Encoding]::UTF8
Write-Host '=== CodeRunner cliente $i ===' -ForegroundColor Cyan
& java -jar $(& $quote $JarPath) $(& $quote $ScriptPath) $(& $quote $InputPath) $HostIp $ManagerPort 2>&1 | Tee-Object -FilePath $(& $quote $outputPath)
`$exitCode = `$LASTEXITCODE
Set-Content -LiteralPath $(& $quote $statusPath) -Value `$exitCode
Write-Host "=== Cliente $i terminou com exit code `$exitCode ===" -ForegroundColor Cyan
Read-Host 'Prima Enter para fechar este terminal'
"@

        Write-Host "A lançar cliente $i..." -ForegroundColor Cyan
        $processes += Start-Process `
            -FilePath "powershell" `
            -ArgumentList @("-NoProfile", "-NoExit", "-Command", $command) `
            -WorkingDirectory $Root `
            -PassThru
    }

    $failed = $false
    for ($i = 0; $i -lt $processes.Count; $i++) {
        $clientNumber = $i + 1
        $outputPath = Join-Path $OutputDirectory "client-$clientNumber.output.log"
        $statusPath = Join-Path $OutputDirectory "client-$clientNumber.status"
        $deadline = (Get-Date).AddSeconds(120)
        while (-not (Test-Path -LiteralPath $statusPath)) {
            if ((Get-Date) -gt $deadline) {
                throw "Timeout à espera do cliente $clientNumber."
            }
            Start-Sleep -Milliseconds 250
        }

        $stdout = [System.IO.File]::ReadAllText($outputPath)
        $exitCode = [int][System.IO.File]::ReadAllText($statusPath).Trim()
        $clientFailed = $exitCode -ne 0
        foreach ($expected in $ExpectedOutput) {
            if ($stdout -notmatch [regex]::Escape($expected)) {
                $clientFailed = $true
                Write-Host "Cliente ${clientNumber}: resultado esperado ausente: $expected" -ForegroundColor Red
            }
        }

        if ($clientFailed) {
            $failed = $true
            Write-Host "Cliente ${clientNumber}: FALHOU (exit code $exitCode)" -ForegroundColor Red
            Write-Host $stdout
        } else {
            Write-Host "Cliente ${clientNumber}: OK" -ForegroundColor Green
        }
    }

    if ($failed) {
        throw "O teste de stress falhou."
    }
    Write-Host "Teste concluído com sucesso: $NumberOfClients clientes terminaram em FINISHED." -ForegroundColor Green
    Write-Host "Os três terminais permanecem abertos para consulta do output." -ForegroundColor Cyan
} finally {
    Write-Host "Logs guardados em: $OutputDirectory" -ForegroundColor DarkGray
}