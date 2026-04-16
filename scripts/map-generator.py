import json
import random
import math

class MapGenerator:
    def __init__(self, map_size, num_rooms):
        self.map_size = map_size
        self.num_rooms = num_rooms
        self.map = []
        self.start_pos = None
        self.exit_pos = None
        self.points_of_maximum_distance = {}
        self.room_centers = []

    def generate_base_map(self):
        self.map = [[1 for _ in range(self.map_size)] for _ in range(self.map_size)]

    def do_job(self):
        self.generate_base_map()
        self.place_rooms_and_corridors_doom()
        self.place_start_and_exit()
        self.find_largest_area()

    def in_bounds(self, r, c):
        return 0 <= r < self.map_size and 0 <= c < self.map_size

    def generate_cave_noise(self, fill_chance=0.47):
        for r in range(self.map_size):
            for c in range(self.map_size):
                if r == 0 or c == 0 or r == self.map_size - 1 or c == self.map_size - 1:
                    self.map[r][c] = 1
                else:
                    self.map[r][c] = 1 if random.random() < fill_chance else 0

    def count_wall_neighbors(self, r, c):
        count = 0
        for dr in (-1, 0, 1):
            for dc in (-1, 0, 1):
                if dr == 0 and dc == 0:
                    continue
                nr, nc = r + dr, c + dc
                if not self.in_bounds(nr, nc) or self.map[nr][nc] == 1:
                    count += 1
        return count

    def smooth_map(self, iterations=5):
        for _ in range(iterations):
            new_map = [[1 for _ in range(self.map_size)] for _ in range(self.map_size)]
            for r in range(1, self.map_size - 1):
                for c in range(1, self.map_size - 1):
                    walls = self.count_wall_neighbors(r, c)
                    new_map[r][c] = 1 if walls >= 5 else 0
            self.map = new_map

    def carve_room(self, top, left, height, width):
        for r in range(top, top + height):
            for c in range(left, left + width):
                if self.in_bounds(r, c):
                    self.map[r][c] = 0

    def carve_horizontal(self, c1, c2, row, width=1):
        start, end = sorted((c1, c2))
        radius = max(0, width // 2)
        for c in range(start, end + 1):
            for dr in range(-radius, radius + 1):
                rr = row + dr
                if self.in_bounds(rr, c):
                    self.map[rr][c] = 0

    def carve_vertical(self, r1, r2, col, width=1):
        start, end = sorted((r1, r2))
        radius = max(0, width // 2)
        for r in range(start, end + 1):
            for dc in range(-radius, radius + 1):
                cc = col + dc
                if self.in_bounds(r, cc):
                    self.map[r][cc] = 0

    def carve_corridor(self, a, b, width=1):
        r1, c1 = a
        r2, c2 = b
        if random.random() < 0.5:
            self.carve_horizontal(c1, c2, r1, width)
            self.carve_vertical(r1, r2, c2, width)
        else:
            self.carve_vertical(r1, r2, c1, width)
            self.carve_horizontal(c1, c2, r2, width)

    def rects_intersect(self, a, b):
        a_top, a_left, a_h, a_w = a
        b_top, b_left, b_h, b_w = b
        return not (
            a_left + a_w <= b_left
            or b_left + b_w <= a_left
            or a_top + a_h <= b_top
            or b_top + b_h <= a_top
        )

    def place_rooms_and_corridors_doom(self):
        
        rooms = []
        centers = []

        target_rooms = max(6, int(self.num_rooms))
        attempts = target_rooms * 20
        padding = 2

        for _ in range(attempts):
            if len(rooms) >= target_rooms:
                break

            room_w = random.randint(5, 11)
            room_h = random.randint(5, 11)
            top = random.randint(2, self.map_size - room_h - 3)
            left = random.randint(2, self.map_size - room_w - 3)

            candidate = (top, left, room_h, room_w)
            padded = (top - padding, left - padding, room_h + padding * 2, room_w + padding * 2)

            overlap = False
            for existing in rooms:
                ex_top, ex_left, ex_h, ex_w = existing
                ex_padded = (ex_top - padding, ex_left - padding, ex_h + padding * 2, ex_w + padding * 2)
                if self.rects_intersect(padded, ex_padded):
                    overlap = True
                    break
            if overlap:
                continue

            self.carve_room(top, left, room_h, room_w)
            rooms.append(candidate)
            centers.append((top + room_h // 2, left + room_w // 2))

        if not centers:
            center = (self.map_size // 2, self.map_size // 2)
            self.carve_room(center[0] - 3, center[1] - 3, 7, 7)
            centers = [center]

        self.room_centers = centers

        connected = [centers[0]]
        remaining = centers[1:]

        while remaining:
            best = None
            best_dist = float("inf")
            for a in connected:
                for b in remaining:
                    d = abs(a[0] - b[0]) + abs(a[1] - b[1])
                    if d < best_dist:
                        best_dist = d
                        best = (a, b)
            a, b = best

            self.carve_corridor(a, b, width=random.choice((2, 3))) 
            connected.append(b)
            remaining.remove(b)

        extra_links = max(1, len(centers) // 4)
        for _ in range(extra_links):
            a, b = random.sample(centers, 2)
            self.carve_corridor(a, b, width=random.choice((2, 3)))

        self.add_dead_end_spurs(count=max(8, self.map_size // 8), max_len=10)

        self.connect_all_regions()

    def add_dead_end_spurs(self, count=12, max_len=10):
        floors = [(r, c) for r in range(1, self.map_size - 1) for c in range(1, self.map_size - 1) if self.map[r][c] == 0]
        if not floors:
            return

        for _ in range(count):
            r, c = random.choice(floors)
            dr, dc = random.choice(((1, 0), (-1, 0), (0, 1), (0, -1)))
            length = random.randint(3, max_len)
            width = 2 
            rr, cc = r, c
            for _step in range(length):
                rr += dr
                cc += dc
                if not self.in_bounds(rr, cc) or rr in (0, self.map_size - 1) or cc in (0, self.map_size - 1):
                    break
                if self.map[rr][cc] == 0:
                    break
                if dr != 0:
                    self.carve_vertical(rr, rr, cc, width=width)
                else:
                    self.carve_horizontal(cc, cc, rr, width=width)

    def place_halls_and_rooms(self):
        centers = []
        room_attempts = self.num_rooms * 12

        for _ in range(room_attempts):
            if len(centers) >= self.num_rooms:
                break

            room_w = random.randint(7, 16)
            room_h = random.randint(6, 13)
            top = random.randint(2, self.map_size - room_h - 3)
            left = random.randint(2, self.map_size - room_w - 3)
            center = (top + room_h // 2, left + room_w // 2)

            overlap = False
            for rr, cc in centers:
                if abs(center[0] - rr) < 8 and abs(center[1] - cc) < 8:
                    overlap = True
                    break
            if overlap:
                continue

            self.carve_room(top, left, room_h, room_w)
            centers.append(center)

        if not centers:
            center = (self.map_size // 2, self.map_size // 2)
            self.carve_room(center[0] - 3, center[1] - 3, 7, 7)
            centers.append(center)

        unvisited = centers[:]
        connected = [unvisited.pop(0)]
        while unvisited:
            best_pair = None
            best_dist = float("inf")
            for a in connected:
                for b in unvisited:
                    dist = abs(a[0] - b[0]) + abs(a[1] - b[1])
                    if dist < best_dist:
                        best_dist = dist
                        best_pair = (a, b)
            a, b = best_pair
            self.carve_corridor(a, b, width=random.choice((2, 3)))
            connected.append(b)
            unvisited.remove(b)

        extra_links = min(4, len(centers) // 2)
        for _ in range(extra_links):
            a, b = random.sample(centers, 2)
            self.carve_corridor(a, b, width=random.choice((2, 3)))

    def flood_fill_regions(self):
        visited = [[False for _ in range(self.map_size)] for _ in range(self.map_size)]
        regions = []

        for r in range(1, self.map_size - 1):
            for c in range(1, self.map_size - 1):
                if self.map[r][c] != 0 or visited[r][c]:
                    continue
                stack = [(r, c)]
                visited[r][c] = True
                region = []
                while stack:
                    rr, cc = stack.pop()
                    region.append((rr, cc))
                    for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                        nr, nc = rr + dr, cc + dc
                        if self.in_bounds(nr, nc) and self.map[nr][nc] == 0 and not visited[nr][nc]:
                            visited[nr][nc] = True
                            stack.append((nr, nc))
                regions.append(region)

        return regions

    def closest_pair_between_regions(self, region_a, region_b):
        sample_a = random.sample(region_a, min(250, len(region_a)))
        sample_b = random.sample(region_b, min(250, len(region_b)))

        best_a = sample_a[0]
        best_b = sample_b[0]
        best_dist = float("inf")
        for a in sample_a:
            for b in sample_b:
                dist = abs(a[0] - b[0]) + abs(a[1] - b[1])
                if dist < best_dist:
                    best_dist = dist
                    best_a, best_b = a, b
        return best_a, best_b

    def connect_all_regions(self):
        regions = self.flood_fill_regions()
        if not regions:
            center = self.map_size // 2
            self.map[center][center] = 0
            return

        regions.sort(key=len, reverse=True)
        main_region = regions[0]
        for region in regions[1:]:
            a, b = self.closest_pair_between_regions(main_region, region)
            self.carve_corridor(a, b, width=random.choice((2, 3)))
            main_region.extend(region)

    def farthest_floor_from(self, start):
        queue = [start]
        dist = {start: 0}
        head = 0
        farthest = start

        while head < len(queue):
            current = queue[head]
            head += 1
            if dist[current] > dist[farthest]:
                farthest = current
            r, c = current
            for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nr, nc = r + dr, c + dc
                nxt = (nr, nc)
                if self.in_bounds(nr, nc) and self.map[nr][nc] == 0 and nxt not in dist:
                    dist[nxt] = dist[current] + 1
                    queue.append(nxt)

        return farthest

    def place_start_and_exit(self):
        if len(self.room_centers) >= 2:
            best_dist = 0
            start = self.room_centers[0]
            end = self.room_centers[-1]
            for c1 in self.room_centers:
                for c2 in self.room_centers:
                    dist = math.hypot(c1[0] - c2[0], c1[1] - c2[1])
                    if dist > best_dist:
                        best_dist = dist
                        start = c1
                        end = c2
        else:
            start = (self.map_size // 2, self.map_size // 2)
            end = (self.map_size // 2 + 5, self.map_size // 2 + 5)

        self.start_pos = start
        self.exit_pos = end
        self.map[start[0]][start[1]] = 2
        self.map[end[0]][end[1]] = 3

    def find_largest_area(self):
        self.points_of_maximum_distance = {}
        for i, row in enumerate(self.map):
            countleft = 0
            countright = 0

            for cell in row:
                if cell != 1:
                    countleft += 1
                else:
                    break

            for cell in reversed(row):
                if cell != 1:
                    countright += 1
                else:
                    break

            if countleft > countright:
                index = countleft
                length = countleft
            else:
                index = self.map_size - countright - 1
                length = countright
            self.points_of_maximum_distance[i] = (index, length)

    def export_to_json(self, filename="map.json"):
        with open(filename, "w") as f:
            json.dump(self.map, f)
        print(f"Map successfully exported to {filename}")

    def print_map(self):
        for row in self.map:
            print(" ".join(str(c) for c in row))


if __name__ == "__main__":
    m = MapGenerator(map_size=100, num_rooms=18)
    m.do_job()
    m.export_to_json()