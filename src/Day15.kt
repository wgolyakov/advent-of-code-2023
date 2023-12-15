fun main() {
	fun hash(s: String): Int {
		return s.fold(0) { h, c -> (h + c.code) * 17 % 256 }
	}

	fun part1(input: List<String>): Int {
		return input[0].split(',').sumOf { hash(it) }
	}

	fun part2(input: List<String>): Int {
		val boxes = mutableMapOf<Int, MutableList<Pair<String, Int>>>()
		for (step in input[0].split(',')) {
			if (step.endsWith('-')) {
				val label = step.dropLast(1)
				val boxIndex = hash(label)
				boxes[boxIndex]?.removeAll { it.first == label }
			} else {
				val (label, focalLength) = step.split('=')
				val f = focalLength.toInt()
				val boxIndex = hash(label)
				val box = boxes.getOrPut(boxIndex) { mutableListOf() }
				val lensIndex = box.indexOfFirst { it.first == label }
				if (lensIndex == -1) {
					box.add(label to f)
				} else {
					box.removeAt(lensIndex)
					box.add(lensIndex, label to f)
				}
			}
		}
		return boxes.entries.sumOf { (ind, v) -> (ind + 1) * v.withIndex().sumOf { (it.index + 1) * it.value.second } }
	}

	check(hash("HASH") == 52)
	val testInput = readInput("Day15_test")
	check(part1(testInput) == 1320)
	check(part2(testInput) == 145)

	val input = readInput("Day15")
	part1(input).println()
	part2(input).println()
}
