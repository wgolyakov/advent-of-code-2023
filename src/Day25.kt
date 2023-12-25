fun main() {
	fun parse(input: List<String>): MutableMap<String, MutableSet<String>> {
		val graph = mutableMapOf<String, MutableSet<String>>()
		for (line in input) {
			val (name, s) = line.split(": ")
			val list = s.split(' ')
			graph.getOrPut(name) { mutableSetOf() }.addAll(list)
			for (child in list) graph.getOrPut(child) { mutableSetOf() }.add(name)
		}
		return graph
	}

	fun part1(input: List<String>): Int {
		val graph = parse(input)
		val min = graph.minBy { it.value.size }
		val gr1 = mutableSetOf(min.key)
		val gr2 = (graph.keys - gr1).toMutableSet()
		val interGrWires = min.value.map { setOf(min.key, it) }.toMutableSet()
		while (interGrWires.size > 3) {
			val node = gr2.minBy { n -> graph[n]!!.sumOf { if (it == n) 0L else (if (it in gr1) -1 else 1) } }
			gr1.add(node)
			gr2.remove(node)
			for (node2 in graph[node]!!) {
				if (node2 == node) continue
				if (node2 in gr1) {
					interGrWires.remove(setOf(node, node2))
				} else {
					interGrWires.add(setOf(node, node2))
				}
			}
		}
		return gr1.size * gr2.size
	}

	val testInput = readInput("Day25_test")
	check(part1(testInput) == 54)

	val input = readInput("Day25")
	part1(input).println()
}
