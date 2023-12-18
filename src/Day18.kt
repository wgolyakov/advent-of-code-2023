import java.util.TreeMap

fun main() {
	fun part1(input: List<String>): Int {
		var x = 0
		var y = 0
		var xMin = 0
		var yMin = 0
		var xMax = 0
		var yMax = 0
		val loop = mutableSetOf(0 to 0)
		for (line in input) {
			val (dir, dist) = line.split(' ').take(2).let { it[0][0] to it[1].toInt() }
			when (dir) {
				'U' -> for (i in 0 until dist) { y--; loop.add(x to y) }
				'D' -> for (i in 0 until dist) { y++; loop.add(x to y) }
				'L' -> for (i in 0 until dist) { x--; loop.add(x to y) }
				'R' -> for (i in 0 until dist) { x++; loop.add(x to y) }
				else -> error("Wrong direction: $dir")
			}
			if (x < xMin) xMin = x
			if (y < yMin) yMin = y
			if (x > xMax) xMax = x
			if (y > yMax) yMax = y
		}

		val grid = loop.toMutableSet()
		for (y in yMin .. yMax) {
			var interior = false
			var hor = false
			var horUp = false
			var horDown = false
			for (x in xMin .. xMax) {
				if (x to y in loop) {
					val up = x to y - 1 in loop
					val down = x to y + 1 in loop
					if (hor) {
						if (up != down) {
							hor = false
							if (horUp && down || horDown && up) interior = !interior
						}
					} else {
						if (up && down) interior = !interior
						if (up != down) {
							hor = true
							horUp = up
							horDown = down
						}
					}
				} else {
					if (interior) grid.add(x to y)
				}
			}
		}
		return grid.size
	}

	fun inLoop(x: Int, y: Int,
			   horRanges: Map<Int, TreeMap<Int, IntRange>>,
			   verRanges: Map<Int, TreeMap<Int, IntRange>>): Boolean {
		return verRanges[x]?.floorEntry(y)?.value?.contains(y) == true ||
				horRanges[y]?.floorEntry(x)?.value?.contains(x) == true
	}

	fun part2(input: List<String>): Long {
		var x = 0
		var y = 0
		val horRanges = mutableMapOf<Int, TreeMap<Int, IntRange>>()
		val verRanges = mutableMapOf<Int, TreeMap<Int, IntRange>>()
		for (line in input) {
			val color = line.split(' ').last()
			val dist = color.substring(2, 7).toInt(16)
			val dir = color[7]
			when (dir) {
				'3' -> { verRanges.getOrPut(x) { TreeMap() }[y - dist] = y - dist .. y; y -= dist }
				'1' -> { verRanges.getOrPut(x) { TreeMap() }[y] = y .. y + dist; y += dist }
				'2' -> { horRanges.getOrPut(y) { TreeMap() }[x - dist] = x - dist .. x; x -= dist }
				'0' -> { horRanges.getOrPut(y) { TreeMap() }[x] = x .. x + dist; x += dist }
				else -> error("Wrong direction: $dir")
			}
		}

		var count = 0L
		var xCount = 0L
		val yPoints = horRanges.keys.flatMap { listOf(it, it + 1) }.sorted().dropLast(1)
		var yPrev = 0
		for (y in yPoints) {
			if (y - yPrev > 1) count += xCount * (y - yPrev - 1)
			xCount = 0
			var interior = false
			var hor = false
			var horUp = false
			var horDown = false
			val xPoints = (verRanges.keys +
					(horRanges[y]?.values?.flatMap { listOf(it.first, it.last) } ?: emptyList())).sorted()
			var xPrev = xPoints.first()
			for (x in xPoints) {
				if (inLoop(x, y, horRanges, verRanges)) {
					val up = inLoop(x, y - 1, horRanges, verRanges)
					val down = inLoop(x, y + 1, horRanges, verRanges)
					if (hor) {
						xCount += x - xPrev
						if (up != down) {
							hor = false
							if (horUp && down || horDown && up) interior = !interior
						}
					} else {
						if (interior) xCount += x - xPrev else xCount++
						if (up && down) {
							interior = !interior
						} else if (up != down) {
							hor = true
							horUp = up
							horDown = down
						}
					}
				} else {
					if (interior) xCount += x - xPrev
				}
				xPrev = x
			}
			count += xCount
			yPrev = y
		}
		return count
	}

	val testInput = readInput("Day18_test")
	check(part1(testInput) == 62)
	check(part2(testInput) == 952408144115L)

	val input = readInput("Day18")
	part1(input).println()
	part2(input).println()
}
