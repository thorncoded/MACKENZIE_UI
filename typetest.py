from __future__ import annotations

import urwid
import sys
import time
import curses

#dialog array of lines, might change this to alternate data structure
#to accomodate for alternate font colors for accessiblity
dialogArray = []
starterDialog = ""
listIndex = 0


def set_window_title(title):
    curses.setupterm()
    sys.stdout.write("\x1b]2;{}\x07".format(title))
    sys.stdout.flush()


def readDialogFile(fileName):
    with open(fileName) as f:
                #Content_list is the list that contains the read lines.     
                for line in f:
                        dialogArray.append(line)




def show_or_exit(key):
    global starterDialog, listIndex  # Declare global variables
    set_window_title("MACKENZIE.EXE")  # Set the window title
    if key in ('q', 'Q'):
        raise urwid.ExitMainLoop()
    starterDialog += dialogArray[listIndex] + "\n"
    txt.set_text(starterDialog)
    if listIndex != len(dialogArray) - 1:
        listIndex += 1

txt = urwid.Text(starterDialog)
fill = urwid.Filler(txt, "top")
loop = urwid.MainLoop(fill, unhandled_input=show_or_exit)
readDialogFile("dfile.txt")
loop.run()