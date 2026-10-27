@echo off
title Build Vault-X 3D Bank Management System
echo ========================================================
echo  Compiling Java 3D Bank Management System...
echo ========================================================
if not exist bin mkdir bin
javac -d bin -sourcepath src src\com\bank3d\Main.java
if %errorlevel% equ 0 (
    echo [SUCCESS] Compilation complete!
) else (
    echo [ERROR] Compilation failed.
)
pause
