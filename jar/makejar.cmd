@REM включение кодировки utf-8 
chcp 65001 
@REM Создание исполняемого jar-архива
@REM в винде можно просто как экзешник запускать

javac JarExample.java
jar cvfe JarExample.jar JarExample JarExample.class
java -jar JarExample.jar