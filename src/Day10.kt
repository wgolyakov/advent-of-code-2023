fun main() {
	fun findStart(input: List<String>): Pair<Int, Int> {
		for ((y, line) in input.withIndex()) {
			for ((x, c) in line.withIndex()) {
				if (c == 'S') return x to y
			}
		}
		error("Starting position not found")
	}

	fun findFirstStep(input: List<String>, x: Int, y: Int): Pair<Int, Int> {
		if (x > 0 && input[y][x - 1] in "-LF") return x - 1 to y
		if (x < input[y].length - 1 && input[y][x + 1] in "-J7") return x + 1 to y
		if (y > 0 && input[y - 1][x] in "|F7") return x to y - 1
		if (y < input.size - 1 && input[y + 1][x] in "|LJ") return x to y + 1
		error("First step not found")
	}

	fun findNextStep(input: List<String>, x: Int, y: Int, xp: Int, yp: Int): Pair<Int, Int> {
		val c = input[y][x]
		if (x > 0 && x - 1 != xp && c in "-J7") return x - 1 to y
		if (x < input[y].length - 1 && x + 1 != xp && c in "-LF") return x + 1 to y
		if (y > 0 && y - 1 != yp && c in "|LJ") return x to y - 1
		if (y < input.size - 1 && y + 1 != yp && c in "|F7") return x to y + 1
		error("Next step not found ($x, $y)")
	}

	fun part1(input: List<String>): Int {
		val (x0, y0) = findStart(input)
		var (x, y) = findFirstStep(input, x0, y0)
		var (xp, yp) = x0 to y0
		var step = 1
		while (x != x0 || y != y0) {
			val (xi, yi) = findNextStep(input, x, y, xp, yp)
			xp = x; yp = y
			x = xi;	y = yi
			step++
		}
		return step / 2
	}

	fun findLoop(input: List<String>): Set<Pair<Int, Int>> {
		val (x0, y0) = findStart(input)
		var (x, y) = findFirstStep(input, x0, y0)
		var (xp, yp) = x0 to y0
		val loop = mutableSetOf(x0 to y0, x to y)
		while (x != x0 || y != y0) {
			val (xi, yi) = findNextStep(input, x, y, xp, yp)
			xp = x; yp = y
			x = xi;	y = yi
			loop.add(x to y)
		}
		return loop
	}

	fun convertStart(input: List<String>, x: Int, y: Int): Char {
		val left = x > 0 && input[y][x - 1] in "-LF"
		val right = x < input[y].length - 1 && input[y][x + 1] in "-J7"
		val up = y > 0 && input[y - 1][x] in "|F7"
		val down = y < input.size - 1 && input[y + 1][x] in "|LJ"
		return when {
			!left && right && !up && down -> 'F'
			left && right && !up && !down -> '-'
			left && !right && !up && down -> '7'
			!left && !right && up && down -> '|'
			!left && right && up && !down -> 'L'
			left && !right && up && !down -> 'J'
			else -> error("Wrong starting position")
		}
	}

	fun part2(input: List<String>): Int {
		val loop = findLoop(input)
		var enclosed = 0
		for ((y, line) in input.withIndex()) {
			var enc = false
			var hor = false
			var horBegin = '.'
			for ((x, s) in line.withIndex()) {
				if (x to y in loop) {
					val c = if (s == 'S') convertStart(input, x, y) else s
					if (hor) {
						when (c) {
							'7' -> { hor = false; if (horBegin == 'L') enc = !enc }
							'J' -> { hor = false; if (horBegin == 'F') enc = !enc }
							'-' -> {}
							else -> error("Wrong loop: $c, $x, $y")
						}
					} else {
						when (c) {
							'F','L' -> { hor = true; horBegin = c }
							'|' -> enc = !enc
							else -> error("Wrong loop: $c, $x, $y")
						}
					}
				} else {
					if (enc) enclosed++
				}
			}
		}
		return enclosed
	}

	check(part1(readInput("Day10_test")) == 4)
	check(part1(readInput("Day10_test2")) == 8)
	check(part2(readInput("Day10_test3")) == 4)
	check(part2(readInput("Day10_test4")) == 4)
	check(part2(readInput("Day10_test5")) == 8)
	check(part2(readInput("Day10_test6")) == 10)

	val input = readInput("Day10")
	part1(input).println()
	part2(input).println()
}
