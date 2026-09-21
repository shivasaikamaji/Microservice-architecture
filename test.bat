@echo off
curl -s -X POST http://localhost:8081/api/v1/auth/login -H "Content-Type: application/json" -d "{\"email\":\"test1@example.com\",\"password\":\"Password@123\"}" > login.json

powershell -Command "$json = Get-Content login.json | ConvertFrom-Json; $token = $json.accessToken; Write-Host 'TOKEN:' $token; $headers = @{Authorization = 'Bearer ' + $token}; try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/users' -Headers $headers; Write-Host 'STATUS CODE:' $response.StatusCode } catch { Write-Host 'STATUS CODE:' $_.Exception.Response.StatusCode.value__ }"
