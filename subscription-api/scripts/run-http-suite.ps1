# PowerShell HTTP test runner for subscription-api/request.http
# Runs bootstrap and repository-backed tests top-to-bottom.
# Usage: powershell -ExecutionPolicy Bypass -File .\scripts\run-http-suite.ps1

$ErrorActionPreference = 'Stop'
$VerbosePreference = 'Continue'

# Capture a transcript of the run for richer CI artifacts
$LogFile = Join-Path $PSScriptRoot 'run-http-suite-output.log'
try { Start-Transcript -Path $LogFile -Force -ErrorAction SilentlyContinue } catch {}

$baseUrl = 'http://localhost:8080'
$suffix = [guid]::NewGuid().ToString()

$adminPassword = 'Password123!'
$userPassword = 'Password123!'
$operatorPassword = 'Password123!'

$adminEmail = "admin_$suffix@example.com"
$userEmail = "user_$suffix@example.com"
$operatorUserEmail = "operator_$suffix@example.com"

function Invoke-JsonRequest {
    param(
        [Parameter(Mandatory = $true)] [string] $Method,
        [Parameter(Mandatory = $true)] [string] $Uri,
        [Parameter(Mandatory = $false)] $Body,
        [Parameter(Mandatory = $false)] [string] $Token
    )

    $headers = @{}
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }

    try {
        if ($null -ne $Body) {
            $json = $Body | ConvertTo-Json -Depth 10
            Write-Host "-> $Method $Uri`n$json" -ForegroundColor Cyan
            return Invoke-RestMethod -Method $Method -Uri $Uri -Headers $headers -ContentType 'application/json' -Body $json -ErrorAction Stop
        }
        else {
            Write-Host "-> $Method $Uri" -ForegroundColor Cyan
            return Invoke-RestMethod -Method $Method -Uri $Uri -Headers $headers -ErrorAction Stop
        }
    }
    catch {
        Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            try { $_.Exception.Response | Format-List | Out-String | Write-Host -ForegroundColor Yellow } catch {}
        }
        return $null
    }
}

function Wait-ForPortOpen {
    param(
        [Parameter(Mandatory = $true)] [string] $Host,
        [Parameter(Mandatory = $true)] [int] $Port,
        [int] $TimeoutSeconds = 120
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $tcp = New-Object System.Net.Sockets.TcpClient
            $async = $tcp.BeginConnect($Host, $Port, $null, $null)
            $wait = $async.AsyncWaitHandle.WaitOne(2000)
            if ($wait -and $tcp.Connected) {
                $tcp.EndConnect($async)
                $tcp.Close()
                Write-Host "Port $Port on $Host is open." -ForegroundColor Green
                return $true
            }
            $tcp.Close()
        }
        catch {
            # ignore and retry
        }
        Start-Sleep -Seconds 2
    }
    throw "Timed out waiting for $Host:$Port to be open after $TimeoutSeconds seconds"
}

# Ensure the application is listening before starting the test sequence
try {
    $uri = [uri]$baseUrl
    $hostToCheck = $uri.Host
    $portToCheck = $uri.Port
    Write-Host "Waiting for $hostToCheck:$portToCheck to accept connections..." -ForegroundColor Cyan
    Wait-ForPortOpen -Host $hostToCheck -Port $portToCheck -TimeoutSeconds 120
}
catch {
    Write-Host "Warning: readiness check failed: $($_.Exception.Message)" -ForegroundColor Yellow
    Write-Host "Proceeding anyway; tests may fail if the app is not up." -ForegroundColor Yellow
}

Write-Host "Starting HTTP test run with suffix: $suffix" -ForegroundColor Green

# 1) Register admin
$adminBody = @{
    id          = $null
    credentials = 'ROLE_ADMIN'
    email       = $adminEmail
    firstName   = "Admin$suffix"
    lastName    = 'Tester'
    password    = $adminPassword
    address     = 'Admin Street 1'
    phoneNumber = '+46100000001'
    preferences = 'http-suite'
}
$registerAdmin = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/register" -Body $adminBody
if ($null -eq $registerAdmin) { throw 'registerAdmin failed' }

# 2) Login admin
$loginAdminBody = @{ email = $registerAdmin.email; password = $adminPassword }
$loginAdmin = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/login" -Body $loginAdminBody
if ($null -eq $loginAdmin) { throw 'loginAdmin failed' }
$adminToken = $loginAdmin.accessToken
Write-Host "Admin token acquired." -ForegroundColor Green

