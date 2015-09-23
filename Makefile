JFLAGS = -g
JC = javac
SRCPATH = src
BUILDPATH = classes
CP = src
.SUFFIXES: .java .class
.java.class:
	$(JC) -d $(BUILDPATH) -cp $(CP) $(JFLAGS) $*.java 

CLASSES = $(shell find $(SRCPATH) -name "*.java")

default: classes

classes: $(CLASSES:.java=.class)

clean:
	rm -rf $(BUILDPATH)/*


