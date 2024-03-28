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
                cell_text = str((i * self.columns) + j + 1)
                # Apply different palettes based on row parity
                if i % 2 == 0:
                    palette = 'cell'
                else:
                    palette = 'header'
                row.append(create_text_widget(cell_text, palette))
            grid_content.append(row)
        return grid_content

    def get_first_row(self):
        # Get the first row of the grid
        return [row[0] for row in self.grid_content]

    def apply_palettes_to_first_row(self):
        # Apply different palettes to each cell in the first row
        first_row = self.get_first_row()
        for i, cell in enumerate(first_row):
            palette_index = i + 1
            cell.original_widget = create_text_widget(str(palette_index), f'palette{palette_index}')

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

def create_text_widget(text: str, palette: str) -> urwid.Widget:
    # Create a text widget with the specified text and palette
    return urwid.AttrMap(urwid.Text(text, align='center'), palette)

# Define palettes with color attributes for the grid elements
palette = [
    ('default', 'default', 'default'),
    ('header', 'white', 'dark blue'),
    ('cell', 'white', 'dark gray'),
    ('palette1', 'dark magenta', 'light magenta'),
    ('palette2', 'dark green', 'light green'),
    ('palette3', 'light magenta', 'light gray'),
]

# Create a Grid instance with 8 rows and 8 columns
grid = Grid(8, 8)
# Apply different palettes to each cell in the first row
grid.apply_palettes_to_first_row()
# Transpose the grid content to switch rows and columns
grid.transpose()
# Create Rows for each column
rows = grid.create_rows()
# Combine rows vertically using Pile
pile = urwid.Pile(rows)
# Create a Filler widget with Pile and "top" alignment
fill = urwid.Filler(pile, "top")

# Create the main loop
loop = urwid.MainLoop(fill, palette=palette, unhandled_input=show_or_exit)
# Run the main loop
loop.run()