# 3) Register ROLE_USER
$userBody = @{
    id          = $null
    credentials = 'ROLE_USER'
    email       = $userEmail
    firstName   = "User$suffix"
    lastName    = 'Tester'
    password    = $userPassword
    address     = 'Test Street 1'
    phoneNumber = '+46123456789'
    preferences = 'http-suite'
}
$registerUser = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/register" -Body $userBody
if ($null -eq $registerUser) { throw 'registerUser failed' }
$userId = $registerUser.id
$userRegisteredEmail = $registerUser.email
Write-Host "User created: id=$userId email=$userRegisteredEmail" -ForegroundColor Green

# 4) Login ROLE_USER
$loginUserBody = @{ email = $userRegisteredEmail; password = $userPassword }
$loginUser = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/login" -Body $loginUserBody
if ($null -eq $loginUser) { Write-Host 'loginUser failed (will continue)' -ForegroundColor Yellow } else { $userToken = $loginUser.accessToken; Write-Host 'User token acquired.' -ForegroundColor Green }

# 5) Register ROLE_OPERATOR (security principal)
$opUserBody = @{
    id          = $null
    credentials = 'ROLE_OPERATOR'
    email       = $operatorUserEmail
    firstName   = "Operator$suffix"
    lastName    = 'Tester'
    password    = $operatorPassword
    address     = 'Operator Street 1'
    phoneNumber = '+46111222333'
    preferences = 'http-suite'
}
$registerOpUser = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/register" -Body $opUserBody
if ($null -eq $registerOpUser) { throw 'registerOpUser failed' }
$operatorUserRegisteredEmail = $registerOpUser.email

# 6) Login ROLE_OPERATOR
$loginOpBody = @{ email = $operatorUserRegisteredEmail; password = $operatorPassword }
$loginOp = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/login" -Body $loginOpBody
if ($null -eq $loginOp) { Write-Host 'loginOp failed (will continue)' -ForegroundColor Yellow } else { $operatorToken = $loginOp.accessToken; Write-Host 'Operator token acquired.' -ForegroundColor Green }

# 7) Re-login admin to ensure fresh adminToken
$reloginAdmin = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/auth/login" -Body @{ email = $registerAdmin.email; password = $adminPassword }
if ($null -eq $reloginAdmin) { throw 'reloginAdmin failed' }
$adminToken = $reloginAdmin.accessToken

# 8) Create operator entity (admin)
$createOperator = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/operators" -Body @{ operator = $null; name = "OperatorEntity$suffix" } -Token $adminToken
if ($null -eq $createOperator) { throw 'createOperator failed' }
$operatorEntityId = $createOperator.id
Write-Host "Operator entity created: id=$operatorEntityId" -ForegroundColor Green

# 9) Create plan (admin)
$createPlan = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/plans" -Body @{ kind = 'INTERNET'; name = "PlanSeed$suffix"; operator = $operatorEntityId; price = 199.00; status = 'ACTIVE'; uploadSpeedMbps = 100; downloadSpeedMbps = 500; networkGeneration = $null; dataLimitGb = $null; callCostPerMinute = $null; smsCostPerMessage = $null } -Token $adminToken
if ($null -eq $createPlan) { throw 'createPlan failed' }
$planId = $createPlan.id
Write-Host "Plan created: id=$planId" -ForegroundColor Green

# 10) Create subscription seed (admin)
$createSub = Invoke-JsonRequest -Method POST -Uri "$baseUrl/api/v1/subscriptions" -Body @{ operatorId = $operatorEntityId; planId = $planId; userId = $userId; status = 'ACTIVE' } -Token $adminToken
if ($null -eq $createSub) { Write-Host 'createSub failed (will continue)' -ForegroundColor Yellow } else { $subscriptionId = $createSub.id; Write-Host "Subscription created: id=$subscriptionId" -ForegroundColor Green }

Write-Host "\n--- Running repository-backed checks ---\n" -ForegroundColor Magenta

# Example repo-backed tests
# Find plan by name
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/plans/name/PlanSeed$suffix" -Token $adminToken | Format-List

# Find plans by operator id
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/plans/operator/$operatorEntityId" -Token $adminToken | Format-List

# Price range
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/plans/price?min=100&max=300" -Token $adminToken | Format-List

# List internet plans
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/plans/internet" -Token $adminToken | Format-List

# Operator search ignore case
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/operators/search/ignorecase?name=OperatorEntity$suffix" -Token $adminToken | Format-List

# Subscription by user name (search)
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/subscriptions/search/userName?name=User$suffix" -Token $adminToken | Format-List

# User exists
Invoke-JsonRequest -Method GET -Uri "$baseUrl/api/v1/Users/exists?email=$userRegisteredEmail" -Token $adminToken | Format-List

Write-Host "\nDone." -ForegroundColor Green
