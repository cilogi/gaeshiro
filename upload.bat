set APPENGINE=C:\software\appengine-java-sdk-1.6.4\bin
set EMAIL=tim.niblett@cilogi.com
set PATH=%JAVA_HOME%\bin;%PATH%;%APPENGINE%

rem appcfg.cmd -e %EMAIL% update target/mapOverlay-0.1
appcfg.cmd rollback target\gaeshiro-0.1

