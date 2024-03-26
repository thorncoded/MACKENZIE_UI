from __future__ import annotations
import urwid

# Define palettes with color attributes for the grid elements
palette = [
    ('default', 'default', 'default'),
    ('header', 'white', 'dark blue'),
    ('cell', 'white', 'dark gray'),
    ('palette1', 'dark magenta', 'light magenta'),  # Example palette
    ('palette2', 'dark green', 'light green'),  # Example palette
    ('palette3', 'light magenta', 'light gray'),  # Example palette
    # Add more palettes as needed
]

def show_or_exit(key: str) -> None:
    if key in {"q", "Q"}:
        raise urwid.ExitMainLoop()

# Function to create a text widget with the specified text and palette
def create_text_widget(text: str, palette: str) -> urwid.Widget:
    return urwid.AttrMap(urwid.Text(text, align='center'), palette)

# Define grid content
grid_content = [
    [create_text_widget(str(i), 'cell') for i in range(1, 9)],
    [create_text_widget(str(i), 'header') for i in range(9, 17)],
    [create_text_widget(str(i), 'cell') for i in range(17, 25)],
    [create_text_widget(str(i), 'header') for i in range(25, 33)],
    [create_text_widget(str(i), 'cell') for i in range(33, 41)],
    [create_text_widget(str(i), 'header') for i in range(41, 49)],
    [create_text_widget(str(i), 'cell') for i in range(49, 57)],
    [create_text_widget(str(i), 'header') for i in range(57, 65)],
]

# Get the first column of the grid
first_column = [row[0] for row in grid_content]

# Apply different palettes to each cell in the first column
for i, cell in enumerate(first_column):
    # Use the modulo operator to cycle through palettes
    palette_index = i + 1  # Start from the fourth palette
    cell.original_widget = create_text_widget(str(palette_index), f'palette{palette_index}')

# Transpose the grid content to switch rows and columns
transposed_grid_content = list(map(list, zip(*grid_content)))

# Create Columns for each row
rows = [urwid.Columns(row, dividechars=0) for row in transposed_grid_content]

# Combine rows vertically using Pile
pile = urwid.Pile(rows)

# Create a Filler widget with Pile and "top" alignment
fill = urwid.Filler(pile, "top")

# Create the main loop
loop = urwid.MainLoop(fill, palette=palette, unhandled_input=show_or_exit)
loop.run()
