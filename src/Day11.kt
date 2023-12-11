import kotlin.math.absoluteValue

fun main() {
	fun part1(input: List<String>): Int {
		val space = input.map { StringBuilder(it) }.toMutableList()
		// Expand space
		for (y in space.size - 1 downTo 0) {
			if (space[y].all { it == '.' }) {
				space.add(y, StringBuilder(space[y]))
			}
		}
		for (x in space[0].length - 1 downTo 0) {
			if (space.all { it[x] == '.' }) {
				for (row in space) {
					row.insert(x, '.')
				}
			}
		}
		// Measure paths between galaxies
		var sum = 0
		for ((y, row) in space.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c != '#') continue
				for (x2 in x + 1 until row.length) {
					if (space[y][x2] != '#') continue
					val len = x2 - x
					sum += len
				}
				for (y2 in y + 1 until space.size) {
					for ((x2, c2) in space[y2].withIndex()) {
						if (c2 != '#') continue
						val len = (x2 - x).absoluteValue + y2 - y
						sum += len
					}
				}
			}
		}
		return sum
	}

	fun part2(input: List<String>, times: Int = 1_000_000): Long {
		// Find expanded rows and columns
		val expRows = input.withIndex().filter { row -> row.value.all { it == '.' } }.map { it.index }.toSet()
		val expCols = input[0].indices.filter { x -> input.all { it[x] == '.' } }.toSet()
		// Measure paths between galaxies
		var sum = 0L
		for ((y1, row) in input.withIndex()) {
			for ((x1, c) in row.withIndex()) {
				if (c != '#') continue
				for (x2 in x1 + 1 until row.length) {
					if (input[y1][x2] != '#') continue
					var len = x2 - x1
					for (x in x1 + 1 until x2) if (x in expCols) len += times - 1
					sum += len
				}
				for (y2 in y1 + 1 until input.size) {
					for ((x2, c2) in input[y2].withIndex()) {
						if (c2 != '#') continue
						var len = (x2 - x1).absoluteValue + y2 - y1
						if (x1 < x2) {
							for (x in x1 + 1 until x2) if (x in expCols) len += times - 1
						} else {
							for (x in x2 + 1 until x1) if (x in expCols) len += times - 1
						}
						for (y in y1 + 1 until y2) if (y in expRows) len += times - 1
						sum += len
					}
				}
			}
		}
		return sum
	}

	val testInput = readInput("Day11_test")
	check(part1(testInput) == 374)
	check(part2(testInput, 10) == 1030L)
	check(part2(testInput, 100) == 8410L)

	val input = readInput("Day11")
	part1(input).println()
	part2(input).println()
}
