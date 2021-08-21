


set MAINJARS=../dist/lib/amigram.jar;

set JARS=;../lib/looks.jar;../lib/jh.jar;../lib/nxt.jar;../lib/xercesImpl.jar;../lib/jdom.jar;../lib/xalan.jar;../lib/xml-apis.jar;../lib/smoothmetal.jar;../lib/jh.jar;../lib/jmanual.jar;../lib/icons.jar

set MAIN_CLASS=de.dfki.ami.amigram.gui.MainFrame

%JAVA_HOME%/bin/java -cp %MAINJARS%%JARS%; %MAIN_CLASS% 
pause