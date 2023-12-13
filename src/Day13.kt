fun main() {
	fun parse(input: List<String>):List<List<String>> {
		val result = mutableListOf<List<String>>()
		var start = 0
		for ((i, line) in input.withIndex()) {
			if (line.isEmpty()) {
				result.add(input.subList(start, i))
				start = i + 1
			}
		}
		result.add(input.subList(start, input.size))
		return result
	}

	fun findVerticalCandidates(pattern: List<CharSequence>): List<Int> {
		val result = mutableListOf<Int>()
		for ((x1, x2) in pattern[0].indices.windowed(2)) {
			if (pattern.all { it[x1] == it[x2] }) result.add(x1)
		}
		return result
	}

	fun findHorizontalCandidates(pattern: List<CharSequence>): List<Int> {
		val result = mutableListOf<Int>()
		for ((y1, y2) in pattern.indices.windowed(2)) {
			if (pattern[y1].indices.all { pattern[y1][it] == pattern[y2][it] }) result.add(y1)
		}
		return result
	}

	fun checkVertical(pattern: List<CharSequence>, x: Int): Boolean {
		for (i in 1..x) {
			if (x + 1 + i >= pattern[0].length) break
			if (pattern.any { it[x - i] != it[x + 1 + i] }) return false
		}
		return true
	}

	fun checkHorizontal(pattern: List<CharSequence>, y: Int): Boolean {
		for (i in 1..y) {
			if (y + 1 + i >= pattern.size) break
			if (pattern[y - i].indices.any { pattern[y - i][it] != pattern[y + 1 + i][it] }) return false
		}
		return true
	}

	fun summarizeReflectionsVer(pattern: List<CharSequence>, skip: Int = -1): Int {
		var sum = 0
		for (x in findVerticalCandidates(pattern)) {
			if (checkVertical(pattern, x)) {
				val value = x + 1
				if (value != skip) sum += value
			}
		}
		return sum
	}

	fun summarizeReflectionsHor(pattern: List<CharSequence>, skip: Int = -1): Int {
		var sum = 0
		for (y in findHorizontalCandidates(pattern)) {
			if (checkHorizontal(pattern, y)) {
				val value = (y + 1) * 100
				if (value != skip) sum += value
			}
		}
		return sum
	}

	fun summarizeReflections(pattern: List<CharSequence>): Int {
		return summarizeReflectionsVer(pattern) + summarizeReflectionsHor(pattern)
	}

	fun findFixCandidatesVer(pattern: List<String>): Set<Pair<Int, Int>> {
		val result = mutableSetOf<Pair<Int, Int>>()
		for ((x1, x2) in pattern[0].indices.windowed(2)) {
			if (pattern.count { it[x1] != it[x2] } == 1) {
				val y = pattern.withIndex().find { it.value[x1] != it.value[x2] }!!.index
				result.add(x1 to y)
			}
		}
		for (x in findVerticalCandidates(pattern)) {
			for (i in 1..x) {
				if (x + 1 + i >= pattern[0].length) break
				val diff = pattern.count { it[x - i] != it[x + 1 + i] }
				if (diff > 1) break
				if (diff == 1) {
					val y = pattern.withIndex().find { it.value[x - i] != it.value[x + 1 + i] }!!.index
					result.add(x - i to y)
					break
				}
			}
		}
		return result
	}

	fun findFixCandidatesHor(pattern: List<String>): Set<Pair<Int, Int>> {
		val result = mutableSetOf<Pair<Int, Int>>()
		for ((y1, y2) in pattern.indices.windowed(2)) {
			if (pattern[y1].indices.count { pattern[y1][it] != pattern[y2][it] } == 1) {
				val x = pattern[y1].indices.find { pattern[y1][it] != pattern[y2][it] }!!
				result.add(x to y1)
			}
		}
		for (y in findHorizontalCandidates(pattern)) {
			for (i in 1..y) {
				if (y + 1 + i >= pattern.size) break
				val diff = pattern[y - i].indices.count { pattern[y - i][it] != pattern[y + 1 + i][it] }
				if (diff > 1) break
				if (diff == 1) {
					val x = pattern[y - i].indices.find { pattern[y - i][it] != pattern[y + 1 + i][it] }!!
					result.add(x to y - i)
					break
				}
			}
		}
		return result
	}

	fun summarizeFixedReflections(pattern: List<String>): Int {
		val oldValue = summarizeReflections(pattern)
		var sum = 0
		for ((x, y) in findFixCandidatesVer(pattern)) {
			val grid = pattern.map { StringBuilder(it) }
			grid[y][x] = if (grid[y][x] == '.') '#' else '.'
			sum += summarizeReflectionsVer(grid, oldValue)
		}
		for ((x, y) in findFixCandidatesHor(pattern)) {
			val grid = pattern.map { StringBuilder(it) }
			grid[y][x] = if (grid[y][x] == '.') '#' else '.'
			sum += summarizeReflectionsHor(grid, oldValue)
		}
		return sum
	}

	fun part1(input: List<String>): Int {
		return parse(input).sumOf { summarizeReflections(it) }
	}

	fun part2(input: List<String>): Int {
		return parse(input).sumOf { summarizeFixedReflections(it) }
	}

	val testInput = readInput("Day13_test")
	check(part1(testInput) == 405)
	check(part2(testInput) == 400)

	val input = readInput("Day13")
	part1(input).println()
	part2(input).println()
}
