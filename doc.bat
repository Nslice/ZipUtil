SET dest=".\out\production\Docs"

javadoc -d %dest% -encoding utf8 -private -author -version^
    src\com\myzip\ZipUtil.java

REM EXPLORER %dest%
%dest%\index.html


