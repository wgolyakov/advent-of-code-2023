enum class HandType {
	HighCard,
	OnePair,
	TwoPair,
	ThreeOfAKind,
	FullHouse,
	FourOfAKind,
	FiveOfAKind
}

fun main() {
	class Hand(val cards: String, val bid: Int) : Comparable<Hand> {
		private val labels = "23456789TJQKA".withIndex().associate { it.value to it.index }
		val type = determineType()

		private fun determineType(): HandType {
			val map = mutableMapOf<Char, Int>()
			for (c in cards) {
				map[c] = (map[c] ?: 0) + 1
			}
			val counts = map.map { it.value }.sorted().reversed()
			return when {
				counts[0] == 5 -> HandType.FiveOfAKind
				counts[0] == 4 -> HandType.FourOfAKind
				counts[0] == 3 && counts[1] == 2 -> HandType.FullHouse
				counts[0] == 3 -> HandType.ThreeOfAKind
				counts[0] == 2 && counts[1] == 2 -> HandType.TwoPair
				counts[0] == 2 -> HandType.OnePair
				else -> HandType.HighCard
			}
		}

		override fun compareTo(other: Hand): Int {
			val c = type.ordinal.compareTo(other.type.ordinal)
			if (c == 0) {
				for (i in 0 until 5) {
					val a = labels[cards[i]]!!
					val b = labels[other.cards[i]]!!
					if (a < b) return -1
					if (a > b) return 1
				}
				return 0
			} else return c
		}

		override fun toString() = cards
	}

	fun part1(input: List<String>): Int {
		val hands = input.map { line ->	line.split(' ').let { Hand(it[0], it[1].toInt()) }}
		return hands.sorted().withIndex().sumOf { it.value.bid * (it.index + 1) }
	}

	class Hand2(val cards: String, val bid: Int) : Comparable<Hand2> {
		private val labels = "J23456789TQKA".withIndex().associate { it.value to it.index }
		val type = determineType()

		private fun determineType(): HandType {
			val j = cards.count { it == 'J' }
			if (j == 5) return HandType.FiveOfAKind
			val map = mutableMapOf<Char, Int>()
			for (c in cards.filter { it != 'J' }) {
				map[c] = (map[c] ?: 0) + 1
			}
			val counts = map.map { it.value }.sorted().reversed()
			return when {
				counts[0] + j == 5 -> HandType.FiveOfAKind
				counts[0] + j == 4 -> HandType.FourOfAKind
				counts[0] + j == 3 && counts[1] == 2 -> HandType.FullHouse
				counts[0] + j == 3 -> HandType.ThreeOfAKind
				counts[0] + j == 2 && counts[1] == 2 -> HandType.TwoPair
				counts[0] + j == 2 -> HandType.OnePair
				else -> HandType.HighCard
			}
		}

		override fun compareTo(other: Hand2): Int {
			val c = type.ordinal.compareTo(other.type.ordinal)
			if (c == 0) {
				for (i in 0 until 5) {
					val a = labels[cards[i]]!!
					val b = labels[other.cards[i]]!!
					if (a < b) return -1
					if (a > b) return 1
				}
				return 0
			} else return c
		}

		override fun toString() = cards
	}

	fun part2(input: List<String>): Int {
		val hands = input.map { line ->	line.split(' ').let { Hand2(it[0], it[1].toInt()) }}
		return hands.sorted().withIndex().sumOf { it.value.bid * (it.index + 1) }
	}

	val testInput = readInput("Day07_test")
	check(part1(testInput) == 6440)
	check(part2(testInput) == 5905)

	val input = readInput("Day07")
	part1(input).println()
	part2(input).println()
}
