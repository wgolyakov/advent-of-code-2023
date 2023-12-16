import kotlin.math.max

enum class Direction(val dx: Int, val dy: Int) {
	Up(0, -1),
	Down(0, 1),
	Left(-1, 0),
	Right(1, 0)
}

fun main() {
	class Beam(val x: Int, val y: Int, val dir: Direction)

	fun light(input: List<String>, startBeam: Beam): Int {
		val beams = mutableListOf(startBeam)
		val energized = input.map { Array(it.length) { mutableMapOf<Direction, Boolean>() } }
		while (beams.isNotEmpty()) {
			val beam = beams.removeLast()
			var x = beam.x
			var y = beam.y
			var dir = beam.dir
			while (true) {
				if (x < 0 || y < 0 || x >= input[0].length || y >= input.size) break
				if (energized[y][x].containsKey(dir)) break
				energized[y][x][dir] = true
				when (input[y][x]) {
					'/' -> dir = when (dir) {
						Direction.Up -> Direction.Right
						Direction.Down -> Direction.Left
						Direction.Left -> Direction.Down
						Direction.Right -> Direction.Up
					}
					'\\' -> dir = when (dir) {
						Direction.Up -> Direction.Left
						Direction.Down -> Direction.Right
						Direction.Left -> Direction.Up
						Direction.Right -> Direction.Down
					}
					'|' -> if (dir == Direction.Left || dir == Direction.Right) {
						dir = Direction.Up
						beams.add(Beam(x, y + 1, Direction.Down))
					}
					'-' -> if (dir == Direction.Up || dir == Direction.Down) {
						dir = Direction.Left
						beams.add(Beam(x + 1, y, Direction.Right))
					}
				}
				x += dir.dx
				y += dir.dy
			}
		}
		return energized.sumOf { row -> row.count { it.isNotEmpty() } }
	}

	fun part1(input: List<String>): Int {
		return light(input, Beam(0, 0, Direction.Right))
	}

	fun part2(input: List<String>): Int {
		var maxEnergy = -1
		for (x in input[0].indices) {
			maxEnergy = max(maxEnergy, light(input, Beam(x, 0, Direction.Down)))
			maxEnergy = max(maxEnergy, light(input, Beam(x, input.lastIndex, Direction.Up)))
		}
		for (y in input.indices) {
			maxEnergy = max(maxEnergy, light(input, Beam(0, y, Direction.Right)))
			maxEnergy = max(maxEnergy, light(input, Beam(input[0].lastIndex, y, Direction.Left)))
		}
		return maxEnergy
	}

	val testInput = readInput("Day16_test")
	check(part1(testInput) == 46)
	check(part2(testInput) == 51)

	val input = readInput("Day16")
	part1(input).println()
	part2(input).println()
}
