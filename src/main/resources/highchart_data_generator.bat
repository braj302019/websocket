:loop
set /a num=%random% %%100
curl -X POST http://localhost:8080/websocket/highchart/data --data "value=%num%"
timeout /t 1
goto loop