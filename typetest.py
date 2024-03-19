from __future__ import annotations

import urwid
import sys
import curses

# dialog array of lines
dialogArray = []
starterDialog = ""
listIndex = 0


def set_window_title(title):
    curses.setupterm()
    sys.stdout.write("\x1b]2;{}\x07".format(title))
    sys.stdout.flush()

def readDialogFile(fileName):
    with open(fileName) as f:
        for line in f:
            dialogArray.append(line)

class AutoScrollListBox(urwid.ListBox):
    def __init__(self, body):
        super().__init__(urwid.SimpleFocusListWalker(body))

    def add_line(self, line):
        self.body.append(urwid.Text(line))

        # Scroll to the bottom
        self.set_focus(len(self.body) - 1)

def show_or_exit(key):
    global starterDialog, listIndex  # Declare global variables
    set_window_title("MACKENZIE.EXE")  # Set the window title
    if key in ('q', 'Q'):
        raise urwid.ExitMainLoop()
    txt.set_text(starterDialog)
    if listIndex != len(dialogArray) - 1:
        listIndex += 1
    # Add the line to the custom list box and scroll to the bottom
    listbox.add_line(dialogArray[listIndex])

txt = urwid.Text(starterDialog)
listbox = AutoScrollListBox([txt])
loop = urwid.MainLoop(listbox, unhandled_input=show_or_exit)

readDialogFile("dfile.txt")
loop.run()
