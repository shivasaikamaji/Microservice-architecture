@echo off
for /f "delims=" %%a in ('curl -s -X POST http://localhost:8081/api/v1/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test1@example.com\",\"password\":\"Password@123\"}" ^| findstr /o "accessToken"') do set FOUND=1

curl -s -X POST http://localhost:8081/api/v1/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test1@example.com\",\"password\":\"Password@123\"}" > login.json

powershell -Command "$json = Get-Content login.json | ConvertFrom-Json; $token = $json.accessToken; Write-Host 'TOKEN:' $token; $headers = @{Authorization = 'Bearer ' + $token}; $response = Invoke-WebRequest -Uri 'http://localhost:8081/users' -Headers $headers -SkipHttpErrorCheck; Write-Host 'STATUS CODE:' $response.StatusCode"