import random
import json
import pygame

class MapGenerator:
    def __init__(self, map_size, num_rooms):
        self.map_size = map_size
        self.num_rooms = num_rooms
        self.map = []
        self.points_of_maximum_distance = {}
        self.start_pos = (0, 0)

    def generate_base_map(self):
        self.map = [["." for _ in range(self.map_size)] for _ in range(self.map_size)]

    def do_job(self):
        self.generate_base_map()
        self.generate_base_branch(branch_thickness=7)
        self.find_largest_area()
        self.generate_sub_branch()
        json_data = self.export_to_json()
        self.save_json_file(json_data)
        return self.map

    def generate_base_branch(self, branch_thickness):
        current_thickness = branch_thickness // 2
        chance = 0.005
        start_col = random.randint(1 + current_thickness, self.map_size - 2 - current_thickness)
        exit_col = random.randint(1 + branch_thickness, self.map_size - 2 - branch_thickness)
        row, col = 0, start_col
        self.start_pos = (0, start_col)

        while row < self.map_size - 1:
            for r in range(current_thickness):
                for c in range(current_thickness):
                    draw_r, draw_c = row + r, col + c
                    if draw_r < self.map_size and draw_c < self.map_size:
                        self.map[draw_r][draw_c] = "0" 

            if current_thickness < branch_thickness and random.random() < chance:
                current_thickness += 1
            if col >= self.map_size // 4: chance = 0.01
            if col >= (self.map_size * 3) // 4: chance = 0.5

            moves = [(row + 1, col)]
            if col > 1: moves.append((row, col - 1))
            if col < self.map_size - 2: moves.append((row, col + 1))
            if col < exit_col and col < self.map_size - 2: moves.append((row, col + 1))
            elif col > exit_col and col > 1: moves.append((row, col - 1))
            row, col = random.choice(moves)

        while col != exit_col:
            self.map[row][col] = "0"
            col += 1 if col < exit_col else -1

        self.map[0][start_col] = "2" 
        self.map[self.map_size - 1][exit_col] = "3"

    def find_largest_area(self):
        self.points_of_maximum_distance = {}
        for i in range(len(self.map)):
            countleft = countright = 0
            for j in self.map[i]:
                if j not in ["0", "2", "3"]: countleft += 1
                else: break
            for j in self.map[i][::-1]:
                if j not in ["0", "2", "3"]: countright += 1
                else: break
            
            if countleft > countright:
                index, length, side = countleft // 2, countleft, "left"
            else:
                index, length, side = self.map_size - (countright // 2) - 1, countright, "right"
            
            self.points_of_maximum_distance[i] = (index, length, side)

    def generate_sub_branch(self):
        store = []
        for r, data in self.points_of_maximum_distance.items():
            store.append([r, data[0], data[1], data[2]])
        
        for i in range(len(store)):
            for j in range(len(store) - 1):
                if store[j][2] < store[j+1][2]:
                    store[j], store[j+1] = store[j+1], store[j]

        rooms_placed = 0
        used_rows = []
        
        for row_idx, col_idx, length, side in store:
            if rooms_placed >= 5: 
                break
            
            too_close = False
            for r in used_rows:
                if abs(row_idx - r) < 12: 
                    too_close = True
            if too_close or length < 10: 
                continue

            rw, rh = 4, 4
            for r in range(row_idx - rh, row_idx + rh):
                for c in range(col_idx - rw, col_idx + rw):
                    if 1 <= r < self.map_size - 1 and 1 <= c < self.map_size - 1:
                        self.map[r][c] = "0"

            temp_c = col_idx
            direction = 1 if side == "left" else -1
            
            while 0 < temp_c < self.map_size - 1:
                temp_c += direction
                if self.map[row_idx][temp_c] == "0" and abs(temp_c - col_idx) > rw:
                    break
                
                for dr in range(2):
                    if 0 <= row_idx + dr < self.map_size:
                        self.map[row_idx + dr][temp_c] = "0"

            used_rows.append(row_idx)
            rooms_placed += 1

    def export_to_json(self):
        data = {"config": {"size": self.map_size, "rooms": self.num_rooms}, "grid": self.map}
        return json.dumps(data) 

    def save_json_file(self, json_string):
        with open("map_store.json", "w") as f:
            f.write(json_string)
        print("Map saved as 'map_store.json'")

    def print_map(self):
        for row in self.map:
            print(" ".join(str(c) for c in row))
    
def visualize_map(generator):
    pygame.init()
    cell_size = 12
    grid_size = generator.map_size
    window_dim = grid_size * cell_size
    
    colors = {
        ".": (30, 30, 30),
        "0": (200, 200, 200),
        "2": (0, 255, 0),
        "3": (255, 0, 0)
    }

    screen = pygame.display.set_mode((window_dim, window_dim))
    pygame.display.set_caption("Map Visualizer")
    
    map = generator.do_job()
    
    running = True
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            if event.type == pygame.KEYDOWN:
                if event.key == pygame.K_SPACE:
                    map = generator.do_job()

        screen.fill((0, 0, 0))
        for r in range(grid_size):
            for c in range(grid_size):
                tile = map[r][c]
                color = colors.get(tile, (255, 255, 255))
                pygame.draw.rect(screen, color, (c * cell_size, r * cell_size, cell_size - 1, cell_size - 1))
        
        pygame.display.flip()
    pygame.quit()

if __name__ == "__main__":
    m = MapGenerator(map_size=50, num_rooms=4)
    visualize_map(m)
