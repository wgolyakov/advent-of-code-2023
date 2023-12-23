enum class Dir23(val dx: Int, val dy: Int, val c: Char) {
	Down(0, 1, 'v'),
	Right(1, 0, '>'),
	Left(-1, 0, '<'),
	Up(0, -1, '^');

	fun reverse(): Dir23 {
		return when (this) {
			Up -> Down
			Down -> Up
			Left -> Right
			Right -> Left
		}
	}
}

fun main() {
	class Node(val num: Int, val exits: MutableMap<Node, Int>) {
		override fun equals(other: Any?) = num == (other as? Node)?.num
		override fun hashCode() = num.hashCode()
		override fun toString() = "$num"
	}
	class Step(val x: Int, val y: Int, val dir: Dir23, val distance: Int)
	class Step2(val x: Int, val y: Int, val dir: Dir23, val distance: Int, val node: Node)

	fun part1(input: List<String>): Int {
		var maxDistance = 0
		val start = Step(1, 0, Dir23.Down, 0)
		val end = Step(input.last().lastIndex - 1, input.lastIndex, Dir23.Down, -1)
		val queue = mutableListOf(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr.x == end.x && curr.y == end.y) {
				if (curr.distance > maxDistance) maxDistance = curr.distance
				continue
			}
			for (dir in Dir23.entries) {
				if (dir == curr.dir.reverse()) continue
				val x = curr.x + dir.dx
				val y = curr.y + dir.dy
				if (x < 0 || y < 0 || y >= input.size || x >= input[y].length) continue
				if (input[y][x] != '.' && input[y][x] != dir.c) continue
				queue.add(Step(x, y, dir, curr.distance + 1))
			}
		}
		return maxDistance
	}

	fun createGraph(input: List<String>): List<Node> {
		val graph = mutableListOf<Node>()
		val nodes = mutableMapOf<Pair<Int, Int>, Node>()
		var num = 0
		val startNode = Node(num++, mutableMapOf())
		val endNode = Node(num++, mutableMapOf())
		graph.add(startNode)
		graph.add(endNode)
		val start = Step2(1, 0, Dir23.Down, 0, startNode)
		val end = Step2(input.last().lastIndex - 1, input.lastIndex, Dir23.Down, 0, endNode)
		nodes[start.x to start.y] = startNode
		nodes[end.x to end.y] = endNode
		val queue = mutableListOf(start)
		while (queue.isNotEmpty()) {
			val curr = queue.removeFirst()
			if (curr.x == end.x && curr.y == end.y) {
				curr.node.exits[endNode] = curr.distance
				continue
			}
			val neighbors = listOf(Dir23.Down, Dir23.Right, Dir23.Left, Dir23.Up)
				.filter { it != curr.dir.reverse() }
				.filter { curr.x + it.dx >= 0 && curr.y + it.dy >= 0 &&
						curr.x + it.dx < input[0].length && curr.y + it.dy < input.size }
				.filter { input[curr.y + it.dy][curr.x + it.dx] != '#' }
			if (neighbors.size == 1) {
				val dir = neighbors.single()
				val x = curr.x + dir.dx
				val y = curr.y + dir.dy
				queue.add(Step2(x, y, dir, curr.distance + 1, curr.node))
			} else {
				var node = nodes[curr.x to curr.y]
				if (node != null) {
					curr.node.exits[node] = curr.distance
					node.exits[curr.node] = curr.distance
					continue
				}
				node = Node(num++, mutableMapOf())
				nodes[curr.x to curr.y] = node
				graph.add(node)
				curr.node.exits[node] = curr.distance
				node.exits[curr.node] = curr.distance
				for (dir in neighbors) {
					val x = curr.x + dir.dx
					val y = curr.y + dir.dy
					queue.add(Step2(x, y, dir, 1, node))
				}
			}
		}
		return graph
	}

	var maxDistance = 0

	fun recurs(node: Node, distance: Int, path: MutableSet<Int>) {
		if (node.num == 1) {
			if (maxDistance < distance) maxDistance = distance
			return
		}
		path.add(node.num)
		for ((child, s) in node.exits) {
			if (child.num in path) continue
			recurs(child, distance + s, path)
		}
		path.remove(node.num)
	}

	fun part2(input: List<String>): Int {
		val graph = createGraph(input)
		maxDistance = 0
		recurs(graph[0], 0, mutableSetOf())
		return maxDistance
	}

	val testInput = readInput("Day23_test")
	check(part1(testInput) == 94)
	check(part2(testInput) == 154)

	val input = readInput("Day23")
	part1(input).println()
	part2(input).println()
}
