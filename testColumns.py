from PIL import Image
import urwid


class Grid:
    def __init__(self, pixel_art):
        # Initialize the grid with the size of pixel_art
        self.rows = len(pixel_art)
        self.columns = len(pixel_art[0])
        # Create the grid content
        self.grid_content = self._create_grid_content(pixel_art)

    def _create_grid_content(self, pixel_art):
    # Create the content for the grid based on pixel_art
        grid_content = []
        for row in pixel_art:
            row_widgets = []
            for color_code in row:
                # Create a new Text widget with the specified color for each cell
                text_widget = urwid.Text("", align='center')
                red_text_attr = urwid.AttrSpec('#00ff5f', color_code)
                attr_map = urwid.AttrMap(text_widget, red_text_attr)  # Remove the 'default' attribute
                row_widgets.append(attr_map)
            grid_content.append(row_widgets)
        return grid_content


    def create_rows(self):
        # Create Columns for each row
        return [urwid.Columns(row, dividechars=0) for row in self.grid_content]


def read_pixel_art(file_path):
    pixel_art = []
    try:
        # Open the image file
        img = Image.open(file_path)

        # Convert to RGB mode if it's in RGBA mode
        if img.mode == 'RGBA':
            img = img.convert('RGB')

        # Get the size of the image
        width, height = img.size

        # Create a 2D list to store color information
        for y in range(height):
            row = []
            for x in range(width):
                # Get the RGB color of the pixel at (x, y)
                r, g, b = img.getpixel((x, y))
                # Convert RGB to hex color code
                color_code = "#{:02x}{:02x}{:02x}".format(r, g, b)
                row.append(color_code)
            pixel_art.append(row)

        return pixel_art
    except Exception as e:
        print("Error:", e)
        return None


def show_or_exit(key):
    # Function to handle input events
    if key in {"q", "Q"}:
        raise urwid.ExitMainLoop()


# Example usage
file_path = "C:\\Users\\madti\\Downloads\\TestProject\\pixelArt\\mak16Test.png"
pixel_art = read_pixel_art(file_path)

if pixel_art:
    # Create a Grid instance with the pixel_art
    grid = Grid(pixel_art)
    # Transpose the grid content to match the orientation of pixel_art
    #grid.transpose()
    # Create Rows for each column
    rows = grid.create_rows()
    # Combine rows vertically using Pile
    pile = urwid.Pile(rows)
    # Create a Filler widget with Pile and "top" alignment
    fill = urwid.Filler(pile, "top")

    # Create the main loop
    loop = urwid.MainLoop(fill, unhandled_input=show_or_exit)
    # Run the main loop
    loop.run()
