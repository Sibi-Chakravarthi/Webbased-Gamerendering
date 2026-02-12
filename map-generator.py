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
        self.find_largest_area()

    def generate_base_branch(self,branch_thickness):
        current_thickness = branch_thickness//2
        chance = 0.005
        start_col = random.randint(1 + current_thickness, self.map_size - 2 - current_thickness)
        exit_col = random.randint(1 + branch_thickness, self.map_size - 2 - branch_thickness)
        row, col = 0, start_col

        while row < self.map_size - 1:

            for r in range(current_thickness):
                for c in range(current_thickness):
                    draw_r = row + r
                    draw_c = col + c
                    
                    if draw_r < self.map_size and draw_c < self.map_size:
                        self.map[draw_r][draw_c] = 1

            if current_thickness < branch_thickness:
                if random.random() < chance: 
                    current_thickness += 1
            if col >= self.map_size//4:
                chance = 0.01
            if col >= (self.map_size*3)//4:
                chance = 0.5

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
        self.map[self.map_size-1][exit_col] = 3

    def find_largest_area(self):
        self.points_of_maximum_distance = {}
        for i in range(len(self.map)):
            largest_distace = []
            countleft = 0
            countright = 0
            for j in self.map[i]:
                if j !=1:
                    countleft += 1
                else:
                    break
            for j in self.map[i][::-1]:
                if j != 1:
                    countright += 1
                else:
                    break
            if countleft > countright :
                index =  countleft
                length = countleft
            else:
                index = self.map_size - countright -1
                length = countright
            self.points_of_maximum_distance[i] = (index,length)
    
    def print_map(self):
        for row in self.map:
            print(" ".join(str(c) for c in row))

m = MapGenerator(map_size=100, num_rooms=5)
m.do_job()
m.print_map()