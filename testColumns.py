from __future__ import annotations
import urwid

class Grid:
    def __init__(self, rows: int, columns: int):
        # Initialize the grid with the specified number of rows and columns
        self.rows = rows
        self.columns = columns
        # Create the grid content
        self.grid_content = self._create_grid_content()

    def _create_grid_content(self):
        # Create the content for the grid
        grid_content = []
        for i in range(self.rows):
            row = []
            for j in range(self.columns):
                # Generate cell text based on its position in the grid
                #cell_text = str((i * self.columns) + j + 1)
                #no lol
                cell_text = ""
                # Apply different attributes based on row parity
                if i % 2 == 0:
                    attrmap = urwid.AttrMap(urwid.Text(cell_text, align='center'), 'cell')
                else:
                    attrmap = urwid.AttrMap(urwid.Text(cell_text, align='center'), 'header')
                row.append(attrmap)
            grid_content.append(row)
        return grid_content

    def apply_attrmaps_to_full_grid(self):
        # Apply different attribute maps to each cell in the grid
        for i in range(self.rows):
            for j in range(self.columns):
                cell = self.grid_content[i][j]
                if i % 2 == 0:
                    cell.original_widget = urwid.AttrMap(urwid.Text("", align='center'), 'cell')
                else:
                    cell.original_widget = urwid.AttrMap(urwid.Text("", align='center'), 'header')

    def transpose(self):
        # Transpose the grid content to switch rows and columns
        self.grid_content = list(map(list, zip(*self.grid_content)))

    def create_rows(self):
        # Create Columns for each row
        return [urwid.Columns(row, dividechars=0) for row in self.grid_content]

def show_or_exit(key: str) -> None:
    # Function to handle input events
    if key in {"q", "Q"}:
        raise urwid.ExitMainLoop()

# Define attribute maps with color attributes for the grid elements
attrmap = [
    ('default', 'default', 'default'),
    ('header', 'white', 'dark blue'),
    ('cell', 'white', 'dark gray'),
]

# Create a Grid instance with 16 rows and 16 columns
grid = Grid(16, 16)
# Apply different attribute maps to each cell in the grid
grid.apply_attrmaps_to_full_grid()
# Transpose the grid content to switch rows and columns
grid.transpose()
# Create Rows for each column
rows = grid.create_rows()
# Combine rows vertically using Pile
pile = urwid.Pile(rows)
# Create a Filler widget with Pile and "top" alignment
fill = urwid.Filler(pile, "top")

# Create the main loop
loop = urwid.MainLoop(fill, palette=attrmap, unhandled_input=show_or_exit)
# Run the main loop
loop.run()
