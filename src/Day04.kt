import kotlin.math.pow

fun main() {
	fun winCount(line: String): Int {
		val (winPart, myPart) = line.split(':').last().split('|')
		val winNumbers = winPart.trim().split(' ').filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
		val myNumbers = myPart.trim().split(' ').filter { it.isNotEmpty() }.map { it.toInt() }
		return myNumbers.count { it in winNumbers }
	}

	fun part1(input: List<String>): Int {
		var sum = 0
		for (line in input) {
			val winCount = winCount(line)
			if (winCount > 0) sum += 2.0.pow(winCount - 1).toInt()
		}
		return sum
	}

	fun part2(input: List<String>): Int {
		val cards = IntArray(input.size) { 1 }
		for ((n, line) in input.withIndex()) {
			val winCount = winCount(line)
			for (i in 0 until winCount) {
				cards[n + 1 + i] += cards[n]
			}
		}
		return cards.sum()
	}

	val testInput = readInput("Day04_test")
	check(part1(testInput) == 13)
	check(part2(testInput) == 30)

	val input = readInput("Day04")
	part1(input).println()
	part2(input).println()
}
