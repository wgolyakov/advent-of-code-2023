fun main() {
	fun part1(input: List<String>): Int {
		return input.sumOf { line ->
			("" + line.find { it.isDigit() } + line.findLast { it.isDigit() }).toInt()
		}
	}

	fun part2(input: List<String>): Int {
		val digits = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
		val digitsRev = digits.map { it.reversed() }
		val regex = ("\\d|" + digits.joinToString("|")).toRegex()
		val regexRev = ("\\d|" + digitsRev.joinToString("|")).toRegex()
		val map = digits.withIndex().associate { it.value to (it.index + 1).toString() }
		val mapRev = digitsRev.withIndex().associate { it.value to (it.index + 1).toString() }
		return input.sumOf { line ->
			val first = regex.find(line)!!.value
			val last = regexRev.find(line.reversed())!!.value
			("" + (map[first] ?: first) + (mapRev[last] ?: last)).toInt()
		}
	}

	val testInput = readInput("Day01_test")
	check(part1(testInput) == 142)
	val testInput2 = readInput("Day01_test2")
	check(part2(testInput2) == 281)

	val input = readInput("Day01")
	part1(input).println()
	part2(input).println()
}
