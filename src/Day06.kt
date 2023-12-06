fun main() {
	fun part1(input: List<String>): Int {
		val times = input[0].substringAfter("Time:").trim().split(" +".toRegex()).map { it.toInt() }
		val distances = input[1].substringAfter("Distance:").trim().split(" +".toRegex()).map { it.toInt() }
		var mul = 1
		for (i in times.indices) {
			val time = times[i]
			val distance = distances[i]
			var winCount = 0
			for (buttonTime in 0 .. time) {
				val s = buttonTime * (time - buttonTime)
				if (s > distance) winCount++
			}
			mul *= winCount
		}
		return mul
	}

	fun part2(input: List<String>): Int {
		val time = input[0].substringAfter("Time:").trim().filter { it != ' ' }.toInt()
		val distance = input[1].substringAfter("Distance:").trim().filter { it != ' ' }.toLong()
		for (buttonTime in 0 .. time) {
			val s = buttonTime.toLong() * (time - buttonTime)
			if (s > distance) {
				return time + 1 - buttonTime * 2
			}
		}
		return 0
	}

	val testInput = readInput("Day06_test")
	check(part1(testInput) == 288)
	check(part2(testInput) == 71503)

	val input = readInput("Day06")
	part1(input).println()
	part2(input).println()
}
