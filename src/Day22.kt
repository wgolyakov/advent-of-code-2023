fun main() {
	data class Cube(var x: Int, var y: Int, var z: Int)

	class Brick(val num: Int, val cubes: List<Cube>) {
		val downCubes = cubes.filter { it.z == zMin() }
		val upCubes = cubes.filter { it.z == zMax() }
		val supportedBy = mutableSetOf<Brick>()
		val support = mutableSetOf<Brick>()

		fun zMin() = cubes.minOf { it.z }
		fun zMax() = cubes.maxOf { it.z }

		fun fall(allCubes: MutableSet<Cube>): Boolean {
			var isFall = false
			while (downCubes.all { it.z > 1 && Cube(it.x, it.y, it.z - 1) !in allCubes } ) {
				for (cube in cubes.toList()) {
					allCubes.remove(cube)
					cube.z--
					allCubes.add(cube)
				}
				isFall = true
			}
			return isFall
		}

		override fun equals(other: Any?) = num == (other as? Brick)?.num
		override fun hashCode() = num.hashCode()
		override fun toString() = num.toString()
		fun clone() = Brick(num, cubes.map { it.copy() })
	}

	fun parse(input: List<String>): List<Brick> {
		return input.withIndex().map { (num, line) ->
			val (c1, c2) = line.split('~').map { it.split(',') }
				.map { Cube(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
			val cubes = mutableListOf<Cube>()
			for (x in c1.x .. c2.x) {
				for (y in c1.y .. c2.y) {
					for (z in c1.z .. c2.z) {
						cubes.add(Cube(x, y, z))
					}
				}
			}
			Brick(num, cubes)
		}
	}

	fun fall(bricks: List<Brick>): Int {
		val allCubes = bricks.flatMap { it.cubes }.toMutableSet()
		var fallCount = 0
		for (brick in bricks.sortedBy { it.zMin() }) {
			val res = brick.fall(allCubes)
			if (res) fallCount++
		}
		return fallCount
	}

	fun fillSupport(bricks: List<Brick>) {
		val allCubes = bricks.flatMap { b -> b.cubes.map { it to b } }.associate { it }
		for (brick in bricks) {
			for (cube in brick.downCubes) {
				val supportByBrick = allCubes[Cube(cube.x, cube.y, cube.z - 1)]
				if (supportByBrick != null) brick.supportedBy.add(supportByBrick)
			}
			for (cube in brick.upCubes) {
				val supportBrick = allCubes[Cube(cube.x, cube.y, cube.z + 1)]
				if (supportBrick != null) brick.support.add(supportBrick)
			}
		}
	}

	fun part1(input: List<String>): Int {
		val bricks = parse(input)
		fall(bricks)
		fillSupport(bricks)
		return bricks.count { brick -> brick.support.all { it.supportedBy.size > 1 } }
	}

	fun part2(input: List<String>): Int {
		val bricks = parse(input)
		fall(bricks)
		var fallCount = 0
		for (disintegratedBrick in bricks) {
			val otherBricks = bricks.map { it.clone() } - disintegratedBrick
			val falls = fall(otherBricks)
			fallCount += falls
		}
		return fallCount
	}

	val testInput = readInput("Day22_test")
	check(part1(testInput) == 5)
	check(part2(testInput) == 7)

	val input = readInput("Day22")
	part1(input).println()
	part2(input).println()
}
