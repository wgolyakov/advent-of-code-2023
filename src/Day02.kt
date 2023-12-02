fun main() {
	class Subset(val red: Int, val green: Int, val blue: Int) {
		fun possible(max: Subset) = red <= max.red && green <= max.green && blue <= max.blue
	}

	class Game(val num: Int, val subsets: List<Subset>) {
		fun possible(max: Subset) = subsets.all { it.possible(max) }
		fun power() = subsets.maxOf { it.red } * subsets.maxOf { it.green } * subsets.maxOf { it.blue }
	}

	fun parse(input: List<String>): List<Game> {
		return input.map { line ->
			val (numPart, subPart) = line.split(':')
			val num = numPart.split(' ').last().toInt()
			val subsets = subPart.split(';').map {
				val subset = mutableMapOf<String, Int>()
				for (cube in it.split(',')) {
					val (value, color) = cube.trim().split(' ')
					subset[color] = value.toInt()
				}
				Subset(subset["red"] ?: 0, subset["green"] ?: 0, subset["blue"] ?: 0)
			}
			Game(num, subsets)
		}
	}

	fun part1(input: List<String>): Int {
		val games = parse(input)
		val maxBag = Subset(12, 13, 14)
		return games.filter { it.possible(maxBag) }.sumOf { it.num }
	}

	fun part2(input: List<String>): Int {
		val games = parse(input)
		return games.sumOf { it.power() }
	}

	val testInput = readInput("Day02_test")
	check(part1(testInput) == 8)
	check(part2(testInput) == 2286)

	val input = readInput("Day02")
	part1(input).println()
	part2(input).println()
}
