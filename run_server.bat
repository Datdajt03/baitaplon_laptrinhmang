@echo off
title May Chu Thu Vien Tai Lieu
echo Dang khoi dong may chu...
cd /d "%~dp0"

REM Kiem tra neu thu muc build\classes chua duoc bien dich thi su dung lenh ant de bien dich
if not exist "build\classes\may_chu\chay_may_chu.class" (
    echo Dang bien dich ma nguon...
    ant compile
)

echo Bat dau chay may chu...
java -cp build\classes may_chu.chay_may_chu
pause
