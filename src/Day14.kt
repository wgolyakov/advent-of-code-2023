fun main() {
	fun tiltUp(grid: List<StringBuilder>) {
		for (x in grid[0].indices) {
			var yFree = -1
			for (y in grid.indices) {
				when (grid[y][x]) {
					'.' -> if (yFree == -1) yFree = y
					'#' -> yFree = -1
					'O' -> if (yFree != -1) {
						grid[yFree][x] = 'O'
						grid[y][x] = '.'
						yFree++
					}
				}
			}
		}
	}

	fun tiltLeft(grid: List<StringBuilder>) {
		for (y in grid.indices) {
			var xFree = -1
			for (x in grid[y].indices) {
				when (grid[y][x]) {
					'.' -> if (xFree == -1) xFree = x
					'#' -> xFree = -1
					'O' -> if (xFree != -1) {
						grid[y][xFree] = 'O'
						grid[y][x] = '.'
						xFree++
					}
				}
			}
		}
	}

	fun tiltDown(grid: List<StringBuilder>) {
		for (x in grid[0].indices) {
			var yFree = -1
			for (y in grid.indices.reversed()) {
				when (grid[y][x]) {
					'.' -> if (yFree == -1) yFree = y
					'#' -> yFree = -1
					'O' -> if (yFree != -1) {
						grid[yFree][x] = 'O'
						grid[y][x] = '.'
						yFree--
					}
				}
			}
		}
	}

	fun tiltRight(grid: List<StringBuilder>) {
		for (y in grid.indices) {
			var xFree = -1
			for (x in grid[y].indices.reversed()) {
				when (grid[y][x]) {
					'.' -> if (xFree == -1) xFree = x
					'#' -> xFree = -1
					'O' -> if (xFree != -1) {
						grid[y][xFree] = 'O'
						grid[y][x] = '.'
						xFree--
					}
				}
			}
		}
	}

	fun countLoad(grid: List<StringBuilder>): Int {
		return grid.reversed().withIndex().sumOf { it.value.count { c -> c == 'O' } * (it.index + 1) }
	}

	fun part1(input: List<String>): Int {
		val grid = input.map { StringBuilder(it) }
		tiltUp(grid)
		return countLoad(grid)
	}

	fun part2(input: List<String>): Int {
		val cycles = 1000000000
		val grid = input.map { StringBuilder(it) }
		val states = mutableListOf<List<String>>()
		var remainder = 0
		for (i in 1 .. cycles) {
			tiltUp(grid)
			tiltLeft(grid)
			tiltDown(grid)
			tiltRight(grid)
			val state = grid.map { it.toString() }
			val period = states.indexOf(state) + 1
			if (period != 0) {
				remainder = (cycles - i) % period
				break
			}
			states.add(0, state)
		}
		for (i in 1 .. remainder) {
			tiltUp(grid)
			tiltLeft(grid)
			tiltDown(grid)
			tiltRight(grid)
		}
		return countLoad(grid)
	}

	val testInput = readInput("Day14_test")
	check(part1(testInput) == 136)
	check(part2(testInput) == 64)

	val input = readInput("Day14")
	part1(input).println()
	part2(input).println()
}
