import random

class MapGenerator:
    def __init__(self, map_size, num_rooms):
        self.map_size = map_size
        self.num_rooms = num_rooms
        self.map = []

    def generate_base_map(self):
        self.map = [["." for _ in range(self.map_size)] for _ in range(self.map_size)]

    def do_job(self):
        self.generate_base_map()
        self.generate_base_branch()

    def generate_base_branch(self):
        start_col = random.randint(1, self.map_size - 2)
        exit_col = random.randint(1, self.map_size - 2)
        row, col = 0, start_col

        while row < self.map_size - 1:
            self.map[row][col] = 1
            moves = []
            moves.append((row + 1, col))
            if col > 1:
                moves.append((row, col - 1))
            if col < self.map_size - 2:
                moves.append((row, col + 1))
            if col < exit_col and col < self.map_size - 2:
                moves.append((row, col + 1))
            elif col > exit_col and col > 1:
                moves.append((row, col - 1))
            row, col = random.choice(moves)

        while col != exit_col:
            self.map[row][col] = 1
            if col < exit_col:
                col += 1
            else:
                col -= 1

        self.map[0][start_col] = 2
        self.map[row][col] = 3

    def print_map(self):
        for row in self.map:
            print(" ".join(str(c) for c in row))

m = MapGenerator(map_size=20, num_rooms=5)
m.do_job()
m.print_map()
