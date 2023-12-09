fun main() {
	fun part1(input: List<String>): Int {
		var sum = 0
		for (line in input) {
			val data = mutableListOf<MutableList<Int>>()
			var list = line.split(' ').map { it.toInt() }.toMutableList()
			data.add(list)
			do {
				list = list.windowed(2).map { it[1] - it[0] }.toMutableList()
				data.add(list)
			} while(list.any { it != 0 })
			list.add(0)
			for (i in data.size - 2 downTo 0) {
				data[i].add(data[i].last() + data[i + 1].last())
			}
			sum += data.first().last()
		}
		return sum
	}

	fun part2(input: List<String>): Int {
		var sum = 0
		for (line in input) {
			val data = mutableListOf<MutableList<Int>>()
			var list = line.split(' ').map { it.toInt() }.toMutableList()
			data.add(list)
			do {
				list = list.windowed(2).map { it[1] - it[0] }.toMutableList()
				data.add(list)
			} while(list.any { it != 0 })
			list.add(0, 0)
			for (i in data.size - 2 downTo 0) {
				data[i].add(0, data[i].first() - data[i + 1].first())
			}
			sum += data.first().first()
		}
		return sum
	}

	val testInput = readInput("Day09_test")
	check(part1(testInput) == 114)
	check(part2(testInput) == 2)

	val input = readInput("Day09")
	part1(input).println()
	part2(input).println()
}
