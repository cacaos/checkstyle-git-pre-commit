@echo off

echo "Generate uber jar (checkstyle-all-X.XX.jar)"
mvn -P assembly package
pause