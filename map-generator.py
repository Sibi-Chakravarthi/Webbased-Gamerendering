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
        self.generate_base_branch(branch_thickness = 9)
        self.rectangularize_branch()

    def generate_base_branch(self,branch_thickness):
        starting_thickness = branch_thickness//2
        chance = 0.005
        start_col = random.randint(1 + starting_thickness, self.map_size - 2 - starting_thickness)
        exit_col = random.randint(1 + branch_thickness, self.map_size - 2 - branch_thickness)
        row, col = 0, start_col

        while row < self.map_size - 1:

            for r in range(starting_thickness):
                for c in range(starting_thickness):
                    draw_r = row + r
                    draw_c = col + c
                    
                    if draw_r < self.map_size and draw_c < self.map_size:
                        self.map[draw_r][draw_c] = 1

            if starting_thickness < branch_thickness:
                if random.random() < chance: 
                    starting_thickness += 1
            if col >= self.map_size//4:
                chance = 0.01
            if col >= (self.map_size*3)//4:
                chance = 0.5

            moves = []
            moves.append((row + 1, col))
            print(starting_thickness) 
            
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
        self.map[self.map_size-1][exit_col] = 3

    def rectangularize_branch(self):
        print()
    
    def print_map(self):
        for row in self.map:
            print(" ".join(str(c) for c in row))

m = MapGenerator(map_size=123, num_rooms=5)
m.do_job()
m.print_map()
