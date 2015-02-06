SOURCE=$(shell find ~/Documents/intellij/BigMLHW2/src -name '*.java')

.PHONY: all clean

all:
	javac -d bin $(SOURCE)

clean:
	find . -name '*'.class -exec rm -f {} ';'

# ensure the next line is always the last line in this file.
# vi:noet
