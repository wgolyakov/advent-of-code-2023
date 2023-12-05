fun main() {
	class Range(val destination: Long, val source: Long, val length: Long) {
		operator fun contains(x: Long) = x >= source && x < source + length

		fun sourceEnd() = source + length - 1

		fun map(x: Long) = x - source + destination

		fun map(range: LongRange): Pair<List<LongRange>, List<LongRange>> {
			val allRanges = setOf(range.first, range.last, source - 1, source, sourceEnd(), sourceEnd() + 1)
				.filter { it in range }.sorted().windowed(2).map { it[0]..it[1] }
				.filter { it.first in this == it.last in this }
			return allRanges.filter { it.first in this }.map { map(it.first) .. map(it.last) } to
					allRanges.filter { it.first !in this }
		}

		override fun toString() = "$source..${sourceEnd()}"
	}

	fun parse(input: List<String>): Pair<MutableList<Long>, List<List<Range>>> {
		val seeds = input[0].split(' ').drop(1).map { it.toLong() }.toMutableList()
		val maps = mutableListOf<MutableList<Range>>()
		for (line in input.drop(2)) {
			if (line.isEmpty()) continue
			if (line[0].isDigit()) {
				val range = line.split(' ').map { it.toLong() }
				maps.last().add(Range(range[0], range[1], range[2]))
			} else {
				maps.add(mutableListOf())
			}
		}
		return seeds to maps
	}

	fun part1(input: List<String>): Long {
		val (seeds, maps) = parse(input)
		for (map in maps) {
			for ((i, seed) in seeds.withIndex()) {
				val range = map.find { seed in it }
				if (range != null) {
					seeds[i] = range.map(seed)
				}
			}
		}
		return seeds.min()
	}

	fun part2(input: List<String>): Long {
		val (seeds, maps) = parse(input)
		var ranges = seeds.windowed(2, 2).map { it[0] until it[0] + it[1] }.toMutableList()
		for (map in maps) {
			val nextRanges = mutableListOf<LongRange>()
			while (ranges.isNotEmpty()) {
				var range: LongRange? = ranges.removeFirst()
				for (mapRange in map) {
					val (mapped, unmapped) = mapRange.map(range!!)
					nextRanges.addAll(mapped)
					if (unmapped.isEmpty()) {
						range = null
						break
					}
					range = unmapped[0]
					if (unmapped.size > 1) {
						ranges.addAll(unmapped.drop(1))
					}
				}
				if (range != null) nextRanges.add(range)
			}
			ranges = nextRanges
		}
		return ranges.minOf { it.first }
	}

	val testInput = readInput("Day05_test")
	check(part1(testInput) == 35L)
	check(part2(testInput) == 46L)

	val input = readInput("Day05")
	part1(input).println()
	part2(input).println()
}
