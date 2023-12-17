enum class Dir(val dx: Int, val dy: Int) {
	Up(0, -1),
	Down(0, 1),
	Left(-1, 0),
	Right(1, 0);

	fun reverse(): Dir {
		return when (this) {
			Up -> Down
			Down -> Up
			Left -> Right
			Right -> Left
		}
	}
}

fun main() {
	data class Way(val dir: Dir, val straight: Int)
	class Step(val x: Int, val y: Int, val way: Way)

	fun part1(input: List<String>): Int {
		val map = input.map { line -> line.map { it.digitToInt() } }
		val grid = map.map { Array(it.size) { mutableMapOf<Way, Int>() } }
		grid[0][1][Way(Dir.Right, 1)] = map[0][1]
		grid[1][0][Way(Dir.Down, 1)] = map[1][0]
		val queue = ArrayDeque<Step>()
		queue.addLast(Step(1, 0, Way(Dir.Right, 1)))
		queue.addLast(Step(0, 1, Way(Dir.Down, 1)))
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			for (dir in Dir.entries) {
				if (dir == curr.way.dir.reverse()) continue
				val x = curr.x + dir.dx
				val y = curr.y + dir.dy
				if (x < 0 || y < 0 || x >= map[0].size || y >= map.size) continue
				if (dir == curr.way.dir && curr.way.straight >= 3) continue
				val nextHeatLoss = grid[curr.y][curr.x][curr.way]!! + map[y][x]
				val straight = if (dir == curr.way.dir) curr.way.straight + 1 else 1
				val way = Way(dir, straight)
				val heatLoss = grid[y][x][way]
				if (heatLoss == null || nextHeatLoss < heatLoss) {
					grid[y][x][way] = nextHeatLoss
					if (y == map.lastIndex && x == map[y].lastIndex) continue
					queue.addLast(Step(x, y, way))
				}
			}
		}
		return grid.last().last().values.min()
	}

	fun part2(input: List<String>): Int {
		val map = input.map { line -> line.map { it.digitToInt() } }
		val grid = map.map { Array(it.size) { mutableMapOf<Way, Int>() } }
		grid[0][1][Way(Dir.Right, 1)] = map[0][1]
		grid[1][0][Way(Dir.Down, 1)] = map[1][0]
		val queue = ArrayDeque<Step>()
		queue.addLast(Step(1, 0, Way(Dir.Right, 1)))
		queue.addLast(Step(0, 1, Way(Dir.Down, 1)))
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			for (dir in Dir.entries) {
				if (dir == curr.way.dir.reverse()) continue
				val x = curr.x + dir.dx
				val y = curr.y + dir.dy
				if (x < 0 || y < 0 || x >= map[0].size || y >= map.size) continue
				if (dir == curr.way.dir && curr.way.straight >= 10) continue
				if (dir != curr.way.dir && curr.way.straight < 4) continue
				val nextHeatLoss = grid[curr.y][curr.x][curr.way]!! + map[y][x]
				val straight = if (dir == curr.way.dir) curr.way.straight + 1 else 1
				if (y == map.lastIndex && x == map[y].lastIndex && straight < 4) continue
				val way = Way(dir, straight)
				val heatLoss = grid[y][x][way]
				if (heatLoss == null || nextHeatLoss < heatLoss) {
					grid[y][x][way] = nextHeatLoss
					if (y == map.lastIndex && x == map[y].lastIndex) continue
					queue.addLast(Step(x, y, way))
				}
			}
		}
		return grid.last().last().values.min()
	}

	val testInput = readInput("Day17_test")
	check(part1(testInput) == 102)
	check(part2(readInput("Day17_test2")) == 71)
	check(part2(testInput) == 94)

	val input = readInput("Day17")
	part1(input).println()
	part2(input).println()
}
