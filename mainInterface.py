import urwid
import sys
import curses

def set_window_title(title):
    curses.setupterm()
    sys.stdout.write("\x1b]2;{}\x07".format(title))
    sys.stdout.flush()

class DashboardUI:
    orders_txt: urwid.widget.Text

    def show_or_exit(self, key):
        set_window_title("MACKENZIE.EXE")  # Set window title immediately
        if key in ('q', 'Q'):
            raise urwid.ExitMainLoop()
        self.orders_txt.set_text(repr(key))

    def show(self):
        
        algo_view = urwid.Text(u"Main Content Goes Here")
        algo_view_fill = urwid.Filler(algo_view, 'top')
        algo_view_linebox = urwid.LineBox(algo_view_fill,
                                          tlcorner=u'', tline=u'', lline=u'', trcorner=u'',
                                          blcorner=u'', rline=u'│', bline=u'', brcorner=u'│')

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

        layout = urwid.Columns([('weight', 3, algo_view_linebox), right_pile], dividechars=0)

        loop = urwid.MainLoop(layout, unhandled_input=self.show_or_exit)
        loop.run()
ui = DashboardUI()
ui.show()
