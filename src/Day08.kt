fun main() {
	fun parse(input: List<String>): Pair<String, Map<String, Pair<String, String>>> {
		val instruction = input[0]
		val network = mutableMapOf<String, Pair<String, String>>()
		for (line in input.drop(2)) {
			val (node, left, right) = Regex("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)")
				.matchEntire(line)!!.groupValues.takeLast(3)
			network[node] = left to right
		}
		return instruction to network
	}

	fun part1(input: List<String>): Int {
		val (instruction, network) = parse(input)
		var node = "AAA"
		var i = 0
		var step = 0
		while (node != "ZZZ") {
			val next = network[node]!!
			node = if (instruction[i++] == 'L') next.first else next.second
			if (i >= instruction.length) i = 0
			step++
		}
		return step
	}

	fun countPath(startNode: String, instruction: String, network: Map<String, Pair<String, String>>): Pair<Int, Int> {
		var node = startNode
		var i = 0
		var step = 0
		while (node[2] != 'Z') {
			val next = network[node]!!
			node = if (instruction[i++] == 'L') next.first else next.second
			if (i >= instruction.length) i = 0
			step++
		}
		val offset = step
		step = 0
		do {
			val next = network[node]!!
			node = if (instruction[i++] == 'L') next.first else next.second
			if (i >= instruction.length) i = 0
			step++
		} while (node[2] != 'Z')
		return offset to step
	}

	fun gcd(x: Long, y: Long): Long = if (y == 0L) x else gcd(y, x % y)

	fun lcm(numbers: List<Long>): Long = numbers.reduce { x, y -> x * (y / gcd(x, y)) }

	fun part2(input: List<String>): Long {
		val (instruction, network) = parse(input)
		val nodes = network.keys.filter { it[2] == 'A' }
		val periods = mutableListOf<Long>()
		for (node in nodes) {
			val (offset, period) = countPath(node, instruction, network)
			check(offset == period)
			periods.add(period.toLong())
		}
		return lcm(periods)
	}

	val testInput = readInput("Day08_test")
	check(part1(testInput) == 2)
	val testInput2 = readInput("Day08_test2")
	check(part1(testInput2) == 6)
	val testInput3 = readInput("Day08_test3")
	check(part2(testInput3) == 6L)

	val input = readInput("Day08")
	part1(input).println()
	part2(input).println()
}
