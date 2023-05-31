##
# source directory
##
SRC_DIR := src

##
# output directory
##
OUT_DIR := out

##
# sources
##
SRCS := $(wildcard $(SRC_DIR)/*.java)

##
# classes
## 
CLS := $(SRCS:$(SRC_DIR)/%.java=$(OUT_DIR)/%.class)

##
# server
## 
SERVER := TimeServer

##
# server
## 
CLIENT := TimeClient

##
# compiler and compiler flags
##
JC := javac
JCFLAGS := -d $(OUT_DIR)/ -cp $(SRC_DIR)/
JVM := java
SERVERFLAGS := 3000	
CLIENTFLAGS := localhost 3000

##
# suffixes
##
.SUFFIXES: .java

##
# targets that do not produce output files
##
.PHONY: all clean

##
# default target(s)
##

default: all

all: $(CLS)

$(CLS): $(OUT_DIR)/%.class: $(SRC_DIR)/%.java
	$(JC) $(JCFLAGS) $<

clean: 
	rm $(OUT_DIR)/*

server: all
	$(JVM) -cp $(OUT_DIR) $(SERVER) $(SERVERFLAGS)

client: all
	$(JVM) -cp $(OUT_DIR) $(CLIENT) $(CLIENTFLAGS)
