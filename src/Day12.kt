fun main() {
	fun checkGroups(records: CharSequence, groups: List<Int>): Boolean {
		var i = 0
		var gr = 0
		for (c in records) {
			if (c == '#') {
				gr++
			} else if (gr > 0)  {
				if (i >= groups.size || groups[i++] != gr) return false
				gr = 0
			}
		}
		if (gr > 0 && (i >= groups.size || groups[i++] != gr)) return false
		return i == groups.size
	}

	fun part1(input: List<String>): Int {
		var sum = 0
		for (line in input) {
			val (pattern, strGroups) = line.split(' ')
			val groups = strGroups.split(',').map { it.toInt() }
			val unkIndexes = pattern.withIndex().filter { it.value == '?' }.map { it.index }
			val records = StringBuilder(pattern)
			val maxMask = 1 shl unkIndexes.size
			var arrangementCount = 0
			for (mask in 0 until maxMask) {
				var bitMask = 1
				for (bit in unkIndexes.indices) {
					records[unkIndexes[bit]] = if (mask and bitMask > 0) '#' else '.'
					bitMask = bitMask shl 1
				}
				if (checkGroups(records, groups)) arrangementCount++
			}
			sum += arrangementCount
		}
		return sum
	}

	fun notInRange(sym: Char, records: String, start: Int, end: Int): Boolean {
		for (i in start until end) if (records[i] == sym) return false
		return true
	}

	var count = 0L
	val cache = mutableMapOf<Pair<Int, Int>, Long>()

	fun countRecurs(group: Int, start: Int, records: String, groups: List<Int>) {
		val gr = groups[group]
		if (start + gr > records.length) return
		for (i in start .. records.length - gr) {
			if (i == start && group != 0) {
				if (records[i] == '#') return
				continue
			}
			if (notInRange('.', records, i, i + gr)) {
				if (group < groups.size - 1) {
					val cnt = cache[group to i]
					if (cnt == null) {
						val prevCount = count
						countRecurs(group + 1, i + gr, records, groups)
						cache[group to i] = count - prevCount
					} else {
						count += cnt
					}
				} else if (notInRange('#', records, i + gr, records.length)) {
					count++
				}
			}
			if (records[i] == '#') return
		}
	}

	fun part2(input: List<String>): Long {
		var sum = 0L
		for (line in input) {
			val (r, strGroups) = line.split(' ')
			val records = "$r?$r?$r?$r?$r"
			val g = strGroups.split(',').map { it.toInt() }
			val groups = g + g + g + g + g
			count = 0L
			cache.clear()
			countRecurs(0, 0, records, groups)
			sum += count
		}
		return sum
	}

	val testInput = readInput("Day12_test")
	check(part1(testInput) == 21)
	check(part2(testInput) == 525152L)

	val input = readInput("Day12")
	part1(input).println()
	part2(input).println()
}
