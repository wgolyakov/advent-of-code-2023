fun main() {
	fun adjacentToSymbol(grid: List<String>, xEnd: Int, y0: Int, size: Int): Boolean {
		for (y in y0 - 1 .. y0 + 1) {
			if (y < 0 || y >= grid.size) continue
			for (x in xEnd - size - 1 .. xEnd) {
				if (x < 0 || x >= grid[y].length) continue
				val c = grid[y][x]
				if (!c.isDigit() && c != '.') return true
			}
		}
		return false
	}

	fun part1(input: List<String>): Int {
		var sum = 0
		for ((y, line) in input.withIndex()) {
			val num = StringBuilder()
			for ((x, c) in line.withIndex()) {
				if (c.isDigit()) {
					num.append(c)
				} else if (num.isNotEmpty()) {
					if (adjacentToSymbol(input, x, y, num.length)) {
						sum += num.toString().toInt()
					}
					num.clear()
				}
			}
			if (num.isNotEmpty() && adjacentToSymbol(input, line.length, y, num.length)) {
				sum += num.toString().toInt()
			}
		}
		return sum
	}

	fun addGears(grid: List<String>, gears: MutableMap<Pair<Int, Int>, MutableList<Int>>,
				 xEnd: Int, y0: Int, num: StringBuilder) {
		for (y in y0 - 1 .. y0 + 1) {
			if (y < 0 || y >= grid.size) continue
			for (x in xEnd - num.length - 1 .. xEnd) {
				if (x < 0 || x >= grid[y].length) continue
				if (grid[y][x] != '*') continue
				gears.getOrPut(x to y) { mutableListOf() }.add(num.toString().toInt())
			}
		}
	}

	fun part2(input: List<String>): Int {
		val gears = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()
		for ((y, line) in input.withIndex()) {
			val num = StringBuilder()
			for ((x, c) in line.withIndex()) {
				if (c.isDigit()) {
					num.append(c)
				} else if (num.isNotEmpty()) {
					addGears(input, gears, x, y, num)
					num.clear()
				}
			}
			if (num.isNotEmpty()) {
				addGears(input, gears, line.length, y, num)
			}
		}
		return gears.values.filter { it.size == 2 }.sumOf { it[0] * it[1] }
	}

	val testInput = readInput("Day03_test")
	check(part1(testInput) == 4361)
	check(part2(testInput) == 467835)

	val input = readInput("Day03")
	part1(input).println()
	part2(input).println()
}
