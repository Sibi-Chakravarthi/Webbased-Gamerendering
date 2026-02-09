class MapGenerator:
    def __init__(self, map_size, num_rooms):
        self.map_size = map_size
        self.num_rooms = num_rooms
        self.map = []

    def generate_base_map(self):
        for row in range(self.map_size):
            self.map.append([])
            for col in range(self.map_size):
                self.map[row].append(".")
                
    def do_job(self):
        self.generate_base_map()

    def print_map(self):
        for row in self.map:
            for col in row:
                print(". ", end="")
            print()

map = MapGenerator(map_size=10, num_rooms=5)
map.do_job()
map.print_map()
