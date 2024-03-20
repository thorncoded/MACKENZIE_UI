import urwid
import sys
import curses
import csv


# Define a palette with color attributes
palette = [
    ('sko', 'light blue', 'default'),    # Body text
    ('mak', 'light magenta', 'default'),  
]

# dialog array of lines
dialogDict = {}
starterDialog = ""
listIndex = 0

def set_window_title(title):
    curses.setupterm()
    sys.stdout.write("\x1b]2;{}\x07".format(title))
    sys.stdout.flush()

def readDialogCSVFile(fileName):
    with open(fileName, 'r') as data:
        reader = csv.reader(data, delimiter="@")
        for row in reader:
            # Assuming each row has at least two columns
            key, value = row[:2]  # Extract the first two elements from the row
            dialogDict[key] = value  # Assign the key-value pair to the dictionary

class AutoScrollListBox(urwid.ListBox):
    def __init__(self, body):
        super().__init__(urwid.SimpleFocusListWalker(body))

    def add_line(self, line, character):
        text = urwid.Text(line)
        if character == "sko":
            text = urwid.AttrMap(text, 'sko')
        if character == "mak":
            text = urwid.AttrMap(text, 'mak')
        if character == "lucas":
            text = urwid.AttrMap(text, "lucas")
        self.body.append(text)

        # Scroll to the bottom
        self.set_focus(len(self.body) - 1)

class DashboardUI:
    orders_txt: urwid.widget.Text

    def __init__(self):
        self.txt = urwid.Text(starterDialog)
        self.listbox = AutoScrollListBox([self.txt])

    def show_or_exit(self, key):
        global starterDialog, listIndex  # Declare global variables
        set_window_title("MACKENZIE.EXE")  # Set the window title
        if key in ('q', 'Q'):
            raise urwid.ExitMainLoop()
        if listIndex != len(dialogDict) - 1:
            listIndex += 1
        # Add the line to the custom list box and scroll to the bottom
        line = list(dialogDict.keys())[listIndex]
        character = dialogDict[line]
        self.listbox.add_line(line, character)

    def show(self):
        self.orders_txt = urwid.Text(u"Orders\norder2\norder3")
        orders_fill = urwid.Filler(self.orders_txt, 'bottom')
        orders_linebox = urwid.LineBox(orders_fill,
                                       tlcorner=u'', tline=u'', lline=u'', trcorner=u'',
                                       blcorner=u'─', rline=u'', bline=u'─', brcorner=u'─')

        counter_txt = urwid.Text(u"Counter 1\nCounter 2\nCounter 3")
        counters_fill = urwid.Filler(counter_txt, 'top')
        counters_linebox = urwid.LineBox(counters_fill,
                                         tlcorner=u'', tline=u'', lline=u'', trcorner=u'',
                                         blcorner=u'─', rline=u'', bline=u'─', brcorner=u'─')

        resources_txt = urwid.Text(u"Resources 1\nResources 2\nResources 3")
        resources_fill = urwid.Filler(resources_txt, 'top')
        resources_linebox = urwid.LineBox(resources_fill,
                                          tlcorner=u'', tline=u'', lline=u'', trcorner=u'',
                                          blcorner=u'', rline=u'', bline=u'', brcorner=u'')

        right_pile = urwid.Pile([orders_linebox, counters_linebox, resources_linebox])

        layout = urwid.Columns([('weight', 3, self.listbox), right_pile], dividechars=0)

        loop = urwid.MainLoop(layout, palette=palette, unhandled_input=self.show_or_exit)

        
        readDialogCSVFile("C:\\Users\\madti\\Downloads\\TestProject\\dfile.csv")
        loop.run()

ui = DashboardUI()
ui.show()
