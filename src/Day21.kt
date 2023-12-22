import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

fun main() {
	fun findStart(input: List<String>): Pair<Int, Int> {
		for ((y, row) in input.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c == 'S') return x to y
			}
		}
		error("Starting position not found")
	}

	fun part1(input: List<String>, maxSteps: Int = 64): Int {
		val (x0, y0) = findStart(input)
		var grid = input.map { StringBuilder(it) }
		grid[y0][x0] = 'O'
		for (i in 0 until maxSteps) {
			val next = input.map { StringBuilder(it) }
			for ((y, row) in grid.withIndex()) {
				for ((x, c) in row.withIndex()) {
					if (c == 'O') {
						val neighbors = listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
							.filter { (x, y) -> x >= 0 && x < row.length && y >= 0 && y < grid.size }
							.filter { (x, y) -> grid[y][x] != '#' }
						for ((xn, yn) in neighbors) next[yn][xn] = 'O'
					}
				}
			}
			grid = next
		}
		return grid.sumOf { row -> row.count { it == 'O' } }
	}

	fun countSteps(input: List<String>, x0: Int, y0: Int): List<IntArray> {
		val grid = input.map { IntArray(it.length) { -1 } }
		grid[y0][x0] = 0
		val queue = mutableListOf(x0 to y0)
		while (queue.isNotEmpty()) {
			val (x, y) = queue.removeFirst()
			val distance = grid[y][x]
			val neighbors = listOf(x + 1 to y, x - 1 to y, x to y + 1, x to y - 1)
				.filter { (x, y) -> x >= 0 && y >= 0 && y < input.size && x < input[y].length }
				.filter { (x, y) -> input[y][x] != '#' }
			for ((xn, yn) in neighbors) {
				if (grid[yn][xn] != -1) continue
				grid[yn][xn] = distance + 1
				queue.add(xn to yn)
			}
		}
		return grid
	}

	fun fillReachableTiles(input: List<String>, cGrid: List<IntArray>, x0: Int, y0: Int, r: Int): List<StringBuilder> {
		val grid = input.map { StringBuilder(it) }
		for ((y, row) in grid.withIndex()) {
			for ((x, c) in row.withIndex()) {
				if (c != '#' && abs(x - x0 + y - y0) % 2 == r && cGrid[y][x] != -1) {
					grid[y][x] = 'O'
				}
			}
		}
		return grid
	}

	fun part2(input: List<String>, maxSteps: Int = 26501365): Long {
		val h = input.size
		val h2 = h / 2
		val r = maxSteps % 2
		val (x0, y0) = findStart(input)

		val cGrid = countSteps(input, x0, y0)
		val sGridEven = fillReachableTiles(input, cGrid, x0, y0, r)
		val sGridOdd = fillReachableTiles(input, cGrid, x0, y0, 1 - r)
		val cellStepsEven = sGridEven.sumOf { row -> row.count { it == 'O' } }
		val cellStepsOdd = sGridOdd.sumOf { row -> row.count { it == 'O' } }

		val g = 7
		val g2 = g / 2
		val grid7 = mutableListOf<String>()
		for (a in 0 until g) {
			for (row in input) {
				grid7.add(row.repeat(g))
			}
		}
		val cGrid7 = countSteps(grid7, h * g2 + x0, h * g2 + y0)

		val m = (maxSteps + h2) / h
		var count = 0L
		for (a in -m .. m) {
			val bMin = max(abs(a) - m - 2, -m)
			val bMax = min(m - abs(a) + 2, m)
			for (b in bMin .. bMax) {
				val d = abs(a) + abs(b)
				if (d > m + 1) continue
				if (d < m - 1) {
					count += if (d % 2 == 0) cellStepsEven else cellStepsOdd
					continue
				}
				val p = a.sign
				val q = b.sign
				for (x in 0 until h) {
					for (y in 0 until h) {
						val rx = a * h - h2 + x
						val ry = b * h - h2 + y
						if (abs(rx + ry) % 2 != r) continue
						if (ry > rx + maxSteps) continue
						if (ry < rx - maxSteps) continue
						if (ry > -rx + maxSteps) continue
						if (ry < -rx - maxSteps) continue
						if (input[y][x] == '#') continue
						var len: Int
						if (abs(a) <= g2 && abs(b) <= g2) {
							val s = (g2 + a) * h + x
							val t = (g2 + b) * h + y
							len = cGrid7[t][s]
							if (len == -1) continue
						} else {
							val u = min(abs(a), g2) * p
							val v = min(abs(b), g2) * q
							val s = (g2 + u) * h + x
							val t = (g2 + v) * h + y
							len = cGrid7[t][s]
							if (len == -1) continue
							len +=  (abs(a) - abs(u)) * h + (abs(b) - abs(v)) * h
						}
						if (len <= maxSteps) count++
					}
				}
			}
		}
		return count
	}

	val testInput = readInput("Day21_test")
	check(part1(testInput, 6) == 16)
	check(part2(testInput, 6) == 16L)
	check(part2(testInput, 10) == 50L)
	check(part2(testInput, 50) == 1594L)
	check(part2(testInput, 100) == 6536L)
	check(part2(testInput, 500) == 167004L)
	check(part2(testInput, 1000) == 668697L)
	check(part2(testInput, 5000) == 16733044L)

	val input = readInput("Day21")
	part1(input).println()
	part2(input).println()
	// 3748
	// 616951804315987  ~5 minutes
}
